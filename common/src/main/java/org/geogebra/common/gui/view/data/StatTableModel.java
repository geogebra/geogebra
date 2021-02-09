package org.geogebra.common.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoListLength;
import org.geogebra.common.kernel.algos.AlgoListMinMax;
import org.geogebra.common.kernel.algos.AlgoMedian;
import org.geogebra.common.kernel.algos.AlgoQ1;
import org.geogebra.common.kernel.algos.AlgoQ3;
import org.geogebra.common.kernel.algos.AlgoSum;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoListMeanX;
import org.geogebra.common.kernel.statistics.AlgoListMeanY;
import org.geogebra.common.kernel.statistics.AlgoListPMCC;
import org.geogebra.common.kernel.statistics.AlgoListSXX;
import org.geogebra.common.kernel.statistics.AlgoListSXY;
import org.geogebra.common.kernel.statistics.AlgoListSYY;
import org.geogebra.common.kernel.statistics.AlgoListSampleSDX;
import org.geogebra.common.kernel.statistics.AlgoListSampleSDY;
import org.geogebra.common.kernel.statistics.AlgoMean;
import org.geogebra.common.kernel.statistics.AlgoRSquare;
import org.geogebra.common.kernel.statistics.AlgoSampleStandardDeviation;
import org.geogebra.common.kernel.statistics.AlgoSigmaXX;
import org.geogebra.common.kernel.statistics.AlgoSpearman;
import org.geogebra.common.kernel.statistics.AlgoStandardDeviation;
import org.geogebra.common.kernel.statistics.AlgoSumSquaredErrors;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

/**
 * Displays statistics for DataAnalysisView when in one variable or regression
 * mode.
 * 
 * @author G. Sturr
 * 
 */
public class StatTableModel {
	private App app;
	private Construction cons;
	private StatTableListener listener;

	public interface StatTableListener {

		GeoList getDataSelected();

		GeoElement getRegressionModel();

		DataAnalysisModel.Regression getRegressionMode();

		boolean isValidData();

		void setValueAt(double value, int row, int i);

		boolean isViewValid();

		int getMode();

		GroupType groupType();

		boolean isNumericData();
	}

	public enum Stat {
		NULL, LENGTH, MEAN, SD, SAMPLE_SD, SUM, SIGMAXX, MIN, Q1, MEDIAN, Q3,

		MAX, MEANX, MEANY, SX, SY, PMCC, SPEARMAN, SXX, SYY, SXY, RSQUARE, SSE
	}

	/*************************************************
	 * Construct the panel
	 * 
	 * @param app
	 *            application
	 * @param listener
	 *            change listener
	 */
	public StatTableModel(App app, StatTableListener listener) {
		this.setListener(listener);
		this.app = app;
		cons = app.getKernel().getConstruction();
	}

	/**
	 * @return row names
	 */
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
	 * Evaluates all s tatistics for the selected data list. If data source is
	 * not valid, the result cells are set blank.
	 * 
	 */
	public void updatePanel() {
		// App.printStacktrace("update stat panel");
		GeoList dataList = getListener().getDataSelected();

		GeoElement geoRegression = getListener().getRegressionModel();
		// when the regression mode is NONE geoRegression is a dummy linear
		// model, so reset it to null
		if (getListener().getRegressionMode().equals(Regression.NONE)) {
			geoRegression = null;
		}

		double value;

		ArrayList<Stat> list = getStatList();

		for (int row = 0; row < list.size(); row++) {
			for (int column = 0; column < 1; column++) {
				Stat stat = list.get(row);
				if (getListener().isValidData() && stat != Stat.NULL) {
					AlgoElement algo = getAlgo(stat, dataList, geoRegression);
					if (algo != null) {
						getConstruction().removeFromConstructionList(algo);
						value = ((GeoNumeric) algo.getGeoElements()[0])
								.getDouble();
						getListener().setValueAt(value, row, 0);
					}
				}
			}
		}

	}

	/**
	 * @return list of statistics
	 */
	public ArrayList<Stat> getStatList() {
		ArrayList<Stat> list = new ArrayList<>();

		if (getListener().isViewValid()) {
			return list;
		}

		switch (getListener().getMode()) {
		default:
		case DataAnalysisModel.MODE_ONEVAR:

			if (!getListener().isNumericData()) {
				list.add(Stat.LENGTH);

			} else if (getListener().groupType() == GroupType.RAWDATA
					|| getListener().groupType() == GroupType.FREQUENCY) {

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

			} else if (getListener().groupType() == GroupType.CLASS) {

				list.add(Stat.LENGTH);
				list.add(Stat.MEAN);
				list.add(Stat.SD);
				list.add(Stat.SAMPLE_SD);
				list.add(Stat.SUM);
				list.add(Stat.SIGMAXX);
			}

			break;

		case DataAnalysisModel.MODE_REGRESSION:

			list.add(Stat.MEANX);
			list.add(Stat.MEANY);
			list.add(Stat.SX);
			list.add(Stat.SY);
			list.add(Stat.PMCC);
			list.add(Stat.SPEARMAN);
			list.add(Stat.SXX);
			list.add(Stat.SYY);
			list.add(Stat.SXY);

			if (getListener().getRegressionMode() != Regression.NONE) {
				list.add(Stat.NULL);
				list.add(Stat.RSQUARE);
				list.add(Stat.SSE);
			}
			break;
		}

		return list;
	}

	protected String getStatName(Stat stat) {
		Localization loc = app.getLocalization();
		switch (stat) {
		case LENGTH:
			return loc.getMenu("Length.short");
		case MEAN:
			return loc.getMenu("Mean");
		case SD:
			return loc.getMenu("StandardDeviation.short");
		case SAMPLE_SD:
			return loc.getMenu("SampleStandardDeviation.short");
		case SUM:
			return loc.getMenu("Sum");
		case SIGMAXX:
			return loc.getMenu("Sum2");
		case MIN:
			return loc.getMenu("Minimum.short");
		case Q1:
			return loc.getMenu("LowerQuartile.short");
		case MEDIAN:
			return loc.getMenu("Median");
		case Q3:
			return loc.getMenu("UpperQuartile.short");
		case MAX:
			return loc.getMenu("Maximum.short");
		case MEANX:
			return loc.getMenu("MeanX");
		case MEANY:
			return loc.getMenu("MeanY");
		case SX:
			return loc.getMenu("Sx");
		case SY:
			return loc.getMenu("Sy");
		case PMCC:
			return loc.getMenu("CorrelationCoefficient.short");
		case SPEARMAN:
			return loc.getMenu("Spearman.short");
		case SXX:
			return loc.getMenu("Sxx");
		case SYY:
			return loc.getMenu("Syy");
		case SXY:
			return loc.getMenu("Sxy");
		case RSQUARE:
			return loc.getMenu("RSquare.Short");
		case SSE:
			return loc.getMenu("SumSquaredErrors.short");
		default:
			return null;
		}

	}

	/**
	 * @param algoName
	 *            statistic type
	 * @param dataList
	 *            data
	 * @param geoRegression
	 *            regression line/function (null when not needed)
	 * @return stt algo
	 */
	public AlgoElement getAlgo(Stat algoName, GeoList dataList,
			GeoElement geoRegression) {

		switch (getListener().getMode()) {

		case DataAnalysisModel.MODE_ONEVAR:
			if (getListener().groupType() == GroupType.RAWDATA) {
				return getAlgoRawData(algoName, dataList, geoRegression);

			} else if (getListener().groupType() == GroupType.FREQUENCY) {
				return getAlgoFrequency(algoName, dataList);

			} else if (getListener().groupType() == GroupType.CLASS) {
				return getAlgoClass(algoName, dataList);
			}

		case DataAnalysisModel.MODE_REGRESSION:
			return getAlgoRawData(algoName, dataList, geoRegression);

		case DataAnalysisModel.MODE_MULTIVAR:
			return getAlgoRawData(algoName, dataList, geoRegression);

		default:
			return null;
		}
	}

	/**
	 * @param stat
	 *            stat type
	 * @param dataList
	 *            data list
	 * @param geoRegression
	 *            rgression line or function (null if not needed)
	 * @return stats algo
	 */
	public AlgoElement getAlgoRawData(Stat stat, GeoList dataList,
			GeoElement geoRegression) {

		switch (stat) {

		case LENGTH:
			return new AlgoListLength(getConstruction(), dataList);
		case MEAN:
			return new AlgoMean(getConstruction(), dataList);
		case SD:
			return new AlgoStandardDeviation(getConstruction(), dataList);
		case SAMPLE_SD:
			return new AlgoSampleStandardDeviation(getConstruction(), dataList);
		case SUM:
			return new AlgoSum(getConstruction(), dataList);
		case SIGMAXX:
			return new AlgoSigmaXX(getConstruction(), dataList);
		case MIN:
			return new AlgoListMinMax(getConstruction(), dataList, true);
		case Q1:
			return new AlgoQ1(getConstruction(), dataList);
		case MEDIAN:
			return new AlgoMedian(getConstruction(), dataList);
		case Q3:
			return new AlgoQ3(getConstruction(), dataList);
		case MAX:
			return new AlgoListMinMax(getConstruction(), dataList, false);
		case MEANX:
			return new AlgoListMeanX(getConstruction(), dataList);
		case MEANY:
			return new AlgoListMeanY(getConstruction(), dataList);
		case SX:
			return new AlgoListSampleSDX(getConstruction(), dataList);
		case SY:
			return new AlgoListSampleSDY(getConstruction(), dataList);
		case PMCC:
			return new AlgoListPMCC(getConstruction(), dataList);
		case SPEARMAN:
			return new AlgoSpearman(getConstruction(), dataList);
		case SXX:
			return new AlgoListSXX(getConstruction(), dataList);
		case SYY:
			return new AlgoListSYY(getConstruction(), dataList);
		case SXY:
			return new AlgoListSXY(getConstruction(), dataList);
		case RSQUARE:
			if (geoRegression == null) {
				return null;
			}
			return new AlgoRSquare(getConstruction(), dataList,
					(GeoFunctionable) geoRegression);
		case SSE:
			if (geoRegression == null) {
				return null;
			}
			return new AlgoSumSquaredErrors(getConstruction(), dataList,
					(GeoFunctionable) geoRegression);
		default:
			return null;
		}
	}

	/**
	 * Gets stat algo for frequency grouping
	 * 
	 * @param stat
	 *            statistic type
	 * @param frequencyData
	 *            list with 2 items: {data points, frequencies}
	 * @return stats algo
	 */
	public AlgoElement getAlgoFrequency(Stat stat, GeoList frequencyData) {

		GeoList dataList = (GeoList) frequencyData.get(0);
		GeoList freqList = (GeoList) frequencyData.get(1);

		switch (stat) {

		case LENGTH:
			return new AlgoSum(getConstruction(), freqList);
		case MEAN:
			return new AlgoMean(getConstruction(), dataList, freqList);
		case SD:
			return new AlgoStandardDeviation(getConstruction(), dataList,
					freqList);
		case SAMPLE_SD:
			return new AlgoSampleStandardDeviation(getConstruction(), dataList,
					freqList);
		case SUM:
			return new AlgoSum(getConstruction(), dataList, freqList);
		case SIGMAXX:
			return new AlgoSigmaXX(getConstruction(), dataList, freqList);
		case MIN:
			return new AlgoListMinMax(getConstruction(), dataList, freqList, true);
		case Q1:
			return new AlgoQ1(getConstruction(), dataList, freqList);
		case MEDIAN:
			return new AlgoMedian(getConstruction(), dataList, freqList);
		case Q3:
			return new AlgoQ3(getConstruction(), dataList, freqList);
		case MAX:
			return new AlgoListMinMax(getConstruction(), dataList, freqList, false);
		default:
			return null;
		}
	}

	/**
	 * Get stats algo for class grouping
	 * 
	 * @param stat
	 *            statistic type
	 * @param frequencyData
	 *            two item list {class borders, frequencies}
	 * @return stats algo
	 */
	public AlgoElement getAlgoClass(Stat stat, GeoList frequencyData) {

		GeoList classList = (GeoList) frequencyData.get(0);
		GeoList freqList = (GeoList) frequencyData.get(1);

		switch (stat) {

		case LENGTH:
			return new AlgoSum(getConstruction(), freqList);
		case MEAN:
			return new AlgoMean(getConstruction(), classList, freqList);
		case SD:
			return new AlgoStandardDeviation(getConstruction(), classList,
					freqList);
		case SAMPLE_SD:
			return new AlgoSampleStandardDeviation(getConstruction(), classList,
					freqList);
		case SUM:
			return new AlgoSum(getConstruction(), classList, freqList);
		case SIGMAXX:
			return new AlgoSigmaXX(getConstruction(), classList);
		default:
			return null;
		}
	}

	public Construction getConstruction() {
		return cons;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public StatTableListener getListener() {
		return listener;
	}

	public void setListener(StatTableListener listener) {
		this.listener = listener;
	}

}
