package geogebra.server;

import geogebra.server.HandleOAuth2ServiceImpl.GetCredentialsException.CodeExchangeException;
import geogebra.server.HandleOAuth2ServiceImpl.GetCredentialsException.NoRefreshTokenException;
import geogebra.web.asyncservices.HandleGoogleDriveService;

import java.io.IOException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author gabor server side handling of Ajax calls.
 *
 */
public class HandleGoogleDriveServiceImpl extends RemoteServiceServlet
        implements HandleGoogleDriveService {

	/**
     * 
     */
    private static final long serialVersionUID = -4084638709387660408L;

	public String saveToGoogleDrive(String base64)
	        throws IllegalArgumentException {
		
		
		
		return "";
	}

	public Boolean fileCreated(String action, String parentId,String code) {
	    Credential c = null;
		try {
	        c = HandleOAuth2ServiceImpl.getCredentials(code, "");
        } catch (CodeExchangeException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (NoRefreshTokenException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	    if (c != null) {
	    	return new Boolean(true);
	    } else {
	    	return new Boolean(false);
	    }
    }

}
