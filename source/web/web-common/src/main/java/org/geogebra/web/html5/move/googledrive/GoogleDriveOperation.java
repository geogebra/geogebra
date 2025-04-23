package org.geogebra.web.html5.move.googledrive;

/**
 * Google Drive connector.
 */
public interface GoogleDriveOperation {

	void resetStorageInfo();

	void requestPicker();

	void refreshCurrentFileDescriptors(String fName);

	void initGoogleDriveApi();

	void afterLogin(Runnable runnable);

}
