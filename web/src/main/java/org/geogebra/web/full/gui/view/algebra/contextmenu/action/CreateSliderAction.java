package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

public class CreateSliderAction extends MenuAction<GeoElement> {

	public CreateSliderAction() {
		super("CreateSlider");
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		if (geo instanceof GeoSymbolic) {
			return ((GeoSymbolic) geo).canBecomeSlider();
		}
		return AlgebraItem.shouldShowSlider(geo);
	}
}
