package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Creates a slider.
 */
public class CreateSliderAction extends DefaultMenuAction<GeoElement> {

	private LabelController labelController;

	/**
	 * Default constructor
	 */
	public CreateSliderAction() {
		labelController = new LabelController();
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		if (!(geo instanceof GeoNumeric)) {
			return;
		}
		labelController.ensureHasLabel(geo);
		((GeoNumeric) geo).createSlider();
		geo.getKernel().storeUndoInfo();
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return geo instanceof GeoNumeric && !((GeoNumeric) geo).isShowingExtendedAV();
	}
}
