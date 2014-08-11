package geogebra.common.gui.view.data;

import geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import geogebra.common.gui.view.data.DataVariable.GroupType;
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
import geogebra.common.main.App;

import java.util.ArrayList;

/**
 * Displays statistics for DataAnalysisView when in one variable or regression
 * mode.
 * 
 * @author G. Sturr
 * 
 */
public class StatTableModel {
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
	protected App app;
	private Construction cons;
	private StatTableListener listener;
	//!TODO: to common!
	public enum Stat {
		NULL, LENGTH, MEAN, SD, SAMPLE_SD, SUM, SIGMAXX, MIN, Q1, MEDIAN, Q3, MAX, MEANX, MEANY, SX, SY, PMCC, SPEARMAN, SXX, SYY, SXY, RSQUARE, SSE
	};

	/*************************************************
	 * Construct the panel
	 * 
	 * @param app
	 * @param listener
	 */
	public StatTableModel(App app, StatTableListener listener) {
		this.listener = listener;
		this.app = app;
		cons = app.getKernel().getConstruction();
	}


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
		GeoList dataList = listener.getDataSelected();

		GeoElement geoRegression = listener.getRegressionModel();
		// when the regression mode is NONE geoRegression is a dummy linear
		// model, so reset it to null
		if (listener.getRegressionMode().equals(Regression.NONE)) {
			geoRegression = null;
		}

		double value;

		ArrayList<Stat> list = getStatList();

		for (int row = 0; row < list.size(); row++) {
			for (int column = 0; column < 1; column++) {
				Stat stat = list.get(row);
				if (listener.isValidData() && stat != Stat.NULL) {
					AlgoElement algo = getAlgo(stat, dataList, geoRegression);
					if (algo != null) {
						getConstruction().removeFromConstructionList(algo);
						value = ((GeoNumeric) algo.getGeoElements()[0])
								.getDouble();
						listener.setValueAt(value, row,	0);
					}
				}
			}
		}

	}

	protected ArrayList<Stat> getStatList() {
		ArrayList<Stat> list = new ArrayList<Stat>();

		if (listener.isViewValid()) {
			return list;
		}

		switch (listener.getMode()) {
		case DataAnalysisModel.MODE_ONEVAR:

			if (!listener.isNumericData()) {
				list.add(Stat.LENGTH);

			} else if (listener.groupType() == GroupType.RAWDATA
					|| listener.groupType() == GroupType.FREQUENCY) {

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

			} else if (listener.groupType() == GroupType.CLASS) {

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
			return app.getMenu("RSquare.Short");
		case SSE:
			return app.getMenu("SumSquaredErrors.short");
		default:
			return null;
		}

	}

	public AlgoElement getAlgo(Stat algoName, GeoList dataList,
			GeoElement geoRegression) {

		switch (listener.getMode()) {

		case DataAnalysisModel.MODE_ONEVAR:
			if (listener.groupType() == GroupType.RAWDATA) {
				return getAlgoRawData(algoName, dataList, geoRegression);

			} else if (listener.groupType() == GroupType.FREQUENCY) {
				return getAlgoFrequency(algoName, dataList, geoRegression);

			} else if (listener.groupType() == GroupType.CLASS) {
				return getAlgoClass(algoName, dataList, geoRegression);
			}

		case DataAnalysisModel.MODE_REGRESSION:
			return getAlgoRawData(algoName, dataList, geoRegression);

		case DataAnalysisModel.MODE_MULTIVAR:
			return getAlgoRawData(algoName, dataList, geoRegression);

		default:
			return null;
		}
	}

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
			return new AlgoListMin(getConstruction(), dataList);
		case Q1:
			return new AlgoQ1(getConstruction(), dataList);
		case MEDIAN:
			return new AlgoMedian(getConstruction(), dataList);
		case Q3:
			return new AlgoQ3(getConstruction(), dataList);
		case MAX:
			return new AlgoListMax(getConstruction(), dataList);
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

	public AlgoElement getAlgoFrequency(Stat stat, GeoList frequencyData,
			GeoElement geoRegression) {

		GeoList dataList = (GeoList) frequencyData.get(0);
		GeoList freqList = (GeoList) frequencyData.get(1);

		switch (stat) {

		case LENGTH:
			return new AlgoSum(getConstruction(), freqList);
		case MEAN:
			return new AlgoMean(getConstruction(), dataList, freqList);
		case SD:
			return new AlgoStandardDeviation(getConstruction(), dataList, freqList);
		case SAMPLE_SD:
			return new AlgoSampleStandardDeviation(getConstruction(), dataList, freqList);
		case SUM:
			return new AlgoSum(getConstruction(), dataList, freqList);
		case SIGMAXX:
			return new AlgoSigmaXX(getConstruction(), dataList, freqList);
		case MIN:
			return new AlgoListMin(getConstruction(), dataList, freqList);
		case Q1:
			return new AlgoQ1(getConstruction(), dataList, freqList);
		case MEDIAN:
			return new AlgoMedian(getConstruction(), dataList, freqList);
		case Q3:
			return new AlgoQ3(getConstruction(), dataList, freqList);
		case MAX:
			return new AlgoListMax(getConstruction(), dataList, freqList);
		default:
			return null;
		}
	}

	public AlgoElement getAlgoClass(Stat stat, GeoList frequencyData,
			GeoElement geoRegression) {

		GeoList classList = (GeoList) frequencyData.get(0);
		GeoList freqList = (GeoList) frequencyData.get(1);

		switch (stat) {

		case LENGTH:
			return new AlgoSum(getConstruction(), freqList);
		case MEAN:
			return new AlgoMean(getConstruction(), classList, freqList);
		case SD:
			return new AlgoStandardDeviation(getConstruction(), classList, freqList);
		case SAMPLE_SD:
			return new AlgoSampleStandardDeviation(getConstruction(), classList, freqList);
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

}
