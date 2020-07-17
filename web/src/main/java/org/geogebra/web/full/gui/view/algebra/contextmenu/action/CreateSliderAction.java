package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.contextmenu.impl.CreateSlider;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Creates a slider.
 */
public class CreateSliderAction extends DefaultMenuAction<GeoElement> {

	private final CreateSlider createSlider;

	/**
	 * Default constructor
	 * @param processor processor
	 */
	public CreateSliderAction(AlgebraProcessor processor) {
		LabelController labelController = new LabelController();
		createSlider = new CreateSlider(processor, labelController);
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		createSlider.execute(geo);
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return createSlider.isAvailable(geo);
	}
}
