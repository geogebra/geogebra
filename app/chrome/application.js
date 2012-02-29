/*
 * 
 * Collect links and article elements
 * 
 * */
var geogebratubeLinks = [];

$(document).on("DOMSubtreeModified",function() {
	$('iframe').each(function() {
		var as = $(this).contents().find('a[href*="geogebratube.org/student/"]');
		if (as && as.length && as.length > 0) {
			console.log(as.length);
		}
	});
});
