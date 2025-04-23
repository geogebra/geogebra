package org.geogebra.common.move.ggtapi.requests;

import java.util.List;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Pagination;

/**
 * Callback for resource-related REST APIs.
 */
public interface MaterialCallbackI {

	void onLoaded(List<Material> result, Pagination meta);

	void onError(Throwable exception);

}
