package geogebra.web.asyncservices;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("geoIPService")
public interface GeoIPService extends RemoteService {
	
	public String getCountry();
	
	public String getLanguage();

}
