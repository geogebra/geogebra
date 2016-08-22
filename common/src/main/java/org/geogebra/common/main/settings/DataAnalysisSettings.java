package org.geogebra.common.main.settings;

import java.util.ArrayList;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;

public class DataAnalysisSettings {
	private ArrayList<String> items = new ArrayList<String>();
	private int mode = DataAnalysisModel.MODE_ONEVAR;
	private Regression regression;
	public void addItem(String ranges) {
		items.add(ranges);
	}

	public void setMode(int mode) {
		this.mode = mode;

	}

	public ArrayList<String> getItems() {
		return items;
	}

	public int getMode() {
		return mode;
	}

	public void setPlotType(int i, PlotType valueOf) {
		// TODO Auto-generated method stub

	}

	public void setRegression(Regression valueOf) {
		this.regression = valueOf;

	}

	public Regression getRegression() {
		return regression;
	}

}
