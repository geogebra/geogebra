package geogebra.common.gui.view.probcalculator;

import geogebra.common.main.App;

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

}
