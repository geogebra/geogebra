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
