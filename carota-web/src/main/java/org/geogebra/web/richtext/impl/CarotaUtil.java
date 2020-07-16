package org.geogebra.web.richtext.impl;

import com.google.gwt.core.client.ScriptInjector;

/**
 * Utility class for the editor.
 */
public class CarotaUtil {

	private static boolean isScriptInjected = false;

	/**
	 * Injects the javascript if necessary.
	 */
	public static void ensureInitialized(double fontSize) {
		if (isScriptInjected) {
			return;
		}
		String javascript = JavascriptBundle.INSTANCE.carotaJs().getText();
		ScriptInjector.fromString(javascript).setWindow(ScriptInjector.TOP_WINDOW).inject();
		isScriptInjected = true;
		setDefaultFontSize(fontSize);
	}

	public static void setDefaultFontSize(double fontSize) {
		if (Carota.get() != null) {
			Carota.get().getRuns().getDefaultFormatting().setSize(fontSize);
		}
	}
}
