package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

public class TableOfValuesAction extends DefaultMenuAction<GeoElement> {

	@Override
	public void execute(GeoElement item, AppWFull app) {
		app.getGuiManager().showTableValuesView(item);
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return item.hasTableOfValues();
	}
}
