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
import java.awt.Font;

import javax.swing.JPanel;

import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.gui.view.data.StatTableModel;
import org.geogebra.common.gui.view.data.StatTableModel.StatTableListener;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.statistics.Regression;
import org.geogebra.desktop.main.AppD;

/**
 * Displays statistics for DataAnalysisView when in one variable or regression
 * mode.
 * 
 * @author G. Sturr
 * 
 */
public class BasicStatTable extends JPanel
		implements StatPanelInterface, StatTableListener {
	private static final long serialVersionUID = 1L;
	private StatTableModel model;

	protected DataAnalysisViewD daView;
	protected StatTable statTable;
	private AppD app;

	/**
	 * Construct the panel
	 * 
	 * @param app application
	 * @param statDialog DA dialog
	 */
	public BasicStatTable(AppD app, DataAnalysisViewD statDialog) {
		this(app, statDialog, true);
	}

	/**
	 * Construct the panel
	 *
	 * @param app application
	 * @param statDialog DA dialog
	 */
	public BasicStatTable(AppD app, DataAnalysisViewD statDialog,
			boolean defaultModel) {
		this.app = app;
		this.daView = statDialog;
		this.setLayout(new BorderLayout());
		if (defaultModel) {
			setModel(new StatTableModel(app, this));
		}
	}

	/**
	 * @param model stat table model
	 */
	public void setModel(StatTableModel model) {
		this.model = model;
		initStatTable();
		updateFonts(app.getPlainFont());
	}

	protected void initStatTable() {

		statTable = new StatTable(getApp());
		statTable.setStatTable(getModel().getRowCount(),
				getModel().getRowNames(), getModel().getColumnCount(),
				getModel().getColumnNames());
		this.removeAll();
		this.add(statTable, BorderLayout.CENTER);
	}

	public int getRowCount() {
		return getModel().getRowCount();
	}

	public int getColumnCount() {
		return getModel().getColumnCount();
	}

	// =======================================================

	/**
	 * Evaluates all statistics for the selected data list. If data source is
	 * not valid, the result cells are set blank.
	 * 
	 */
	@Override
	public void updatePanel() {
		statTable.setStatTable(getModel().getRowCount(),
				getModel().getRowNames(), getModel().getColumnCount(),
				getModel().getColumnNames());
		getModel().updatePanel();
	}

	@Override
	public void updateFonts(Font font) {
		statTable.updateFonts(font);
	}

	@Override
	public void setLabels() {
		statTable.setLabels(getModel().getRowNames(),
				getModel().getColumnNames());
	}

	@Override
	public GeoList getDataSelected() {
		return daView.getController().getDataSelected();
	}

	@Override
	public GeoElement getRegressionModel() {
		return daView.getRegressionModel();
	}

	@Override
	public Regression getRegressionMode() {
		return daView.getModel().getRegressionMode();
	}

	@Override
	public boolean isValidData() {
		return daView.getController().isValidData();
	}

	@Override
	public void setValueAt(double value, int row, int column) {
		statTable.getModel().setValueAt(daView.getModel().format(value), row,
				column);
	}

	@Override
	public boolean isViewValid() {
		return daView == null || daView.getDataSource() == null;
	}

	@Override
	public int getMode() {
		return daView.getModel().getMode();
	}

	@Override
	public GroupType groupType() {
		return daView.groupType();
	}

	@Override
	public boolean isNumericData() {
		return daView.getDataSource().isNumericData();
	}

	public AppD getApp() {
		return app;
	}

	public void setApp(AppD app) {
		this.app = app;
	}

	public StatTableModel getModel() {
		return model;
	}

}
