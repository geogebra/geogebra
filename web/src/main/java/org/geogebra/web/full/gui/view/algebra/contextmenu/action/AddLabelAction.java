package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Add label to a geo in AV
 */
public class AddLabelAction extends MenuAction<GeoElement> {
	/**
	 * New add label action
	 */
	public AddLabelAction() {
		super("AddLabel");
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		new LabelController().showLabel(geo);
		app.storeUndoInfo();
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return !geo.isAlgebraLabelVisible();
	}
}