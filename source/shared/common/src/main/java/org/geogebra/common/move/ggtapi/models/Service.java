package org.geogebra.common.move.ggtapi.models;

import java.util.Collection;

import org.geogebra.common.move.ggtapi.GroupIdentifier;
import org.geogebra.common.util.HttpRequest;

/**
 * Backend service providing access to ggb/ggs resources.
 */
public interface Service {

	/**
	 * Create an authenticated request.
	 * @param model authentication model
	 * @return HTTP request
	 */
	HttpRequest createRequest(AuthenticationModel model);

	/**
	 * If deletion for the material type is done via PATCH, return the patch JSON.
	 * Return null otherwise.
	 * @param materialType material type
	 * @return JSON for delete request
	 */
	String getDeletionJson(Material.MaterialType materialType);

	/**
	 * @param userId current user ID
	 * @return endpoint for loading GGS templates
	 */
	String getGgsTemplateEndpoint(int userId);

	/**
	 * Get available actions for a resource.
	 * @param owns whether current user owns it
	 * @param isTeacher whether current user is a teacher
	 * @return available actions
	 */
	Collection<ResourceAction> getActions(boolean owns, boolean isTeacher);

	/**
	 * Get endpoint for group sharing info.
	 * @param materialId resource ID
	 * @param category group category
	 * @return endpoint listing group sharing info for given resource
	 */
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

	/**
	 * @return additional URL parameters for search endpoint
	 */
	String getSearchMaterialFilter();
}
