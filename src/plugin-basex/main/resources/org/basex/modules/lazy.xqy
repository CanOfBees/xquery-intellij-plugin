xquery version "3.0";
(:~
 : BaseX Lazy Module functions
 :
 : @see http://docs.basex.org/wiki/Lazy_Module
 :)
module namespace lazy = "http://basex.org/modules/lazy";

declare namespace a = "http://reecedunn.co.uk/xquery/annotations";
declare namespace o = "http://reecedunn.co.uk/xquery/options";

declare option o:requires "basex/9.0";

declare %a:restrict-until("return", "basex", "9.1", "item()")
        %a:since("basex", "9.0") function lazy:cache($items as cache-items) as item()* external;
declare %a:since("basex", "9.1") function lazy:cache($items as item()*, $lazy as xs:boolean) as item()* external;
declare %a:since("basex", "9.0") function lazy:is-lazy($item as item()) as xs:boolean external;
declare %a:since("basex", "9.0") function lazy:is-cached($item as item()) as xs:boolean external;