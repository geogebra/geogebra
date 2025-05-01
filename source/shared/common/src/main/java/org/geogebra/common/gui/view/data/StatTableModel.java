package org.geogebra.common.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.Regression;
import org.geogebra.common.kernel.statistics.Statistic;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

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

	/**
	 * UI delegate for this model
	 */
	public interface StatTableListener {

		/**
		 * @return list of selected data
		 */
		GeoList getDataSelected();

		/**
		 * @return regression model
		 */
		GeoElement getRegressionModel();

		/**
		 * @return regression mode
		 */
		Regression getRegressionMode();

		/**
		 * @return whether data is valid
		 */
		boolean isValidData();

		/**
		 * Set value in the table.
		 * @param value value
		 * @param row row
		 * @param column column (excluding header)
		 */
		void setValueAt(double value, int row, int column);

		/**
		 * @return whether the view is valid (correct data source)
		 */
		boolean isViewValid();

		/**
		 * @return app mode
		 */
		int getMode();

		/**
		 * @return group type
		 */
		GroupType groupType();

		/**
		 * @return whether data is all numeric
		 */
		boolean isNumericData();
	}

	/**
	 * Construct the model
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
		ArrayList<Statistic> list = getStatList();
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
		GeoList dataList = getListener().getDataSelected();

		GeoElement geoRegression = getListener().getRegressionModel();
		// when the regression mode is NONE geoRegression is a dummy linear
		// model, so reset it to null
		if (getListener().getRegressionMode().equals(Regression.NONE)) {
			geoRegression = null;
		}

		double value;

		ArrayList<Statistic> list = getStatList();

		for (int row = 0; row < list.size(); row++) {
			for (int column = 0; column < 1; column++) {
				Statistic stat = list.get(row);
				if (getListener().isValidData() && stat != Statistic.NULL) {
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
	public ArrayList<Statistic> getStatList() {
		ArrayList<Statistic> list = new ArrayList<>();

		if (getListener().isViewValid()) {
			return list;
		}

		switch (getListener().getMode()) {
		default:
		case DataAnalysisModel.MODE_ONEVAR:

			if (!getListener().isNumericData()) {
				list.add(Statistic.LENGTH);

			} else if (getListener().groupType() == GroupType.RAWDATA
					|| getListener().groupType() == GroupType.FREQUENCY) {

				list.add(Statistic.LENGTH);
				list.add(Statistic.MEAN);
				list.add(Statistic.SD);
				list.add(Statistic.SAMPLE_SD);
				list.add(Statistic.SUM);
				list.add(Statistic.SIGMAXX);
				list.add(Statistic.MIN);
				list.add(Statistic.Q1);
				list.add(Statistic.MEDIAN);
				list.add(Statistic.Q3);
				list.add(Statistic.MAX);

			} else if (getListener().groupType() == GroupType.CLASS) {

				list.add(Statistic.LENGTH);
				list.add(Statistic.MEAN);
				list.add(Statistic.SD);
				list.add(Statistic.SAMPLE_SD);
				list.add(Statistic.SUM);
				list.add(Statistic.SIGMAXX);
			}

			break;

		case DataAnalysisModel.MODE_REGRESSION:

			list.add(Statistic.MEANX);
			list.add(Statistic.MEANY);
			list.add(Statistic.SX);
			list.add(Statistic.SY);
			list.add(Statistic.PMCC);
			list.add(Statistic.SPEARMAN);
			list.add(Statistic.SXX);
			list.add(Statistic.SYY);
			list.add(Statistic.SXY);

			if (getListener().getRegressionMode() != Regression.NONE) {
				list.add(Statistic.NULL);
				list.add(Statistic.RSQUARE);
				list.add(Statistic.SSE);
			}
			break;
		}

		return list;
	}

	protected String getStatName(Statistic stat) {
		return app.getLocalization().getMenu(stat.getTranslationKey());
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
	public AlgoElement getAlgo(Statistic algoName, GeoList dataList,
                               GeoElement geoRegression) {
		try {
			Command command = getCommand(algoName, dataList, geoRegression);
			AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
			GeoElementND geo = algebraProcessor.processValidExpressionSilent(command)[0];
			return geo.getParentAlgorithm();
		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	private Command getCommand(Statistic algoName, GeoList dataList, GeoElement geoRegression) {
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
	public Command getAlgoRawData(Statistic stat, GeoList dataList,
                                  GeoElement geoRegression) {
		Command command = new Command(app.getKernel(), stat.getCommandName(), false);
		switch (stat) {
		case LENGTH:
		case MEAN:
		case SD:
		case SAMPLE_SD:
		case SUM:
		case SIGMAXX:
		case MIN:
		case Q1:
		case MEDIAN:
		case Q3:
		case MAX:
		case MEANX:
		case MEANY:
		case SX:
		case SY:
		case PMCC:
		case SPEARMAN:
		case SXX:
		case SYY:
		case SXY:
			command.addArgument(dataList.wrap());
			break;
		case RSQUARE:
		case SSE:
			if (geoRegression == null) {
				return null;
			}
			command.addArgument(dataList.wrap());
			command.addArgument(geoRegression.wrap());
			break;
		default:
			return null;
		}

		return command;
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
	public Command getAlgoFrequency(Statistic stat, GeoList frequencyData) {

		GeoList dataList = (GeoList) frequencyData.get(0);
		GeoList freqList = (GeoList) frequencyData.get(1);

		switch (stat) {

		case LENGTH:
			Command cmd = new Command(app.getKernel(), "Sum", false);
			cmd.addArgument(freqList.wrap());
			return cmd;
		case MEAN:
		case SD:
		case SAMPLE_SD:
		case SUM:
		case SIGMAXX:
		case MIN:
		case Q1:
		case MEDIAN:
		case Q3:
		case MAX:
			cmd = new Command(app.getKernel(), stat.getCommandName(), false);
			cmd.addArgument(dataList.wrap());
			cmd.addArgument(freqList.wrap());
			return cmd;
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
	public Command getAlgoClass(Statistic stat, GeoList frequencyData) {

		GeoList classList = (GeoList) frequencyData.get(0);
		GeoList freqList = (GeoList) frequencyData.get(1);

		switch (stat) {

		case LENGTH:
			Command cmd = new Command(app.getKernel(), "Sum", false);
			cmd.addArgument(freqList.wrap());
			return cmd;
		case MEAN:
		case SD:
		case SAMPLE_SD:
		case SUM:
		case SIGMAXX:
			cmd = new Command(app.getKernel(), stat.getCommandName(), false);
			cmd.addArgument(classList.wrap());
			cmd.addArgument(freqList.wrap());
			return cmd;
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
