package org.geogebra.common.move.ggtapi.models;

import java.util.Collection;

import org.geogebra.common.move.ggtapi.GroupIdentifier;
import org.geogebra.common.util.HttpRequest;

/**
 * Backend service providing access to ggb/ggs resources.
 */
public interface Service {

	HttpRequest createRequest(AuthenticationModel model);

	String getDeletionJson(Material.MaterialType materialType);

	String getGgsTemplateEndpoint(int userId);

	Collection<ResourceAction> getActions(boolean owns, boolean isTeacher);

	String getGroupsEndpoint(String materialId, GroupIdentifier.GroupCategory category);

	/**
	 * @return whether multiuser is supported.
	 */
	default boolean hasMultiuser() {
		return false;
	}

	/**
	 * @return whether CSRF is required.
	 */
	default boolean requiresCSRF() {
		return false;
	}

	String getSearchMaterialFilter();
}
