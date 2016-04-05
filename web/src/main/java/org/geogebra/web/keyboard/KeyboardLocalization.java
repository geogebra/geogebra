package org.geogebra.web.keyboard;

public class KeyboardLocalization {

	/**
	 * get translations for the onScreenKeyboard-buttons
	 * 
	 * @param key
	 *            String to translate
	 * @param section
	 *            "lowerCase" or "shiftDown"
	 * @param lang
	 *            language
	 * @return String for keyboardButton
	 */
	public String getKey(String key, String section, String lang) {
		return getKeyboardNative(lang, key, section);
	}

	private native String getKeyboardNative(String lang, String key,
			String section) /*-{

		if (!$wnd["__GGB__keyboard"]) {
			return "";
		}

		if ($wnd["__GGB__keyboard"][lang]) {
			// translated
			return $wnd["__GGB__keyboard"][lang][section][key];
		} else if ($wnd["__GGB__keyboard"]["en"]) { // English (probably available)
			return $wnd["__GGB__keyboard"]["en"][section][key];
		} else {
			return "";
		}

	}-*/;

}
