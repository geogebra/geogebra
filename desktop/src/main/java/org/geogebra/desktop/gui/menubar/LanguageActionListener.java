package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;

/**
 * Handle the change of the language.
 */
public class LanguageActionListener implements ActionListener {

	private AppD app;

	public LanguageActionListener(AppD app) {
		this.app = app;
	}

	public void actionPerformed(ActionEvent e) {
		app.setLanguage(AppD.getLocale(e.getActionCommand()));
		// make sure axes labels are updated eg for Arabic
		app.getEuclidianView1().updateBackground();
		if (app.hasEuclidianView2EitherShowingOrNot(1))
			app.getEuclidianView2(1).updateBackground();
		GeoGebraPreferencesD.getPref().saveDefaultLocale(app.getLocale());
	}
}
