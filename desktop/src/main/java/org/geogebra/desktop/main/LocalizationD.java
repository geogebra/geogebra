package org.geogebra.desktop.main;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.lang.Language;

/**
 * Desktop localization
 */
public class LocalizationD extends LocalizationJre {

	private static final String PROPERTIES_DIR = "/org/geogebra/common/jre/properties/";
	/** path to menu */
	static final String RB_MENU = PROPERTIES_DIR + "menu";
	/** path to commands */
	static final String RB_COMMAND = PROPERTIES_DIR + "command";
	private static final String RB_ERROR = PROPERTIES_DIR + "error";
	private static final String RB_SYMBOL = PROPERTIES_DIR + "symbols";
	/** path to javaui properties (without extension) */
	public static final String RB_JAVA_UI = PROPERTIES_DIR + "javaui";
	private static final String RB_COLORS = PROPERTIES_DIR + "colors";

	/**
	 * @param dimension
	 *            3 for 3D
	 */
	public LocalizationD(int dimension) {
		super(dimension);
	}

	private static Object lock = new Object();

	@Override
	protected ArrayList<Locale> getSupportedLocales() {
		return getSupportedLocales(
				app != null && app.has(Feature.ALL_LANGUAGES));
	}

	@Override
	protected Locale getCommandLocale() {
		return currentLocale;
	}

	/**
	 * @param prerelease
	 *            whether we also have prereleased languages
	 * @return locales
	 */
	@Override
	public ArrayList<Locale> getSupportedLocales(boolean prerelease) {
		if (supportedLocales == null) {
			ArrayList<Locale> supportedLocales0 = new ArrayList<>();

			Language[] languages = Language.values();

			for (int i = 0; i < languages.length; i++) {
				Language language = languages[i];

				if (language.fullyTranslated || prerelease) {
					if (language.locale.length() == 2) {
						// eg "en"
						supportedLocales0.add(new Locale(language.locale));
					} else if (language.locale.length() == 4) {
						// eg "enGB" -> "en", "GB"
						supportedLocales0
								.add(new Locale(language.locale.substring(0, 2),
										language.locale.substring(2, 4)));
					} else if (language.locale.length() == 6) {
						// eg "noNONY" -> "no", "NO", "NY"
						supportedLocales0
								.add(new Locale(language.locale.substring(0, 2),
										language.locale.substring(2, 4),
										language.locale.substring(4, 6)));
					}
				}
			}
			supportedLocales = supportedLocales0;
		}

		return supportedLocales;
	}

	@Override
	protected ResourceBundle createBundle(String key, Locale locale) {
		return MyResourceBundle.createBundle(key, locale);
	}

	@Override
	protected String getMenuRessourcePath() {
		return RB_MENU;
	}

	@Override
	protected String getCommandRessourcePath() {
		return RB_COMMAND;
	}

	@Override
	protected String getColorRessourcePath() {
		return RB_COLORS;
	}

	@Override
	protected String getErrorRessourcePath() {
		return RB_ERROR;
	}

	@Override
	protected String getSymbolRessourcePath() {
		return RB_SYMBOL;
	}


}
