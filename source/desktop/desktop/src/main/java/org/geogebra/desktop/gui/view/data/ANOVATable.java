package org.geogebra.desktop.gui.view.data;

import javax.swing.table.DefaultTableModel;

import org.geogebra.common.gui.view.data.ANOVAStatTableModel;
import org.geogebra.common.gui.view.data.ANOVAStatTableModel.AnovaStats;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.desktop.main.AppD;

public class ANOVATable extends BasicStatTable {
	private static final long serialVersionUID = 1L;

	/**
	 * @param app application
	 * @param statDialog DA dialog
	 */
	public ANOVATable(AppD app, DataAnalysisViewD statDialog) {
		super(app, statDialog, false);
		setModel(new ANOVAStatTableModel(app, this));
		this.setMinimumSize(this.getPreferredSize());
	}

	@Override
	public void updatePanel() {

		GeoList dataList = daView.getController().getDataSelected();
		DefaultTableModel model = statTable.getModel();
		model.setColumnCount(getModel().getColumnCount());
		model.setRowCount(getModel().getRowCount());
		model.setColumnIdentifiers(getModel().getColumnNames());

		AnovaStats stats = ANOVAStatTableModel
				.getStatsSilent(dataList);
		if (stats != null) {
			// first column, degrees of freedom
			model.setValueAt(daView.format(stats.getDfbg()), 0, 0);
			model.setValueAt(daView.format(stats.getDfwg()), 1, 0);
			model.setValueAt(daView.format(stats.getDfbg() + stats.getDfwg()),
					2, 0);

			// second column, sum of squares
			model.setValueAt(daView.format(stats.getSsbg()), 0, 1);
			model.setValueAt(daView.format(stats.getSswg()), 1, 1);
			model.setValueAt(daView.format(stats.getSst()), 2, 1);

			// third column, mean sum of squares
			model.setValueAt(daView.format(stats.getMsbg()), 0, 2);
			model.setValueAt(daView.format(stats.getMswg()), 1, 2);

			// fourth column, F test statistics
			model.setValueAt(daView.format(stats.getF()), 0, 3);

			// fifth column, P value
			model.setValueAt(daView.format(stats.getP()), 0, 4);

		}

		repaint();
	}

}
