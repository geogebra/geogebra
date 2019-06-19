package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.AppConfig;
import org.geogebra.web.html5.gui.zoompanel.FullScreenHandler;

/**
 * Ggb specific settings
 */
public class GgbSettings implements VendorSettings {

	private ViewPreferences viewPreferences;

	/**
	 * Ggb specific settings
	 */
	public GgbSettings() {
		viewPreferences = new ViewPreferences();
		viewPreferences.setMobileFullScreenButtonEnabled(false);
	}

	@Override
	public String getLicenseURL() {
		return GeoGebraConstants.GGW_ABOUT_LICENSE_URL;
	}

	@Override
	public String getAppTitle(AppConfig config) {
		return config.getAppTitle();
	}

	@Override
	public ViewPreferences getViewPreferences() {
		return viewPreferences;
	}

	@Override
	public FullScreenHandler getFullscreenHandler() {
		return null;
	}

	@Override
	public String getVideoAccessErrorKey() {
		return "VideoAccessError";
	}

	@Override
	public String getUnsupportedBrowserErrorKey() {
		return null;
	}
}
