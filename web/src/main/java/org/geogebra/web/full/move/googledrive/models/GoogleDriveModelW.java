package org.geogebra.web.full.move.googledrive.models;

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

	@Override
	public String getEncoded() {
		return null;
	}

	/**
	 * Update info about goofgle login in local storage.
	 * 
	 * @param loggedInFrom
	 *            whether we logged in from google
	 */
	public void setLoggedInFromGoogleDrive(boolean loggedInFrom) {
		if (storage == null) {
			return;
		}
		if (loggedInFrom) {
			storage.setItem(GGT_GOOGLE_KEY_NAME, "true");
		} else {
			storage.removeItem(GGT_GOOGLE_KEY_NAME);
		}
	}

	/**
	 * @return whether we logged in from Google last time
	 */
	public boolean lastLoggedInFromGoogleDrive() {
		if (storage == null) {
			return false;
		}
		return "true".equals(storage.getItem(GGT_GOOGLE_KEY_NAME));
	}

}
