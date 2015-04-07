package org.geogebra.web.html5.move.googledrive;

public interface GoogleDriveOperation {

	void resetStorageInfo();

	void requestPicker();

	void refreshCurrentFileDescriptors(String fName, String desc);

	void initGoogleDriveApi();

	void afterLogin(Runnable runnable);

}
