package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Removes the slider layout.
 */
public class RemoveSliderAction extends MenuAction<GeoElement> {

	public RemoveSliderAction() {
		super("RemoveSlider");
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		if (!(geo instanceof GeoNumeric)) {
			return;
		}
		((GeoNumeric) geo).setShowExtendedAV(false);
		geo.getKernel().storeUndoInfo();
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return geo instanceof GeoNumeric && ((GeoNumeric) geo).isShowingExtendedAV();
	}
}
