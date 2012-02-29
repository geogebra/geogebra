/*
 * 
 * Decorate correct links
 * 
 * */
function decorateLinks(links) {
	var link;
	for (var i = 0, l = links.length; i < l; i++) {
		link = $(links.get(i));
		if (!link.hasClass("geogebraweblink")) {
			link.addClass("geogebraweblink");
		}
	}
}

function collectLinks() {
	//see document
	decorateLinks($('a[href*="geogebratube.org/student/"]'));
	//see iframes
	$('iframe').each(function() {
		var as = $(this).contents().find('a[href*="geogebratube.org/student/"]');
		if (as && as.length && as.length > 0) {
			decorateLinks(as);
		}
	});	
}

$(document).on("DOMSubtreeModified",function() {
	collectLinks();
});
