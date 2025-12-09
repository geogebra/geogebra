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

package org.geogebra.web.full.gui.util;

import elemental2.core.JsDate;
import jsinterop.base.JsPropertyMap;

public class DateTimeFormat {

	/**
	 * Prints date in german locale
	 * @param jsDate date
	 * @return formatted date
	 */
	public static String format(JsDate jsDate) {
		// dd.MM.yyyy HH:mm
		JsPropertyMap<?> timeOptions = JsPropertyMap.of("timeStyle", "short",
				"hour12", false);
		return jsDate.toLocaleDateString("de-DE")
				+ " " + jsDate.toLocaleTimeString("de-DE", timeOptions);
	}

	/**
	 * Format date only, using medium verbosity
	 * @param date date
	 * @param languageTag locale's BCP47 language tag
	 * @return formatted date
	 */
	public static String formatDate(JsDate date, String languageTag) {
		JsPropertyMap<?> timeOptions = JsPropertyMap.of("dateStyle", "medium");
		return date.toLocaleDateString(languageTag, timeOptions);
	}
}
