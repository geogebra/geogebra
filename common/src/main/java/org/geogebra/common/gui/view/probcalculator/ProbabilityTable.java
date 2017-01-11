package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import org.geogebra.common.util.Unicode;

/**
 * @author gabor
 * 
 *         superclass for probability table
 *
 */
public abstract class ProbabilityTable {

	/**
	 * Application
	 */
	private App app;
	private ProbabilityManager probManager;
	private ProbabilityCalculatorView probCalc;
	private String[] columnNames;

	private DIST distType;
	private int xMin;
	private int xMax;
	private boolean isIniting;
	private double[] parms;

	public ProbabilityTable(App app2, ProbabilityCalculatorView probCalc2) {
		this.app = app2;
		this.probCalc = probCalc2;
		this.probManager = probCalc.getProbManager();
	}

	/**
	 * @param lowValue
	 *            lowest value
	 * @param highValue
	 *            highest value
	 */
	public abstract void setSelectionByRowValue(int lowValue, int highValue);

	public void setColumnNames() {
		Localization loc = app.getLocalization();
		columnNames = new String[2];
		columnNames[0] = "k";
		if (isCumulative()) {
			columnNames[1] = loc.getMenu("ProbabilityOf") + "X "
					+ Unicode.LESS_EQUAL + " k"
					+ loc.getMenu("EndProbabilityOf");
		} else {
			columnNames[1] = loc.getMenu("ProbabilityOf") + "X = k"
					+ loc.getMenu("EndProbabilityOf");
		}

	}

	protected boolean isCumulative() {
		return probCalc.isCumulative();
	}

	public final void setLabels() {
		setTable(distType, parms, xMin, xMax);
	}

	public abstract void setTable(DIST distType2, double[] parms2, int xMin2,
			int xMax2);

	protected ProbabilityManager getProbManager() {
		return probManager;
	}

	protected ProbabilityCalculatorView getProbCalc() {
		return probCalc;
	}

	protected App getApp() {
		return app;
	}

	protected void setApp(App app) {
		this.app = app;
	}

	protected DIST getDistType() {
		return distType;
	}

	protected void setDistType(DIST distType) {
		this.distType = distType;
	}

	protected int getXMin() {
		return xMin;
	}

	protected double[] getParms() {
		return parms;
	}

	protected boolean isIniting() {
		return isIniting;
	}

	protected void setIniting(boolean isIniting) {
		this.isIniting = isIniting;
	}

	protected int getxMax() {
		return xMax;
	}

	protected String[] getColumnNames() {
		return columnNames;
	}

	protected void setTableModel(DIST distType1, double[] parms1, int xMin1,
			int xMax1) {
		this.distType = distType1;
		this.xMin = xMin1;
		this.xMax = xMax1;
		this.parms = parms1;
		setColumnNames();

	}

}
