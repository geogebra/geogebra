package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Adds a functionable geo to the table view
 */
public class TableOfValuesAction extends MenuAction<GeoElement> {
	/**
	 * New table view action
	 */
	public TableOfValuesAction() {
		super("TableOfValues");
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		app.getGuiManager().showTableValuesView(geo);
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return geo.hasTableOfValues();
	}
}