package org.geogebra.common.move.ggtapi.models;

import java.util.Collection;

import org.geogebra.common.move.ggtapi.GroupIdentifier;
import org.geogebra.common.util.HttpRequest;

public interface Service {

	HttpRequest createRequest(AuthenticationModel model);

	String getDeletionJson(Material.MaterialType materialType);

	String getGgsTemplateEndpoint(int userId);

	Collection<ResourceAction> getActions(boolean owns, boolean isTeacher);

	String getGroupsEndpoint(String materialId, GroupIdentifier.GroupCategory category);

	default boolean hasMultiuser() {
		return false;
	}

	default boolean requiresCSRF() {
		return false;
	}

	String getSearchMaterialFilter();
}
