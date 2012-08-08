package geogebra.web.gui.view.spreadsheet;

import geogebra.common.gui.view.spreadsheet.SpreadsheetTableModel;
import geogebra.common.main.App;
import geogebra.web.main.AppW;

import java.util.ArrayList;

public class SpreadsheetTableModelW extends SpreadsheetTableModel {

	// try with one-dimension ArrayList to represent two dimensions
	private ArrayList<Object> defaultTableModel;

	// it is easier to store the rowNum and colNum than computing them
	int rowNum = 0;
	int colNum = 0;

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
	public SpreadsheetTableModelW(AppW app, int rows, int columns) {
		super(app, rows, columns);
		rowNum = rows;
		colNum = columns;
		defaultTableModel = new ArrayList(rows * columns);
		for (int i = 0; i < rows * columns; i++)
			defaultTableModel.add(null);
		attachView();
		isIniting=false;
	}

	/**
	 * Gets the JTable table model.
	 * 
	 * @return instance of Swing DefaultTableModel class
	 */
	public ArrayList<Object> getDefaultTableModel() {
		return defaultTableModel;
	}

	@Override
	public int getRowCount() {
		return rowNum;
	}

	@Override
	public int getColumnCount() {
		return colNum;
	}

	@Override
	public void setRowCount(int rowCount) {
		if (rowNum == rowCount) {
			return;
		} else if (rowNum > rowCount) {
			for (int i = rowNum * colNum - 1; i >= rowCount * colNum; i--)
				defaultTableModel.remove(i);
		} else {
			for (int i = rowCount * colNum; i < rowNum * colNum; i++)
				defaultTableModel.add(null);
		}
		rowNum = rowCount;
	}

	@Override
	public void setColumnCount(int columnCount) {
		if (colNum == columnCount) {
			return;
		} else if (colNum > columnCount) {
			for (int i = rowNum - 1; i >= 0; i--)
				for (int j = colNum - 1; j >= columnCount; j--)
					defaultTableModel.remove(i*colNum + j);
		} else {
			for (int i = rowNum - 1; i >= 0; i--)
				for (int j = colNum; j < columnCount; j++)
					if (i*colNum + j >= defaultTableModel.size())
						defaultTableModel.add(null);
					else
						defaultTableModel.add(i*colNum + j, null);
		}
		colNum = columnCount;
	}

	@Override
	public Object getValueAt(int row, int column) {
		return defaultTableModel.get(row*colNum+column);
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		// update column count if needed
		if (column >= getColumnCount()) {
			setColumnCount(column + 1);
		}
		defaultTableModel.set(row*colNum+column, value);
	}

	public boolean hasFocus() {
		App.debug("unimplemented");
		return false;
	}

}
