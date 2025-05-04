package org.geogebra.common.move.ggtapi.requests;

import java.util.List;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Pagination;

/**
 * Callback for resource-related REST APIs.
 */
public interface MaterialCallbackI {

	/**
	 * Materials parsed from API call.
	 * @param result materials
	 * @param meta pagination
	 */
	void onLoaded(List<Material> result, Pagination meta);

	/**
	 * @param exception exception
	 */
	void onError(Throwable exception);

}
