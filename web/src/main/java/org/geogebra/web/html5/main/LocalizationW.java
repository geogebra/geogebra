package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.lang.Language;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.MyDictionary;
import org.geogebra.web.html5.util.ScriptLoadCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;

/**
 * JSON based localization for Web
 *
 */
public final class LocalizationW extends Localization {

	/**
	 * Default locale string
	 */
	public final static String DEFAULT_LANGUAGE = "en";

	/**
	 * eg "en_GB", "es" // remains null until we're sure keys are loaded
	 */
	private String localeStr = DEFAULT_LANGUAGE;
	// must be updated whenever localeStr changes
	// (cached for speed)
	private Language lang = Language.English_US;
	private String langGWT = lang.getLocaleGWT();

	private ScriptLoadCallback scriptCallback;

	private boolean commandChanged = true;

	private ArrayList<SetLabels> setLabelsListeners;

	/**
	 * @param config
	 * 			app config
	 * @param dimension
	 *           3 for 3D
	 */
	public LocalizationW(AppConfig config, int dimension) {
		super(dimension, 13);
		getCommandSyntax().setSyntaxFilter(config.newCommandSyntaxFilter());
	}

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

		return getPropertyWithFallback(getCommandLocaleString(), key, key, "command");
	}

	private String getPropertyWithFallback(String lang, String key,
			String fallback, String category) {
		String ret = getPropertyNative(lang, key, category);
		if (ret == null || "".equals(ret)) {
			if (GWT.isScript()) { // no error message in test
				Log.debug(category + " key not found: " + key);
			}
			return fallback;
		}

		return ret;
	}

	@Override
	public String getEnglishCommand(String key) {
		if (key == null) {
			return "";
		}
		return getPropertyWithFallback("en", key, key, "command");
	}

	// TODO: implement getCommandLocale()
	private String getCommandLocaleString() {
		if (!lang.hasTranslatedKeyboard()) {
			// TODO: implement if LocalizationW uses Locale rather than String
			return "en";
		}
		return langGWT;
	}

	/**
	 * @author Rana This method should work for both menu and menu tooltips
	 *         items
	 */
	@Override
	public String getMenu(String key) {
		if ("undefined".equalsIgnoreCase(key)) {
			Log.error("undefined");
		}
		if (key == null) {
			return "";
		}

		String ret = getPropertyNative(localeStr, key, "menu");

		// eg webSimple
		if (ret == null || "".equals(ret)) {
			// Log.debug("menu key not found: "+key);

			// eg Symbol.And
			if (key.startsWith(SYMBOL_PREFIX)) {
				return key.substring(SYMBOL_PREFIX.length());
			}

			// eg Function.sin
			if (key.startsWith(FUNCTION_PREFIX)) {
				return key.substring(FUNCTION_PREFIX.length());
			}
			return key;
		}

		return ret;

	}

	@Override
	public String getError(String key) {
		if (key == null) {
			return "";
		}

		return getPropertyWithFallback(localeStr, key, key, "error");
	}

	@Override
	public String getSymbol(int key) {
		return getPropertyWithFallback(localeStr, "S_" + key, null, "symbols");
	}

	@Override
	public String getSymbolTooltip(int key) {
		return getPropertyWithFallback(localeStr, "T_" + key, null, "symbols");
	}

	@Override
	public String reverseGetColor(String locColor) {
		String str = StringUtil.removeSpaces(StringUtil.toLowerCaseUS(locColor));

		try {
			MyDictionary colorKeysDict = MyDictionary
					.getDictionary("colors", localeStr);
			for (String key : colorKeysDict.keySet()) {
				if (key != null
						&& str.equals(StringUtil.removeSpaces(StringUtil
						.toLowerCaseUS(this.getColor(key))))) {
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
		        && StringUtil.toLowerCaseUS(key).startsWith("gray")) {

			return StringUtil.getGrayString(key.charAt(4), this);
		}

		return getPropertyWithFallback(localeStr, key, key, "colors");
	}

	/**
	 * Following Java's convention, the return string should only include the
	 * language part of the locale. The assumption here that the "default"
	 * locale is English.
	 */
	@Override
	public String getLanguage() {
		return localeStr.substring(0, 2);
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
	 * @param lang0
	 *            preferred language
	 */
	public void setLanguage(String lang0) {
		if ("".equals(lang0)) {
			localeStr = "en";
		} else {
			localeStr = lang0;
		}

		// these must be updated whenever localeStr changes
		lang = Language.getClosestGWTSupportedLanguage(localeStr);
		langGWT = lang.getLocaleGWT();

		setCommandChanged(true);

		Log.debug("keys loaded for language: " + lang0);

		updateLanguageFlags(lang0);

		// For styling on Firefox. (Mainly for rtl-languages.)
		// TODO set RTL to the correct element when ready
		// if (rightToLeftReadingOrder) {
		// RootPanel.getBodyElement().setAttribute("dir", "rtl");
		// } else {
		// RootPanel.getBodyElement().setAttribute("dir", "ltr");
		// }

		saveLanguageToSettings(lang0);
	}

	@Override
	public String getLocaleStr() {
		return localeStr;
	}

	/**
	 * @param lang0
	 *            language (assuming it is supported)
	 * @param version
	 *            app version
	 * @return true when available
	 */
	static native boolean loadPropertiesFromStorage(String lang0,
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
		if (storedTranslation && storedTranslation[lang0]) {
			$wnd["__GGB__keysVar"] = {};
			$wnd["__GGB__keysVar"][lang0] = storedTranslation[lang0];
			return true;
		}
		return false;
	}-*/;

	/**
	 * Saves properties loaded from external JSON to localStorage
	 *
	 * @param lang0
	 *            language
	 * @param version
	 *            app version
	 */
	static native void savePropertiesToStorage(String lang0,
			String version) /*-{
		var storedTranslation = {};
		if ($wnd.localStorage && $wnd["__GGB__keysVar"]
				&& $wnd["__GGB__keysVar"][lang0]) {
			var obj = {};
			obj.version = version;
			obj[lang0] = $wnd.__GGB__keysVar[lang0];
			$wnd.localStorage.translation = JSON.stringify(obj);
		}
	}-*/;

	@Override
	protected ArrayList<Locale> getSupportedLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Cancel script load callback.
	 */
	public void cancelCallback() {
		if (scriptCallback != null) {
			scriptCallback.cancel();
		}
	}

	/**
	 * @param lang0
	 *            language
	 * @param app
	 *            callback
	 */
	public void loadScript(final String lang0, final HasLanguage app) {
		if (Browser.supportsSessionStorage()
				&& LocalizationW.loadPropertiesFromStorage(lang0,
						GeoGebraConstants.VERSION_STRING)) {
			app.doSetLanguage(lang0, false);
		} else {
			// load keys (into a JavaScript <script> tag)
			ScriptElement script = Document.get().createScriptElement();
			String url = GWT.getModuleBaseURL();
			if (url.startsWith(GeoGebraConstants.CDN_APPS + "latest")) {
				url = GeoGebraConstants.CDN_APPS
						+ GeoGebraConstants.VERSION_STRING + "/web3d/";
			}
			script.setSrc(url + "js/properties_keys_" + lang0 + ".js");
			scriptCallback = new ScriptLoadCallback() {
				private boolean canceled = false;

				@Override
				public void onLoad() {
					if (canceled) {
						Log.debug("Async language file load canceled.");
						return;
					}
					// force reload
					app.doSetLanguage(lang0, true);

					if (Browser.supportsSessionStorage()) {
						LocalizationW.savePropertiesToStorage(lang0,
								GeoGebraConstants.VERSION_STRING);
					}
				}

				@Override
				public void onError() {
					if (canceled) {
						Log.debug("Async language file load canceled.");
						return;
					}
					LocalizationW.loadPropertiesFromStorage(lang0, "");
					app.doSetLanguage(lang0, false);
				}

				@Override
				public void cancel() {
					canceled = true;
				}

			};
			ResourcesInjector.loadJS(script, scriptCallback);
		}

	}

	private native void saveLanguageToSettings(String lang0) /*-{
		if ($wnd.android && $wnd.android.savePreference) {
			$wnd.android.savePreference("language", lang0);
		}
	}-*/;

	/**
	 * @param localizedUI
	 *            localized UI element
	 */
	public void registerLocalizedUI(SetLabels localizedUI) {
		if (setLabelsListeners == null) {
			setLabelsListeners = new ArrayList<>();
		}
		setLabelsListeners.add(localizedUI);
	}

	/**
	 * Call setLabels() on all registered UI elements
	 */
	public void notifySetLabels() {
		if (setLabelsListeners != null) {
			for (SetLabels ui : setLabelsListeners) {
				ui.setLabels();
			}
		}
	}
}
