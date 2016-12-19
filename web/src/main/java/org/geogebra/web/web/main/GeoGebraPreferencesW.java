package org.geogebra.web.web.main;

import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraPreferences;
import org.geogebra.common.main.GeoGebraPreferencesXML;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.storage.client.Storage;

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
	 */
	public void clearPreferences() {
		Storage stockStore = null;
		stockStore = Storage.getLocalStorageIfSupported();
		if (stockStore != null) {
			stockStore.removeItem(XML_USER_PREFERENCES);
			stockStore.removeItem(XML_DEFAULT_OBJECT_PREFERENCES);
		}

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

	public void saveXMLPreferences(App app) {
		String xml = app.getPreferencesXML();
		Storage stockStore = null;
		stockStore = Storage.getLocalStorageIfSupported();
		if (stockStore != null) {
			stockStore.setItem(XML_USER_PREFERENCES, xml);
			StringBuilder sb2d = new StringBuilder();
			StringBuilder sb3d = new StringBuilder();
			app.getKernel().getConstruction().getConstructionDefaults()
					.getDefaultsXML(sb2d, sb3d);
			String objectPrefsXML = sb2d.toString();
			stockStore.setItem(XML_DEFAULT_OBJECT_PREFERENCES, objectPrefsXML);
		}
	}

	public void loadForApp(AppW app, Perspective p0) {
		Perspective p = p0;
		// code moved here from AppWapplication.afterCoreObjectsInited - end
		Storage stockStore = null;

		stockStore = Storage.getLocalStorageIfSupported();
		// if (stockStore != null) {
		String xml = stockStore == null ? null
				: stockStore.getItem(GeoGebraPreferences.XML_USER_PREFERENCES);
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

	private static void readObjectDefaults(App app, Storage stockStore) {
		if (stockStore == null) {
			return;
		}
		String xmlDef = stockStore
				.getItem(GeoGebraPreferences.XML_DEFAULT_OBJECT_PREFERENCES);
		boolean eda = app.getKernel().getElementDefaultAllowed();
		app.getKernel().setElementDefaultAllowed(true);
		if (xmlDef != null) {
			app.setXML(xmlDef, false);
		}
		app.getKernel().setElementDefaultAllowed(eda);

	}
}
