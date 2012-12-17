<?php

// Loads a GGB file for testing.
// Example: http://URL?f=Girl_in_Mirror.ggb
// Parameters:
//  f: parameter file
//  s: show filelist (set 0 for off)
//  h: show hidden files in filelist (set 1 for on)
//  m: milestones directory
//  v: show version number (if possible, set 0 for off)
//  c: codebase
//  t: toolbar
//  mb: menubar
//  i: inputbar
//  w: width
//  he: height
//  a: show animation button

// Copyright (c) 2012 The GeoGebra Team <geogebra-dev@geogebra.org>

// Defaults, override them with parameters:
// (If you add more parameters, don't forget to add them into
// function passoptions() as well!)

// $MILESTONES="milestones/ggbFiles"; // Please don't use this any longer. It's too long.
$MILESTONES="test";

$DEFAULTFILE="Girl_in_Mirror.ggb";
//$CODEBASE="http://dev.geogebra.org/~build/trunk-geogebra/web/war/";

$SHOWFILELIST=TRUE;
$SHOWHIDDEN=FALSE;
$VERSION=TRUE;
$TOOLBAR=NULL;
$MENUBAR=NULL;
$INPUTBAR=NULL;
$WIDTH=800;
$HEIGHT=550;
$SHOWANIMBUTTON=NULL;

if ($_GET['m']!="")
 $MILESTONES=$_GET['m'];
if ($_GET['s']=="0")
 $SHOWFILELIST=FALSE;
if ($_GET['h']=="1")
 $SHOWHIDDEN=TRUE;
if ($_GET['t']=="1")
 $TOOLBAR="true";
else if ($_GET['t']=="0")
 $TOOLBAR="false";
if ($_GET['mb']=="1")
 $MENUBAR="true";
if ($_GET['mb']=="0")
 $MENUBAR="false";
if ($_GET['i']=="1")
 $INPUTBAR="true";
if ($_GET['i']=="0")
 $INPUTBAR="false";
if ($_GET['v']=="0")
 $VERSION=FALSE;
if ($_GET['c']=="0")
 $CODEBASE=$_GET['c'];
if ($_GET['w']>"0")
 $WIDTH=$_GET['w'];
if ($_GET['he']>"0")
 $HEIGHT=$_GET['he'];
if ($_GET['a']=="0")
 $SHOWANIMBUTTON="false";
else if ($_GET['a']=="1")
 $SHOWANIMBUTTON="true";



// Importing all milestones files into $filenames:
$dh=opendir($MILESTONES);
$filenames=array();
while($f=readdir($dh)) {
 $condition=TRUE;
 if ($SHOWHIDDEN)
  $condition=($f[0]!="_");
 if ($f[0]!="." && $condition)
  $filenames[]=$f;
 }
sort($filenames);

// Checking version
$FILE=$_GET['f'];
$TITLE=pretty_filename($FILE,".ggb");
if ($VERSION) {
 $VERSIONNUMBER=file_get_contents($CODEBASE."web/version.txt");
 if ($VERSIONNUMBER != "") {
  $TITLE="[$VERSIONNUMBER] $TITLE";
  }
 } 

?>
<!DOCTYPE html>
<html>
<head><?php echo style() ?>
<title><?php echo $TITLE ?></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
    <script type="text/javascript" language="javascript"
     src="<?php echo $CODEBASE ?>web/web.nocache.js"></script>
    <article class="geogebraweb"
    data-param-enableLabelDrags="false"
    data-param-enableShiftDragZoom="false"
    <?php echo "data-param-width=\"$WIDTH\"" ?>
    <?php echo "data-param-height=\"$HEIGHT\"" ?>
    <?php if ($TOOLBAR) echo "data-param-showToolbar=\"$TOOLBAR\"" ?>
    <?php if ($MENUBAR) echo "data-param-showMenuBar=\"$MENUBAR\"" ?>
    <?php if ($INPUTBAR) echo "data-param-showAlgebraInput=\"$INPUTBAR\"" ?>
    <?php if ($SHOWANIMBUTTON) echo "data-param-showAnimationButton=\"$SHOWANIMBUTTON\"" ?>
    style="border: 1px solid black; display:inline-block;"
    data-param-ggbbase64=<?php

echo '"';
// Load the file:
$tempfile = in_array($FILE,$filenames) ? "$MILESTONES/$FILE" : "$MILESTONES/$DEFAULTFILE";
// Encode the file in base 64...
$handle = fopen($tempfile,'rb');
$file_content = fread($handle,filesize($tempfile));
fclose($handle);
//$encoded = chunk_split(base64_encode($file_content)); 
$encoded = base64_encode($file_content); 
// ... and print
echo $encoded;
echo '"'

?>></article><?php

// When asked, show the filelist:
if ($SHOWFILELIST) {
 echo "<div style=\"float:right;height:550px;overflow:auto\"><ul>";
 foreach ($filenames as $f) {
  
  echo "<li><a href=\"".$_SERVER['PHP_SELF']."?f=$f".passoptions()."\">".
   "<div style=\"".classify_filename($f)."\">".
   pretty_filename($f)."</div></a></li>";
  }

 echo "</ul></div>";
 
 // Adding ggbOnInit()
 echo '<script type="text/javascript">
  function ggbOnInit() { console.log("ggbOnInit: page loaded"); }
  </script>';

 echo "</body></html>";

 }

function bool2int($bool) {
 if ($bool)
  return 1;
 return 0;
}

function passoptions() {
 global $MILESTONES, $CODEBASE, $SHOWFILELIST, $SHOWHIDDEN, $VERSION;

 $ret = "&m=".$MILESTONES.
  "&c=".$CODEBASE.
  "&s=".bool2int($SHOWFILELIST).
  "&h=".bool2int($SHOWHIDDEN).
  "&v=".bool2int($VERSION);

 if ($_GET['w']!="") $ret .= "&w=".$_GET['w'];
 if ($_GET['he']!="") $ret .= "&he=".$_GET['he'];
 if ($_GET['t']!="") $ret .= "&t=".$_GET['t'];
 if ($_GET['mb']!="") $ret .= "&mb=".$_GET['mb'];
 if ($_GET['i']!="") $ret .= "&i=".$_GET['i'];
 if ($_GET['a']!="") $ret .= "&a=".$_GET['a'];

 return $ret;
 }

function pretty_filename($name) {
 return str_replace("_", " ", ltrim(basename($name, ".ggb"),"_"));
 }

function classify_filename($name) {
 global $FILE;
 if ($name[0]=="_")
  $style="color:#808080;";
 else
  $style="color:#200000;";
 if ($name==$FILE)
  $style.="background-color:yellow;";
 return $style;
 }

// Add here extra styles...
function style() {
 //return "<style>html,body { height: 100% }</style>";
 }

?>
