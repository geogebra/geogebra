package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.debug.Log;

public class MarvlService implements Service {

	@Override
	public HttpRequest createRequest(AuthenticationModel model) {
		HttpRequest httpRequest = UtilFactory.getPrototype().newHttpRequest();
		Log.debug("setting token to " + model.getEncoded());
		httpRequest.setAuth(model.getEncoded());
		return httpRequest;
	}
}
