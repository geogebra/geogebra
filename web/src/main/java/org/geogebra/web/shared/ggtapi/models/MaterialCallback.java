package org.geogebra.web.shared.ggtapi.models;

import java.util.List;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.debug.Log;

/**
 * Callback for Tube API material load.
 */
public abstract class MaterialCallback implements MaterialCallbackI {

	@Override
	public void onError(Throwable exception) {
		Log.error("Tube API error:" + exception.getMessage());
	}

	@Override
	public void onLoaded(List<Material> result, Pagination meta) {
		// onLoaded(result);
	}

}
