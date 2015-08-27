package org.geogebra.web.keyboard;

import org.geogebra.web.html5.util.DynamicScriptElement;
import org.geogebra.web.html5.util.ScriptLoadCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;

public class KeyboardLocalization {

	/**
	 * loads the javascript file and updates the keys to the given language
	 * 
	 * @param lang
	 *            the language
	 */
	public void loadLang(final String lang, ScriptLoadCallback callback) {
		DynamicScriptElement script = (DynamicScriptElement) Document.get()
				.createScriptElement();
		script.setSrc(GWT.getModuleBaseURL() + "js/keyboard_" + lang + ".js");

		script.addLoadHandler(callback);
		Document.get().getBody().appendChild(script);
	}

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
