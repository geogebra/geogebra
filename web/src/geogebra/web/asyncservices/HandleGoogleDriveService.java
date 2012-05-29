package geogebra.web.asyncservices;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author gabor Handles the Ajax calls from and to appspot
 *
 */
@RemoteServiceRelativePath("handleGD")
public interface HandleGoogleDriveService extends RemoteService {
	/**
	 * @param base64 saves the given construction to Google Drive
	 * @return the result answer
	 * @throws IllegalArgumentException thrown if something wrong
	 */
	String saveToGoogleDrive(String base64) throws IllegalArgumentException;
}
