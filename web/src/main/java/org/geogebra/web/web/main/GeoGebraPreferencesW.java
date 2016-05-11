package org.geogebra.web.web.main;

import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraPreferences;
import org.geogebra.common.main.GeoGebraPreferencesXML;

import com.google.gwt.storage.client.Storage;

public class GeoGebraPreferencesW extends GeoGebraPreferences {

	private static GeoGebraPreferencesW singleton;

	public static GeoGebraPreferencesW getPref() {
		if (singleton == null) {
			singleton = new GeoGebraPreferencesW();
		}
		return singleton;
	}

	public void clearPreferences() {
		Storage stockStore = null;
		stockStore = Storage.getLocalStorageIfSupported();
		if (stockStore != null) {
			stockStore.removeItem(XML_USER_PREFERENCES);
			stockStore.removeItem(XML_DEFAULT_OBJECT_PREFERENCES);
		}

	}

	public void loadXMLPreferences(final App app) {

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
}
