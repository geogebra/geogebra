package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoDistribution;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;

import com.google.j2objc.annotations.AutoreleasePool;
import com.himamis.retex.editor.share.util.Unicode;

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

	private Dist distType;
	private int xMin;
	private int xMax;
	private boolean isIniting;
	private GeoNumberValue[] params;

	/**
	 * @param app
	 *            application
	 * @param probCalc
	 *            probability calculator view
	 */
	public ProbabilityTable(App app, ProbabilityCalculatorView probCalc) {
		this.app = app;
		this.probCalc = probCalc;
		this.probManager = probCalc.getProbManager();
	}

	/**
	 * @param lowValue
	 *            lowest value
	 * @param highValue
	 *            highest value
	 */
	public abstract void setSelectionByRowValue(int lowValue, int highValue);

	/**
	 * Select rows 0 - lowValue and highValue to the last one.
	 *
	 * @param lowValue
	 *            lowest value
	 * @param highValue
	 *            highest value
	 */
	public abstract void setTwoTailedSelection(int lowValue, int highValue);

	/**
	 * Update localized column names
	 */
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
		setTable(distType, params, xMin, xMax);
	}

	public abstract void setTable(Dist distType2, GeoNumberValue[] params, int xMin2,
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

	protected Dist getDistType() {
		return distType;
	}

	protected void setDistType(Dist distType) {
		this.distType = distType;
	}

	protected int getXMin() {
		return xMin;
	}

	protected GeoNumberValue[] getParams() {
		return params;
	}

	protected boolean isIniting() {
		return isIniting;
	}

	protected void setIniting(boolean isIniting) {
		this.isIniting = isIniting;
	}

	/**
	 * @return column names
	 */
	protected String[] getColumnNames() {
		return columnNames;
	}

	protected void setTableModel(Dist distType1, GeoNumberValue[] params1, int xMin1,
			int xMax1) {
		this.distType = distType1;
		this.xMin = xMin1;
		this.xMax = xMax1;
		this.params = params1;
		setColumnNames();
	}

	protected void fillRows(ProbabilityCalculatorSettings.Dist distType,
			GeoNumberValue[] params, int xMin, int xMax) {
		if (distType == null) {
			return;
		}
		GeoNumeric xValue = new GeoNumeric(app.getKernel().getConstruction(), xMin);
		AlgoDistribution algoDistribution = getProbManager().getDistributionAlgorithm(xValue,
				this.params, distType, isCumulative());

		for (@AutoreleasePool int x = xMin; x <= xMax; x++) {
			xValue.setValue(x);
			algoDistribution.compute();

			double prob = algoDistribution.getResult().getDouble();
			setRowValues(x - xMin, "" + x, "" + getProbCalc().format(prob));
		}
	}

	protected abstract void setRowValues(int row, String k, String prob);

}
