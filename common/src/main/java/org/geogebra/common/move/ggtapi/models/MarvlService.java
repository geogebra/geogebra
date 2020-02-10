package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.HttpRequest;

public class MarvlService implements Service {

	@Override
	public HttpRequest createRequest(AuthenticationModel model) {
		HttpRequest httpRequest = UtilFactory.getPrototype().newHttpRequest();
		httpRequest.setAuth(model.getLoginToken());
		return httpRequest;
	}
}
