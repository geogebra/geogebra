package org.geogebra.common.gui.view.algebra.contextmenu.impl;

import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.AsyncOperation;

/** Removes existing sliders */
public class RemoveSlider implements MenuAction<GeoElement>, AsyncOperation<GeoElementND> {

	private final AlgebraProcessor processor;

	/**
	 * Create a new instance of RemoveSlider
	 * @param processor processor
	 */
	public RemoveSlider(AlgebraProcessor processor) {
		this.processor = processor;
	}

	@Override
	public boolean isAvailable(GeoElement element) {
		if (element instanceof GeoNumeric) {
			GeoNumeric numeric = (GeoNumeric) element;
			return !numeric.isDependentConst();
		}
		return false;
	}

	@Override
	public void execute(GeoElement element) {
		String newValue = element.toString(StringTemplate.defaultTemplate);
		EvalInfo info = new EvalInfo(false).withSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		processor.changeGeoElementNoExceptionHandling(element, newValue, info, false, this,
				ErrorHelper.silent());
	}

	@Override
	public void callback(GeoElementND element) {
		element.updateRepaint();
		element.getKernel().storeUndoInfo();
	}
}
