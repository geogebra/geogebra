package org.geogebra.common.main.settings;

import java.util.ArrayList;

import org.geogebra.common.gui.view.data.DataAnalysisModel;

public class DataAnalysisSettings {
	private ArrayList<String> items = new ArrayList<String>();
	private int mode = DataAnalysisModel.MODE_ONEVAR;
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

}
