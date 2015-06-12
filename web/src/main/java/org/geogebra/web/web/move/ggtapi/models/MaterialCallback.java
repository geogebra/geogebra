package org.geogebra.web.web.move.ggtapi.models;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;

public abstract class MaterialCallback {



	public void onError(Throwable exception) {
		Log.error("Tube API error:" + exception.getMessage());
		// TODO
		ToolTipManagerW.sharedInstance().showBottomMessage(
				exception.getMessage(), true, null);
	}

	public void onLoaded(List<Material> result, ArrayList<Chapter> meta) {
		// onLoaded(result);

	}

}
