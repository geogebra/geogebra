package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.main.App;

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
	private App app;
	private StatisticsCalculator statCalc;
	private StatisticsCalculatorProcessor statProcessor;
	private StatisticsCollection sc;

	/**
	 * @param app
	 *            Application
	 * @param statCalc
	 *            SatisticCalculator
	 */
	public ChiSquarePanel(App app, StatisticsCalculator statCalc) {
		this.app = app;
		this.statCalc = statCalc;
		this.statProcessor = statCalc.getStatProcessor();
		this.sc = statCalc.getStatististicsCollection();

		sc.setChiSqData(3, 3);
	}

	public App getApp() {
		return app;
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
