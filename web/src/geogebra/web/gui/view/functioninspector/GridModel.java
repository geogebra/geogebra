package geogebra.web.gui.view.functioninspector;

import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.List;

public class GridModel {
	public interface IGridListener {
		void updateHeader(int col, String title);
		void updateCell(int col, int row, String value);
		void addRow(List<String> row);
		void addCell(int row);
		void removeCell(int row);
		
		void removeAllRows();
		void setHeaders(String[] names);
	}

	private IGridListener listener;
	private List<String> headers;
	private List<List<String>> data;
	private int columnCount;
	private int rowCount;
	
	public GridModel(int col, IGridListener listener) {
		columnCount = col;
		this.listener = listener;
		headers = new ArrayList<String>();
		data = new ArrayList<List<String>>();
		init();
	}

	private void init() {
		for (int col=0; col < getColumnCount(); col++) {
			headers.add("");
		}
//
//		for (int row=0; row < rows; row++) {
//			List<String> aRow = new ArrayList<String>();
//			for (int col=0; col < columns; col++) {
//				aRow.add("");
//			}
//			data.add(aRow);
//		}
	}

	
	public void setHeader(int col, String title) {
		if (col < getColumnCount())  {
			headers.set(col, title);
			listener.updateHeader(col, title);
		}
	}

	public void setData(int col, int row, String value) {
		if (col < columnCount && row < rowCount)  {
			List<String> list = data.get(row);
			list.set(col, value);
			listener.updateCell(col, row, value);
		}
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
	    return columnCount;
    }

	public void setColumnCount(int columnCount) {
	    this.columnCount = columnCount;
    }

	public int getRowCount() {
	    return rowCount;
    }

	public void setRowCount(int rowCount) {
		removeAll();
		List<String> rowData = new ArrayList<String>();
		addRow(headers);
		for (int col=0; col < columnCount; col++) {
			rowData.add("");
		}
		for (int row=0;row  < rowCount; row++ ) {
	    	addRow(rowData);
	    }
	    this.rowCount = rowCount;
    }

	public void addColumn(String name) {
		columnCount++;
		headers.add(name);
		int row = 0;
		for (List<String> rowData: data) {
			rowData.add("");
			listener.addCell(row);
			row++;
		}
    }
	
	public void removeLastColumn() {
		headers.remove(columnCount);
		int row = 0;
		for (List<String> rowData: data) {
			rowData.remove(columnCount);
			listener.removeCell(row);
		}
		columnCount--;
		
    }
}
