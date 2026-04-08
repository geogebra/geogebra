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
import java.util.List;

import org.geogebra.common.gui.view.spreadsheet.MyTableInterface;

/**
 * Web implementation of the table model
 *
 */
public class SpreadsheetTableModelSimple extends SpreadsheetTableModel {

	private MyTableInterface table;

	private ChangeListener listener = null;

	// try with two-dimension ArrayList to represent two dimensions
	private final ArrayList<ArrayList<Object>> columns;

	// it is easier to store the rowNum and colNum than computing them
	private int rowNum;

	/**
	 * Listens to changes in table size and values
	 */
	public interface ChangeListener {
		/**
		 * Fired when a row/column is added/removed
		 */
		void dimensionChange();

		/**
		 * Fired when a value is changed
		 */
		void valueChange();
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
		this.columns = new ArrayList<>(columns);
		for (int i =  0; i < columns; i++) {
			this.columns.add(null);
		}
		attachView();
		isIniting = false;
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
				for (int j = 0; j < columns.size(); j++) {
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
		return columns.size();
	}

	@Override
	public void setRowCount(int rowCount) {
		// only shrink lists that are too long,
		// expanding lists that are too short happens in setValue
		if (rowNum == rowCount) {
			return;
		} else if (rowNum > rowCount) {
			for (ArrayList<Object> column : columns) {
				if (column != null && column.size() > rowCount) {
					column.subList(rowCount, column.size()).clear();
				}
			}
		}
		rowNum = rowCount;
		notifyDimensionChange();
	}

	@Override
	public void setColumnCount(int columnCount) {
		int colNum = columns.size();
		if (colNum == columnCount) {
			return;
		} else if (colNum > columnCount) {
			columns.subList(columnCount, colNum).clear();
		} else {
			for (int col = colNum; col < columnCount; col++) {
				columns.add(null);
			}
		}
		notifyDimensionChange();
	}

	@Override
	public Object getValueAt(int row, int column) {
		List<Object> columnValues = columns.get(column);
		return columnValues != null && columnValues.size() > row
				? columnValues.get(row) : null;
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		// Log.debug(row + "," + column);
		// update column count if needed
		if (column >= getColumnCount()) {
			setColumnCount(column + 1);
			notifyDimensionChange();
		}
		ArrayList<Object> columnValues = columns.get(column);
		if (columnValues == null) {
			columns.set(column, columnValues = new ArrayList<>());
		}
		if (columnValues.size() <= row) {
			for (int r = columnValues.size(); r <= row; r++) {
				columnValues.add(null);
			}
		}
		if (row >= rowNum) {
			rowNum = row + 1;
			notifyDimensionChange();
		}
		if (value != null || columnValues.get(row) != null) {
			columnValues.set(row, value);
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

	private void notifyDimensionChange() {
		if (listener != null) {
			listener.dimensionChange();
		}
	}

	@Override
	protected void resetValues() {
		columns.clear();
		rowNum = 0;
	}

	@Override
	public boolean hasFocus() {
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
