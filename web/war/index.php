<?php

  if ($_GET['f']!="") {
    // This is a web call ("web applet mode") and test.php must be used:
    $dir=rtrim($_SERVER['SCRIPT_URL'],'index.php');
    $dir=rtrim($dir,"/");
    header("Location: $dir/test.php?".$_SERVER['QUERY_STRING']);
    die();
  }

  // Redirect the application to GeoGebra.
  header('Location: app.html');

?>