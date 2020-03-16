package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Deletes a geo
 */
public class DeleteAction extends MenuAction<GeoElement> {
	/**
	 * New delete action
	 */
	public DeleteAction() {
		super("Delete", MaterialDesignResources.INSTANCE.delete_black());
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		geo.remove();
		app.storeUndoInfo();
	}
}