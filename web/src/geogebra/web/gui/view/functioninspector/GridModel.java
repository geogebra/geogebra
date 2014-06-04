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
		void updateCell(int col, int row, String value);
		void removeLastCell(int row);

		/** Row operations */
		void addRow(List<String> row);
		void removeAllRows();
		void removeLastRow();
	}

	private IGridListener listener;
	private List<String> headers;
	private List<List<String>> data;
	private int columnCount;
	private int rowCount;

	public GridModel(int col, IGridListener listener) {
		columnCount = col;
		rowCount = 0;
		this.listener = listener;
		headers = new ArrayList<String>();
		data = new ArrayList<List<String>>();
	}


	public void setHeader(int col, String title) {
		if (col < getColumnCount())  {
			headers.set(col, title);
			listener.updateHeader(col, title);
		}
	}

	public void setData(int col, int row, String value) {
//		if (col < columnCount && row < rowCount)  {
//			List<String> list = data.get(row);
//			list.set(col, value);
//			listener.updateCell(col, row, value);
//		}
	}

	public String getData(int col, int row) {
		String result = "";
		if (col < columnCount && row + 1 < rowCount)  {
			List<String> list = data.get(row + 1);
			result = list.get(col);
		}

		App.debug("[GRIDMODEL] getData(" + col + ", " + row + ") = " + result);

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

	public void addRow(List<String> row) {
		data.add(row);
		listener.addRow(row);
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
			List<String> rowData = new ArrayList<String>();
			for (int col=0; col < columnCount; col++) {
				rowData.add("");
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
			List<String> rowData = data.get(row);
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
