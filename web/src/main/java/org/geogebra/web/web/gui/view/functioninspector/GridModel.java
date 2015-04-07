package org.geogebra.web.web.gui.view.functioninspector;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;

public class GridModel {

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
	
	public class DataCell {
		private boolean editable;
		private Object value;

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

		};
	}

	private static final String PREFIX = "[GRID MODEL]";
	
	private IGridListener listener;
	private List<String> headers;
	private List<List<DataCell>> data;
	private int columnCount;
	private int rowCount;
	private DataCell editCell;

	public GridModel(int col, IGridListener listener) {
		columnCount = col;
		rowCount = 0;
		this.listener = listener;
		headers = new ArrayList<String>();
		data = new ArrayList<List<DataCell>>();
		editCell = null;
	}         


	public void setHeader(int col, String title) {
		if (col < getColumnCount())  {
			headers.set(col, title);
			// App.debug("[GRIDMODEL] setHeader(" + col + "," + title +")");
			listener.updateHeader(col, title);
		}
	}

	public void setData(int row, int col, Object value) {
		if (col < getColumnCount() && row < getRowCount())  {
			App.debug("[GRID MODEL] setData(" + row + ", " + col + ", " +value + ")");
			List<DataCell> list = data.get(row);
			DataCell cell = new DataCell(value, false);
			data.get(row).set(col, cell);
			listener.updateDataCell(row, col, cell);
			App.debug(toString());
		}
	}

	public DataCell getData(int row, int col) {
	//	// App.debug("[GRIDMODEL] getData(" + col + ", " + row + ")");
		DataCell result = null;
		if (col < columnCount && row < rowCount)  {
			List<DataCell> list = data.get(row);
			result = list.get(col);
			
		}
		// App.debug("[GRIDMODEL] = " + result);
		
		return result;
	}


	public String getHeader(int col) {
		if (col < getColumnCount())  {
			return headers.get(col);
		}
		return "";
	}

	public void removeAll() {
		data.clear();
		listener.removeAllRows();
	}

	
	public void addRow(List<DataCell> row) {
		// App.debug("[GRIDMODEL] addRow(" + row + ")");
		data.add(row);
		listener.addRow(row);
	}
	
	public void addAsRow(List<String> values) {
		List<DataCell> row = new ArrayList<DataCell>();
		for (Object value: values)  {
			row.add(new DataCell(value, false));
		}
		
		addRow(row);
	}

	public void setHeaders(String[] names) {
		headers.clear();
		int col = 0;
		for (String title: names) {
			headers.add(title);
			col++;
		}
		listener.setHeaders(names);

	}

	public int getColumnCount() {
		return headers.size();//columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public int getRowCount() {
		return rowCount;
	}

	private List<DataCell> newRowCells() {
		ArrayList<DataCell> cells = new ArrayList<DataCell>();
		for (int col=0; col < columnCount; col++) {
			cells.add(new DataCell(null, false));
		}
		return cells;
	}
	
	public void setRowCount(int rows) {
		if (rows == rowCount) {
			return;
		}
		
		if (rows > rowCount) {
			for (int row=rowCount;row  < rows; row++ ) {
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


	public void addColumn(String name) {
		columnCount++;
		headers.add(name);
		for (List<DataCell> rowData: data) {
			rowData.add(new DataCell(null, false));
		}
		// App.debug(headers.toString());
		listener.appendColumn(name);
	}

	public void removeColumn() {
		// App.debug("removeColumn");
		int col = headers.size() - 1;
		
		// App.debug(headers.toString());
		headers.remove(col);
		for (int row = 0; row < data.size(); row++) {
			List<DataCell> rowData = data.get(row);
			if (col < rowData.size()) {
				rowData.remove(col);
			} else {
				// App.debug("Warning: rowData size is " + rowData.size());
			}
			
			
		}
		
		listener.removeColumn();
		columnCount--;

	}


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
			sb.append(header + " ");
		}
		sb.append("\n");
		int rowIdx = 0;
		for (List<DataCell> row: data) {
			sb.append(PREFIX + rowIdx +  ". : (");
			for (DataCell cell: row) {
				sb.append(cell.toString() + " ");
			}
			rowIdx++;
			sb.append(")\n");
		}
		return sb.toString();
	}
}
