package geogebra.web.main;

import geogebra.common.main.App;
import geogebra.common.main.GeoGebraPreferences;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class GeoGebraPreferencesW implements GeoGebraPreferences {

	private static GeoGebraPreferencesW singleton;

	public static GeoGebraPreferencesW getPref() {
		if (singleton == null) {
			singleton = new GeoGebraPreferencesW();
		}
		return singleton;
	}

	public void clearPreferences() {
		// TODO Auto-generated method stub
		App.debug("unimplemented method");

	}

	public void loadXMLPreferences(final App app) {

		try {
			new RequestBuilder(RequestBuilder.GET,
			        "web/default_preferences.xml").sendRequest("",
			        new RequestCallback() {

				        public void onResponseReceived(Request request,
				                Response response) {
					        if (200 == response.getStatusCode()) {
						        app.setXML(response.getText(), false);
					        }

				        }

				        public void onError(Request request, Throwable exception) {
					        App.error(exception.getMessage());

				        }

			        });
		} catch (RequestException e) {
			App.error(e.getMessage());
		}
	}
}
