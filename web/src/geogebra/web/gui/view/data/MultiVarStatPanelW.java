package geogebra.web.gui.view.data;

import geogebra.common.gui.view.data.MultiVarStatTableModel;
import geogebra.common.gui.view.data.MultiVarStatTableModel.MultiVarStatTableListener;
import geogebra.html5.main.AppW;

/**
 * Extension of BasicStatTable that displays summary statistics for multiple
 * data sets.
 * 
 * @author G. Sturr
 * 
 */
public class MultiVarStatPanelW extends BasicStatTableW implements
		MultiVarStatTableListener {
	private static final long serialVersionUID = 1L;

	private boolean isMinimalTable = false;

	/***************************************************
	 * Constructs a MultiVarStatPanel
	 * 
	 * @param app
	 * @param statDialog
	 */
	public MultiVarStatPanelW(AppW app, DataAnalysisViewW statDialog) {
		super(app, statDialog, false);
		setModel(new MultiVarStatTableModel(app, this));
	}

	public void setMinimalTable(boolean isMinimalTable) {
		this.isMinimalTable = isMinimalTable;
		initStatTable();

	}

	@Override
	public String[] getRowNames() {
		return getModel().getRowNames();
	}

	@Override
	public String[] getColumnNames() {
		return getModel().getColumnNames();
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
		getModel().updatePanel();
	}

	public String[] getDataTitles() {
		return daView.getDataTitles();
	}

	public boolean isMinimalTable() {
		// TODO Auto-generated method stub
		return isMinimalTable;
	}

}
