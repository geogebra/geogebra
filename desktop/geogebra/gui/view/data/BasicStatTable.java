package geogebra.gui.view.data;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.advanced.AlgoListMax;
import geogebra.common.kernel.advanced.AlgoListMin;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoListLength;
import geogebra.common.kernel.algos.AlgoSum;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.statistics.AlgoListMeanX;
import geogebra.common.kernel.statistics.AlgoListMeanY;
import geogebra.common.kernel.statistics.AlgoListPMCC;
import geogebra.common.kernel.statistics.AlgoListSXX;
import geogebra.common.kernel.statistics.AlgoListSXY;
import geogebra.common.kernel.statistics.AlgoListSYY;
import geogebra.common.kernel.statistics.AlgoListSampleSDX;
import geogebra.common.kernel.statistics.AlgoListSampleSDY;
import geogebra.common.kernel.statistics.AlgoMean;
import geogebra.common.kernel.statistics.AlgoMedian;
import geogebra.common.kernel.statistics.AlgoQ1;
import geogebra.common.kernel.statistics.AlgoQ3;
import geogebra.common.kernel.statistics.AlgoRSquare;
import geogebra.common.kernel.statistics.AlgoSampleStandardDeviation;
import geogebra.common.kernel.statistics.AlgoSigmaXX;
import geogebra.common.kernel.statistics.AlgoSpearman;
import geogebra.common.kernel.statistics.AlgoStandardDeviation;
import geogebra.common.kernel.statistics.AlgoSumSquaredErrors;
import geogebra.gui.view.data.DataAnalysisViewD.Regression;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

public class BasicStatTable extends JPanel implements StatPanelInterface {
	private static final long serialVersionUID = 1L;
	// ggb
	protected AppD app;
	private Kernel kernel;
	protected DataAnalysisViewD statDialog;
	private int mode;
	protected StatTable statTable;
	private String[][] statMap;

	/*************************************************
	 * Construct the panel
	 */
	public BasicStatTable(AppD app, DataAnalysisViewD statDialog, int mode) {

		this.app = app;
		this.kernel = app.getKernel();
		this.statDialog = statDialog;
		this.mode = mode;
		this.setLayout(new BorderLayout());
		initStatTable();
		updateFonts(app.getPlainFont());

	} // END constructor

	private void initStatTable() {
		statMap = getStatMap();

		statTable = new StatTable(app);
		statTable.setStatTable(getRowCount(), getRowNames(), getColumnCount(),
				getColumnNames());
		this.removeAll();
		this.add(statTable, BorderLayout.CENTER);
	}

	// =======================================================
	// override theses classes

	public String[] getRowNames() {
		statMap = getStatMap();
		String[] rowNames = new String[statMap.length];
		for (int i = 0; i < statMap.length; i++) {
			rowNames[i] = statMap[i][0];
		}
		return rowNames;
	}

	public String[] getColumnNames() {
		return null;
	}

	public int getRowCount() {
		return getRowNames().length;
	}

	public int getColumnCount() {
		return 1;
	}

	// =======================================================

	private String[][] getStatMap() {
		if (mode == DataAnalysisViewD.MODE_ONEVAR) {
			return createOneVarStatMap();
		}
		return createTwoVarStatMap();
	}

	private String[][] createOneVarStatMap() {

		String[][] statMap1 = { { app.getMenu("Length.short"), "Length" },
				{ app.getMenu("Mean"), "Mean" },
				{ app.getMenu("StandardDeviation.short"), "SD" },
				{ app.getMenu("SampleStandardDeviation.short"), "SampleSD" },
				{ app.getMenu("Sum"), "Sum" },
				{ app.getMenu("Sum2"), "SigmaXX" }, { null, null },
				{ app.getMenu("Minimum.short"), "Min" },
				{ app.getMenu("LowerQuartile.short"), "Q1" },
				{ app.getMenu("Median"), "Median" },
				{ app.getMenu("UpperQuartile.short"), "Q3" },
				{ app.getMenu("Maximum.short"), "Max" }, };

		return statMap1;
	}

	private String[][] createTwoVarStatMap() {

		String[][] statMap2 = {
				{ app.getMenu("Length.short"), "Length" },
				{ app.getMenu("MeanX"), "MeanX" },
				{ app.getMenu("MeanY"), "MeanY" },
				{ app.getMenu("Sx"), "SampleSDX" },
				{ app.getMenu("Sy"), "SampleSDY" },
				{ app.getMenu("CorrelationCoefficient.short"), "PMCC" },
				{ app.getMenu("Spearman.short"), "Spearman" },
				{ app.getMenu("Sxx"), "SXX" },
				{ app.getMenu("Syy"), "SYY" },
				{ app.getMenu("Sxy"), "SXY" },
				{ null, null },
				{ app.getMenu("RSquare.Short"), "RSquare", "regression" },
				{ app.getMenu("SumSquaredErrors.short"), "SumSquaredErrors",
						"regression" } };

		return statMap2;
	}

	/**
	 * Evaluates all statistics for the selected data list. If data source is
	 * not valid, the result cells are set blank.
	 * 
	 */
	public void updatePanel() {
		// App.printStacktrace("update stat panel");
		GeoList dataList = statDialog.getController()
				.getDataSelected();

		GeoElement geoRegression = statDialog.getRegressionModel();
		// when the regression mode is NONE geoRegression is a dummy linear
		// model, so reset it to null
		if (statDialog.getRegressionMode().equals(Regression.NONE)) {
			geoRegression = null;
		}

		DefaultTableModel model = statTable.getModel();
		double value;

		for (int row = 0; row < statMap.length; row++) {
			for (int column = 0; column < 1; column++) {
				if (statMap[row].length == 2) {
					if (statMap[row][1] != null
							&& statDialog.getController()
									.isValidData()) {
						AlgoElement algo = getStatMapAlgo(statMap[row][1],
								dataList, geoRegression);
						kernel.getConstruction().removeFromConstructionList(
								algo);
						value = ((GeoNumeric) algo.getGeoElements()[0])
								.getDouble();
						model.setValueAt(statDialog.format(value), row, 0);
					} else {
						model.setValueAt("", row, 0);
					}
				} else if (statMap[row].length == 3) {
					if (statMap[row][1] != null
							&& geoRegression != null
							&& statDialog.getController()
									.isValidData()) {
						AlgoElement algo = getStatMapAlgo(statMap[row][1],
								dataList, geoRegression);
						kernel.getConstruction().removeFromConstructionList(
								algo);
						value = ((GeoNumeric) algo.getGeoElements()[0])
								.getDouble();
						model.setValueAt(statDialog.format(value), row, 0);
					} else {
						model.setValueAt("", row, 0);
					}
				}
			}
		}

	}

	protected AlgoElement getStatMapAlgo(String algoName, GeoList dataList,
			GeoElement geoRegression) {
		AlgoElement ret = null;
		Construction cons = kernel.getConstruction();

		if (algoName.equals("Length")) {
			ret = new AlgoListLength(cons, dataList);
		} else if (algoName.equals("Mean")) {
			ret = new AlgoMean(cons, dataList);
		} else if (algoName.equals("SD")) {
			ret = new AlgoStandardDeviation(cons, dataList);
		} else if (algoName.equals("SampleSD")) {
			ret = new AlgoSampleStandardDeviation(cons, dataList);
		} else if (algoName.equals("Sum")) {
			ret = new AlgoSum(cons, dataList);
		} else if (algoName.equals("SigmaXX")) {
			ret = new AlgoSigmaXX(cons, dataList);
		} else if (algoName.equals("Min")) {
			ret = new AlgoListMin(cons, dataList);
		} else if (algoName.equals("Q1")) {
			ret = new AlgoQ1(cons, dataList);
		} else if (algoName.equals("Median")) {
			ret = new AlgoMedian(cons, dataList);
		} else if (algoName.equals("Q3")) {
			ret = new AlgoQ3(cons, dataList);
		} else if (algoName.equals("Max")) {
			ret = new AlgoListMax(cons, dataList);
		} else if (algoName.equals("MeanX")) {
			ret = new AlgoListMeanX(cons, dataList);
		} else if (algoName.equals("MeanY")) {
			ret = new AlgoListMeanY(cons, dataList);
		} else if (algoName.equals("SampleSDX")) {
			ret = new AlgoListSampleSDX(cons, dataList);
		} else if (algoName.equals("SampleSDY")) {
			ret = new AlgoListSampleSDY(cons, dataList);
		} else if (algoName.equals("PMCC")) {
			ret = new AlgoListPMCC(cons, dataList);
		} else if (algoName.equals("Spearman")) {
			ret = new AlgoSpearman(cons, dataList);
		} else if (algoName.equals("SXX")) {
			ret = new AlgoListSXX(cons, dataList);
		} else if (algoName.equals("SYY")) {
			ret = new AlgoListSYY(cons, dataList);
		} else if (algoName.equals("SXY")) {
			ret = new AlgoListSXY(cons, dataList);
		} else if (algoName.equals("RSquare")) {
			ret = new AlgoRSquare(cons, dataList,
					(GeoFunctionable) geoRegression);
		} else if (algoName.equals("SumSquaredErrors")) {
			ret = new AlgoSumSquaredErrors(cons, dataList,
					(GeoFunctionable) geoRegression);
		}

		return ret;
	}

	public void updateFonts(Font font) {
		statTable.updateFonts(font);
	}

	public void setLabels() {
		statTable.setLabels(getRowNames(), getColumnNames());
	}

}
