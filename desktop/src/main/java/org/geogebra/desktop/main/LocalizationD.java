package org.geogebra.desktop.main;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.Language;

/**
 * Desktop localization
 */
public class LocalizationD extends LocalizationJre {

	private App app;

	/**
	 * @param dimension
	 *            3 for 3D
	 */
	public LocalizationD(int dimension) {
		super(dimension);
	}

	// supported GUI languages (from properties files)
	private static ArrayList<Locale> supportedLocales = null;

	@Override
	protected ArrayList<Locale> getSupportedLocales() {
		return getSupportedLocales(
				app != null && app.has(Feature.ALL_LANGUAGES));
	}

	/**
	 * @param prerelease
	 *            whether we also have prereleased languages
	 * @return locales
	 */
	public static ArrayList<Locale> getSupportedLocales(boolean prerelease) {

		if (supportedLocales != null) {
			return supportedLocales;
		}

		supportedLocales = new ArrayList<Locale>();

		Language[] languages = Language.values();

		for (int i = 0; i < languages.length; i++) {

			Language language = languages[i];

			if (language.fullyTranslated || prerelease) {

				if (language.locale.length() == 2) {
					// eg "en"
					supportedLocales.add(new Locale(language.locale));
				} else if (language.locale.length() == 4) {
					// eg "enGB" -> "en", "GB"
					supportedLocales.add(new Locale(language.locale.substring(
							0, 2), language.locale.substring(2, 4)));
				} else if (language.locale.length() == 6) {
					// eg "noNONY" -> "no", "NO", "NY"
					supportedLocales.add(new Locale(language.locale.substring(
							0, 2), language.locale.substring(2, 4),
							language.locale.substring(4, 6)));
				}
			}

		}

		return supportedLocales;

	}


	@Override
	protected ResourceBundle createBundle(String key, Locale locale) {
		// TODO Auto-generated method stub
		return MyResourceBundle.createBundle(key, locale);
	}

	@Override
	protected String getLanguage(Locale locale) {
		return locale.getLanguage();
	}

	@Override
	protected String getCountry(Locale locale) {
		return locale.getCountry();
	}

}
