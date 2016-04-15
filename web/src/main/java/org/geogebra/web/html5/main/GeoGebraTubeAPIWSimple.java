package org.geogebra.web.html5.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.util.HttpRequestIE;
import org.geogebra.web.html5.util.HttpRequestW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window.Location;

public class GeoGebraTubeAPIWSimple extends GeoGebraTubeAPI {



	public GeoGebraTubeAPIWSimple(boolean beta) {
		super(beta);
	}
	@Override
	protected HttpRequest createHttpRequest() {
		return Browser.isIE9() ? new HttpRequestIE() : new HttpRequestW();
	}

	@Override
	protected boolean parseUserDataFromResponse(GeoGebraTubeUser user,
	        String response) {
		return false;
	}

	@Override
	public String getClientInfo() {
		if (!Browser.runningLocal()) {
			return "";
		}
		return "\"client\":{\"-id\":"
		        + new JSONString(Location.getHref()
		                + ":"
		                + GeoGebraConstants.VERSION_STRING
		                + (GWT.getModuleBaseURL().contains("geogebra.org") ? ""
		                        : ":pack")).toString()
		        + ", \"-type\":\"web\", \"-language\":"
		        + new JSONString(Browser.navigatorLanguage()).toString() + "},";
	}



	@Override
	protected String getToken() {
		return "";
	}

}
