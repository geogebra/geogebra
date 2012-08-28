package geogebra.main;

import geogebra.common.main.App;
import geogebra.common.main.SpreadsheetTableModel;

import javax.swing.table.DefaultTableModel;

/**
 * Desktop implementation of AbstractTableModel. To handle the abstract table
 * methods an instance of the Swing DefaultTableModel class is constructed. This
 * DefaultTableModel is used by the spreadsheet as the data model for MyTable
 * (an extended JTable).
 * 
 * @author G. Sturr
 * 
 */
public class SpreadsheetTableModelD extends SpreadsheetTableModel {

	private DefaultTableModel defaultTableModel;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            application
	 * @param rows
	 *            number of rows
	 * @param columns
	 *            number of columns
	 */
	public SpreadsheetTableModelD(AppD app, int rows, int columns) {
		super(app, rows, columns);
		defaultTableModel = new DefaultTableModel(rows, columns);
		attachView();
		isIniting=false;
	}

	/**
	 * Gets the JTable table model.
	 * 
	 * @return instance of Swing DefaultTableModel class
	 */
	public DefaultTableModel getDefaultTableModel() {
		return defaultTableModel;
	}

	@Override
	public int getRowCount() {
		return defaultTableModel.getRowCount();
	}

	@Override
	public int getColumnCount() {
		return defaultTableModel.getColumnCount();
	}

	@Override
	public void setRowCount(int rowCount) {
		defaultTableModel.setRowCount(rowCount);

	}

	@Override
	public void setColumnCount(int columnCount) {
		defaultTableModel.setColumnCount(columnCount);

	}

	@Override
	public Object getValueAt(int row, int column) {
		return defaultTableModel.getValueAt(row, column);
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		// update column count if needed
		if (column >= defaultTableModel.getColumnCount()) {
			defaultTableModel.setColumnCount(column + 1);
		}
		defaultTableModel.setValueAt(value, row, column);
	}

	public boolean hasFocus() {
		App.debug("unimplemented");
		return false;
	}

	public void repaint() {
	    App.debug("unimplemented");
	}

	public boolean isShowing() {
	    App.debug("unimplemented");
		return false;
	}

}
