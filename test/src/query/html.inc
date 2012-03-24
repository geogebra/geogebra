<?php
function myheader($title,$refresh) {
 echo '
<!DOCTYPE html>
<html>
<head>
   <title>'.$title.'</title>
   <link rel="stylesheet" type="text/css" href="style.css" />
   <link rel="shortcut icon" href="favicon.ico" />
   <meta charset="utf-8">';
if ($refresh)
 echo "<meta http-equiv=\"refresh\" content=\"$refresh\"/>";
echo '   
</head>
<!-- Defining Header -->
  <header>
          <h1>'.$title.'</h1>
     </header>
  ';
 }

function content($title,$content) {
 echo '
<div id="content">
<div id="mcontent">
<!-- Defining Header for content section -->
<header>
<h2>'.$title.'</h2>
</header>
<!-- Defining Content Section -->
<section>
<!-- Defining content section article -->
<article class="bpage">
<div>'.$content.'</div>
				</article>
</section></div>
</div>
<!-- Defining Footer -->
<footer>
<section>JKU Institut für Didaktik der Mathematik, Altenbergerstr 69, 4040, Linz, Austria, Tel. +43 732 2468 4010</section>
</footer>
</html>';
 }

function mydir() {
 global $_SERVER;
 return "http://".$_SERVER["HTTP_HOST"].dirname($_SERVER["SCRIPT_NAME"])."/";
 }

?>
