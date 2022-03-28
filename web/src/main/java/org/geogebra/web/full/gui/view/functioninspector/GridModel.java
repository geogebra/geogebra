package org.geogebra.web.full.gui.view.functioninspector;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.util.debug.Log;

public class GridModel {
	private static final String PREFIX = "[GRID MODEL]";

	private IGridListener listener;
	private List<String> headers;
	private List<List<DataCell>> data;
	private int columnCount;
	private int rowCount;
	private DataCell editCell;

	public interface IGridListener {
		/** Column/header operations */
		void appendColumn(String name);

		void setHeaders(String[] names);

		void updateHeader(int col, String title);

		void removeColumn();

		/** Body cell operations */
		void updateDataCell(int row, int col, DataCell cell);

		void removeLastCell(int row);

		/** Row operations */
		void addRow(List<DataCell> row);

		void removeAllRows();

		void removeLastRow();
	}
	
	public static class DataCell {
		private boolean editable;
		private Object value;

		/**
		 * @param value
		 *            data
		 * @param editable
		 *            whether it's ediatble
		 */
		public DataCell(Object value, boolean editable) {
			this.value = value;
			this.setEditable(editable);
		}

		public boolean isEditable() {
	        return editable;
        }

		public void setEditable(boolean editable) {
	        this.editable = editable;
        }
		
		@Override
		public String toString() {
			String result = "";
			if (value != null) {
				return value.toString();
			}
			return result;
		}
	}

	/**
	 * 
	 * @param col
	 *            number of columns
	 * @param listener
	 *            listener
	 */
	public GridModel(int col, IGridListener listener) {
		columnCount = col;
		rowCount = 0;
		this.listener = listener;
		headers = new ArrayList<>();
		data = new ArrayList<>();
		editCell = null;
	}

	/**
	 * @param row
	 *            row
	 * @param col
	 *            column
	 * @param value
	 *            value
	 */
	public void setData(int row, int col, Object value) {
		if (col < getColumnCount() && row < getRowCount())  {
			Log.debug("[GRID MODEL] setData(" + row + ", " + col + ", " + value
					+ ")");
			DataCell cell = new DataCell(value, false);
			data.get(row).set(col, cell);
			listener.updateDataCell(row, col, cell);
		}
	}

	/**
	 * @param row
	 *            row
	 * @param col
	 *            column
	 * @return cell content
	 */
	public String getData(int row, int col) {
		// // Log.debug("[GRIDMODEL] getData(" + col + ", " + row + ")");
		DataCell result = null;
		if (col < columnCount && row < rowCount)  {
			List<DataCell> list = data.get(row);
			result = list.get(col);
		}
		// Log.debug("[GRIDMODEL] = " + result);
		
		return result == null ? "" : result.toString();
	}

	/**
	 * @param col
	 *            column
	 * @return column's header
	 */
	public String getHeader(int col) {
		if (col < getColumnCount())  {
			return headers.get(col);
		}
		return "";
	}

	/**
	 * Clear the model and UI.
	 */
	public void removeAll() {
		data.clear();
		listener.removeAllRows();
	}

	/**
	 * @param row
	 *            row data
	 */
	public void addRow(List<DataCell> row) {
		// Log.debug("[GRIDMODEL] addRow(" + row + ")");
		data.add(row);
		listener.addRow(row);
	}
	
	/**
	 * Add a row with given cell contents.
	 * 
	 * @param values
	 *            row content
	 */
	public void addAsRow(List<String> values) {
		List<DataCell> row = new ArrayList<>();
		for (Object value: values)  {
			row.add(new DataCell(value, false));
		}
		
		addRow(row);
	}

	/**
	 * @param names
	 *            header titles
	 */
	public void setHeaders(String[] names) {
		headers.clear();
		for (String title: names) {
			headers.add(title);
		}
		listener.setHeaders(names);
	}

	/**
	 * @return number of columns
	 */
	public int getColumnCount() {
		return headers.size(); // columnCount;
	}

	/**
	 * @return number of rows
	 */
	public int getRowCount() {
		return rowCount;
	}

	private List<DataCell> newRowCells() {
		ArrayList<DataCell> cells = new ArrayList<>();
		for (int col = 0; col < columnCount; col++) {
			cells.add(new DataCell(null, false));
		}
		return cells;
	}
	
	/**
	 * @param rows
	 *            number of rows
	 */
	public void setRowCount(int rows) {
		if (rows == rowCount) {
			return;
		}
		
		if (rows > rowCount) {
			for (int row = rowCount; row < rows; row++) {
				addRow(newRowCells());
			}
		} else {
			for (int row = rowCount; row > rows; row--) {
				removeLastRow();
			}
		}
		this.rowCount = rows;
	}

	private void removeLastRow() {
		data.remove(data.size() - 1);
		listener.removeLastRow();
	}

	/**
	 * @param name
	 *            column name
	 */
	public void addColumn(String name) {
		columnCount++;
		headers.add(name);
		for (List<DataCell> rowData: data) {
			rowData.add(new DataCell(null, false));
		}
		// Log.debug(headers.toString());
		listener.appendColumn(name);
	}

	/**
	 * Remove last column
	 */
	public void removeColumn() {
		// Log.debug("removeColumn");
		int col = headers.size() - 1;
		
		// Log.debug(headers.toString());
		headers.remove(col);
		for (int row = 0; row < data.size(); row++) {
			List<DataCell> rowData = data.get(row);
			if (col < rowData.size()) {
				rowData.remove(col);
			} else {
				// Log.debug("Warning: rowData size is " + rowData.size());
			}
		}
		
		listener.removeColumn();
		columnCount--;
	}

	/**
	 * Make a cell editable.
	 * 
	 * @param row
	 *            row
	 * @param col
	 *            column
	 */
	public void setCellEditable(int row, int col) {
		if (editCell != null) {
			editCell.setEditable(false);
		}

		if (row >= 0 &&  row < data.size() && col >= 0 && col < getColumnCount()) {
			editCell = data.get(row).get(col);
			editCell.setEditable(false);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n " + PREFIX + "    ");
		for (String header: headers) {
			sb.append(header);
			sb.append(" ");
		}
		sb.append("\n");
		int rowIdx = 0;
		for (List<DataCell> row: data) {
			sb.append(PREFIX);
			sb.append(rowIdx);
			sb.append(". : (");
			for (DataCell cell: row) {
				sb.append(cell.toString());
				sb.append(" ");
			}
			rowIdx++;
			sb.append(")\n");
		}
		return sb.toString();
	}
}
