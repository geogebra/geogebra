package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;

public class TableOfValuesAction implements MenuAction<GeoElement> {

	private final GuiManagerInterfaceW guiManager;

	public TableOfValuesAction(GuiManagerInterfaceW guiManager) {
		this.guiManager = guiManager;
	}

	@Override
	public void execute(GeoElement item) {
		guiManager.showTableValuesView(item);
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return item.hasTableOfValues();
	}
}
