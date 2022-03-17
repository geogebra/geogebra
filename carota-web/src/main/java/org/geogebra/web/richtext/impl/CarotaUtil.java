package org.geogebra.web.richtext.impl;

import org.geogebra.gwtutil.JavaScriptInjector;
import org.murok.editor.MurokResources;

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
		JavaScriptInjector.inject(MurokResources.INSTANCE.murokJs());
		isScriptInjected = true;
		setDefaultFontSize(fontSize);
	}

	/**
	 * @param fontSize default font size shared by all editors
	 */
	public static void setDefaultFontSize(double fontSize) {
		if (Carota.get() != null) {
			Carota.get().getRuns().getDefaultFormatting().setSize(fontSize);
		}
	}

	/**
	 * @param selectionColor preferred selection color (for all editors)
	 */
	public static void setSelectionColor(String selectionColor) {
		if (Carota.get() != null) {
			Carota.get().getText().setSelectionColor(selectionColor);
		}
	}
}
