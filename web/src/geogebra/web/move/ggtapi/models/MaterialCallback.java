package geogebra.web.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.Chapter;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.util.debug.Log;
import geogebra.html5.gui.tooltip.ToolTipManagerW;

import java.util.ArrayList;
import java.util.List;

public abstract class MaterialCallback {



	public void onError(Throwable exception) {
		Log.error("Tube API error:" + exception.getMessage());
		// TODO
		ToolTipManagerW.sharedInstance().showBottomMessage(
		        exception.getMessage(), true);
	}

	public void onLoaded(List<Material> result, ArrayList<Chapter> meta) {
		// onLoaded(result);

	}

}
