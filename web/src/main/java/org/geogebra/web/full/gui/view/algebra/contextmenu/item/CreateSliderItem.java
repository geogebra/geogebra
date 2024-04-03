package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.gui.view.algebra.contextmenu.impl.CreateSlider;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.gui.view.algebra.MenuItem;

public class CreateSliderItem extends MenuItem<GeoElement> {

	public CreateSliderItem(AlgebraProcessor processor) {
		super("Suggestion.CreateSlider", new CreateSlider(processor, new LabelController()));
	}
}
