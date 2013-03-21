package geogebra.web.main;

import geogebra.common.main.App;
import geogebra.common.main.GeoGebraPreferences;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.storage.client.Storage;

public class GeoGebraPreferencesW extends GeoGebraPreferences {

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
			        "web/default-preferences.xml").sendRequest("",
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
	
	public void saveXMLPreferences(App app) {
		String xml = app.getPreferencesXML();
		Storage stockStore = null;
		stockStore = Storage.getLocalStorageIfSupported();
		if(stockStore!=null){
			stockStore.setItem(XML_USER_PREFERENCES, xml);
	       	String xmlDef = app.getKernel().getConstruction().getConstructionDefaults().getCDXML();
	       	stockStore.setItem(XML_DEFAULT_OBJECT_PREFERENCES, xmlDef);
		}
	}
}
