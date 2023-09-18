package org.geogebra.desktop.gui.menubar;

import org.geogebra.common.util.lang.Language;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;

/**
 * Handle the change of the language.
 */
public class LanguageActionListener {

	private AppD app;

	public LanguageActionListener(AppD app) {
		this.app = app;
	}

	/**
	 * @param language language
	 */
	public void setLanguage(Language language) {
		app.setLanguage(app.getLocalization().convertToLocale(language));
		// make sure axes labels are updated eg for Arabic
		app.getEuclidianView1().updateBackground();
		if (app.hasEuclidianView2EitherShowingOrNot(1)) {
			app.getEuclidianView2(1).updateBackground();
		}
		GeoGebraPreferencesD.getPref().saveDefaultLocale(app.getLocale());
	}
}
