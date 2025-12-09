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
