package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.CreateSliderAction;

public class CreateSliderItem extends MenuItem<GeoElement> {

	public CreateSliderItem(AlgebraProcessor processor) {
		super("Suggestion.CreateSlider", new CreateSliderAction(processor));
	}
}
