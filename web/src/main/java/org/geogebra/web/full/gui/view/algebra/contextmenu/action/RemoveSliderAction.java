package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.contextmenu.impl.RemoveSlider;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Removes the slider layout.
 */
public class RemoveSliderAction extends DefaultMenuAction<GeoElement> {

	private final RemoveSlider removeSlider;

	public RemoveSliderAction(AlgebraProcessor processor) {
		removeSlider = new RemoveSlider(processor);
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		removeSlider.execute(geo);
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return removeSlider.isAvailable(geo);
	}
}
