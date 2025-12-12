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

package org.geogebra.web.html5.euclidian;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.web.html5.gui.laf.FontFamily;
import org.geogebra.web.html5.util.WebFont;
import org.gwtproject.dom.client.StyleInjector;

import elemental2.core.JsArray;
import jsinterop.annotations.JsFunction;
import jsinterop.base.JsPropertyMap;

public final class FontLoader {
	private static Map<String, FontState> injected = new HashMap<>();
	private static FontFamily[] bundled = new FontFamily[]{FontFamily.DYSLEXIC,
			FontFamily.QUICKSAND, FontFamily.SOURCE_SANS_PRO, FontFamily.TITILLIUM,
			FontFamily.ABeZehBlueRedEDUBold, FontFamily.ABeZehBlueRedEDULight,
			FontFamily.ABeZehBlueRedEDURegular, FontFamily.ABeZehEDUBold,
			FontFamily.ABeZehEDUBoldItalic, FontFamily.ABeZehEDUItalic,
			FontFamily.ABeZehEDULight, FontFamily.ABeZehEDULightItalic,
			FontFamily.ABeZehEDURegular, FontFamily.ABeZehHokuspokusEDUDEBold,
			FontFamily.ABeZehHokuspokusEDUDERegular, FontFamily.ABeZehHokuspokusEDUENBold,
			FontFamily.ABeZehHokuspokusEDUENRegular, FontFamily.ABeZehIconsEDUDeutsch,
			FontFamily.ABeZehIconsEDUEnglish, FontFamily.ABeZehIconsEDUFrancais,
			FontFamily.ABeZehLinieEDULight, FontFamily.ABeZehLinieEDURegular,
			FontFamily.ABeZehPfeilEDULight, FontFamily.ABeZehPfeilEDURegular,
			FontFamily.ABeZehPfeilEDULINKSLight, FontFamily.ABeZehPunktEDULight,
			FontFamily.ABeZehPunktEDURegular, FontFamily.TEST, FontFamily.TEST2};

	private enum FontState { LOADING, ACTIVE }

	private FontLoader() {
		// utility class: font shared for all app instances
	}

	@JsFunction
	interface FontLoadCallback {
		void fontLoaded(String familyName, String variation);
	}

	/**
	 * @param familyName font name
	 * @param baseUrl Url from where to load the font from
	 * @param callback kernel to be notified on font load
	 */
	public static void loadFont(String familyName, String baseUrl, final Runnable callback) {
		if (baseUrl.isEmpty()) {
			return;
		}
		for (FontFamily family: bundled) {
			if (family.cssName().equals(familyName)) {
				loadFontFile(familyName.split(",")[0], baseUrl, callback);
				return;
			}
		}
	}

	private static void loadFontFile(String familyName, String baseUrl, final Runnable callback) {
		if (!injected.containsKey(familyName)) {
			String fileName = baseUrl + familyName;
			String css = "@font-face {  font-family: \"" + familyName + "\";"
					+ "src: url(\"" + fileName + ".woff2\") format(\"woff2\");"
					+ "font-weight: normal; font-style: normal;}";
			StyleInjector.inject(css, true);
			injected.put(familyName, FontState.LOADING);
		}
		if (injected.get(familyName) != FontState.ACTIVE) {
			loadWebFont(familyName, (activeFontName, variation) -> {
				injected.put(activeFontName, FontState.ACTIVE);
				callback.run();
			});
		}
	}

	private static void loadWebFont(String family, FontLoadCallback callback) {
		JsPropertyMap<?> toLoad = JsPropertyMap.of(
			"fontactive", callback,
			"custom", JsPropertyMap.of(
				"families", JsArray.of(family)
			)
		);
		if (WebFont.get() != null) {
			WebFont.get().load(toLoad);
		}
	}
}
