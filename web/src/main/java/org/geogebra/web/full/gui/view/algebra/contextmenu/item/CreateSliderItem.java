package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.CreateSliderAction;

public class CreateSliderItem extends MenuItem<GeoElement> {

	public CreateSliderItem() {
		super("Suggestion.CreateSlider", new CreateSliderAction());
	}
}
