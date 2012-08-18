package geogebra.gui.menubar;

import geogebra.main.AppD;
import geogebra.main.GeoGebraPreferencesD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
		if(app.hasEuclidianView2EitherShowingOrNot())
			app.getEuclidianView2().updateBackground();
		GeoGebraPreferencesD.getPref().saveDefaultLocale(app.getLocale());
	}
}

