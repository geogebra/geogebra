package geogebra.gui.view.data;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoListLength;
import geogebra.common.kernel.algos.AlgoListMax;
import geogebra.common.kernel.algos.AlgoListMin;
import geogebra.common.kernel.algos.AlgoMedian;
import geogebra.common.kernel.algos.AlgoQ1;
import geogebra.common.kernel.algos.AlgoQ3;
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
import geogebra.common.kernel.statistics.AlgoRSquare;
import geogebra.common.kernel.statistics.AlgoSampleStandardDeviation;
import geogebra.common.kernel.statistics.AlgoSigmaXX;
import geogebra.common.kernel.statistics.AlgoSpearman;
import geogebra.common.kernel.statistics.AlgoStandardDeviation;
import geogebra.common.kernel.statistics.AlgoSumSquaredErrors;
import geogebra.gui.view.data.DataAnalysisViewD.Regression;
import geogebra.gui.view.data.DataVariable.GroupType;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 * Displays statistics for DataAnalysisView when in one variable or regression
 * mode.
 * 
 * @author G. Sturr
 * 
 */
public class BasicStatTable extends JPanel implements StatPanelInterface {
	private static final long serialVersionUID = 1L;
	
	protected AppD app;
	private Construction cons;
	protected DataAnalysisViewD daView;
	protected StatTable statTable;

	protected enum Stat {
		NULL, LENGTH, MEAN, SD, SAMPLE_SD, SUM, SIGMAXX, MIN, Q1, MEDIAN, Q3, MAX, MEANX, MEANY, SX, SY, PMCC, SPEARMAN, SXX, SYY, SXY, RSQUARE, SSE
	};

	/*************************************************
	 * Construct the panel
	 * @param app 
	 * @param statDialog 
	 * @param mode 
	 */
	public BasicStatTable(AppD app, DataAnalysisViewD statDialog) {

		this.app = app;
		this.cons = app.getKernel().getConstruction();
		this.daView = statDialog;
		
		this.setLayout(new BorderLayout());
		initStatTable();
		updateFonts(app.getPlainFont());

	} // END constructor

	protected void initStatTable() {

		statTable = new StatTable(app);
		statTable.setStatTable(getRowCount(), getRowNames(), getColumnCount(),
				getColumnNames());
		this.removeAll();
		this.add(statTable, BorderLayout.CENTER);
	}

	// =======================================================
	// override these classes

	public String[] getRowNames() {
		ArrayList<Stat> list = getStatList();
		String[] rowNames = new String[list.size()];
		for (int i = 0; i < rowNames.length; i++) {
			rowNames[i] = getStatName(list.get(i));
		}
		return rowNames;
	}

	public String[] getColumnNames() {
		return null;
	}

	public int getRowCount() {
		return getStatList().size();
	}

	public int getColumnCount() {
		return 1;
	}

	// =======================================================

	/**
	 * Evaluates all statistics for the selected data list. If data source is
	 * not valid, the result cells are set blank.
	 * 
	 */
	public void updatePanel() {
		// App.printStacktrace("update stat panel");
		GeoList dataList = daView.getController().getDataSelected();

		GeoElement geoRegression = daView.getRegressionModel();
		// when the regression mode is NONE geoRegression is a dummy linear
		// model, so reset it to null
		if (daView.getRegressionMode().equals(Regression.NONE)) {
			geoRegression = null;
		}

		DefaultTableModel model = statTable.getModel();
		double value;

		ArrayList<Stat> list = getStatList();

		for (int row = 0; row < list.size(); row++) {
			for (int column = 0; column < 1; column++) {
				Stat stat = list.get(row);
				if (daView.getController().isValidData() && stat != Stat.NULL) {
					AlgoElement algo = getAlgo(stat, dataList, geoRegression);
					if (algo != null) {
						cons.removeFromConstructionList(
								algo);
						value = ((GeoNumeric) algo.getGeoElements()[0])
								.getDouble();
						model.setValueAt(daView.format(value), row, 0);
					}
				}
			}
		}

	}

	private ArrayList<Stat> getStatList() {
		ArrayList<Stat> list = new ArrayList<Stat>();

		if (daView == null || daView.getDataSource() == null) {
			return list;
		}

		switch (daView.getMode()) {
		case DataAnalysisViewD.MODE_ONEVAR:

			if (!daView.getDataSource().isNumericData()) {
				list.add(Stat.LENGTH);

			} else if (daView.groupType() == GroupType.RAWDATA
					|| daView.groupType() == GroupType.FREQUENCY) {

				list.add(Stat.LENGTH);
				list.add(Stat.MEAN);
				list.add(Stat.SD);
				list.add(Stat.SAMPLE_SD);
				list.add(Stat.SUM);
				list.add(Stat.SIGMAXX);
				list.add(Stat.MIN);
				list.add(Stat.Q1);
				list.add(Stat.MEDIAN);
				list.add(Stat.Q3);
				list.add(Stat.MAX);

			} else if (daView.groupType() == GroupType.CLASS) {

				list.add(Stat.LENGTH);
				list.add(Stat.MEAN);
				list.add(Stat.SD);
				list.add(Stat.SAMPLE_SD);
				list.add(Stat.SUM);
				list.add(Stat.SIGMAXX);
			}

			break;

		case DataAnalysisViewD.MODE_REGRESSION:

			list.add(Stat.MEANX);
			list.add(Stat.MEANY);
			list.add(Stat.SX);
			list.add(Stat.SY);
			list.add(Stat.PMCC);
			list.add(Stat.SPEARMAN);
			list.add(Stat.SXX);
			list.add(Stat.SYY);
			list.add(Stat.SXY);
			list.add(Stat.NULL);
			list.add(Stat.RSQUARE);
			list.add(Stat.SSE);
			break;
		}

		return list;
	}

	protected String getStatName(Stat stat) {
		switch (stat) {
		case LENGTH:
			return app.getMenu("Length.short");
		case MEAN:
			return app.getMenu("Mean");
		case SD:
			return app.getMenu("StandardDeviation.short");
		case SAMPLE_SD:
			return app.getMenu("SampleStandardDeviation.short");
		case SUM:
			return app.getMenu("Sum");
		case SIGMAXX:
			return app.getMenu("Sum2");
		case MIN:
			return app.getMenu("Minimum.short");
		case Q1:
			return app.getMenu("LowerQuartile.short");
		case MEDIAN:
			return app.getMenu("Median");
		case Q3:
			return app.getMenu("UpperQuartile.short");
		case MAX:
			return app.getMenu("Maximum.short");
		case MEANX:
			return app.getMenu("MeanX");
		case MEANY:
			return app.getMenu("MeanY");
		case SX:
			return app.getMenu("Sx");
		case SY:
			return app.getMenu("Sy");
		case PMCC:
			return app.getMenu("CorrelationCoefficient.short");
		case SPEARMAN:
			return app.getMenu("Spearman.short");
		case SXX:
			return app.getMenu("Sxx");
		case SYY:
			return app.getMenu("Syy");
		case SXY:
			return app.getMenu("Sxy");
		case RSQUARE:
			return app.getMenu("RSquare");
		case SSE:
			return app.getMenu("SumSquaredErrors.short");
		default:
			return null;
		}

	}

	protected AlgoElement getAlgo(Stat algoName, GeoList dataList,
			GeoElement geoRegression) {

		switch (daView.getMode()) {

		case DataAnalysisViewD.MODE_ONEVAR:
			if (daView.groupType() == GroupType.RAWDATA) {
				return getAlgoRawData(algoName, dataList, geoRegression);

			} else if (daView.groupType() == GroupType.FREQUENCY) {
				return getAlgoFrequency(algoName, dataList, geoRegression);

			} else if (daView.groupType() == GroupType.CLASS) {
				return getAlgoClass(algoName, dataList, geoRegression);
			}

		case DataAnalysisViewD.MODE_REGRESSION:
			return getAlgoRawData(algoName, dataList, geoRegression);

		case DataAnalysisViewD.MODE_MULTIVAR:
			return getAlgoRawData(algoName, dataList, geoRegression);

		default:
			return null;
		}
	}

	protected AlgoElement getAlgoRawData(Stat stat, GeoList dataList,
			GeoElement geoRegression) {

		switch (stat) {

		case LENGTH:
			return new AlgoListLength(cons, dataList);
		case MEAN:
			return new AlgoMean(cons, dataList);
		case SD:
			return new AlgoStandardDeviation(cons, dataList);
		case SAMPLE_SD:
			return new AlgoSampleStandardDeviation(cons, dataList);
		case SUM:
			return new AlgoSum(cons, dataList);
		case SIGMAXX:
			return new AlgoSigmaXX(cons, dataList);
		case MIN:
			return new AlgoListMin(cons, dataList);
		case Q1:
			return new AlgoQ1(cons, dataList);
		case MEDIAN:
			return new AlgoMedian(cons, dataList);
		case Q3:
			return new AlgoQ3(cons, dataList);
		case MAX:
			return new AlgoListMax(cons, dataList);
		case MEANX:
			return new AlgoListMeanX(cons, dataList);
		case MEANY:
			return new AlgoListMeanY(cons, dataList);
		case SX:
			return new AlgoListSampleSDX(cons, dataList);
		case SY:
			return new AlgoListSampleSDY(cons, dataList);
		case PMCC:
			return new AlgoListPMCC(cons, dataList);
		case SPEARMAN:
			return new AlgoSpearman(cons, dataList);
		case SXX:
			return new AlgoListSXX(cons, dataList);
		case SYY:
			return new AlgoListSYY(cons, dataList);
		case SXY:
			return new AlgoListSXY(cons, dataList);
		case RSQUARE:
			if (geoRegression == null) {
				return null;
			}
			return new AlgoRSquare(cons, dataList,
					(GeoFunctionable) geoRegression);
		case SSE:
			if (geoRegression == null) {
				return null;
			}
			return new AlgoSumSquaredErrors(cons, dataList,
					(GeoFunctionable) geoRegression);
		default:
			return null;
		}
	}

	protected AlgoElement getAlgoFrequency(Stat stat, GeoList frequencyData,
			GeoElement geoRegression) {

		GeoList dataList = (GeoList) frequencyData.get(0);
		GeoList freqList = (GeoList) frequencyData.get(1);

		switch (stat) {

		case LENGTH:
			return new AlgoListLength(cons, freqList);
		case MEAN:
			return new AlgoMean(cons, dataList, freqList);
		case SD:
			return new AlgoStandardDeviation(cons, dataList, freqList);
		case SAMPLE_SD:
			return new AlgoSampleStandardDeviation(cons, dataList, freqList);
		case SUM:
			return new AlgoSum(cons, dataList, freqList);
		case SIGMAXX:
			return new AlgoSigmaXX(cons, dataList, freqList);
		case MIN:
			return new AlgoListMin(cons, dataList, freqList);
		case Q1:
			return new AlgoQ1(cons, dataList, freqList);
		case MEDIAN:
			return new AlgoMedian(cons, dataList, freqList);
		case Q3:
			return new AlgoQ3(cons, dataList, freqList);
		case MAX:
			return new AlgoListMax(cons, dataList, freqList);
		default:
			return null;
		}
	}

	protected AlgoElement getAlgoClass(Stat stat, GeoList frequencyData,
			GeoElement geoRegression) {

		GeoList classList = (GeoList) frequencyData.get(0);
		GeoList freqList = (GeoList) frequencyData.get(1);

		switch (stat) {

		case LENGTH:
			return new AlgoListLength(cons, freqList);
		case MEAN:
			return new AlgoMean(cons, classList, freqList);
		case SD:
			return new AlgoStandardDeviation(cons, classList, freqList);
		case SAMPLE_SD:
			return new AlgoSampleStandardDeviation(cons, classList, freqList);
		case SUM:
			return new AlgoSum(cons, classList, freqList);
		case SIGMAXX:
			return new AlgoSigmaXX(cons, classList);
		default:
			return null;
		}
	}

	

	public void updateFonts(Font font) {
		statTable.updateFonts(font);
	}

	public void setLabels() {
		statTable.setLabels(getRowNames(), getColumnNames());
	}

}
