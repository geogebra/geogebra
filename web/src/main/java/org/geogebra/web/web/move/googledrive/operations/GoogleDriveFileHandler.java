package org.geogebra.web.web.move.googledrive.operations;

public interface GoogleDriveFileHandler {
	// value.originalFilename,value.lastModifyingUserName,value.downloadUrl,
	// value.title, value.description, value.id
	public void show(String title, String username, String date, String url,
	        String description, String googleID, String thumbnail);

	public void done();

	public void clearMaterials();
}
