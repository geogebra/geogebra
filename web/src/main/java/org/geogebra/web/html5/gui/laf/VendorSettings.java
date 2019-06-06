package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.main.AppConfig;

public interface VendorSettings {

	/**
	 * License URL should contain ? so that params can be appended.
	 *
	 * @return base URL of the license
	 */
	String getLicenseURL();

	/**
	 * Allows overriding the app title
	 *
	 * @param config
	 *            app configuration
	 * @return app title translation key
	 */
	String getAppTitle(AppConfig config);

	ViewPreferences getViewPreferences();
}
