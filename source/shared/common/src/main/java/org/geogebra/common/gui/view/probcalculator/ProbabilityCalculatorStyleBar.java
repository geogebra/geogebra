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

package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.gui.menubar.RoundingOptions;
import org.geogebra.common.main.App;

/**
 * Superclass for probability calculator stylebar
 * 
 * @author gabor
 */
public class ProbabilityCalculatorStyleBar {

	private final App app;

	/** probability calculator */
	private final ProbabilityCalculatorView probCalc;
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
