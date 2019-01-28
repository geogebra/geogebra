package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Removes label from a ageo in AV
 */
public class RemoveLabelAction extends MenuAction {
	/**
	 * New remove label action
	 */
	public RemoveLabelAction() {
		super("RemoveLabel", MaterialDesignResources.INSTANCE.label_off());
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