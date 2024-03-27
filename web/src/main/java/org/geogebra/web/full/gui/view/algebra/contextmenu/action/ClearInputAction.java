package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;

public class ClearInputAction extends DefaultMenuAction<GeoElement> {

	private RadioTreeItem inputItem;

	public ClearInputAction(RadioTreeItem inputItem) {
		this.inputItem = inputItem;
	}

	@Override
	public void execute(GeoElement item) {
		inputItem.onClear();
	}
}
