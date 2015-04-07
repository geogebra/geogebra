package org.geogebra.web.web.gui.view.data;

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
		setStyleName("daMultiVarStatistics");
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
	
	
	

	protected void initStatTable() {

		statTable = new StatTableW(app);
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
		getModel().updatePanel();
	}

	public String[] getDataTitles() {
		return daView.getDataTitles();
	}

	public boolean isMinimalTable() {
		// TODO Auto-generated method stub
		return isMinimalTable;
	}

	@Override
    public void setValueAt(double value, int row, int column) {
		   super.setValueAt(value, row, column + 1);
	    }

}
