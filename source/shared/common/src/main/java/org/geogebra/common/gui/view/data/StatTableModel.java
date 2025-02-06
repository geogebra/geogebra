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
import org.geogebra.common.kernel.statistics.Stat;
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

	public interface StatTableListener {

		GeoList getDataSelected();

		GeoElement getRegressionModel();

		Regression getRegressionMode();

		boolean isValidData();

		void setValueAt(double value, int row, int i);

		boolean isViewValid();

		int getMode();

		GroupType groupType();

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
	public AlgoElement getAlgo(Stat algoName, GeoList dataList,
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

	private Command getCommand(Stat algoName, GeoList dataList, GeoElement geoRegression) {
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
	public Command getAlgoRawData(Stat stat, GeoList dataList,
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
	public Command getAlgoFrequency(Stat stat, GeoList frequencyData) {

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
	public Command getAlgoClass(Stat stat, GeoList frequencyData) {

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
