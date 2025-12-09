/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
