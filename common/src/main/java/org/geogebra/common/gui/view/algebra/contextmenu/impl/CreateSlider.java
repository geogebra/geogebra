package org.geogebra.common.gui.view.algebra.contextmenu.impl;

import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.AsyncOperation;

/** Creates sliders from simple GeoSymbolics */
public class CreateSlider implements MenuAction<GeoElement>,
		AsyncOperation<GeoElementND> {

	private final AlgebraProcessor processor;
	private final LabelController labelController;

	/**
	 * Create a new instance of CreateSlider
	 * @param processor processor
	 * @param labelController label controller
	 */
	public CreateSlider(AlgebraProcessor processor,
			LabelController labelController) {
		this.processor = processor;
		this.labelController = labelController;
	}

	@Override
	public boolean isAvailable(GeoElement element) {
		if (element instanceof GeoSymbolic) {
			GeoSymbolic symbolic = (GeoSymbolic) element;
			return symbolic.getDefinition().isSimpleNumber();
		}
		return false;
	}

	@Override
	public void execute(GeoElement element) {
		GeoSymbolic symbolic = (GeoSymbolic) element;
		symbolic.setEuclidianVisible(false);
		processor.changeGeoElementNoExceptionHandling(symbolic, symbolic.getDefinition(),
				new EvalInfo(false).withKeepDefinition(false), false, this, ErrorHelper.silent());
	}

	@Override
	public void callback(GeoElementND result) {
		((GeoNumeric) result).createSlider();
		labelController.ensureHasLabel(result);
		result.setEuclidianVisible(true);
		result.getKernel().storeUndoInfo();
		result.updateRepaint();
	}
}
