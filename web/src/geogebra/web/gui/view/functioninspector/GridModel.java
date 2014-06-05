package geogebra.web.gui.view.functioninspector;

import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.List;

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
	};
	
	private IGridListener listener;
	private List<String> headers;
	private List<List<DataCell>> data;
	private int columnCount;
	private int rowCount;

	public GridModel(int col, IGridListener listener) {
		columnCount = col;
		rowCount = 0;
		this.listener = listener;
		headers = new ArrayList<String>();
		data = new ArrayList<List<DataCell>>();
	}         


	public void setHeader(int col, String title) {
		if (col < getColumnCount())  {
			headers.set(col, title);
			App.debug("[GRIDMODEL] setHeader(" + col + "," + title +")");
			listener.updateHeader(col, title);
		}
	}

	public void setData(int row, int col, Object value) {
		App.debug("[GRIDMODEL] setData(" + row + ", " + col + ", " +value + ")");
		if (col < getColumnCount() && row < getRowCount())  {
			List<DataCell> list = data.get(row);
			DataCell cell = new DataCell(value, false);
			list.set(col, cell);
			listener.updateDataCell(row, col, cell);
		}
	}

	public DataCell getData(int col, int row) {
		App.debug("[GRIDMODEL] getData(" + col + ", " + row + ")");
		DataCell result = null;
		if (col < columnCount && row < rowCount)  {
			List<DataCell> list = data.get(row);
			result = list.get(col);
			
		}
		App.debug("[GRIDMODEL] = " + result);
		
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
		App.debug("[GRIDMODEL] addRow(" + row + ")");
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

	public void setRowCount(int rows) {
		if (rows == rowCount) {
			return;
		}
		
		if (rows > rowCount) {
			List<DataCell> rowData = new ArrayList<DataCell>();
			for (int col=0; col < columnCount; col++) {
				rowData.add(new DataCell(null, false));
			}
			for (int row=rowCount;row  < rows; row++ ) {
				addRow(rowData);
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
		App.debug(headers.toString());
		listener.appendColumn(name);
	}

	public void removeColumn() {
		App.debug("removeColumn");
		int col = headers.size() - 1;
		
		App.debug(headers.toString());
		headers.remove(col);
		for (int row = 0; row < data.size(); row++) {
			List<DataCell> rowData = data.get(row);
			if (col < rowData.size()) {
				rowData.remove(col);
			} else {
				App.debug("Warning: rowData size is " + rowData.size());
			}
			
			
		}
		
		listener.removeColumn();
		columnCount--;

	}
}
