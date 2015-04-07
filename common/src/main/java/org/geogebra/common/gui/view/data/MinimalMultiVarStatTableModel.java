package org.geogebra.common.gui.view.data;

import org.geogebra.common.main.App;

public class MinimalMultiVarStatTableModel extends MultiVarStatTableModel {

	public MinimalMultiVarStatTableModel(App app, MultiVarStatTableListener listener) {
		super(app, listener);
	}
	

	@Override
	public String[] getColumnNames() {

		String[][] cmdMap = getCmdMap();
		String[] names = new String[cmdMap.length];
		for (int i = 0; i < cmdMap.length; i++) {
			names[i] = cmdMap[i][0];
		}
		return names;
	}

	@Override
	public int getRowCount() {
		return getRowNames().length;
	}

	@Override
	public int getColumnCount() {
		return getColumnNames().length;
	}

	

	private String[][] getCmdMap() {
		String[][] map = { { getApp().getMenu("Length.short"), "Length" },
				{ getApp().getMenu("Mean"), "Mean" },
				{ getApp().getMenu("SampleStandardDeviation.short"), "SampleSD" } };
		return map;
	}


}
