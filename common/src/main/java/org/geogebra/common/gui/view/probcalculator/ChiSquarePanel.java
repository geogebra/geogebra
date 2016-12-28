package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.main.Localization;

/**
 * @author gabor
 * 
 *         common superclass for ChiSquarePanel
 *
 */
public abstract class ChiSquarePanel {

	// ======================================
	// GeoGebra fields
	// ======================================
	protected Localization loc;
	protected StatisticsCalculator statCalc;
	private StatisticsCalculatorProcessor statProcessor;
	private StatisticsCollection sc;

	/**
	 * @param app
	 *            Application
	 * @param statCalc
	 *            SatisticCalculator
	 */
	public ChiSquarePanel(Localization loc, StatisticsCalculator statCalc) {
		this.loc = loc;
		this.statCalc = statCalc;
		this.statProcessor = statCalc.getStatProcessor();
		this.sc = statCalc.getStatististicsCollection();

		sc.setChiSqData(3, 3);
	}


	public StatisticsCalculator getStatCalc() {
		return statCalc;
	}


	public StatisticsCalculatorProcessor getStatProcessor() {
		return statProcessor;
	}

	public StatisticsCollection getSc() {
		return sc;
	}



}
