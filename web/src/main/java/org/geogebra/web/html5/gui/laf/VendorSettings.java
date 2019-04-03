package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.AppConfig;

public class VendorSettings {
	/**
	 * License URL should contain ? so that params can be appended.
	 * 
	 * @return base URL of the license
	 */
	public String getLicenseURL() {
		return GeoGebraConstants.GGW_ABOUT_LICENSE_URL;
	}

	/**
	 * Allows overriding the app title
	 * 
	 * @param config
	 *            app configuration
	 * @return app title translation key
	 */
	public String getAppTitle(AppConfig config) {
		return config.getAppTitle();
	}
}
