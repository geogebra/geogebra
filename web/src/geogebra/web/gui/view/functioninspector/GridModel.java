package geogebra.web.gui.view.functioninspector;

import java.util.ArrayList;
import java.util.List;

public class GridModel {
	public interface IGridListener {
		void updateHeader(int col, String title);
		void updateCell(int col, int row, String value);
		void addRow(List<String> row);
		void removeAllRows();
	}

	private IGridListener listener;
	private List<String> headers;
	private List<List<String>> data;
	private int columns;

	public GridModel(int col, IGridListener listener) {
		this.setColunms(col);
		this.listener = listener;
		headers = new ArrayList<String>();
		data = new ArrayList<List<String>>();
		init();
	}

	private void init() {
		for (int col=0; col < columns; col++) {
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

	public int getRows() {
		return data.size();
	}

	
	public int getColunms() {
		return columns;
	}

	public void setColunms(int colunms) {
		this.columns = colunms;
	}

	public void setHeader(int col, String title) {
		if (col < columns)  {
			headers.set(col, title);
			listener.updateHeader(col, title);
		}
	}

	public void setData(int col, int row, String value) {
		if (col < columns && row < getRows())  {
			List<String> list = data.get(row);
			list.set(col, value);
			listener.updateCell(col, row, value);
		}
	}

	
	public String getHeader(int col) {
		if (col < columns)  {
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
}
