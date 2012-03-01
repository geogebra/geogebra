/*
 * 
 * Decorate correct links
 * 
 * */
//ugly hack, but css not inserted somehow...

var geogebraLinkStyles = {
		"border":"1px solid #9999ff",
		"border-radius":"3px",
		"background":"white url("+ chrome.extension.getURL("icon_19.png")+") no-repeat right 2px",
		"display":"inline-block",
		"padding":" 2px 30px 2px 5px",
		"color":"#666",
		"overflow":"auto"
}

var geogebraPopUpStyles = {
		"border-radius":"3px",
		"background":"white",
		"display":"none",
		"color":"#666",
		"display":"none"
}

var geogebraWebLoading = {
		"background":"white url("+ chrome.extension.getURL("spinner.gif")+") no-repeat right 2px",	
}

var geogebraPopUp = $('<p class="geogebrapopup">Click to <a title="click to render the construction here" href="#">Render the construction here</a>, or click to the link above to go to GeoGebraTube</p>')
					.css(geogebraPopUpStyles)
					.find("a")
					.click(grabArticleElement).end();

function grabArticleElement() {
	var parentLink = $(this).parents("a");
	var article;
	if (!parentLink.hasClass("geogebraweb_loading")) {
		parentLink.addClass("geogebraweb_loading")
		parentLink.css(geogebraWebLoading);
		var href = parentLink.attr("href")+"?mobile=true";
		$.get(href,function(data) {
			article = $(data.substring(data.indexOf("<article"),data.indexOf("</article>")+10));
			parentLink.find(".geogebrapopup").remove();
			parentLink.append(article)
		});
	}
}

function handleMouseOver() {
	$(this).css({
		"color" : "black",
		"text-decoration" : "none"
	});
	$(this).find("p").show("fast");
	
}

function handleMouseOut() {
	$(this).css({
		"color" : "#666",
		"text-decoration" : "underline"
	});
	$(this).find('p').hide("fast");
}


function decorateLinks(links) {
	var link;
	for (var i = 0, l = links.length; i < l; i++) {
		link = $(links.get(i));
		if (!link.hasClass("geogebraweblink")) {
			link.addClass("geogebraweblink").css(geogebraLinkStyles)
			.on("mouseenter",handleMouseOver).on("mouseleave",handleMouseOut);
			link.append(geogebraPopUp.clone(true));
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
