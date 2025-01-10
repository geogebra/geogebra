package org.geogebra.web.full.main;

import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraPreferences;
import org.geogebra.common.main.GeoGebraPreferencesXML;
import org.geogebra.web.html5.gui.util.BrowserStorage;

/**
 * Preferences in Web (Local Storage)
 */
public final class GeoGebraPreferencesW {

	/**
	 * Remove all preferences from storage
	 * 
	 * @param app
	 *            application
	 */
	public static void clearPreferences(App app) {
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
	public static void resetPreferences(final App app) {
		app.setXML(GeoGebraPreferencesXML.getXML(app), false);
	}

	/**
	 * Take preferences from app and save them to local storage
	 * 
	 * @param app
	 *            application
	 */
	public static void saveXMLPreferences(App app) {
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
	public static void loadForApp(AppWFull app, Perspective p0) {
		Perspective p = p0;
		// code moved here from AppWapplication.afterCoreObjectsInited - end
		BrowserStorage stockStore = BrowserStorage.LOCAL;

		String xml = stockStore.getItem(getPrefKey(app));
		if (xml != null) {
			app.setXML(xml, false);
		} else if (!app.isWhiteboardActive()) {
			if (app.getPreferredSize() != null) {
				GeoGebraPreferencesXML
						.setDefaultWindowX(app.getPreferredSize().getWidth());
				GeoGebraPreferencesXML
						.setDefaultWindowY(app.getPreferredSize().getHeight());
			}
			app.setXML(GeoGebraPreferencesXML.getXML(app), false);
			if (app.getTmpPerspective() != null) {
				p = app.getTmpPerspective();
			}
		} else {
			app.getSettings().resetNoFireEuclidianSettings();
		}

		readObjectDefaults(app, stockStore);
		if (app.isUnbundled()) {
			app.setPerspectiveForUnbundled(p);
		} else if (app.getGuiManager() != null) {
			app.getGuiManager().getLayout()
					.setPerspectiveOrDefault(p);
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
