package org.geogebra.web.html5.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.MarvlService;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.models.MowService;
import org.geogebra.common.move.ggtapi.models.Service;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.HttpRequestW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window.Location;

/**
 * Simple version of API just for availability check.
 */
public class GeoGebraTubeAPIWSimple extends GeoGebraTubeAPI {

	// We are delegating some functionality to the material api,
	// delegation should be extended, until we can completely get rid
	// of tube and this class.
	private MaterialRestAPI materialRestAPI;
	private AppletParameters articleElement;

	/**
	 * @param beta
	 *            whether to use the beta site
	 * @param articleElement
	 *            parameters
	 */
	public GeoGebraTubeAPIWSimple(boolean beta,
			AppletParameters articleElement) {
		super(beta);
		this.articleElement = articleElement;
		if (!StringUtil.empty(articleElement.getMaterialsAPIurl())) {
			setURL(articleElement.getMaterialsAPIurl());
		}
		if (!StringUtil.empty(articleElement.getLoginAPIurl())) {
			setLoginURL(articleElement.getLoginAPIurl());
		}
	}

	@Override
	protected HttpRequest createHttpRequest() {
		return new HttpRequestW();
	}

	@Override
	public boolean parseUserDataFromResponse(GeoGebraTubeUser user,
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

	@Override
	protected MaterialRestAPI getMaterialRestAPI() {
		if (materialRestAPI == null) {
			String backendURL = articleElement.getParamBackendURL().isEmpty()
					? MaterialRestAPI.marvlUrl : articleElement.getParamBackendURL();
			Service service = "mebis".equals(articleElement.getParamVendor())
					? new MowService() : new MarvlService();

			materialRestAPI = new MaterialRestAPI(backendURL, service);
		}

		materialRestAPI.setClient(client);
		return materialRestAPI;
	}
}
