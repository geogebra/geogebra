package org.geogebra.web.full.move.googledrive.models;

import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.shared.ggtapi.models.AuthenticationModelW;

/**
 * @author gabor Model for google drive handling
 */
public class GoogleDriveModelW extends AuthenticationModelW {

	/**
	 * Session storage key indicating we are logged in
	 */

	public static final String GGT_GOOGLE_KEY_NAME = "ggt_google";

	/**
	 * New model.
	 */
	public GoogleDriveModelW() {
		super(null);
	}

	@Override
	public String getUserName() {
		return null;
	}

	/**
	 * @return that the user is logged in
	 */
	@Override
	public boolean isLoggedIn() {
		return false;
	}

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
