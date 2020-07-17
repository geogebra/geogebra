package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.RemoveSliderAction;

public class RemoveSliderItem extends MenuItem<GeoElement> {

	public RemoveSliderItem(AlgebraProcessor processor) {
		super("RemoveSlider", new RemoveSliderAction(processor));
	}
}
