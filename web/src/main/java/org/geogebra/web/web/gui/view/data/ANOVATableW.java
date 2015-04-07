package org.geogebra.web.web.gui.view.data;

import org.apache.commons.math.MathException;
import org.geogebra.common.gui.view.data.ANOVAStatTableModel;
import org.geogebra.common.gui.view.data.ANOVAStatTableModel.AnovaStats;
import org.geogebra.common.gui.view.data.StatTableModel.StatTableListener;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.web.html5.main.AppW;

public class ANOVATableW extends BasicStatTableW implements StatTableListener {
	private static final long serialVersionUID = 1L;
	private static final int ROW_COUNT = 3;
	private static final int COLUMN_COUNT = 6;

	public ANOVATableW(AppW app, DataAnalysisViewW statDialog) {
		super(app, statDialog, false);
		setModel(new ANOVAStatTableModel(app, this));
		setStyleName("daANNOVA");
		
	}

	protected void initStatTable() {

		statTable = new StatTableW(app);
		statTable.setStatTable(ROW_COUNT, getModel().getRowNames(),
				COLUMN_COUNT, getColumnNames());
		clear();
		add(statTable);
	}
	
	@Override
	public String[] getColumnNames() {
		String[] colNames = getModel().getColumnNames();
		String[] ext = new String[colNames.length + 1];
		ext[0] = "";
   	System.arraycopy(colNames, 0, ext, 1, colNames.length);
		return ext;
	}
	
	@Override
	public void updatePanel() {

		GeoList dataList = daView.getController().getDataSelected();
		statTable.setStatTable(ROW_COUNT, getModel().getRowNames(),
				COLUMN_COUNT, getColumnNames());

		try {
			AnovaStats stats = ANOVAStatTableModel.getStats(dataList);

			// first column, degrees of freedom
			statTable.setValueAt(daView.format(stats.getDfbg()), 0, 1);
			statTable.setValueAt(daView.format(stats.getDfwg()), 1, 1);
			statTable.setValueAt(daView.format(stats.getDfbg() + stats.getDfwg()), 2, 1);

			// second column, sum of squares
			statTable.setValueAt(daView.format(stats.getSsbg()), 0, 2);
			statTable.setValueAt(daView.format(stats.getSswg()), 1, 2);
			statTable.setValueAt(daView.format(stats.getSst()), 2, 2);

			// third column, mean sum of squares
			statTable.setValueAt(daView.format(stats.getMsbg()), 0, 3);
			statTable.setValueAt(daView.format(stats.getMswg()), 1, 3);

			// fourth column, F test statistics
			statTable.setValueAt(daView.format(stats.getF()), 0, 4);

			// fifth column, P value
			statTable.setValueAt(daView.format(stats.getP()), 0, 5);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
