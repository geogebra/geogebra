package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Removes label from a a geo in AV
 */
public class RemoveLabelAction extends MenuAction<GeoElement> {
	/**
	 * New remove label action
	 */
	public RemoveLabelAction() {
		super("RemoveLabel");
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		new LabelController().hideLabel(geo);
		geo.removeDependentAlgos();
		app.storeUndoInfo();
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return geo.isAlgebraLabelVisible();
	}
}