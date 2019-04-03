package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.main.AppConfig;

public class MebisVendorSettings extends VendorSettings {

	@Override
	public String getLicenseURL() {
		return "/static/license.html?";
	}

	@Override
	public String getAppTitle(AppConfig config) {
		return "Tafel";
	}
}
