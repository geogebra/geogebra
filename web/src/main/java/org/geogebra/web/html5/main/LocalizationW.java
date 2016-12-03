package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.StyleInjector;
import org.geogebra.web.html5.util.MyDictionary;

import com.google.gwt.user.client.ui.RootPanel;

/**
 * JSON based localization for Web
 *
 */
public final class LocalizationW extends Localization {
	/**
	 * @param dimension
	 *            3 for 3D
	 */
	public LocalizationW(int dimension) {
		super(dimension, 13);
	}

	/**
	 * eg "en_GB", "es" // remains null until we're sure keys are loaded
	 */
	String localeStr = "en";

	private boolean commandChanged = true;
	/**
	 * Constants related to internationalization
	 * 
	 */
	public final static String DEFAULT_LANGUAGE = "en";

	//
	/*
	 * eg __GGB__keysVar.en.command.Ellipse
	 */
	/**
	 * 
	 * @param language
	 *            language
	 * @param key
	 *            key
	 * @param section
	 *            properties section (menu /error/...)
	 * @return translation or English if translation not found; fallback is
	 *         empty string
	 */
	public native String getPropertyNative(String language, String key,
	        String section) /*-{

		if (!$wnd["__GGB__keysVar"]) {
			return "";
		}

		if ($wnd["__GGB__keysVar"][language]) {
			// translated
			return $wnd["__GGB__keysVar"][language][section][key];
		} else if ($wnd["__GGB__keysVar"]["en"]) { // English (probably available)
			return $wnd["__GGB__keysVar"]["en"][section][key];
		} else {
			return "";
		}

	}-*/;

	@Override
	public String getCommand(String key) {

		if (key == null) {
			return "";
		}

		if (localeStr == null) {
			// keys not loaded yet
			return key;
		}

		String ret = getPropertyNative(localeStr, key, "command");

		if (ret == null || "".equals(ret)) {
			Log.debug("command key not found: " + key);
			return key;
		}

		return ret;

	}

	/**
	 * @author Rana This method should work for both menu and menu tooltips
	 *         items
	 */
	@Override
	public String getMenu(String key) {

		if (key == null) {
			return "";
		}

		if (localeStr == null) {
			// keys not loaded yet
			return key;
		}

		String ret = getPropertyNative(localeStr, key, "menu");

		if (ret == null || "".equals(ret)) {
			// Log.debug("menu key not found: "+key);
			return key;
		}

		return ret;

	}

	@Override
	public String getError(String key) {

		if (key == null) {
			return "";
		}

		if (localeStr == null) {
			// keys not loaded yet
			return key;
		}

		String ret = getPropertyNative(localeStr, key, "error");

		if (ret == null || "".equals(ret)) {
			Log.debug("error key not found: " + key);
			return key;
		}

		return ret;
	}

	@Override
	final public String getSymbol(int key) {

		if (localeStr == null) {
			// keys not loaded yet
			return null;
		}

		String ret = getPropertyNative(localeStr, "S_" + key, "symbols");

		if (ret == null || "".equals(ret)) {
			Log.debug("menu key not found: " + key);
			return null;
		}

		return ret;
	}

	@Override
	final public String getSymbolTooltip(int key) {

		if (localeStr == null) {
			// keys not loaded yet
			return null;
		}

		String ret = getPropertyNative(localeStr, "T_" + key, "symbols");

		if (ret == null || "".equals(ret)) {
			Log.debug("menu key not found: " + key);
			return null;
		}

		return ret;
	}

	@Override
	public void setTooltipFlag() {
		// secondary languages not supported in HTML5
	}

	@Override
	public String reverseGetColor(String locColor) {
		String str = StringUtil.removeSpaces(StringUtil.toLowerCase(locColor));

		try {

			// Dictionary colorKeysDict =
			// Dictionary.getDictionary("__GGB__colors_"+language);
			MyDictionary colorKeysDict = MyDictionary.getDictionary("colors",
					localeStr);
			Iterator<String> colorKeysIterator = colorKeysDict.keySet()
			        .iterator();
			while (colorKeysIterator != null && colorKeysIterator.hasNext()) {
				String key = colorKeysIterator.next();
				if (key != null
				        && str.equals(StringUtil.removeSpaces(StringUtil
				                .toLowerCase(this.getColor(key))))) {
					return key;
				}
			}

			return str;
		} catch (MissingResourceException e) {
			return str;
		}
	}

	@Override
	public String getColor(String key) {

		if (key == null) {
			return "";
		}

		if ((key.length() == 5)
		        && StringUtil.toLowerCase(key).startsWith("gray")) {

			return StringUtil.getGrayString(key.charAt(4), this);

		}

		String ret = getPropertyNative(localeStr, key, "color");

		if (ret == null || "".equals(ret)) {
			Log.debug("error key not found: " + key);
			return key;
		}

		return ret;

	}

	/**
	 * Following Java's convention, the return string should only include the
	 * language part of the locale. The assumption here that the "default"
	 * locale is English.
	 */
	@Override
	public String getLanguage() {
		return localeStr == null ? null : localeStr.substring(0, 2);
	}

	@Override
	protected boolean isCommandChanged() {
		return commandChanged;
	}

	@Override
	protected void setCommandChanged(boolean b) {
		commandChanged = b;
	}

	@Override
	protected boolean isCommandNull() {
		return false;
	}

	@Override
	public void initCommand() {
		//
	}

	/**
	 * @param lang
	 *            preferred language
	 */
	public void setLanguage(String lang) {
		if ("".equals(lang)) {
			localeStr = "en";
		} else {
			localeStr = lang;
		}

		setCommandChanged(true);

		Log.debug("keys loaded for language: " + lang);
		Log.debug("TODO: reinitialize GUI on language change");

		updateLanguageFlags(lang);

		// For styling on Firefox. (Mainly for rtl-languages.)
		if (rightToLeftReadingOrder) {
			RootPanel.getBodyElement().setAttribute("dir", "rtl");
		} else {
			RootPanel.getBodyElement().setAttribute("dir", "ltr");
		}
		StyleInjector
				.inject(rightToLeftReadingOrder ? GuiResourcesSimple.INSTANCE
						.generalStyleRTL() : GuiResourcesSimple.INSTANCE
						.generalStyleLTR());
		StyleInjector
				.inject(rightToLeftReadingOrder ? GuiResourcesSimple.INSTANCE
						.avStyleRTL() : GuiResourcesSimple.INSTANCE
						.avStyleLTR());
	}

	@Override
	public String getLocaleStr() {
		return localeStr;
	}

	/**
	 * @param lang
	 *            language (assuming it is supported)
	 * @param version
	 *            app version
	 * @return true when available
	 */
	static native boolean loadPropertiesFromStorage(String lang,
			String version) /*-{
		var storedTranslation = {};
		if ($wnd.localStorage && $wnd.localStorage.translation) {
			try {
				storedTranslation = JSON.parse(localStorage.translation);
				if (version.length > 0 && storedTranslation
						&& storedTranslation["version"] != version) {
					storedTranslation = {};
				}
			} catch (e) {
				$wnd.console && $wnd.console.log(e.message);
			}
		}
		if (storedTranslation && storedTranslation[lang]) {
			$wnd["__GGB__keysVar"] = {};
			$wnd["__GGB__keysVar"][lang] = storedTranslation[lang];
			return true;
		}
		return false;
	}-*/;

	/**
	 * Saves properties loaded from external JSON to localStorage
	 * 
	 * @param lang
	 *            language
	 * @param version
	 *            app version
	 */
	static native void savePropertiesToStorage(String lang,
			String version) /*-{
		var storedTranslation = {};
		if ($wnd.localStorage && $wnd["__GGB__keysVar"]
				&& $wnd["__GGB__keysVar"][lang]) {
			var obj = {};
			obj.version = version;
			obj[lang] = $wnd.__GGB__keysVar[lang];
			$wnd.localStorage.translation = JSON.stringify(obj);
		}
	}-*/;

	@Override
	protected ArrayList<Locale> getSupportedLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateResourceBundles() {
		// TODO Auto-generated method stub

	}

	protected String getLanguage(Locale locale) {
		// TODO: implement if LocalizationW uses Locale rather than String
		return "en";
	}

	protected String getCountry(Locale locale) {
		// TODO: implement if LocalizationW uses Locale rather than String
		return "US";
	}

}
