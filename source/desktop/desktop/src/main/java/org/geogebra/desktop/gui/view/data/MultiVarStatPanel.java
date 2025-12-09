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

import org.geogebra.common.gui.view.data.MultiVarStatTableModel;
import org.geogebra.common.gui.view.data.MultiVarStatTableModel.MultiVarStatTableListener;
import org.geogebra.desktop.main.AppD;

/**
 * Extension of BasicStatTable that displays summary statistics for multiple
 * data sets.
 * 
 * @author G. Sturr
 * 
 */
public class MultiVarStatPanel extends BasicStatTable
		implements MultiVarStatTableListener {
	private static final long serialVersionUID = 1L;

	private boolean isMinimalTable = false;

	/**
	 * Constructs a MultiVarStatPanel
	 * 
	 * @param app application
	 * @param statDialog DA dialog
	 */
	public MultiVarStatPanel(AppD app, DataAnalysisViewD statDialog) {
		super(app, statDialog, false);
		setModel(new MultiVarStatTableModel(app, this));
	}

	/**
	 * @param isMinimalTable minimal table
	 */
	public void setMinimalTable(boolean isMinimalTable) {
		this.isMinimalTable = isMinimalTable;
		initStatTable();
	}

	@Override
	public int getRowCount() {
		return getModel().getRowCount();
	}

	@Override
	public int getColumnCount() {
		return getModel().getColumnCount();
	}

	@Override
	public void updatePanel() {
		if (getModel() == null) {
			return;
		}
		getModel().updatePanel();
		statTable.repaint();
	}

	@Override
	public String[] getDataTitles() {
		return daView.getDataTitles();
	}

	@Override
	public boolean isMinimalTable() {
		// TODO Auto-generated method stub
		return isMinimalTable;
	}

}
