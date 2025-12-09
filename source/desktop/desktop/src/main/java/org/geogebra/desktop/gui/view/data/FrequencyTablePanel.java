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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.geogebra.common.kernel.statistics.AlgoFrequencyTable;
import org.geogebra.desktop.main.AppD;

public class FrequencyTablePanel extends JPanel implements StatPanelInterface {
	private static final long serialVersionUID = 1L;

	protected AppD app;
	// protected DataAnalysisViewD statDialog;
	// private int mode;
	protected StatTable statTable;

	/**
	 * @param app application
	 */
	public FrequencyTablePanel(AppD app) {
		this.app = app;

		statTable = new StatTable(app);

		this.setLayout(new BorderLayout());
		this.add(statTable, BorderLayout.CENTER);

		statTable.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
				SystemColor.controlShadow));
		setBorder(BorderFactory.createEmptyBorder());
	}

	/**
	 * @param algo frequency algo
	 * @param useClasses whether to use classes for data
	 */
	public void setTableFromGeoFrequencyTable(AlgoFrequencyTable algo,
			boolean useClasses) {
		String[] strValue = algo.getValueString();
		String[] strFrequency = algo.getFrequencyString();
		String[] strHeader = algo.getHeaderString();

		statTable.setStatTable(strValue.length, null, 2, strHeader);
		DefaultTableModel model = statTable.getModel();

		if (useClasses) {
			for (int row = 0; row < strValue.length - 1; row++) {
				model.setValueAt(strValue[row] + " - " + strValue[row + 1], row,
						0);
				model.setValueAt(strFrequency[row], row, 1);
			}
		} else {
			for (int row = 0; row < strValue.length; row++) {
				model.setValueAt(strValue[row], row, 0);
				model.setValueAt(strFrequency[row], row, 1);
			}
		}

		setTableSize();
	}

	private void setTableSize() {

		Dimension d = statTable.getPreferredSize();
		this.setPreferredSize(d);
		int numRows = Math.min(8, statTable.getTable().getRowCount());
		d.height = numRows * statTable.getTable().getRowHeight();
		this.setMaximumSize(d);
		statTable.revalidate();
		updateFonts(app.getPlainFont());
	}

	@Override
	public void updatePanel() {
		// do nothing
	}

	@Override
	public void updateFonts(Font font) {
		statTable.updateFonts(font);
	}

	@Override
	public void setLabels() {
		// statTable.setLabels(null, getColumnNames());
	}

}
