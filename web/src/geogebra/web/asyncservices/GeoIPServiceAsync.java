package geogebra.web.asyncservices;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GeoIPServiceAsync {

	void getCountry(AsyncCallback<String> callback);

	void getLanguage(AsyncCallback<String> callback);
}
