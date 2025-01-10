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
