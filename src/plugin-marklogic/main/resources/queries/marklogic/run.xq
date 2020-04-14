(:
 : Copyright (C) 2018-2019 Reece H. Dunn
 :
 : Licensed under the Apache License, Version 2.0 (the "License");
 : you may not use this file except in compliance with the License.
 : You may obtain a copy of the License at
 :
 : http://www.apache.org/licenses/LICENSE-2.0
 :
 : Unless required by applicable law or agreed to in writing, software
 : distributed under the License is distributed on an "AS IS" BASIS,
 : WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 : See the License for the specific language governing permissions and
 : limitations under the License.
 :)
xquery version "1.0-ml";

import module namespace sem = "http://marklogic.com/semantics" at "/MarkLogic/semantics.xqy";

declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare option o:implementation "marklogic/6.0";

(:~ Run a query on a MarkLogic server.
 :
 : @param $mode Whether to "run", "profile", or "debug" the query.
 : @param $mimetype The mimetype of the query.
 : @param $module-path The path of the module to invoke, or "" for evaluated scripts.
 : @param $query The query script to evaluate, or "" for invoked modules.
 : @param $vars A map of the variable values by the variable name.
 : @param $types A map of the variable types by the variable name.
 : @param $context-value The context item for XSLT queries as an XML object.
 : @param $context-path The context item for XSLT queries as a module file.
 : @param $rdf-output-format The mimetype to format sem:triple results as, or "" to leave them as is.
 : @param $updating An updating query if "true", or a non-updating query if "false".
 : @param $server The name of the server to use, or "" to not use a server.
 : @param $database The name of the database to use, or "" to not use a database.
 : @param $module-root The path to the server modules, or "" to use the default server value.
 :
 : This script has additional logic to map the semantics of the REST/XCC API to
 : the semantics of the API implemented in the xquery-intellij-plugin to support
 : the Run/Debug Configurations of the IntelliJ IDEs. Specifically:
 :
 : 1.  If there is an error, the REST API returns a "500 Internal Error" HTTP
 :     status. This catches the error and returns an element(err:error) object.
 :
 : 2.  The variables are passed via the REST API as xs:untypedAtomic. This
 :     converts the variables to the type they were specified as in the
 :     xquery-intellij-plugin API.
 :
 : 3.  The REST API only returns the primitive type of each item.
 :)

declare variable $mode as xs:string external;
declare variable $mimetype as xs:string external;
declare variable $module-path as xs:string external;
declare variable $query as xs:string external;
declare variable $vars as xs:string external;
declare variable $types as xs:string external;
declare variable $context-value as xs:string external;
declare variable $context-path as xs:string external;
declare variable $rdf-output-format as xs:string external;
declare variable $updating as xs:string external;
declare variable $server as xs:string external;
declare variable $database as xs:string external;
declare variable $module-root as xs:string external;

declare function local:nullize($value) {
    if (string-length($value) eq 0) then () else $value
};

declare function local:version() {
    substring-before(xdmp:version(), "-") cast as xs:double
};

declare function local:version-minor() {
    substring-after(xdmp:version(), "-") cast as xs:double
};

declare function local:function($ref as xs:string) {
    try { xdmp:eval($ref) } catch * { () }
};

declare function local:item($path as xs:string, $value as xs:string, $type as xs:string) {
    if (string-length($value) ne 0) then
        xdmp:unquote($value)
    else if (string-length($path) ne 0) then
        doc($path)
    else
        ()
};

declare function local:cast-as($value, $type) {
    switch ($type)
    case "xs:anyURI" return $value cast as xs:anyURI
    case "xs:base64Binary" return $value cast as xs:base64Binary
    case "xs:boolean" return $value cast as xs:boolean
    case "xs:byte" return $value cast as xs:byte
    case "xs:date" return $value cast as xs:date
    case "xs:dateTime" return $value cast as xs:dateTime
    case "xs:dateTimeStamp" return $value cast as xs:dateTime (: MarkLogic does not support XML Schema 1.1 Part 2 :)
    case "xs:dayTimeDuration" return $value cast as xs:dayTimeDuration
    case "xs:decimal" return $value cast as xs:decimal
    case "xs:double" return $value cast as xs:double
    case "xs:duration" return $value cast as xs:duration
    case "xs:float" return $value cast as xs:float
    case "xs:gDay" return $value cast as xs:gDay
    case "xs:gMonth" return $value cast as xs:gMonth
    case "xs:gMonthDay" return $value cast as xs:gMonthDay
    case "xs:gYear" return $value cast as xs:gYear
    case "xs:gYearMonth" return $value cast as xs:gYearMonth
    case "xs:hexBinary" return $value cast as xs:hexBinary
    case "xs:int" return $value cast as xs:int
    case "xs:integer" return $value cast as xs:integer
    case "xs:language" return $value cast as xs:language
    case "xs:long" return $value cast as xs:long
    case "xs:negativeInteger" return $value cast as xs:negativeInteger
    case "xs:nonNegativeInteger" return $value cast as xs:nonNegativeInteger
    case "xs:nonPositiveInteger" return $value cast as xs:nonPositiveInteger
    case "xs:normalizedString" return $value cast as xs:normalizedString
    case "xs:numeric" return $value cast as xs:numeric
    case "xs:positiveInteger" return $value cast as xs:positiveInteger
    case "xs:short" return $value cast as xs:short
    case "xs:string" return $value cast as xs:string
    case "xs:time" return $value cast as xs:time
    case "xs:token" return $value cast as xs:token
    case "xs:unsignedByte" return $value cast as xs:unsignedByte
    case "xs:unsignedInt" return $value cast as xs:unsignedInt
    case "xs:unsignedLong" return $value cast as xs:unsignedLong
    case "xs:unsignedShort" return $value cast as xs:unsignedShort
    case "xs:yearMonthDuration" return $value cast as xs:yearMonthDuration
    case "xs:ENTITY" return $value cast as xs:ENTITY
    case "xs:ID" return $value cast as xs:ID
    case "xs:IDREF" return $value cast as xs:IDREF
    case "xs:Name" return $value cast as xs:Name
    case "xs:NCName" return $value cast as xs:NCName
    case "xs:NMTOKEN" return $value cast as xs:NMTOKEN
    case "xs:QName" return $value cast as xs:QName
    default return $value
};

declare function local:parse-vars($values as map:map, $types as map:map) as item()* {
    for $key in map:keys($values)
    let $value := local:cast-as(map:get($values, $key), map:get($types, $key))
    return (xs:QName($key), $value)
};

declare function local:parse-vars-map($values as map:map, $types as map:map) as map:map {
    let $ret := map:map()
    let $_ :=
        for $key in map:keys($values)
        let $value := local:cast-as(map:get($values, $key), map:get($types, $key))
        return map:put($ret, $key, $value)
    return $ret
};

declare function local:derived-type-name($value) {
    (: NOTE: Ordering is important here -- reverse type hierarchy :)
    typeswitch ($value)
    case function(*) return "function(*)"
    (: xs:integer derived types :)
    case xs:positiveInteger return "xs:positiveInteger"
    case xs:unsignedByte return "xs:unsignedByte"
    case xs:unsignedShort return "xs:unsignedShort"
    case xs:unsignedInt return "xs:unsignedInt"
    case xs:unsignedLong return "xs:unsignedLong"
    case xs:nonNegativeInteger return "xs:nonNegativeInteger"
    case xs:byte return "xs:byte"
    case xs:short return "xs:short"
    case xs:int return "xs:int"
    case xs:long return "xs:long"
    case xs:negativeInteger return "xs:negativeInteger"
    case xs:nonPositiveInteger return "xs:nonPositiveInteger"
    (: xs:string derived types :)
    case xs:ENTITY return "xs:ENTITY"
    case xs:IDREF return "xs:IDREF"
    case xs:ID return "xs:ID"
    case xs:NCName return "xs:NCName"
    case xs:Name return "xs:Name"
    case xs:NMTOKEN return "xs:NMTOKEN"
    case xs:language return "xs:language"
    case xs:token return "xs:token"
    case xs:normalizedString return "xs:normalizedString"
    default return () (: primitive type, so does not need the type name to be specified. :)
};

declare function local:rdf-format($mimetype) {
    switch ($mimetype)
    case "application/n-quads" return "nquad"
    case "application/n-triples" return "ntriple"
    case "application/rdf+json" return "rdfjson"
    case "application/rdf+xml" return "rdfxml"
    case "application/trig" return "trig"
    case "application/vnd.marklogic.triples+xml" return "triplexml"
    case "text/n3" return "n3"
    case "text/turtle" return "turtle"
    default return fn:error("UNSUPPORTED-RDF-FORMAT", "Unsupported RDF format: " || $mimetype)
};

declare function local:eval-options() {
    let $server := local:nullize($server) ! xdmp:server(.)
    let $database := local:nullize($database) ! xdmp:database(.)
    return <options xmlns="xdmp:eval">{
        if ($mode eq "validate") then
            <static-check>true</static-check>
        else
            (),
        if (exists($database)) then
            <database>{$database}</database>
        else
            (),
        if (exists($server)) then
            <default-collation>{xdmp:server-collation($server)}</default-collation>
        else
            (),
        let $server-coordinate-system := local:function("xdmp:server-coordinate-system#1")
        return if (exists($server) and exists($server-coordinate-system)) then
            <default-coordinate-system>{$server-coordinate-system($server)}</default-coordinate-system>
        else
            (),
        if (exists($server) and $mimetype eq "application/xquery") then
            <default-xquery-version>{xdmp:server-default-xquery-version($server)}</default-xquery-version>
        else
            (),
        if (exists($server)) then
            <modules>{xdmp:server-modules-database($server)}</modules>
        else
            <modules>0</modules> (: file system :),
        if (exists($server) or string-length($module-root) ne 0) then
            <root>{(local:nullize($module-root), xdmp:server-root($server))[1]}</root>
        else
            (),
        let $major := local:version()
        let $minor := local:version-minor()
        return if ($mode = "debug") then
            if ($updating) then
                fn:error(xs:QName("err:FOER0000"), "Debugging an update query is not supported by MarkLogic.")
            else
                ()
        else if ($major lt 7.0 or ($major eq 8.0 and $minor lt 7.0) or ($major eq 9.0 and $minor lt 2.0)) then
            if ($updating) then
                <transaction-mode>update</transaction-mode>
            else
                <transaction-mode>query</transaction-mode>
        else
            (: MarkLogic 8.0-7 and 9.0-2 support <update> :)
            <update>{$updating}</update>
    }</options>
};

declare function local:error($err:additional as element(error:error)) {
    let $code := $err:additional/error:code/text()
    let $name := $err:additional/error:name/text()
    let $message := $err:additional/error:message/text()
    let $code := if ($code = $message) then $name else $code
    return <err:error xmlns:dbg="http://reecedunn.co.uk/xquery/debug">
        <err:code>{
            if (exists($code) and not(starts-with($code, "XDMP-"))) then
                $code
            else
                "err:FOER0000"
        }</err:code>
        <err:vendor-code>{$code}</err:vendor-code>
        <err:description>{$message}</err:description>
        <err:value count="{count($err:additional/error:data/error:datum)}">{
            $err:additional/error:data/error:datum/text() ! <err:item>{.}</err:item>
        }</err:value>
        <dbg:stack>{
            for $frame in $err:additional/error:stack/error:frame[position() != last()]
            let $module := $frame/error:uri/text()
            let $line := $frame/error:line/text() cast as xs:integer?
            let $column := $frame/error:column/text() cast as xs:integer?
            return <dbg:frame><dbg:module line="{$line}" column="{$column + 1}">{$module}</dbg:module></dbg:frame>
        }</dbg:stack>
    </err:error>
};

declare function local:javascript() as item()* {
    let $variables := local:parse-vars(xdmp:unquote($vars), xdmp:unquote($types))
    let $options := local:eval-options()
    return if (local:version() < 8.0) then
        fn:error(
            xs:QName("UNSUPPORTED-VERSION"),
            "MarkLogic " || xdmp:version() || " does not support server-side JavaScript"
        )
    else if (string-length($query) ne 0) then
        switch ($mode)
        case "run" return
            let $f := local:function("xdmp:javascript-eval#3")
            let $start := xdmp:elapsed-time()
            let $ret := $f($query, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        default return ()
    else
        switch ($mode)
        case "run" return
            let $start := xdmp:elapsed-time()
            let $ret := xdmp:invoke($module-path, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        default return ()
};

declare function local:sparql-query() as item()* {
    let $variables := local:parse-vars-map(xdmp:unquote($vars), xdmp:unquote($types))
    let $query :=
        if (string-length($query) ne 0) then
            $query
        else
            () (: TODO: Read the contents of the query file from the modules database. :)
    return switch ($mode)
    case "run" return
        let $start := xdmp:elapsed-time()
        let $ret := sem:sparql($query, $variables)
        return (xdmp:elapsed-time() - $start, $ret)
    default return ()
};

declare function local:sparql-update() as item()* {
    let $variables := local:parse-vars-map(xdmp:unquote($vars), xdmp:unquote($types))
    let $query :=
        if (string-length($query) ne 0) then
            $query
        else
            () (: TODO: Read the contents of the query file from the modules database. :)
    return if (local:version() < 8.0) then
        fn:error(
            xs:QName("UNSUPPORTED-VERSION"),
            "MarkLogic " || xdmp:version() || " does not support SPARQL update queries"
        )
    else switch ($mode)
    case "run" return
        let $start := xdmp:elapsed-time()
        let $ret := sem:sparql-update($query, $variables)
        return (xdmp:elapsed-time() - $start, $ret)
    default return ()
};

declare function local:sql() as item()* {
    let $variables := local:parse-vars(xdmp:unquote($vars), xdmp:unquote($types))
    let $query :=
        if (string-length($query) ne 0) then
            $query
        else
            () (: TODO: Read the contents of the query file from the modules database. :)
    return switch ($mode)
    case "run" return
        if (local:version() < 9.0) then
            let $f := local:function("xdmp:sql#1")
            let $start := xdmp:elapsed-time()
            let $ret := $f($query)
            return (xdmp:elapsed-time() - $start, $ret)
        else
            let $f := local:function("xdmp:sql#3")
            let $start := xdmp:elapsed-time()
            let $ret := $f($query, (), $variables)
            return (xdmp:elapsed-time() - $start, $ret)
    default return ()
};

declare function local:xquery() as item()* {
    let $variables := local:parse-vars(xdmp:unquote($vars), xdmp:unquote($types))
    let $options := local:eval-options()
    return if (string-length($query) ne 0) then
        switch ($mode)
        case "debug" return dbg:eval($query, $variables, $options)
        case "profile" return prof:eval($query, $variables, $options)
        case "run" return
            let $start := xdmp:elapsed-time()
            let $ret := xdmp:eval($query, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        case "validate" return xdmp:eval($query, $variables, $options)
        default return ()
    else
        switch ($mode)
        case "debug" return dbg:invoke($module-path, $variables, $options)
        case "profile" return prof:invoke($module-path, $variables, $options)
        case "run" return
            let $start := xdmp:elapsed-time()
            let $ret := xdmp:invoke($module-path, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        case "validate" return xdmp:invoke($module-path, $variables, $options)
        default return ()
};

declare function local:xslt() as item()* {
    let $variables := local:parse-vars(xdmp:unquote($vars), xdmp:unquote($types))
    let $input := local:item($context-path, $context-value, "application/xml")
    let $options := local:eval-options()
    return if (string-length($query) ne 0) then
        switch ($mode)
        case "profile" return prof:xslt-eval(xdmp:unquote($query), $input, $variables, $options)
        case "run" return
            let $query := xdmp:unquote($query)
            let $start := xdmp:elapsed-time()
            let $ret := xdmp:xslt-eval($query, $input, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        case "validate" return xdmp:xslt-eval(xdmp:unquote($query), $input, $variables, $options)
        default return ()
    else
        switch ($mode)
        case "profile" return prof:xslt-invoke($module-path, $input, $variables, $options)
        case "run" return
            let $start := xdmp:elapsed-time()
            let $ret := xdmp:xslt-invoke($module-path, $input, $variables, $options)
            return (xdmp:elapsed-time() - $start, $ret)
        case "validate" return xdmp:xslt-invoke($module-path, $input, $variables, $options)
        default return ()
};

try {
    let $retvals :=
        switch ($mimetype)
        case "application/sparql-query" return local:sparql-query()
        case "application/sparql-update" return local:sparql-update()
        case "application/sql" return local:sql()
        case "application/vnd.marklogic-javascript" return local:javascript()
        case "application/xquery" return local:xquery()
        case "application/xslt+xml" return local:xslt()
        default return ()
    let $triples := for $item in $retvals where $item instance of sem:triple return $item
    let $retvals := for $item in $retvals where not($item instance of sem:triple) return $item
    return if (exists($triples) and $rdf-output-format ne "") then
        let $rdf-output := sem:rdf-serialize($triples, local:rdf-format($rdf-output-format))
        let $_ := xdmp:add-response-header("X-Content-Type-" || (count($retvals) + 1), $rdf-output-format)
        return (
            for $retval at $i in $retvals
            let $_ := local:derived-type-name($retval) ! xdmp:add-response-header("X-Derived-" || $i, .)
            return $retval,
            $rdf-output
        )
    else
        for $retval at $i in ($retvals, $triples)
        let $_ := local:derived-type-name($retval) ! xdmp:add-response-header("X-Derived-" || $i, .)
        return $retval
} catch * {
    let $_ := xdmp:add-response-header("X-Derived-1", "err:error")
    return local:error($err:additional)
}
