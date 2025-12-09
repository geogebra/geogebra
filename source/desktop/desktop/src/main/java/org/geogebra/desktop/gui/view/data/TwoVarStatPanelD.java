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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import org.geogebra.common.gui.view.data.TwoVarStatModel;
import org.geogebra.common.gui.view.data.TwoVarStatModel.TwoVarStatListener;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.desktop.main.AppD;

/**
 * Extension of StatTable that displays summary statistics for two data sets.
 * The two data sets are taken from the current collection of data provided by a
 * MultiVar StatDialog. JComboBoxes for choosing data sets are embedded in the
 * table.
 * 
 * @author G. Sturr
 * 
 */
public class TwoVarStatPanelD extends StatTable
		implements ActionListener, TwoVarStatListener {
	private static final long serialVersionUID = 1L;
	protected AppD app;
	protected DataAnalysisViewD statDialog;
	protected MyTable statTable;
	private final TwoVarStatModel model;
	private final ActionListener parentActionListener;
	private boolean isIniting;

	/**
	 * @param app application
	 * @param statDialog stats dialog
	 * @param isPairedData whether to use paired data
	 * @param parentActionListener action listener
	 */
	public TwoVarStatPanelD(AppD app, DataAnalysisViewD statDialog,
			boolean isPairedData, ActionListener parentActionListener) {
		super(app);
		model = new TwoVarStatModel(app, isPairedData, this);
		this.app = app;
		this.statDialog = statDialog;
		statTable = this.getTable();
		this.parentActionListener = parentActionListener;

		setTable(isPairedData);

	}

	/**
	 * @param isPairedData whether to use paired data
	 */
	public void setTable(boolean isPairedData) {
		isIniting = true;
		model.setPairedData(isPairedData);
		setStatTable(model.getRowCount(), model.getRowNames(),
				model.getColumnCount(), model.getColumnNames());

		// create an array of data titles for the table cell comboboxes
		// the array includes and extra element to store the combo box label
		String[] titles = statDialog.getDataTitles();
		String[] titlesPlusLabel = new String[titles.length + 1];
		System.arraycopy(titles, 0, titlesPlusLabel, 0, titles.length);

		// create hash maps for the table comboboxes:
		// key = cell location for the combo box
		// value = String[] to hold menu items plus label
		HashMap<Point, String[]> cellMap = new HashMap<>();
		titlesPlusLabel[titlesPlusLabel.length - 1] = loc.getMenu("Sample1");
		cellMap.put(new Point(0, 0), titlesPlusLabel.clone());
		titlesPlusLabel[titlesPlusLabel.length - 1] = loc.getMenu("Sample2");
		cellMap.put(new Point(1, 0), titlesPlusLabel.clone());

		// set the table combo boxes
		int idx0 = model.getSelectedDataIndex0();
		int idx1 = model.getSelectedDataIndex1();

		setComboBoxCells(cellMap, this);
		setComboCellSelectedIndex(idx0, 0, 0);
		setComboCellSelectedIndex(idx1, 1, 0);
		getModel().setValueAt(idx0, 0, 0);
		getModel().setValueAt(idx1, 1, 0);

		this.setMinimumSize(this.getPreferredSize());

		isIniting = false;
	}

	/**
	 * Update the UI
	 */
	public void updatePanel() {
		model.update();
		this.setMinimumSize(this.getPreferredSize());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (isIniting) {
			return;
		}

		if (e.getActionCommand().equals("updateTable")) {
			model.setSelectedDataIndex0(getComboCellEditorSelectedIndex(0, 0));
			model.setSelectedDataIndex1(getComboCellEditorSelectedIndex(1, 0));
			this.updatePanel();
		}
		parentActionListener.actionPerformed(e);
	}

	@Override
	public void setValueAt(String value, int row, int col) {
		statTable.setValueAt(value, row, col);

	}

	@Override
	public void setValueAt(double value, int row, int col) {
		statTable.setValueAt(statDialog.format(value), row, col);
	}

	@Override
	public GeoList getDataSelected() {
		return statDialog.getController().getDataSelected();
	}

	public Integer[] getSelectedDataIndex() {
		return model.getSelectedDataIndex();
	}

}
