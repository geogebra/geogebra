package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.main.App;

/**
 * @author gabor
 * 
 *         Superclass for probability calculator stylebar
 *
 */
public abstract class ProbabilityCalculatorStyleBar {

	private App app;

	/** probabililty calculator */
	private ProbabilityCalculatorView probCalc;

	protected ProbabilityCalculatorStyleBar(App app,
			ProbabilityCalculatorView probCalc) {
		this.probCalc = probCalc;
		this.app = app;
	}
	protected App getApp() {
		return app;
	}



	/**
	 * @return probability calculator
	 */
	public ProbabilityCalculatorView getProbCalc() {
		return probCalc;
	}


}
