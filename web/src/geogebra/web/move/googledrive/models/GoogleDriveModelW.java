package geogebra.web.move.googledrive.models;

import geogebra.html5.move.ggtapi.models.AuthenticationModelW;

/**
 * @author gabor
 *	Model for google drive handling
 */
public class GoogleDriveModelW extends AuthenticationModelW {
	
	/**
	 * @return the username of the signed in user
	 */
	
	public static String GGT_GOOGLE_KEY_NAME = "ggt_google";
	
	public GoogleDriveModelW() {
	    super();
    }
	
	public String getUserName() {
		return null;
    }
	
	/**
	 * @return that the user is logged in
	 */
	public boolean isLoggedIn() {
		return false;
	}

	public void setLoggedInFromGoogleDrive(boolean loggedInFrom) {
		if (loggedInFrom) {
			storage.setItem(GGT_GOOGLE_KEY_NAME, "true");
		} else {
			storage.removeItem(GGT_GOOGLE_KEY_NAME);;

		}
    }
	
	public boolean lastLoggedInFromGoogleDrive() {
		return "true".equals(storage.getItem(GGT_GOOGLE_KEY_NAME));
	}

}
