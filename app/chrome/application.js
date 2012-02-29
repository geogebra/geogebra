/*
 * 
 * Decorate correct links
 * 
 * */
//ugly hack, but css not inserted somehow...

var styles = {
		"border":"1px solid #9999ff",
		"border-radius":"3px",
		"background":"white url("+ chrome.extension.getURL("icon_19.png")+") no-repeat right center",
		"display":"inline-block",
		"padding":" 2px 30px 2px 5px",
		"color":"#666"
}

function handleMouseOver() {
	$(this).css({
		"color" : "black",
		"text-decoration" : "none"
	});
}

function handleMouseOut() {
	$(this).css({
		"color" : "#666",
		"text-decoration" : "underline"
	});
}


function decorateLinks(links) {
	var link;
	for (var i = 0, l = links.length; i < l; i++) {
		link = $(links.get(i));
		if (!link.hasClass("geogebraweblink")) {
			link.addClass("geogebraweblink").css(styles)
			.on("mouseover",handleMouseOver).on("mouseout",handleMouseOut);
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
