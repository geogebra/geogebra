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

package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.gui.view.data.MultiVarStatTableModel;
import org.geogebra.common.gui.view.data.MultiVarStatTableModel.MultiVarStatTableListener;
import org.geogebra.web.html5.main.AppW;

/**
 * Extension of BasicStatTable that displays summary statistics for multiple
 * data sets.
 * 
 * @author G. Sturr
 * 
 */
public class MultiVarStatPanelW extends BasicStatTableW implements
		MultiVarStatTableListener {

	private boolean minimalTable = false;

	/***************************************************
	 * Constructs a MultiVarStatPanel
	 * 
	 * @param app
	 *            application
	 * @param statDialog
	 *            data analysis view
	 */
	public MultiVarStatPanelW(AppW app, DataAnalysisViewW statDialog) {
		super(app, statDialog, false);
		setModel(new MultiVarStatTableModel(app, this));
		setStyleName("daMultiVarStatistics");
	}

	/**
	 * @param isMinimalTable
	 *            whether this table is minimal (just length, mean, SD)
	 */
	public void setMinimalTable(boolean isMinimalTable) {
		this.minimalTable = isMinimalTable;
		initStatTable();
	}

	@Override
	public String[] getRowNames() {
		return getModel().getRowNames();
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
	public int getRowCount() {
		return getModel().getRowCount() - 1;
	}

	@Override
	protected void initStatTable() {
		statTable = new StatTableW();
		statTable.setStatTable(getModel().getRowCount(), getModel().getRowNames(),
				getColumnCount() + 1, getColumnNames());
		clear();
		add(statTable);
	}
	
	@Override
	public int getColumnCount() {
		return getModel().getColumnCount();
	}

	@Override
	public void updatePanel() {
		app.getAsyncManager().scheduleCallback(getModel()::updatePanel);
	}

	@Override
	public String[] getDataTitles() {
		return daView.getDataTitles();
	}

	@Override
	public boolean isMinimalTable() {
		return minimalTable;
	}

}
