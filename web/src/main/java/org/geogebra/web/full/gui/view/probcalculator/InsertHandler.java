package org.geogebra.web.full.gui.view.probcalculator;

public interface InsertHandler {

	/**
	 * @param source changed source
	 * @param intervalCheck true if triggered by enter/blur
	 */
	void doTextFieldActionPerformed(MathTextFieldW source, boolean intervalCheck);
}
