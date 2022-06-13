package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;
import java.util.Collection;

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

	@Override
	public String getGgsTemplateEndpoint(int userId) {
		return "/users/" + userId + "/materials?filter=ggs-template";
	}

	@Override
	public Collection<ResourceAction> getActions(
			boolean owns, boolean isTeacher) {
		ArrayList<ResourceAction> actions = new ArrayList<>();
		actions.add(ResourceAction.EDIT);
		actions.add(ResourceAction.VIEW);
		if (owns) {
			actions.add(ResourceAction.RENAME);
			actions.add(ResourceAction.DELETE);
		}
		return actions;
	}
}
