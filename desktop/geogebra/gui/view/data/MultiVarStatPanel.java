package geogebra.gui.view.data;

import geogebra.common.gui.view.data.MultiVarStatTableModel;
import geogebra.common.gui.view.data.MultiVarStatTableModel.MultiVarStatTableListener;
import geogebra.main.AppD;

/**
 * Extension of BasicStatTable that displays summary statistics for multiple
 * data sets.
 * 
 * @author G. Sturr
 * 
 */
public class MultiVarStatPanel extends BasicStatTable implements
		MultiVarStatTableListener {
	private static final long serialVersionUID = 1L;

	private boolean isMinimalTable = false;
	private MultiVarStatTableModel model;

	/***************************************************
	 * Constructs a MultiVarStatPanel
	 * 
	 * @param app
	 * @param statDialog
	 */
	public MultiVarStatPanel(AppD app, DataAnalysisViewD statDialog) {
		super(app, statDialog);
		model = new MultiVarStatTableModel(app, this);
	}

	public void setMinimalTable(boolean isMinimalTable) {
		this.isMinimalTable = isMinimalTable;
		initStatTable();

	}

	@Override
	public String[] getRowNames() {
		return model.getRowNames();
	}

	@Override
	public String[] getColumnNames() {
		return model.getColumnNames();
	}

	@Override
	public int getRowCount() {
		return model.getRowCount();
	}

	@Override
	public int getColumnCount() {
		return model.getColumnCount();
	}

	@Override
	public void updatePanel() {
		model.updatePanel();
		statTable.repaint();
	}

	public String[] getDataTitles() {
		return daView.getDataTitles();
	}

	public boolean isMinimalTable() {
		// TODO Auto-generated method stub
		return isMinimalTable;
	}

}
