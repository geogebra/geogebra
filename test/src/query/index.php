<?php
# This PHP script will show some JUnit test output
# in a table format. The database is defined
# in ../../setup, and it is filled by running
# ../../junit2sqlite and ../../warnings.

# First make sure that conf.inc contains proper settings.

# @author Zoltan Kovacs <zoltan@geogebra.org>

include_once("conf.inc");
include_once("html.inc");

$lastrev=$_GET['lastrev'];
$firstrev=$_GET['firstrev'];

$openonly= $_GET['openonly'];
$orderbyid=$_GET['orderbyid'];

function add_options($override) {
 global $openonly, $orderbyid;
 parse_str($override);
 $ret="";
 if ($openonly)
  $ret.="&openonly=$openonly";
 if ($orderbyid)
  $ret.="&orderbyid=$orderbyid";
 return $ret;
 }

// Sanitizing input:
if (!is_numeric($lastrev))
 $lastrev="";
if (!is_numeric($firstrev))
 $firstrev="";

myheader("Querying the JUnit database");

$db=new PDO("sqlite:$dbfile"); 

// Getting revisions and creating table header:
if ($firstrev=="") {
 $sql="SELECT id FROM revisions";
 if ($lastrev!="")
  $sql.=" where id<='$lastrev'";
 $sql.=" order by tested desc ";
 foreach ($db->query($sql) as $minrevision) {
  $id=$minrevision['id'];
  $i++;
  if ($i<=$maxrevs)
   $minrev=$id;
  }
 $sql="SELECT id FROM revisions where ";
 if ($lastrev!="")
  $sql.=" id<='$lastrev' and ";
 $sql.=" id>='$minrev'";
 $sql.=" order by tested limit $maxrevs";
 }
else {
  $sql="SELECT id FROM revisions where id>='$firstrev' order by tested limit $maxrevs";
 }

$content.="<table border=1><thead><tr><td>Test name
<a href=\"".mydir()."?".add_options("orderbyid=1")."\" title=\"Ascending order\">&uarr;</a>
<a href=\"".mydir()."?".add_options("orderbyid=-1")."\" title=\"Descending order\">&darr;</a>
<a href=\"".mydir()."?".add_options("orderbyid=")."\" title=\"Introduction order\">~</a>
</td>";

// Unelegant way to get the number of rows, but no other idea
// due to http://www.php.net/manual/en/pdostatement.rowcount.php, Example #2.
$sqlcount=str_replace("SELECT id ","SELECT count(id) ",$sql);
$result=$db->query($sqlcount);
$numrows=$result->fetchColumn();

$i=0;
foreach ($db->query($sql) as $revision) {
 $i++;
 $rev=$revision['id'];
 $revs[]=$rev;
 $content.="<td class=\"rev\">";
 if ($i==1)
  $content.="<a href=\"".mydir()."?lastrev=$rev".add_options()."\" title=\"Earlier revisions\">&larr;</a> ";
 $content.="<a href=\"http://dev.geogebra.org/trac/changeset/$rev\" title=\"Show revision changes\">[$rev]</a>";
 if ($i==$maxrevs || $i==$numrows)
  $content.=" <a href=\"".mydir()."?firstrev=$rev".add_options()."\" title=\"Later revisions\">&rarr;</a>";
 $content.="</td>";
}
$content.="</tr></thead>";

// Get the latest rev from db:
$sql="SELECT id FROM revisions order by tested desc limit 1";
foreach ($db->query($sql) as $id) {
 $latestrev=$id["id"];
 }
$content.="<p>Last revision in database is [$latestrev]. ";

// Collecting info for each test name:
$sql="SELECT id FROM names";
if ($openonly=="1" || $openonly=="yes" || $openonly=="true") {
 $sql.=" where id in (SELECT name from tests where revision='$latestrev')";
 $content.="<a href=\"".mydir()."?".add_options("openonly=0")."\">Show all tests.</a></p>";
 }
else
 $content.="<a href=\"".mydir()."?".add_options("openonly=1")."\">Show problematic tests only.</a></p>";

if ($orderbyid=="1")
 $sql.=" order by id";
if ($orderbyid=="-1")
 $sql.=" order by id desc";

foreach ($db->query($sql) as $name) {
 $n=$name['id'];
 $nformatted=str_replace("_"," ",$n);

 $content.="<tr><td>$nformatted</td>";
 $lastCell = "";
 $colspan = 0;
 foreach ($revs as $rev) {
  $colspan++;
  $cell = "<td colspan=\"%%\"";
  $sql2 = "SELECT * from tests where name='$n' and revision='$rev'";
  $result = $db->query($sql2);
  $sql2count = str_replace("SELECT * ","SELECT count(*) ",$sql2);
  $resultno=$db->query($sql2count);
  $numrows=$resultno->fetchColumn();
  if ($numrows==0) {
   // Checking if there was a problem earlier
   $sql3count="SELECT COUNT(*) from tests where name='$n' and revision<'$rev'";
   $resultno=$db->query($sql3count);
   $numrows=$resultno->fetchColumn();
   if ($numrows>=1){
   $sqlMax="select max(id) from revisions left join tests 
        on tests.revision=revisions.id and tests.name='$n' 
        where revisions.id <= '$rev' and ifnull(message,'ok')!='ok'";

    $resultMax=$db->query($sqlMax); 
    if(is_object($resultMax)){
     $ok = $resultMax->fetchColumn();
    }
    $cell .= "class=\"ok\">";
    $cell.=" last bug <a href=\"?lastrev=$ok".
        (isset($_GET["openonly"]) && $_GET["openonly"] ?"&openonly=1":"")."\">$ok</a>";
    
   }else
    $cell .= "class=\"unknown\">";
   }
  else {
   foreach ($result as $row) {
    $error=$row['error'];
    if ($error==1) {
     $cell .= "class=\"error\">";
     $errors[$rev]++;
     }
    else {
     $cell .= "class=\"failure\">";
     $failures[$rev]++;
     }
    $cn=$row['classname'];
    $t=$row['type'];
    $message=$row['message'];

    // Creating class name (by trimming a bit)


$sqlMax="select max(id) from revisions left join tests 
	on tests.revision=revisions.id and tests.name='$n' 
	where revisions.id <= '$rev' and ifnull(message,'ok')='ok'";

   $resultMax=$db->query($sqlMax); 
//error_reporting(E_ALL);
//ini_set("display_errors",1);
if(is_object($resultMax)){
   $ok = $resultMax->fetchColumn();
$sqlMax="select min(id) from revisions where id>'$ok'";
$resultMax=$db->query($sqlMax); 
//error_reporting(E_ALL);
//ini_set("display_errors",1);
if(is_object($resultMax)){
   $ok = $resultMax->fetchColumn();

}
}
    // Changing " to '' in the message (no better idea)
    $message=str_replace('"',"''",$message);
    
    $cell.="<div title=\"$message\">".strtr(substr($message,0,300),array("Expected:"=>"<b>Exp.:</b>","got:"=>"<br><b>Got:</b>"))." <a href=\"?lastrev=$ok".
	(isset($_GET["openonly"]) && $_GET["openonly"] ?"&openonly=1":"")."\">$ok</a></div>";
    }
   }
  $cell .= "</td>";
  if($lastCell != $cell){
	$content .= str_replace("%%",$colspan,$lastCell);
	$colspan = 0;
  }
  $lastCell = $cell;
  }
 $content .= str_replace("%%",$colspan + 1,$lastCell);
 $content.="</tr>";
 }

// Adding sums:
$content.="<tr class=\"rev\"><td><b>Failures</b></td>";
foreach ($revs as $rev) {
 $content.="<td><b>".$failures[$rev]."</b></td>";
 }
$content.="</tr>";
$content.="<tr class=\"rev\"><td><b>Errors</b></td>";
foreach ($revs as $rev) {
 $content.="<td><b>".$errors[$rev]."</b></td>";
 }
$content.="</tr>";

$content.="</table>";

$title="The <a href=\"".mydir()."?".add_options()."\">recent $maxrevs</a> tests";
if ($lastrev!="")
 $title.=" (not later than [$lastrev])";
if ($firstrev!="")
 $title.=" (not earlier than [$firstrev])";

content($title,$content);

$db=null;

?>
