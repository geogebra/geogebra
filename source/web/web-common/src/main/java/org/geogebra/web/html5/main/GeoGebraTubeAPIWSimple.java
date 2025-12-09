/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.main;

import org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.MarvlService;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.models.Service;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.HttpRequestW;

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
	public boolean parseUserDataFromResponse(GeoGebraTubeUser user, String response) {
		return false;
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
			Service service = new MarvlService();

			materialRestAPI = new MaterialRestAPI(backendURL, service);
		}

		materialRestAPI.setClient(client);
		return materialRestAPI;
	}
}
