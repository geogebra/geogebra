package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.HttpRequest;

public class MarvlService implements Service {

	@Override
	public HttpRequest createRequest(AuthenticationModel model) {
		HttpRequest httpRequest = UtilFactory.getPrototype().newHttpRequest();
		if (model != null) {
			httpRequest.setAuth(model.getEncoded());
		}
		return httpRequest;
	}

	@Override
	public String getDeletionJson(Material.MaterialType materialType) {
		return materialType == Material.MaterialType.ggsTemplate ? null : "{\"deleted\":true}";
	}
}
