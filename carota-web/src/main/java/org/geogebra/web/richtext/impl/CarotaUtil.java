package org.geogebra.web.richtext.impl;

import com.google.gwt.core.client.ScriptInjector;

/**
 * Utility class for the editor.
 */
public class CarotaUtil {

	private static boolean isScriptInjected = false;

	private static final String mebisSelectionColor = "rgba(102, 87, 210, 0.1)";

	/**
	 * Injects the javascript if necessary.
	 */
	public static void ensureInitialized(double fontSize, boolean isMebis) {
		if (isScriptInjected) {
			return;
		}
		String javascript = JavascriptBundle.INSTANCE.carotaJs().getText();
		ScriptInjector.fromString(javascript).setWindow(ScriptInjector.TOP_WINDOW).inject();
		isScriptInjected = true;
		setDefaultFontSize(fontSize);
		if (isMebis && Carota.get() != null) {
			Carota.get().getText().setSelectionColor(mebisSelectionColor);
		}
	}

	public static void setDefaultFontSize(double fontSize) {
		if (Carota.get() != null) {
			Carota.get().getRuns().getDefaultFormatting().setSize(fontSize);
		}
	}
}
