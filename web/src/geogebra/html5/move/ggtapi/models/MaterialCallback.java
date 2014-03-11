package geogebra.html5.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.util.debug.Log;

import java.util.List;

public abstract class MaterialCallback {

	public abstract void onLoaded(List<Material> parseResponse);

	public void onError(Throwable exception) {
	    Log.error(exception.getMessage());
    }

}
