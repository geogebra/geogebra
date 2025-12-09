/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
		// asymmetric with CreateSlider: no need for isSimple check, we have a slider already
		if (element instanceof GeoNumeric) {
			GeoNumeric numeric = (GeoNumeric) element;
			return !numeric.isDependentConst() && numeric.isAVSliderOrCheckboxVisible();
		}
		return false;
	}

	@Override
	public void execute(GeoElement element) {
		if (processor.getKernel().getSymbolicMode() == SymbolicMode.NONE) {
			((GeoNumeric) element).removeSlider();
			processor.getKernel().notifyRepaint();
			return;
		}
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
