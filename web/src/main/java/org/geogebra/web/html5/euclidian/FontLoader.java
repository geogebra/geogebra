package org.geogebra.web.html5.euclidian;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.web.html5.gui.laf.FontFamily;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;

import jsinterop.annotations.JsFunction;

public final class FontLoader {
	private static Map<String, FontState> injected = new HashMap<>();
	private static FontFamily[] bundled = new FontFamily[]{FontFamily.DYSLEXIC,
			FontFamily.QUICKSAND, FontFamily.SOURCE_SANS_PRO, FontFamily.TITILLIUM};

	private enum FontState { LOADING, ACTIVE }

	private FontLoader() {
		// utility class: font shared for all app instances
	}

	@JsFunction
	public interface FontLoadCallback {
		void fontLoadeded(String familyName, String variation);
	}

	/**
	 * @param familyName  font name
	 * @param callback kernel to be notified on font load
	 */
	public static void loadFont(String familyName, final Runnable callback) {
		for (FontFamily family: bundled) {
			if (family.cssName().equals(familyName)) {
				loadFontFile(familyName.split(",")[0], callback);
				return;
			}
		}
	}

	private static void loadFontFile(String familyName, final Runnable callback) {
		if (!injected.containsKey(familyName)) {
			String fileName = GWT.getModuleBaseURL() + "webfont/" + familyName;
			String css = "@font-face {  font-family: \"" + familyName + "\";"
					+ "src: url(\"" + fileName + ".woff2\") format(\"woff2\");"
					+ "font-weight: normal; font-style: normal;}";
			StyleInjector.inject(css, true);
			injected.put(familyName, FontState.LOADING);
		}
		if (injected.get(familyName) != FontState.ACTIVE) {
			loadWebFont(familyName, new FontLoadCallback() {
				@Override
				public void fontLoadeded(String activeFontName, String variation) {
					injected.put(activeFontName, FontState.ACTIVE);
					callback.run();
				}
			});
		}
	}

	private static native void loadWebFont(String family, FontLoadCallback callback) /*-{
		var toLoad = {
			fontactive: callback,
			custom : {
				families : [ family ]
			}
		};
		$wnd.WebFont && $wnd.WebFont.load(toLoad);
	}-*/;
}
