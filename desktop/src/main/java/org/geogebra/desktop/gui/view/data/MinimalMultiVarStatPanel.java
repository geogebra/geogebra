package org.geogebra.desktop.gui.view.data;

import org.geogebra.common.gui.view.data.MinimalMultiVarStatTableModel;
import org.geogebra.common.gui.view.data.MultiVarStatTableModel.MultiVarStatTableListener;
import org.geogebra.desktop.main.AppD;

/**
 * Extension of BasicStatTable that displays summary statistics for multiple
 * data sets.
 * 
 * @author G. Sturr
 * 
 */
public class MinimalMultiVarStatPanel extends BasicStatTable implements
		MultiVarStatTableListener {
	private static final long serialVersionUID = 1L;

	public MinimalMultiVarStatPanel(AppD app, DataAnalysisViewD statDialog) {
		super(app, statDialog, false);
		setModel(new MinimalMultiVarStatTableModel(app, this));
	}

	public String[] getDataTitles() {
		return daView.getDataTitles();
	}

	public boolean isMinimalTable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getColumnNames() {
		return getModel().getColumnNames();
	}

	@Override
	public int getRowCount() {
		return getRowNames().length;
	}

	@Override
	public int getColumnCount() {
		return getColumnNames().length;
	}

}
