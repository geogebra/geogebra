package org.geogebra.common.main.settings;

import java.util.ArrayList;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.util.debug.Log;

public class DataAnalysisSettings {
	private ArrayList<String> items = new ArrayList<>();
	private int mode = DataAnalysisModel.MODE_ONEVAR;
	private Regression regression = Regression.NONE;
	private PlotType plotType1 = null;
	private PlotType plotType2 = null;

	/**
	 * @param ranges
	 *            selected range
	 */
	public void addItem(String ranges) {
		items.add(ranges);
	}

	/**
	 * Update mode and reset plot types.
	 * 
	 * @param mode
	 *            app mode
	 */
	public void setMode(int mode) {
		if (mode != this.mode) {
			plotType1 = null;
			plotType2 = null;
		}
		this.mode = mode;
	}

	public ArrayList<String> getItems() {
		return items;
	}

	/**
	 * @return app mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param i
	 *            plot panel
	 * @param plotType
	 *            plot type
	 */
	public void setPlotType(int i, PlotType plotType) {
		if (i == 0) {
			this.plotType1 = plotType;
		} else {
			this.plotType2 = plotType;
		}

		Log.error("set i = " + i + "  " + plotType1 + " " + plotType2);

	}

	public void setRegression(Regression valueOf) {
		this.regression = valueOf;

	}

	public Regression getRegression() {
		return regression;
	}

	public PlotType getPlotType(int i, PlotType fallback) {
		PlotType ret = i == 0 ? plotType1 : plotType2;
		return ret == null ? fallback : ret;
	}

}
