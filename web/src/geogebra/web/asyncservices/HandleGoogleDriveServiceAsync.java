package geogebra.web.asyncservices;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author gabor Handles the Ajax calls from and to appspot
 *
 */
public interface HandleGoogleDriveServiceAsync {
	/**
	 * @param base64 the base64 to save
	 * @param callback the success callback
	 * @throws IllegalArgumentException thrown if something goes wrong.
	 */
	void saveToGoogleDrive(String base64, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	/**
	 * @param action create
	 * @param parentId the parent folder id
	 * @param code 
	 * @param callback success callback
	 */
	void fileCreated(String action, String parentId, String code, AsyncCallback<Boolean> callback);
}
