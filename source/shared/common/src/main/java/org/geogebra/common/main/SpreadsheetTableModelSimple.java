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

package org.geogebra.common.main;

import java.util.ArrayList;

import org.geogebra.common.gui.view.spreadsheet.MyTableInterface;
import org.geogebra.common.util.debug.Log;

/**
 * Web implementation of the table model
 *
 */
public class SpreadsheetTableModelSimple extends SpreadsheetTableModel {

	private MyTableInterface table;

	private ChangeListener listener = null;

	// try with one-dimension ArrayList to represent two dimensions
	private ArrayList<Object> defaultTableModel;

	// it is easier to store the rowNum and colNum than computing them
	private int rowNum = 0;
	private int colNum = 0;

	/**
	 * Listens to changes in table size and values
	 */
	public interface ChangeListener {
		/**
		 * Fired when a row/column is added/removed
		 */
		public void dimensionChange();

		/**
		 * Fired when a value is changed
		 */
		public void valueChange();
	}

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
	public SpreadsheetTableModelSimple(App app, int rows, int columns) {
		super(app);
		rowNum = rows;
		colNum = columns;
		defaultTableModel = new ArrayList<>(rows * columns);
		for (int i = 0; i < rows * columns; i++) {
			defaultTableModel.add(null);
		}
		attachView();
		isIniting = false;
	}

	/**
	 * Gets the JTable table model.
	 * 
	 * @return instance of Swing DefaultTableModel class
	 */
	public ArrayList<Object> getDefaultTableModel() {
		return defaultTableModel;
	}

	/**
	 * Establishes connection to the table and synces all values from model to
	 * table
	 * 
	 * @param newTable
	 *            table implementation
	 */
	public void attachMyTable(MyTableInterface newTable) {
		this.table = newTable;
		Object value;
		if (newTable != null) {
			for (int i = 0; i < rowNum; i++) {
				for (int j = 0; j < colNum; j++) {
					if ((value = getValueAt(i, j)) != null) {
						newTable.updateTableCellValue(value, i, j);
					}
				}
			}
		}
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
			for (int i = rowNum * colNum - 1; i >= rowCount * colNum; i--) {
				defaultTableModel.remove(i);
			}
		} else {
			defaultTableModel.ensureCapacity(rowCount * colNum);
			for (int i = rowNum * colNum; i < rowCount * colNum; i++) {
				defaultTableModel.add(null);
			}
		}
		rowNum = rowCount;
		if (listener != null) {
			listener.dimensionChange();
		}
	}

	@Override
	public void setColumnCount(int columnCount) {
		if (colNum == columnCount) {
			return;
		} else if (colNum > columnCount) {
			for (int i = rowNum - 1; i >= 0; i--) {
				for (int j = colNum - 1; j >= columnCount; j--) {
					defaultTableModel.remove(i * colNum + j);
				}
			}
		} else {
			defaultTableModel.ensureCapacity(rowNum * columnCount);
			for (int i = rowNum - 1; i >= 0; i--) {
				for (int j = colNum; j < columnCount; j++) {
					if (i * colNum + j >= defaultTableModel.size()) {
						defaultTableModel.add(null);
					} else {
						defaultTableModel.add(i * colNum + j, null);
					}
				}
			}
		}
		colNum = columnCount;
		if (listener != null) {
			listener.dimensionChange();
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		return defaultTableModel.get(row * colNum + column);
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		// Log.debug(row + "," + column);
		// update column count if needed
		if (column >= getColumnCount()) {
			setColumnCount(column + 1);
			if (listener != null) {
				listener.dimensionChange();
			}
		}

		if (row >= getRowCount()) {
			setRowCount(row + 1);
			if (listener != null) {
				listener.dimensionChange();
			}
		}

		if (value != null || defaultTableModel.get(row * colNum + column) != null) {
			defaultTableModel.set(row * colNum + column, value);
			if (table != null) {

				table.updateTableCellValue(value, row, column);
				// do this after updateTableCellValue, as it does no harm
				// and the valueChange might need the table cell value!
				if (listener != null) {
					listener.valueChange();
				}
			}
		}

	}

	@Override
	public boolean hasFocus() {
		Log.debug("unimplemented");
		return false;
	}

	/**
	 * @param cl
	 *            change listener
	 */
	public void setChangeListener(ChangeListener cl) {
		listener = cl;
	}

	@Override
	public boolean suggestRepaint() {
		// repaint not needed
		return false;
	}
}
