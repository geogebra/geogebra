package geogebra.web.helper;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.AbstractApplication;

import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

public class MyGoogleApis {
	
	public static AuthRequest createNewAuthRequest() {
		return new AuthRequest(GeoGebraConstants.GOOGLE_AUTH_URL, GeoGebraConstants.GOOGLE_CLIENT_ID)
		.withScopes(GeoGebraConstants.USERINFO_EMAIL_SCOPE,GeoGebraConstants.USERINFO_PROFILE_SCOPE,GeoGebraConstants.DRIVE_SCOPE);
	}

	public static void executeApi(String urlWithToken,
            final GoogleApiCallback googleApiCallback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(urlWithToken));
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					AbstractApplication.error(exception.getLocalizedMessage());
				}
				
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						googleApiCallback.success(response.getText());
					} else {
						googleApiCallback.failure(response.getStatusText());
					}
				}
			});
        } catch (Exception e) {
	       AbstractApplication.error(e.getLocalizedMessage());
        }
    }

}
