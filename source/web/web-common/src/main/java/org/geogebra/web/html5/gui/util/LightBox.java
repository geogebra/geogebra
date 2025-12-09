/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
