package geogebra.web.gui.view.functioninspector;

import geogebra.common.main.App;
import geogebra.web.gui.view.functioninspector.GridModel.IGridListener;

import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;


public class InspectorTableW extends FlexTable implements IGridListener {
	private static final int HEADER_ROW = 0;
	private static final String TABLE_PREFIX = "[INSPECTOR_TABLE]";
	private GridModel model;
	private int selectedRow;
	public InspectorTableW(int col) {
		super();
		setStyleName("inspectorTable");
		setWidth("100%");
		setModel(new GridModel(col, this));
		selectedRow = 1;
	}

	public void updateHeader(int col, String title) {
		setCellWidget(HEADER_ROW, col, "inspectorTableHeader", title);
    }

	public void updateCell(int row, int col, String value) {
		App.debug(TABLE_PREFIX + "updating cell at row: " + row 
				+ " col: " + col);
		Label label = (Label)getWidget(row, col);
		
		
		if (label != null) {
			label.setText(value);
		} else {
			setCellWidget(row, col, "inspectorTableData", value);
		}

	}

	protected void setCellWidget(int row, int col, String style, String value) {
		Label label = new Label(value);
		getCellFormatter().setStyleName(row, col, style);
		setWidget(row, col, label);
	}
	public GridModel getModel() {
	    return model;
    }

	public void setModel(GridModel model) {
	    this.model = model;
    }

	public void addRow(List<String> row) {
		int numRows = getRowCount();
		int col = 0;
		for (String cellText: row) {
			setCellWidget(numRows, col, "inspectorTableData", cellText);
			col++;
		}
    }

	public void setHeaders(String[] headers) {
		int col = 0;
		for (String title: headers) {
			updateHeader(col, title);
			col++;
		}
    }

	public int getSelectedRow() {
	    return selectedRow;
    }

	public void setSelectedRow(int selectedRow) {
	    this.selectedRow = selectedRow;
    }

	public void removeCell(int row) {
	    removeCell(row, model.getColumnCount());
    }

	public void appendColumn(String name) {
		int col = getCellCount(HEADER_ROW);
		App.debug(TABLE_PREFIX + " last column is: " + col);
		addCell(HEADER_ROW);
		setCellWidget(HEADER_ROW, col, "inspectorTableHeader", name);
		for (int row = 1; row < getRowCount(); row++) {
			setCellWidget(row, col, "inspectorTableData", 
					"(" + row + ", " + col +")");
		}
		
    }

}
