package org.geogebra.web.richtext.impl;

import com.google.gwt.core.client.ScriptInjector;

/**
 * Utility class for the editor.
 */
class CarotaUtil {

	private static boolean isScriptInjected = false;

	/**
	 * Injects the javascript if necessary.
	 */
	static void ensureJavascriptInjected() {
		if (isScriptInjected) {
			return;
		}
		String javascript = JavascriptBundle.INSTANCE.carotaJs().getText();
		ScriptInjector.fromString(javascript).setWindow(ScriptInjector.TOP_WINDOW).inject();
		isScriptInjected = true;
	}
}
