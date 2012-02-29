/*
 * 
 * Collect links and article elements
 * 
 * */
$(document).ready(function(){
	var links = $('a[href^="http://www.geogebratube.org/student/"]');
	console.log(links.length);
});
