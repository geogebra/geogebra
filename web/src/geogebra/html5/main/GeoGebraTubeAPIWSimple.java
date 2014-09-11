package geogebra.html5.main;

import geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.util.HttpRequest;
import geogebra.html5.Browser;

import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window.Location;

public class GeoGebraTubeAPIWSimple extends GeoGebraTubeAPI {

	@Override
	protected HttpRequest createHttpRequest() {
		return new geogebra.html5.util.HttpRequestW();
	}

	@Override
	protected boolean parseUserDataFromResponse(GeoGebraTubeUser user,
	        String response) {		
		return false;
	}

	@Override
	protected String buildTokenLoginRequest(String loginToken, String cookie) {
		return "";
	}
	
	@Override
    public String getClientInfo() {
	    return "\"client\":{\"-id\":"+new JSONString(Location.getHref()).toString()+", \"-type\":\"web\", \"-language\":"+new JSONString(Browser.navigatorLanguage()).toString()+"}";
    }

}
