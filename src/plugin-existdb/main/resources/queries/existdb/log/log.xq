(:
 : Copyright (C) 2020 Reece H. Dunn
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
xquery version "3.1";
declare namespace o = "http://reecedunn.co.uk/xquery/options";
declare option o:implementation "exist-db/4.0";

(: Return the log files on the eXist-db server. :)

declare variable $name as xs:string external;

if (contains($name, "/") or contains($name, "\")) then
    ()
else
    let $log-path := system:get-exist-home() || "/logs"
    return $name || "|" || file:read($log-path || "/" || $name)
