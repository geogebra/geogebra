package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;
import java.util.Collection;

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

	@Override
	public Collection<ResourceAction> getActions(
			boolean owns, boolean isTeacher) {
		ArrayList<ResourceAction> actions = new ArrayList<>();
		if (owns) {
			if (isTeacher) {
				actions.add(ResourceAction.SHARE);
			}
			actions.add(ResourceAction.RENAME);
		}
		actions.add(ResourceAction.COPY);
		if (owns) {
			actions.add(ResourceAction.DELETE);
		}
		return actions;
	}

}
