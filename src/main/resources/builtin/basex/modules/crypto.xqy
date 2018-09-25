xquery version "3.0";

(:~
: User: bridger
: Date: 9/22/18
: Time: 3:39 PM
: To change this template use File | Settings | File Templates.
:)

module namespace crypto = "http://expath.org/ns/crypto";

import module namespace a = "http://reecedunn.co.uk/xquery/annotations" at "res://reecedunn.co.uk/xquery/annotations.xqy";

declare %a:since("basex", "7.0") function crypto:hmac($message as xs:string, $key as xs:anyAtomicType, $algorithm as xs:string) as xs:base64Binary (: $key as [7.0]xs:string [8.6]xs:anyAtomicType :) external;
declare %a:since("basex", "7.0") function crypto:hmac($message as xs:string, $key as xs:anyAtomicType, $algorithm as xs:string, $encoding as xs:string) as xs:base64Binary (: $key as [7.0]xs:string [8.6]xs:anyAtomicType :) external;
declare %a:since("basex", "7.0") function crypto:encrypt($input as xs:string, $encoding as xs:string, $key as xs:string, $algorithm as xs:string) as xs:string external;
declare %a:since("basex", "7.0") function crypto:decrypt($input as xs:string, $type as xs:string, $key as xs:string, $algorithm as xs:string) as xs:string external;
declare %a:since("basex", "7.0") function crypto:generate-signature($input as node(), $canonicalization as xs:string, $digest as xs:string, $signature as xs:string, $prefix as xs:string, $type as xs:string) as node() external;
declare %a:since("basex", "7.0") function crypto:generate-signature($input as node(), $canonicalization as xs:string, $digest as xs:string, $signature as xs:string, $prefix as xs:string, $type as xs:string, $xpath as xs:string, $certificate as node()) as node() external;
declare %a:since("basex", "7.0") function crypto:generate-signature($input as node(), $canonicalization as xs:string, $digest as xs:string, $signature as xs:string, $prefix as xs:string, $type as xs:string, $ext as item()) as node() external;
declare %a:since("basex", "7.0") function crypto:validate-signature($input-doc as node()) as xs:boolean external;