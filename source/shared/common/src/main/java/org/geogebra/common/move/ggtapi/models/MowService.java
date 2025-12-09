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

package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.move.ggtapi.GroupIdentifier;
import org.geogebra.common.util.HttpRequest;

public class MowService implements Service {

	@Override
	public HttpRequest createRequest(AuthenticationModel model) {
		model.restartSession();
		HttpRequest httpRequest = UtilFactory.getPrototype().newHttpRequest();
		httpRequest.setAuth(model.getLoginToken());
		return httpRequest;
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

	@Override
	public String getGroupsEndpoint(String materialId, GroupIdentifier.GroupCategory category) {
		String path = "/materials/" + materialId + "/groups?type=isShared";
		if (category != null) {
			path += "&category=" + category.name().toLowerCase(Locale.ROOT);
		}
		return path;
	}

	@Override
	public boolean hasMultiuser() {
		return true;
	}

	@Override
	public boolean requiresCSRF() {
		return true;
	}

	@Override
	public String getSearchMaterialFilter() {
		return "";
	}

}
