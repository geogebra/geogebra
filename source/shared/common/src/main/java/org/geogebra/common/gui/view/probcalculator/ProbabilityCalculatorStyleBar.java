package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.gui.menubar.RoundingOptions;
import org.geogebra.common.main.App;

/**
 * Superclass for probability calculator stylebar
 * 
 * @author gabor
 */
public abstract class ProbabilityCalculatorStyleBar {

	private App app;

	/** probability calculator */
	private ProbabilityCalculatorView probCalc;
	private RoundingOptions roundingOptions;

	/**
	 * @param app
	 *            application
	 * @param probCalc
	 *            probability calculator
	 */
	protected ProbabilityCalculatorStyleBar(App app,
			ProbabilityCalculatorView probCalc) {
		this.probCalc = probCalc;
		this.app = app;
		this.setOptionsMenu(new RoundingOptions(app.getLocalization()));
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

	protected RoundingOptions getOptionsMenu() {
		return roundingOptions;
	}

	protected void setOptionsMenu(RoundingOptions roundingOptions) {
		this.roundingOptions = roundingOptions;
	}

}
