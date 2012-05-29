package geogebra.web.asyncservices;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author gabor Handles the Ajax calls from and to appspot
 *
 */
public interface HandleGoogleDriveServiceAsync {
	/**
	 * @param base64
	 * @param callback the success callback
	 * @throws IllegalArgumentException thrown if something goes wrong.
	 */
	void saveToGoogleDrive(String base64, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
