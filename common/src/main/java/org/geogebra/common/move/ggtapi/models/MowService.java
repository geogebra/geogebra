package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.HttpRequest;

public class MowService implements Service {

	@Override
	public HttpRequest createRequest(AuthenticationModel model) {
		model.restartSession();
		return UtilFactory.getPrototype().newHttpRequest();
	}

	@Override
	public String getDeletionJson(Material.MaterialType materialType) {
		return null;
	}

	@Override
	public String getGgsTemplateEndpoint(int userId) {
		return "/users/" + userId
				+ "/materials?format=page&filter=ggs-template";
	}
}
