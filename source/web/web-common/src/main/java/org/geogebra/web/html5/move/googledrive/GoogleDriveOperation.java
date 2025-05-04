package org.geogebra.web.html5.move.googledrive;

/**
 * Google Drive connector.
 */
public interface GoogleDriveOperation {

	/**
	 * Reset file descriptors.
	 */
	void resetStorageInfo();

	/**
	 * Open file picker.
	 */
	void requestPicker();

	/**
	 * Refresh current file descriptor.
	 * @param fName filename
	 */
	void refreshCurrentFileDescriptors(String fName);

	/**
	 * Initialize Google Drive API.
	 */
	void initGoogleDriveApi();

	/**
	 * Run callback after the login is finished (runs immediately if already logged in).
	 * @param runnable callback
	 */
	void afterLogin(Runnable runnable);

}
