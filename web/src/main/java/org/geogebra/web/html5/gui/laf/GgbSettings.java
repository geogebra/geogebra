package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.AppConfig;

public class GgbSettings implements VendorSettings {

	private ViewPreferences viewPreferences;

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
}
