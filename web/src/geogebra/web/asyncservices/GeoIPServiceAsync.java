package geogebra.web.asyncservices;

import geogebra.web.gui.app.GeoIPInformation;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Rana
 *
 */
public interface GeoIPServiceAsync {

	/**
	 * @param callback
	 */
	void getGeoIPInformation(AsyncCallback<GeoIPInformation> callback);
}
