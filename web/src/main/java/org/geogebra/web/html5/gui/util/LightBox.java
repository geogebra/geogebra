package org.geogebra.web.html5.gui.util;

public class LightBox {

	public static native boolean showImage(String imageBase64,
			boolean pdf) /*-{

		//closeLightbox();
		var div = $wnd.document.getElementById("myLightboxDiv");
		if (div) {
			$wnd.document.body.removeChild(div);
		}

		div = $wnd.document.createElement("div");
		div.id = "myLightboxDiv";
		div.style.width = "80%";
		div.style.height = "80%";
		div.style.position = "fixed";
		div.style.top = "10%";
		div.style.left = "10%";
		div.style.border = "7px solid rgba(0, 0, 0, 0.5)";
		div.style.background = "#FFF";
		if (!pdf) {
			div.style["background-image"] = "url('" + imageBase64 + "')";
			div.style["background-size"] = "contain";
			div.style["background-repeat"] = "no-repeat";
			div.style["background-position"] = "center";
			div.style.zIndex = 100000000;
		}

		div.onclick = function() {
			$wnd.document.body.removeChild(div);
		};

		$wnd.document.body.appendChild(div);

		// now add transparent image over it
		// so that "Save image as..." works
		// remove this if you don't need it
		var elem;

		if (pdf) {
			elem = $wnd.document.createElement("iframe");
			elem.style.position = "relative";
		} else {
			elem = $wnd.document.createElement("img");
			elem.style.opacity = 0;
		}
		elem.src = imageBase64;
		elem.style.height = "100%";
		elem.style.width = "100%";
		div.appendChild(elem);
	}-*/;
}
