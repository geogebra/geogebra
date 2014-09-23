package geogebra.gui.view.data;

import geogebra.main.AppD;

/**
 * Extension of BasicStatTable that displays summary statistics for multiple
 * data sets.
 * 
 * @author G. Sturr
 * 
 */
public class MinimalMultiVarStatPanel extends BasicStatTable {
	private static final long serialVersionUID = 1L;

	public MinimalMultiVarStatPanel(AppD app, DataAnalysisViewD statDialog) {
		super(app, statDialog);
	}

	@Override
	public String[] getRowNames() {
		return daView.getDataTitles();
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
