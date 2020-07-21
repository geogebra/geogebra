package org.geogebra.web.full.main;

import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraPreferences;
import org.geogebra.common.main.GeoGebraPreferencesXML;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;

/**
 * Preferences in Web (Local Storage)
 */
public class GeoGebraPreferencesW extends GeoGebraPreferences {

	private static GeoGebraPreferencesW singleton;

	/**
	 * @return preferences
	 */
	public static GeoGebraPreferencesW getPref() {
		if (singleton == null) {
			singleton = new GeoGebraPreferencesW();
		}
		return singleton;
	}

	/**
	 * Remove all preferences from storage
	 * 
	 * @param app
	 *            application
	 */
	public void clearPreferences(App app) {
		BrowserStorage stockStore = BrowserStorage.LOCAL;
		stockStore.removeItem(getPrefKey(app));
		stockStore.removeItem(getDefaultsKey(app));
	}

	private static String getPrefKey(App app) {
		return GeoGebraPreferences.XML_USER_PREFERENCES
				+ app.getConfig().getPreferencesKey();
	}

	/**
	 * Set the factory defaults to an application
	 * 
	 * @param app
	 *            application
	 */
	public void resetPreferences(final App app) {

		app.setXML(GeoGebraPreferencesXML.getXML(app), false);
	}

	/**
	 * Take prefernces from app and save them to local storage
	 * 
	 * @param app
	 *            application
	 */
	public void saveXMLPreferences(App app) {
		String xml = app.getPreferencesXML();
		BrowserStorage stockStore = BrowserStorage.LOCAL;
		stockStore.setItem(getPrefKey(app), xml);
		StringBuilder sb = new StringBuilder();
		app.getKernel().getConstruction().getConstructionDefaults()
				.getDefaultsXML(sb);
		String objectPrefsXML = sb.toString();
		stockStore.setItem(getDefaultsKey(app), objectPrefsXML);
	}

	/**
	 * @param app
	 *            app
	 * @param p0
	 *            selected perspective
	 */
	public void loadForApp(AppW app, Perspective p0) {
		Perspective p = p0;
		// code moved here from AppWapplication.afterCoreObjectsInited - end
		BrowserStorage stockStore = BrowserStorage.LOCAL;
		// if (stockStore != null) {
		String xml = stockStore.getItem(getPrefKey(app));
		if (xml != null) {
			app.setXML(xml, false);
		} else {
			if (app.getPreferredSize() != null) {
				GeoGebraPreferencesXML
						.setDefaultWindowX(app.getPreferredSize().getWidth());
				GeoGebraPreferencesXML
						.setDefaultWindowY(app.getPreferredSize().getHeight());
			}
			app.setXML(GeoGebraPreferencesXML.getXML(app), false);
			if (app.getTmpPerspectives().size() > 0 && p0 == null) {
				p = app.getTmpPerspectives().get(0);
			}
		}

		readObjectDefaults(app, stockStore);

		if (app.getGuiManager() != null) {
			app.getGuiManager().getLayout()
					.setPerspectives(app.getTmpPerspectives(), p);
		}

	}

	private static void readObjectDefaults(App app, BrowserStorage stockStore) {
		if (stockStore == null) {
			return;
		}
		String xmlDef = stockStore
				.getItem(getDefaultsKey(app));
		boolean eda = app.getKernel().getElementDefaultAllowed();
		app.getKernel().setElementDefaultAllowed(true);
		if (xmlDef != null) {
			app.setXML(xmlDef, false);
		}
		app.getKernel().setElementDefaultAllowed(eda);

	}

	private static String getDefaultsKey(App app) {
		return GeoGebraPreferences.XML_DEFAULT_OBJECT_PREFERENCES
				+ app.getConfig().getPreferencesKey();
	}
}
