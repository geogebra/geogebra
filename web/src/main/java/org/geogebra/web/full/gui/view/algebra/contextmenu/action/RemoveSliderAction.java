package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Removes the slider layout.
 */
public class RemoveSliderAction extends DefaultMenuAction<GeoElement> {

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		if (!(geo instanceof GeoNumeric)) {
			return;
		}
		((GeoNumeric) geo).removeSlider();
		geo.getKernel().notifyRepaint();
		geo.getKernel().storeUndoInfo();
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return geo instanceof GeoNumeric && ((GeoNumeric) geo).isShowingExtendedAV();
	}
}
