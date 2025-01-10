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
