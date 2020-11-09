xquery version "3.0";
(:~
 : HTTP Request Module 1.0 (EXQuery Unofficial Draft 04 August 2013)
 :
 : @see http://docs.basex.org/wiki/Request_Module
 :)
module namespace request = "http://exquery.org/ns/request";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare namespace http = "http://expath.org/ns/http-client";

declare option o:requires-import "basex/7.5; until=basex/9.2; location-uri=(none)";

declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:address() as xs:string external;
declare %a:restrict-until("return", "basex", "9.2", "xs:string")
        %a:since("basex", "7.7") function request:attribute($name as xs:string) as item()* external;
declare %a:since("basex", "9.3") function request:attribute($name as xs:string, $default as item()*) as item()* external;
declare %a:since("basex", "9.3") function request:attribute-names() as xs:string* external;
declare %a:since("basex", "9.3") function request:set-attribute($name as xs:string, $value as item()*) as empty-sequence() external;
declare %a:restrict-since("return", "exquery-request", "1.0-20130804", "xs:string?")
        %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:cookie($name as xs:string) as xs:string* external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:cookie($name as xs:string, $default as xs:string) as xs:string external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:cookie-names() as xs:string* external;
declare %a:since("basex", "7.8") function request:context-path() as xs:string external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:header($name as xs:string) as xs:string? external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:header($name as xs:string, $default as xs:string) as xs:string external;
declare %a:restrict-since("return", "exquery-request", "1.0-20130804", "xs:string+")
        %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:header-names() as xs:string* external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:hostname() as xs:string external;
declare %a:since("exquery-request", "1.0-20130804") function request:http() as document-node(element(http:request)) external;
declare %a:since("basex", "7.5") function request:method() as xs:string external;
declare %a:since("exquery-request", "1.0-20130804") function request:method($request-context as function() as document-node(element(http:request))) as xs:string external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:parameter($name as xs:string) as xs:string* external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:parameter($name as xs:string, $default as xs:string) as xs:string* external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:parameter-names() as xs:string* external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:path() as xs:string external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:port() as xs:integer external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:query() as xs:string? external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:remote-address() as xs:string external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:remote-hostname() as xs:string external;
declare %a:restrict-since("return", "exquery-request", "1.0-20130804", "xs:integer")
        %a:restrict-since("return", "basex", "7.5", "xs:string")
        %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:remote-port() as (xs:integer|xs:string) external;
declare %a:since("basex", "7.5") function request:scheme() as xs:string external;
declare %a:since("exquery-request", "1.0-20130804") function request:scheme($request-context as function() as document-node(element(http:request))) as xs:string external;
declare %a:since("exquery-request", "1.0-20130804") %a:since("basex", "7.5") function request:uri() as xs:anyURI external;
