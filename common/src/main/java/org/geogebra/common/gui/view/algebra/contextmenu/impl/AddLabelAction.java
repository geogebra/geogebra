package org.geogebra.common.gui.view.algebra.contextmenu.impl;

import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;

public class AddLabelAction implements MenuAction<GeoElement> {

	@Override
	public void execute(GeoElement item) {
		new LabelController().showLabel(item);
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return !item.isAlgebraLabelVisible();
	}
}
