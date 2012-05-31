package geogebra.gui.menubar;

import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handle the change of the language.
 */
class LanguageActionListener implements ActionListener {

	private Application app;

	public LanguageActionListener(Application app) {
		this.app = app;
	}

	public void actionPerformed(ActionEvent e) {
		app.setLanguage(Application.getLocale(e.getActionCommand()));
		// make sure axes labels are updated eg for Arabic 
		app.getEuclidianView1().updateBackground();
		if(app.hasEuclidianView2EitherShowingOrNot())
			app.getEuclidianView2().updateBackground();
		GeoGebraPreferences.getPref().saveDefaultLocale(app.getLocale());
	}
}

