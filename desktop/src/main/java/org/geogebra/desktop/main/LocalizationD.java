package org.geogebra.desktop.main;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.jre.headless.Utf8Control;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.lang.Language;

/**
 * Desktop localization
 */
public class LocalizationD extends LocalizationCommon {

	/**
	 * @param dimension 3 for 3D
	 */
	public LocalizationD(int dimension) {
		super(dimension);
		setResourceBundleControl(new Utf8Control());
	}

	/**
	 * @param prerelease whether we also have prereleased languages
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
}
