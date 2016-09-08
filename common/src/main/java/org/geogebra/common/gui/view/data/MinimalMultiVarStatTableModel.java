package org.geogebra.common.gui.view.data;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

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
	public int getColumnCount() {
		return getColumnNames().length;
	}

	

	private String[][] getCmdMap() {
		Localization loc = getApp().getLocalization();
		String[][] map = { { loc.getMenu("Length.short"), "Length" },
				{ loc.getMenu("Mean"), "Mean" },
				{ loc.getMenu("SampleStandardDeviation.short"), "SampleSD" } };
		return map;
	}


}
