package org.geogebra.web.html5.main;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.lang.Language;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.gwtutil.ScriptLoadCallback;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.bridge.GeoGebraJSNativeBridge;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.resources.StyleInjector;

import com.google.gwt.core.client.GWT;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsObject;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * JSON based localization for Web
 *
 */
public final class LocalizationW extends Localization {

	/**
	 * Default locale string
	 */
	public final static String DEFAULT_LANGUAGE = "en";

	// must be updated whenever localeStr changes
	// (cached for speed)
	private Language lang = Language.English_US;
	private String languageTag = lang.toLanguageTag();
	private String preferredTag = languageTag;

	private ScriptLoadCallback scriptCallback;

	private boolean commandChanged = true;

	private ArrayList<SetLabels> setLabelsListeners;

	/**
	 * @param config app config
	 * @param dimension 3 for 3D
	 */
	public LocalizationW(AppConfig config, int dimension) {
		super(dimension, 13);
		SyntaxFilter syntaxFilter = config.newCommandSyntaxFilter();
		if (syntaxFilter != null) {
			getCommandSyntax().addSyntaxFilter(syntaxFilter);
		}
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
	public String getPropertyNative(String language, String key,
	        String section) {
		// null check needed for tests
		if (Js.isFalsy(GeoGebraGlobal.__GGB__keysVar)
			|| GeoGebraGlobal.__GGB__keysVar == null) {
			return "";
		}

		if (Js.isTruthy(GeoGebraGlobal.__GGB__keysVar.get(language))) {
			// translated
			return GeoGebraGlobal.__GGB__keysVar.get(language).get(section).get(key);
		} else if (Js.isTruthy(GeoGebraGlobal.__GGB__keysVar.get("en"))) {
			// translated
			return GeoGebraGlobal.__GGB__keysVar.get("en").get(section).get(key);
		} else {
			return "";
		}
	}

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
		if (StringUtil.empty(ret)) {
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

	private String getCommandLocaleString() {
		if (!lang.hasTranslatedKeyboard()) {
			return "en";
		}
		return languageTag;
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

		String ret = getPropertyNative(languageTag, key, "menu");

		// eg webSimple
		if (ret == null || "".equals(ret)) {
			// Log.debug("menu key not found: "+key);

			// eg Symbol.And
			if (key.startsWith(Localization.SYMBOL_PREFIX)) {
				return key.substring(Localization.SYMBOL_PREFIX.length());
			}

			// eg Function.sin
			if (key.startsWith(Localization.FUNCTION_PREFIX)) {
				return key.substring(Localization.FUNCTION_PREFIX.length());
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

		return getPropertyWithFallback(languageTag, key, key, "error");
	}

	@Override
	public String getSymbol(int key) {
		return getPropertyWithFallback(languageTag, "S_" + key, null, "symbols");
	}

	@Override
	public String getSymbolTooltip(int key) {
		return getPropertyWithFallback(languageTag, "T_" + key, null, "symbols");
	}

	@Override
	public String reverseGetColor(String locColor) {
		String str = StringUtil.removeSpaces(StringUtil.toLowerCaseUS(locColor));
		JsPropertyMap<JsPropertyMap<String>> dict = GeoGebraGlobal.__GGB__keysVar.get(languageTag);
		if (dict == null || !dict.has("colors")) {
			return str;
		}
		JsArray<String> keys = JsObject.keys(dict.get("colors"));
		for (int i = 0; i < keys.length; i++) {
			String key = keys.getAt(i);
			if (key != null
					&& str.equals(StringUtil.removeSpaces(StringUtil
					.toLowerCaseUS(this.getColor(key))))) {
				return key;
			}
		}
		return str;
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

		return getPropertyWithFallback(languageTag, key, key, "colors");
	}

	/**
	 * Following Java's convention, the return string should only include the
	 * language part of the locale. The assumption here that the "default"
	 * locale is English.
	 */
	@Override
	public Language getLanguage() {
		return lang;
	}

	@Override
	public Language getLanguageEnum() {
		return lang;
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
		// these must be updated whenever language changes
		lang = StringUtil.empty(lang0) ? Language.English_US
				: Language.fromLanguageTagOrLocaleString(lang0);
		preferredTag = languageTag = lang.toLanguageTag();

		setCommandChanged(true);

		Log.debug("keys loaded for language: " + lang0);

		updateLanguageFlags(lang.language);

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
	public String getLanguageTag() {
		return lang.toLanguageTag();
	}

	@Override
	public String getPreferredLanguageTag() {
		return preferredTag;
	}

	/**
	 * @param lang0
	 *            language (assuming it is supported)
	 * @param version
	 *            app version
	 * @return true when available
	 */
	static boolean loadPropertiesFromStorage(String lang0,
			String version) {
		String translationJson = BrowserStorage.LOCAL.getItem("translation");
		if (Js.isTruthy(translationJson)) {
			try {
				JsPropertyMap<Object>
						storedTranslation = Js.uncheckedCast(Global.JSON.parse(translationJson));
				if (version.length() > 0 && Js.isTruthy(storedTranslation)
						&& !version.equals(storedTranslation.get("version"))) {
					storedTranslation = null;
				}
				if (storedTranslation != null
						&& Js.isTruthy(storedTranslation.get(lang0))) {
					GeoGebraGlobal.__GGB__keysVar = JsPropertyMap.of();
					GeoGebraGlobal.__GGB__keysVar.set(lang0,
							Js.uncheckedCast(storedTranslation.get(lang0)));
					return true;
				}
			} catch (Throwable e) {
				Log.debug(e);
			}
		}
		return false;
	}

	/**
	 * Saves properties loaded from external JSON to localStorage
	 *  @param lang0
	 *            language
	 */
	static void savePropertiesToStorage(String lang0) {
		if (Js.isTruthy(GeoGebraGlobal.__GGB__keysVar)
				&& Js.isTruthy(GeoGebraGlobal.__GGB__keysVar.get(lang0))) {
			JsPropertyMap<Object> obj = JsPropertyMap.of();
			obj.set("version", GeoGebraConstants.VERSION_STRING);
			obj.set(lang0, GeoGebraGlobal.__GGB__keysVar.get(lang0));
			BrowserStorage.LOCAL.setItem("translation", Global.JSON.stringify(obj));
		}
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
	 * @param language
	 *            language
	 * @param app
	 *            callback
	 */
	public void loadScript(final Language language, final HasLanguage app) {
		preferredTag = language.toLanguageTag();
		if (LocalizationW.loadPropertiesFromStorage(preferredTag,
				GeoGebraConstants.VERSION_STRING)) {
			app.doSetLanguage(preferredTag, false);
		} else {
			// load keys (into a JavaScript <script> tag)
			String url = StyleInjector.normalizeUrl(GWT.getModuleBaseURL());
			scriptCallback = new ScriptLoadCallback() {
				private boolean canceled = false;

				@Override
				public void onLoad() {
					if (canceled) {
						Log.debug("Async language file load canceled.");
						return;
					}
					// force reload
					app.doSetLanguage(preferredTag, true);

					LocalizationW.savePropertiesToStorage(preferredTag);
				}

				@Override
				public void onError() {
					if (canceled) {
						Log.debug("Async language file load canceled.");
						return;
					}
					LocalizationW.loadPropertiesFromStorage(preferredTag, "");
					app.doSetLanguage(preferredTag, false);
				}

				@Override
				public void cancel() {
					canceled = true;
					preferredTag = languageTag;
				}

			};
			JavaScriptInjector.loadJS(url + "js/properties_keys_" + preferredTag + ".js",
					scriptCallback);
		}

	}

	private void saveLanguageToSettings(String lang0) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().savePreference("language", lang0);
		}
	}

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
