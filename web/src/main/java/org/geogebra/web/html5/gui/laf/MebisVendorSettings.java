package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.main.AppConfig;

import com.google.gwt.user.client.Window.Location;

public class MebisVendorSettings extends VendorSettings {

	private static final String MEBIS_LICENSE_PATH = "/static/license.html?";

	@Override
	public String getLicenseURL() {
		if (!Location.getProtocol().startsWith("http")) {
			return "https://tafel.mebis.bayern.de" + MEBIS_LICENSE_PATH;
		}
		return MEBIS_LICENSE_PATH;
	}

	@Override
	public String getAppTitle(AppConfig config) {
		return "Tafel";
	}
}
