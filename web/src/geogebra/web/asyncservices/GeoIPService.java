package geogebra.web.asyncservices;

import geogebra.web.gui.app.GeoIPInformation;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Rana
 *
 */
@RemoteServiceRelativePath("geoIPService")
public interface GeoIPService extends RemoteService {
	
	/**
	 * @return GeoIPInformation
	 */
	public GeoIPInformation getGeoIPInformation();
}
