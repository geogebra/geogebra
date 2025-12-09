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

package org.geogebra.common.jre.main;

import static org.geogebra.common.main.PreviewFeature.ALL_LANGUAGES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.lang.Language;

/**
 * common jre localization
 */
public abstract class LocalizationJre extends Localization {

	private ResourceBundle rbmenu;
	private ResourceBundle rbmenuTT;
	private ResourceBundle rbcommand;
	private ResourceBundle rbcommandOld;
	private ResourceBundle rberror;
	private ResourceBundle rbcolors;
	private ResourceBundle rbsymbol;

	private Language tooltipLanguage = null;
	/** application */
	protected App app;
	private boolean tooltipFlag = false;
	// supported GUI languages (from properties files)
	protected ArrayList<Locale> supportedLocales = null;

	private Language currentLanguage = Language.English_US;
	private Locale currentLocale = convertToLocale(currentLanguage);

	/**
	 * @param dimension 3 for 3D
	 */
	public LocalizationJre(int dimension) {
		this(dimension, 15);
	}

	/**
	 * @param dimension 3 for 3D, 2 otherwise
	 * @param maxFigures maximum digits
	 */
	public LocalizationJre(int dimension, int maxFigures) {
		super(dimension, maxFigures);
	}

	/**
	 * @param app application
	 */
	final public void setApp(App app) {
		this.app = app;
	}

	public Locale getLocale() {
		return currentLocale;
	}

	@Override
	public Language getLanguageEnum() {
		return currentLanguage;
	}

	@Override
	final public void setTooltipFlag() {
		if (tooltipLanguage != null) {
			tooltipFlag = true;
		}
	}

	/**
	 * Stop forcing usage of tooltip locale for translations
	 */
	@Override
	final public void clearTooltipFlag() {
		tooltipFlag = false;
	}

	@Override
	final public String getCommand(String key) {

		app.initTranslatedCommands();

		try {
			return rbcommand.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public @Nonnull String getMenu(String key) {
		if (key == null) {
			return "";
		}

		if (tooltipFlag) {
			return getMenuTooltip(key);
		}

		if (rbmenu == null) {
			rbmenu = createBundle(getMenuResourcePath(), currentLocale);
		}

		try {
			return rbmenu.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * @param key key name
	 * @param locale locale
	 * @return bundle for key and locale
	 */
	abstract protected ResourceBundle createBundle(String key, Locale locale);

	/** @return path of Menu bundle */
	abstract protected String getMenuResourcePath();

	/** @return path of Command bundle */
	abstract protected String getCommandResourcePath();

	/** @return path of Color bundle */
	abstract protected String getColorResourcePath();

	/** @return path of Error bundle */
	abstract protected String getErrorResourcePath();

	/** @return path of Symbol bundle */
	abstract protected String getSymbolResourcePath();

	@Override
	final public String getMenuTooltip(String key) {

		if (tooltipLanguage == null) {
			return getMenu(key);
		}

		if (rbmenuTT == null) {
			rbmenuTT = createBundle(getMenuResourcePath(),
					Locale.forLanguageTag(tooltipLanguage.toLanguageTag()));
		}

		try {
			return rbmenuTT.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String getError(String key) {
		if (rberror == null) {
			rberror = createBundle(getErrorResourcePath(), currentLocale);
		}

		try {
			return rberror.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String getSymbol(int key) {
		if (rbsymbol == null) {
			initSymbolResourceBundle();
		}

		String ret = null;

		try {
			ret = rbsymbol.getString("S." + key);
		} catch (Exception e) {
			// do nothing
		}

		if ("".equals(ret)) {
			return null;
		}
		return ret;
	}

	@Override
	final public Language getLanguage() {
		return Language.getLanguage(getLanguageTag());
	}

	@Override
	public String getLanguageTag() {
		return getLocale().toLanguageTag();
	}

	@Override
	final public String getSymbolTooltip(int key) {
		if (rbsymbol == null) {
			initSymbolResourceBundle();
		}

		String ret = null;

		try {
			ret = rbsymbol.getString("T." + key);
		} catch (Exception e) {
			// do nothing
		}

		if ("".equals(ret)) {
			return null;
		}
		return ret;
	}

	private void initSymbolResourceBundle() {
		rbsymbol = createBundle(getSymbolResourcePath(), currentLocale);
	}

	@Override
	final public void initCommand() {
		if (rbcommand == null) {
			rbcommand = createBundle(getCommandResourcePath(), getCommandLocale());
		}

	}

	private void initColorsResourceBundle() {
		rbcolors = createBundle(getColorResourcePath(), currentLocale);
	}

	final protected void updateResourceBundles() {
		if (rbmenu != null) {
			rbmenu = createBundle(getMenuResourcePath(), currentLocale);
		}
		if (rberror != null) {
			rberror = createBundle(getErrorResourcePath(), currentLocale);
		}

		if (rbcommand != null) {
			rbcommand = createBundle(getCommandResourcePath(), getCommandLocale());
		}
		if (rbcolors != null) {
			rbcolors = createBundle(getColorResourcePath(), currentLocale);
		}
		if (rbsymbol != null) {
			rbsymbol = createBundle(getSymbolResourcePath(), currentLocale);
		}
	}

	/**
	 * @return whether properties bundles were initiated (at least plain)
	 */
	final public boolean propertiesFilesPresent() {
		return rbmenu != null;
	}

	/**
	 * @param ttLanguage language for tooltips
	 * @return whether the language changed
	 */
	final public boolean setTooltipLanguage(Language ttLanguage) {
		boolean updateNeeded = rbmenuTT != null;

		rbmenuTT = null;

		if (ttLanguage == null) {
			tooltipLanguage = null;
		} else if (getLanguage() == ttLanguage) {
			tooltipLanguage = null;
		} else {
			tooltipLanguage = ttLanguage;
		}
		return updateNeeded;
	}

	/**
	 * @return tooltip locale
	 */
	final public Language getTooltipLanguage() {
		return tooltipLanguage;
	}

	@Override
	final public String getTooltipLanguageString() {
		if (tooltipLanguage == null) {
			return null;
		}
		return tooltipLanguage.toLanguageTag();
	}

	@Override
	final public String getColor(String key) {

		if (key == null) {
			return "";
		}

		if ((key.length() == 5)
				&& StringUtil.toLowerCaseUS(key).startsWith("gray")) {
			return StringUtil.getGrayString(key.charAt(4), this);
		}

		if (rbcolors == null) {
			initColorsResourceBundle();
		}

		try {
			return rbcolors.getString(StringUtil.toLowerCaseUS(key));
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String reverseGetColor(String locColor) {
		String str = StringUtil.removeSpaces(StringUtil.toLowerCaseUS(locColor));
		if (rbcolors == null) {
			initColorsResourceBundle();
		}

		try {

			Enumeration<String> keys = rbcolors.getKeys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				if (str.equals(StringUtil.removeSpaces(
						StringUtil.toLowerCaseUS(rbcolors.getString(key))))) {
					return key;
				}
			}

			return str;
		} catch (Exception e) {
			return str;
		}
	}

	private Language[] getSupportedLanguages() {
		return getSupportedLanguages(hasAllLanguages());
	}

	/**
	 * @return list of supported locales
	 */
	protected ArrayList<Locale> getSupportedLocales() {
		return getSupportedLocales(hasAllLanguages());
	}

	/**
	 * @return true if it allows languages that are not fully translated.
	 */
	public boolean hasAllLanguages() {
		return PreviewFeature.isAvailable(ALL_LANGUAGES);
	}

	private ArrayList<Locale> buildSupportedLocales(boolean prerelease) {
		Language[] languages = getSupportedLanguages(prerelease);
		Locale[] locales = getLocales(languages);
		List<Locale> localeList = Arrays.asList(locales);

		return new ArrayList<>(localeList);
	}

	/**
	 * Returns the supported locales.
	 * @param prerelease if the app is in prerelease
	 * @return locales that the app can handle
	 */
	public ArrayList<Locale> getSupportedLocales(boolean prerelease) {
		if (supportedLocales == null) {
			supportedLocales = buildSupportedLocales(prerelease);
		}
		return supportedLocales;
	}

	@Override
	final protected boolean isCommandChanged() {
		return rbcommandOld != rbcommand;
	}

	@Override
	final protected void setCommandChanged(boolean b) {
		rbcommandOld = rbcommand;

	}

	@Override
	final protected boolean isCommandNull() {
		return rbcommand == null;
	}

	final protected String getLanguage(Locale locale) {
		return locale.getLanguage();
	}

	final protected String getCountry(Locale locale) {
		return locale.getCountry();
	}

	protected String getVariant(Locale locale) {
		return locale.getVariant();
	}

	/**
	 * @param locale current locale
	 */
	public void setLocale(Locale locale) {
		currentLanguage = getClosestSupportedLanguage(locale);
		currentLocale = convertToLocale(currentLanguage);
		updateResourceBundles();
	}

	/**
	 * @return locale for command translation
	 */
	protected Locale getCommandLocale() {
		Language language = getLanguage();
		if (areEnglishCommandsForced() || (language != null && !language.hasTranslatedKeyboard())) {
			return Locale.ENGLISH;
		}
		return currentLocale;
	}

	/**
	 * Returns a language that is supported and is closest to the query locale.
	 * If present, it will return the perfect match.
	 * Else, it looks for the closest, more general locale (for example it returns 'en'
	 * for the query 'en-CA').
	 * Else, it looks for the closest, more specific locale (for example it returns 'zh-CN'
	 * for the query 'zh' out of the supported locales ['zh-Hant-TW', 'zh-CN', 'zh-TW']).
	 * Else, it looks for a matching language.
	 * Else, it returns the default locale (English).
	 * @param query query locale
	 * @return closest supported locale
	 */
	protected Language getClosestSupportedLanguage(Locale query) {
		Set<String> subtags = convertToSubtagSet(query.toLanguageTag());

		Language match = null;
		int generalScore = 0;
		int specificScore = Integer.MAX_VALUE;
		for (Language language : getSupportedLanguages()) {
			if (language.toLanguageTag().equals(query.toLanguageTag())) {
				// A perfect match found, return early
				return language;
			} else if (language.language.equals(query.getLanguage())) {
				// The language matches
				Set<String> supportedLocaleSubtags = convertToSubtagSet(language.toLanguageTag());
				if (subtags.containsAll(supportedLocaleSubtags)
						&& supportedLocaleSubtags.size() > generalScore) {
					// A closer, more general match found
					match = language;
					generalScore = supportedLocaleSubtags.size();
				} else if (generalScore == 0 && supportedLocaleSubtags.containsAll(subtags)
						&& supportedLocaleSubtags.size() < specificScore) {
					// A closer more specific match found, and no general match found
					specificScore = supportedLocaleSubtags.size();
					match = language;
				} else if (match == null) {
					// Store a match to the language if there is none yet.
					match = language;
				}
			}
		}
		if (match != null) {
			return match;
		}

		return Language.English_US;
	}

	private static Set<String> convertToSubtagSet(String languageTag) {
		return new HashSet<>(Arrays.asList(languageTag.split("-")));
	}

	/**
	 * Converts the language to a locale object.
	 * @param language the language to convert to.
	 * @return converted locale
	 */
	public Locale convertToLocale(Language language) {
		return Locale.forLanguageTag(language.toLanguageTag());
	}

	/**
	 * Get an array of locales from languages.
	 * @param languages array of languages
	 * @return an array of locales
	 */
	@Override
	public Locale[] getLocales(Language[] languages) {
		Locale[] locales = new Locale[languages.length];
		for (int i = 0; i < languages.length; i++) {
			Language language = languages[i];
			locales[i] = convertToLocale(language);
		}
		return locales;
	}

	/**
	 * @param key translation key
	 * @return whether translation exists in menu category
	 */
	public boolean hasMenu(String key) {
		if (rbmenu == null) {
			rbmenu = createBundle(getMenuResourcePath(), currentLocale);
		}
		try {
			rbmenu.getString(key);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
