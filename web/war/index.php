<?php

  if ($_SERVER['f']!="") {
    // This is a web call ("web applet mode") and test.php must be used:
    $dir=rtrim($_SERVER['SCRIPT_URL'],'index.php');
    $dir=rtrim($dir,"/");
    header("Location: $dir/test.php?".$_SERVER['QUERY_STRING']);
    die();
  }
	
  // This is a web_gui call.
  $languages = $_SERVER['HTTP_ACCEPT_LANGUAGE'];
  if (isset($languages)) {
    // Find the first comma, get a substring from location 0 up to the location of the comma.
    $comma_pos = strpos($languages, ',');
    $locale = substr($languages, 0, $comma_pos);
	
    // The accepted character among the locale variable are a-z or A-Z or _ or - only.
    if (preg_match('/^[a-z_\-]+$/i', $locale)) {
	  $gwtLocale = str_replace('-', '_', $locale);
    } else {
	$gwtLocale = 'en';
    } 
  } else {
    $gwtLocale = 'en';
  }

  // Redirect the application to GeoGebra and pass the language parameter.
  // If language is not recognised then en is the default.
  header('Location: http://www.geogebra.org/web/web_gui/?locale=' . $gwtLocale);
  // Instead of this, we should build up a full HTML page
  // as it is present on this URL. FIXME! 
?>