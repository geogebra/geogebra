package org.geogebra.web.html5.move.googledrive;

public interface GoogleDriveOperation {

	void resetStorageInfo();

	void requestPicker();

	void refreshCurrentFileDescriptors(String fName);

	void initGoogleDriveApi();

	void afterLogin(Runnable runnable);

}
