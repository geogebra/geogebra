package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.main.App;

/**
 * @author gabor
 * 
 * common superclass for ChiSquarePanel
 *
 */
public abstract class ChiSquarePanel {
	
	// ======================================
	// GeoGebra fields
	// ======================================
	protected App app;
	protected StatisticsCalculator statCalc;
	protected StatisticsCalculatorProcessor statProcessor;
	protected StatisticsCollection sc;
	
	/**
	 * @param app Application
	 * @param statcalc SatisticCalculator
	 */
	public ChiSquarePanel(App app, StatisticsCalculator statCalc) {
		this.app = app;
		this.statCalc = statCalc;
		this.statProcessor = statCalc.getStatProcessor();
		this.sc = statCalc.getStatististicsCollection();

		sc.setChiSqData(3, 3);
	}
		

		
		
}
