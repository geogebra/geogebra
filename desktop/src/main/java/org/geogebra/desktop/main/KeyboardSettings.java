package org.geogebra.desktop.main;

import java.util.ArrayList;
import java.util.LinkedList;

import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.lang.Language;

/**
 * Settings for Virtual Keyboard
 * 
 * @author Zbynek Konecny
 *
 */
public class KeyboardSettings extends AbstractSettings {

	/**
	 * List of supported locales as strings, e.g. hu, en_GB
	 */
	private static final ArrayList<String> supportedLocales = new ArrayList<>();

	static {
		supportedLocales.add(Language.Arabic.language); // Arabic

		// same keyboard layout (TODO: maybe combine)
		supportedLocales.add(Language.Croatian.language); // Croatian
		supportedLocales.add(Language.Serbian.language); // Serbian
		supportedLocales.add(Language.Slovenian.language); // Slovenian

		supportedLocales.add(Language.Czech.language); // Czech
		supportedLocales.add(Language.Danish.language); // Danish
		supportedLocales.add("enGB"); // English (UK)

		supportedLocales.add(Language.French.language); // French
		supportedLocales.add(Language.German.language); // German
		supportedLocales.add(Language.Greek.language); // Greek
		supportedLocales.add(Language.Finnish.language); // Finnish
		supportedLocales.add(Language.Hebrew.language); // Hebrew
		supportedLocales.add(Language.Hindi.language); // Hindi
		supportedLocales.add(Language.Hungarian.language); // Hungarian
		supportedLocales.add(Language.Korean.language); // Korean
		supportedLocales.add(Language.Macedonian.language); // Macedonian
		// supportedLocales.add(Language.Malayalam.language); // Malayalam
		supportedLocales.add("no"); // Norwegian (covers both)
		supportedLocales.add(Language.Persian.language); // Persian
		// supportedLocales.add("pt_PT"); // Portuguese (Portugal)
		supportedLocales.add(Language.Russian.language); // Russian
		supportedLocales.add(Language.Slovak.language); // Slovak
		supportedLocales.add(Language.Spanish.language); // Spanish
		supportedLocales.add(Language.Yiddish.language);
	}

	private double keyboardOpacity = 0.7f;
	private int keyboardWidth = 400;
	private int keyboardHeight = 235;
	private String keyboardLocale = null;
	private boolean showKeyboardOnStart = false;

	public KeyboardSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
	}

	public KeyboardSettings() {
		super();
	}

	public double getKeyboardOpacity() {
		return keyboardOpacity;
	}

	public int getKeyboardWidth() {
		return keyboardWidth;
	}

	public int getKeyboardHeight() {
		return keyboardHeight;
	}

	public String getKeyboardLocale() {
		return keyboardLocale;
	}

	/*
	 * public void setKeyboardLocale(String loc) { keyboardLocale = loc;
	 * settingChanged(); }
	 */

	/**
	 * @param windowWidth
	 *            window width
	 */
	public void setKeyboardWidth(int windowWidth) {
		keyboardWidth = windowWidth;
		settingChanged();
	}

	/**
	 * @param windowHeight
	 *            window height
	 */
	public void setKeyboardHeight(int windowHeight) {
		keyboardHeight = windowHeight;
		settingChanged();
	}

	/**
	 * @param showKeyboardOnStart
	 *            the showKeyboardOnStart to set
	 */
	public void setShowKeyboardOnStart(boolean showKeyboardOnStart) {
		this.showKeyboardOnStart = showKeyboardOnStart;
		settingChanged();
	}

	/**
	 * @return the showKeyboardOnStart
	 */
	public boolean isShowKeyboardOnStart() {
		return showKeyboardOnStart;
	}

	/**
	 * @param opacity
	 *            opacity
	 */
	public void setKeyboardOpacity(double opacity) {
		keyboardOpacity = opacity;
		settingChanged();
	}

	/**
	 * @param windowWidth
	 *            width
	 * @param windowHeight
	 *            height
	 */
	public void keyboardResized(int windowWidth, int windowHeight) {
		keyboardWidth = windowWidth;
		keyboardHeight = windowHeight;
	}

	/**
	 * @param string
	 *            keyboard locale
	 */
	public void setKeyboardLocale(String string) {
		if (string == null) {
			return;
		}
		for (int i = 0; i < supportedLocales.size(); i++) {
			if (supportedLocales.get(i).toString().equals(string)) {
				keyboardLocale = supportedLocales.get(i);
				settingChanged();
				return;
			}
		}
		Log.debug("Unsupported keyboard locale: " + string);
	}

	public void setKeyboardLocale(int index) {
		keyboardLocale = index <=0 ? null : supportedLocales.get(index - 1);
		settingChanged();
	}

	public static int getLocaleCount() {
		return supportedLocales.size();
	}

	public static String getLocale(int i) {
		return supportedLocales.get(i);
	}

	public static int indexOfLocale(String loc) {
		return supportedLocales.indexOf(loc);
	}

}
