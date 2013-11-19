package geogebra.common.gui.view.probcalculator;

import geogebra.common.main.App;

/**
 * @author gabor
 * 
 * superclass for probability table
 *
 */
public abstract class ProbabilityTable {
	
	/**
	 * Application
	 */
	protected App app;

	/**
	 * @param lowValue lowest value
	 * @param highValue highest value
	 */
	public abstract void setSelectionByRowValue(int lowValue, int highValue);

}
