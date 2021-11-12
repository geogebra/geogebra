package org.geogebra.web.html5.gui.util;

import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;

public class LightBox {

	/**
	 * @param dataUrl data URL of image or PDF
	 * @param pdf whether it's PDF
	 */
	public static void showImage(String dataUrl, boolean pdf) {
		Element oldDiv = DomGlobal.document.getElementById("myLightboxDiv");
		if (Js.isTruthy(oldDiv)) {
			oldDiv.remove();
		}

		HTMLElement div = createElement("div");
		div.id = "myLightboxDiv";
		div.classList.add("ggbLightBox");

		if (!pdf) {
			div.style.backgroundImage = "url('" + dataUrl + "')";
			div.style.backgroundSize =  "contain";
			div.style.backgroundRepeat = "no-repeat";
			div.style.backgroundPosition = "center";
		}

		div.addEventListener("click", e -> div.remove());

		DomGlobal.document.body.appendChild(div);
		HTMLElement elem;

		if (pdf) {
			elem = createElement("iframe");
			elem.style.position = "relative";
		} else {
			// now add transparent image over it
			// so that "Save image as..." works
			elem = createElement("img");
			elem.style.opacity = CSSProperties.OpacityUnionType.of(0);
		}
		elem.setAttribute("src", dataUrl);
		elem.style.height = CSSProperties.HeightUnionType.of("100%");
		elem.style.width = CSSProperties.WidthUnionType.of("100%");
		div.appendChild(elem);
	}

	private static HTMLElement createElement(String div) {
		return Js.uncheckedCast(DomGlobal.document.createElement(div));
	}
}
