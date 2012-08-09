<?php
  
  $languaes = $_SERVER['HTTP_ACCEPT_LANGUAGE'];
  if(isset($languaes)) {
    //find the first comma, get a substring from location 0 up to the location of the comma.
    $comma_pos = strpos($languaes, ',');
	$locale = substr($languaes, 0, $comma_pos);
	
	//The accepted character among the locale variable are a-z or A-Z or _ or - only
	if(preg_match('/^[a-z_\-]+$/i', $locale)) {
	  $gwtLocale = str_replace('-', '_', $locale);
	} else {
	  $gwtLocale = 'en';
	} 
	} else {
	  $gwtLocale = 'en';
	}
	
	//redirect the application to GeoGebra and pass the language parameter
	//If language is not recognised then en is the default
    header('Location: http://www.geogebra.org/web/web_gui/?locale=' . $gwtLocale);
  ?>