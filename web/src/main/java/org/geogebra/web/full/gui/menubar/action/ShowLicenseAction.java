package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Shows license.
 */
public class ShowLicenseAction extends DefaultMenuAction<Void> {

	/**
	 * Settings for version/about window
	 */
	protected static final String ABOUT_WINDOW_PARAMS = "width=720,height=600,"
			+ "scrollbars=yes,toolbar=no,location=no,directories=no,"
			+ "menubar=no,status=no,copyhistory=no";

	@Override
	public void execute(Void item, AppWFull app) {
		app.getFileManager()
				.open(app.getVendorSettings().getLicenseURL() + "&version="
						+ app.getVersionString() + "&date="
						+ GeoGebraConstants.BUILD_DATE, ABOUT_WINDOW_PARAMS);
	}
}
