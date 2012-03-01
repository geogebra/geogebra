/*
 * 
 * Decorate correct links
 * 
 * */
//ugly hack, but css not inserted somehow...

var geogebraLinkStyles = {
		"border":"1px solid #9999ff",
		"border-radius":"3px",
		"background":"white url("+ chrome.extension.getURL("icon_19.png")+") no-repeat right center",
		"display":"inline-block",
		"padding":" 2px 30px 2px 5px",
		"color":"#666",
		"position":"relative"
}

var geogebraPopUpStyles = {
		"border-radius":"3px",
		"background":"#9999ff",
		"display":"none",
		"padding":" 2px 30px 2px 5px",
		"color":"#666",
		"display":"none",
		"position":"absolute",
		"top":"10px",
		"right":"-4px"
}

var geogebraPopUp = $('<p><a title="click to render the construction here, or click on the main link to go to GeoGebraTube" href="#">Render the construction here</a></p>')
					.css(geogebraPopUpStyles)
					.find("a")
					.click(grabArticleElement).end();

function grabArticleElement() {
	var href = $(this).parent("a").attr("href");
	console.log(href);
}

function handleMouseOver() {
	$(this).css({
		"color" : "black",
		"text-decoration" : "none"
	});
	$(this).find("p").show();
	console.log($(this));
	
}

function handleMouseOut() {
	$(this).css({
		"color" : "#666",
		"text-decoration" : "underline"
	});
	$(this).find('p').hide("fast");
	console.log($(this));
}


function decorateLinks(links) {
	var link;
	for (var i = 0, l = links.length; i < l; i++) {
		link = $(links.get(i));
		if (!link.hasClass("geogebraweblink")) {
			link.addClass("geogebraweblink").css(geogebraLinkStyles)
			.on("mouseover",handleMouseOver).on("mouseout",handleMouseOut);
			link.append(geogebraPopUp.clone(true));
			console.log(link);
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
