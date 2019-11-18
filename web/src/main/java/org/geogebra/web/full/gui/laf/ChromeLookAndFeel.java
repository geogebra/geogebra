package org.geogebra.web.full.gui.laf;

import org.geogebra.common.GeoGebraConstants;

/**
 * LAF for offline chrome apps
 *
 */
public class ChromeLookAndFeel extends GLookAndFeel {

	@Override
	public boolean isOfflineExamSupported() {
		return true;
	}

	@Override
	public boolean hasHeader() {
		return false;
	}

	@Override
	public boolean examSupported() {
		return true;
	}

	@Override
	public String getClientId() {
		return GeoGebraConstants.CHROME_APP_CLIENT_ID;
	}

	@Override
	public boolean supportsGoogleDrive() {
		return false;
	}
}
