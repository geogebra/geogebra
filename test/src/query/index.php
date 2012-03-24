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

$content.="<table border=1><thead><tr><td>Test name</td>";

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
 $content.="<td align=center>";
 if ($i==1)
  $content.="<a href=".mydir()."?lastrev=$rev>&lt;</a> ";
 $content.="<a href=http://dev.geogebra.org/trac/changeset/$rev>[$rev]</a>";
 if ($i==$maxrevs || $i==$numrows)
  $content.=" <a href=".mydir()."?firstrev=$rev>&gt;</a>";
 $content.="</td>";
}
$content.="</tr></thead>";

// Collecting info for each test name:
$sql="SELECT id FROM names";

foreach ($db->query($sql) as $name) {
 $n=$name['id'];
 $content.="<tr><td>$n</td>";
 foreach ($revs as $rev) {
  $content.="<td ";
  $sql2="SELECT * from tests where name='$n' and revision='$rev'";
  $result=$db->query($sql2);
  if (!$result) {
   $content.="bgcolor=lightgreen align=center>ok";
   }
  else {
   foreach ($result as $row) {
    $content.="bgcolor=pink align=center>";
    $cn=$row['classname'];
    $t=$row['type'];
    $message=$row['message'];

    // Creating class name (by trimming a bit)
    $cname="";
    for ($i=strlen($cn); $i>=0 && $cn[$i]!="."; --$i)
     $cname=$cn[$i].$cname;
    $cnl=strlen($cname);
    if (substr($cname,$cnl-4,4)=="Test");
     $cname=substr($cname,0,$cnl-4);

    // Creating type (plus trimming)
    $type="";
    for ($i=strlen($t); $i>=0 && $t[$i]!="."; --$i)
     $type=$t[$i].$type;
    $tl=strlen($type);
    if (substr($type,$tl-5,5)=="Error");
     $type=substr($type,0,$tl-5);
    
    //$content.="<a href=\"#\" class=info_link>$cname/$type</a>";
    //$content.="<span class=info>$message</span>";

    $content.="<a href=\"#\" title=\"$message\">$cname/$type</a>";

    }
   }
  $content.="</td>";
  
  }
 $content.="</tr>";
 }

$content.="</table>";

$title="The recent $maxrevs tests";
if ($lastrev!="")
 $title.=" (not later than [$lastrev])";
if ($firstrev!="")
 $title.=" (not earlier than [$firstrev])";

content ($title,$content);

$db=null;

?>
