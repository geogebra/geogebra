package org.geogebra.common.gui.view.algebra.contextmenu.impl;

import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.scientific.LabelController;

public class RemoveLabelAction implements MenuAction<GeoElement> {

	@Override
	public void execute(GeoElement item) {
		new LabelController().hideLabel(item);
		item.removeDependentAlgos();
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return item.isAlgebraLabelVisible() && !isAlgebraSlider(item);
	}

	private static boolean isAlgebraSlider(GeoElement geo) {
		return geo.isGeoNumeric() && ((GeoNumeric) geo).isSliderable()
				&& ((GeoNumeric) geo).isShowingExtendedAV();
	}
}
