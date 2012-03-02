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

window.GGW_ext = {};

var GEOGEBRAWEB_SCRIPT_SRC = chrome.extension.getURL("web/web.nocache.js");

function renderGeoGebraWeb() {
	if (window.GGW_ext && typeof window.GGW_ext.render !== "function") {

		    var nav = navigator.userAgent.toLowerCase();
		    var script0 = document.createElement("script");
		    script0.type = "text/javascript";
		    script0.src = GEOGEBRAWEB_SCRIPT_SRC;
		    script0.onload = function(e) {
		        /*var script = document.createElement("script");
		        script.type = "text/javascript";
		        if (nav.indexOf("webkit") > -1) {
		            //chrome
		            script.src = "http://www.geogebra.org/mobile/4.0/geogebramobile/1F6D62FB75345A82BBCEFC2574374C09.cache.js";
		        } else if (nav.indexOf("gecko") > -1) {
		            //firefox
		            script.src = "http://www.geogebra.org/mobile/4.0/geogebramobile/BA3A0EA56212BEC7DEB8430132183510.cache.js";
		        } 
		        existingscripts[0].parentNode.appendChild(script);*/
		    	console.log("script loaded");
		    };
		    var existingscripts = document.getElementsByTagName("script");
		    existingscripts[0].parentNode.appendChild(script0);

	}
	
	
}

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
			renderGeoGebraWeb(article);
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

$(document).ready(function() {
	renderGeoGebraWeb();
});
