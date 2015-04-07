package org.geogebra.web.html5.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.web.html5.Browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window.Location;

public class GeoGebraTubeAPIWSimple extends GeoGebraTubeAPI {

	private boolean beta;

	public GeoGebraTubeAPIWSimple(boolean beta) {
		this.beta = beta;
	}
	@Override
	protected HttpRequest createHttpRequest() {
		return new org.geogebra.web.html5.util.HttpRequestW();
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
	protected String getLoginUrl() {
		return beta ? login_urlBeta : login_url;
	}

	@Override
	protected String getUrl() {
		return beta ? urlBeta : url;
	}

	@Override
	protected String getToken() {
		return "";
	}

}
