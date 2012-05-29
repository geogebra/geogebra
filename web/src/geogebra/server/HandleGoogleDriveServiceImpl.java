package geogebra.server;

import geogebra.web.asyncservices.HandleGoogleDriveService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author gabor server side handling of Ajax calls.
 *
 */
public class HandleGoogleDriveServiceImpl extends RemoteServiceServlet
        implements HandleGoogleDriveService {

	public String saveToGoogleDrive(String base64)
	        throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return "client said"+base64;
	}

}
