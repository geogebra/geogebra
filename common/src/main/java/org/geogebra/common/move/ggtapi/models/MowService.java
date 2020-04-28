package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.debug.Log;

public class MowService implements Service {

	@Override
	public HttpRequest createRequest(AuthenticationModel model) {
		Log.debug("RESTART SESSION TIMER");
		model.getSessionExpireTimer().startRepeat();
		return UtilFactory.getPrototype().newHttpRequest();
	}
}
