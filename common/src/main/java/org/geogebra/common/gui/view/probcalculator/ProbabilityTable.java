package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import org.geogebra.common.util.Unicode;

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
	protected ProbabilityCalculatorView probCalc;
	protected String[] columnNames;
	
	protected DIST distType;
	protected int xMin, xMax;
	protected boolean isIniting;
	protected double[] parms;

	/**
	 * @param lowValue lowest value
	 * @param highValue highest value
	 */
	public abstract void setSelectionByRowValue(int lowValue, int highValue);

	public void setColumnNames() {
		Localization loc = app.getLocalization();
		columnNames = new String[2];
		columnNames[0] = "k";
		if(isCumulative())
			columnNames[1] = loc.getMenu("ProbabilityOf") + "X "
					+ Unicode.LESS_EQUAL + " k"
					+ loc.getMenu("EndProbabilityOf");
		else
			columnNames[1] = loc.getMenu("ProbabilityOf") + "X = k"
					+ loc.getMenu("EndProbabilityOf");
		
	}

	protected boolean isCumulative() {
		return probCalc.isCumulative();
	}

}
