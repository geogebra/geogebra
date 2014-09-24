package geogebra.common.gui.view.data;

import geogebra.common.main.App;

public class ANOVAStatTableModel extends StatTableModel {
	
	public ANOVAStatTableModel(App app, StatTableListener listener) {
		super(app, listener);
	}
	
	@Override
	public String[] getRowNames() {
		String[] names = { getApp().getMenu("BetweenGroups"),
				getApp().getMenu("WithinGroups"), getApp().getMenu("Total"), };
		return names;
	}

	@Override
	public String[] getColumnNames() {

		String[] names = { getApp().getMenu("DegreesOfFreedom.short"),
				getApp().getMenu("SumSquares.short"),
				getApp().getMenu("MeanSquare.short"),
				getApp().getMenu("FStatistic"), getApp().getMenu("PValue"), };

		return names;
	}

	public int getRowCount() {
		return getRowNames().length;
	}

	public int getColumnCount() {
		return getColumnNames().length;
	}
}
