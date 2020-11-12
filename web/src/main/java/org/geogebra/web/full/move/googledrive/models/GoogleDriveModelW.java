package org.geogebra.web.full.move.googledrive.models;

import org.geogebra.web.html5.gui.util.BrowserStorage;

/**
 * @author gabor Model for google drive handling
 */
public class GoogleDriveModelW {

	/**
	 * Session storage key indicating we are logged in
	 */

	public static final String GGT_GOOGLE_KEY_NAME = "ggt_google";

	/**
	 * Update info about goofgle login in local storage.
	 * 
	 * @param loggedInFrom
	 *            whether we logged in from google
	 */
	public void setLoggedInFromGoogleDrive(boolean loggedInFrom) {
		if (loggedInFrom) {
			BrowserStorage.LOCAL.setItem(GGT_GOOGLE_KEY_NAME, "true");
		} else {
			BrowserStorage.LOCAL.removeItem(GGT_GOOGLE_KEY_NAME);
		}
	}

	/**
	 * @return whether we logged in from Google last time
	 */
	public boolean lastLoggedInFromGoogleDrive() {
		return "true".equals(BrowserStorage.LOCAL.getItem(GGT_GOOGLE_KEY_NAME));
	}

}
