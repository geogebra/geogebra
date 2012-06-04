package geogebra.web.asyncservices;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author gabor
 * Handles the Oauth2 service
 *
 */
public interface HandleOAuth2ServiceAsync {
	
	void triggerLoginToGoogle(AsyncCallback<Boolean> callback);
	
	

}
