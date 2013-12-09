package geogebra.common.gui.view.probcalculator;

import geogebra.common.main.App;

public abstract class ChiSquareCell {
	
	protected StatisticsCollection sc;
	protected static App app;
	protected static StatisticsCalculator statCalc;
	
	protected boolean isMarginCell = false;
	protected boolean isHeaderCell = false;

	protected int row, column;

	
	/**
	 * @param app App
	 * 
	 * setst the app
	 * 
	 */
	public void setApp(App app) {
		if (app == null) {
			this.app = app;
		}
	}
	
	public void setStatCalc(StatisticsCalculator statc) {
		if (statCalc == null) {
			this.statCalc = statc;
		}
	}

}
