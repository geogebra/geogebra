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
	protected ProbabilityManager probManager;
	protected ProbabilityCalcualtorView probCalc;
	protected String[] columnNames;

	/**
	 * @param lowValue lowest value
	 * @param highValue highest value
	 */
	public abstract void setSelectionByRowValue(int lowValue, int highValue);

	public void setColumnNames() {
	
		columnNames = new String[2];
		columnNames[0] = "k";
		if(isCumulative())
			columnNames[1] = app.getMenu("ProbabilityOf") + "X ≤ k" + app.getMenu("EndProbabilityOf");
		else
			columnNames[1] = app.getMenu("ProbabilityOf") + "X = k" + app.getMenu("EndProbabilityOf");
		
	}

	protected boolean isCumulative() {
		return probCalc.isCumulative();
	}

}
