package geogebra.web.gui.view.functioninspector;

import geogebra.common.main.App;
import geogebra.web.gui.view.functioninspector.GridModel.DataCell;
import geogebra.web.gui.view.functioninspector.GridModel.IGridListener;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
		addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				updateRowStyle(getCellForEvent(event), "selected");
			}
		});
		
		
	}

	private void updateRowStyle(Cell cell, String style) {
		if (cell == null) {
			return;
		}
		
		RowFormatter rf = getRowFormatter();
		rf.setStyleName(selectedRow, "");
		selectedRow = cell.getRowIndex();
		rf.setStyleName(selectedRow, "selected");
	
	}
	public void updateHeader(int col, String title) {
		setCellWidget(HEADER_ROW, col, "inspectorTableHeader", title);
    }
	
	
	public void updateDataCell(int row, int col, DataCell value) {
		// Cells at row 0 are headers.
		updateCell(row + 1, col, value);
	}
		
	protected void updateCell(int row, int col, DataCell value) {
		App.debug(TABLE_PREFIX + "updating cell at row: " + row 
				+ " col: " + col);
		Label label = (Label)getWidget(row, col);
		
		
		if (label != null) {
			label.setText(value.toString());
		} else {
			setCellWidget(row, col, "inspectorTableData", value);
		}

	}

	protected void setCellWidget(int row, int col, String style, DataCell cell) {
		Label label = new Label(cell.toString());
		getCellFormatter().setStyleName(row, col, style);
		setWidget(row, col, label);
	}
	
	protected void setCellWidget(int row, int col, String style, String text) {
		Label label = new Label(text);
		getCellFormatter().setStyleName(row, col, style);
		setWidget(row, col, label);
	}
	
	public GridModel getModel() {
	    return model;
    }

	public void setModel(GridModel model) {
	    this.model = model;
    }

	public void addRow(List<DataCell> row) {
		int numRows = getRowCount();
		int col = 0;
		for (DataCell cell: row) {
			setCellWidget(numRows, col, "inspectorTableData", cell);
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

	public void removeLastCell(int row) {
	    removeCell(row, model.getColumnCount());
    }

	public void removeLastRow() {
	    removeRow(getRowCount() - 1);
    }

	public void removeColumn() {
		for (int row=0; row < getRowCount(); row++) {
			removeLastCell(row);
		}
    }

	public void appendColumn(String name) {
		int col = getCellCount(HEADER_ROW);
		addCell(HEADER_ROW);
		setCellWidget(HEADER_ROW, col, "inspectorTableHeader", name);
		for (int row = 1; row < getRowCount(); row++) {
			setCellWidget(row, col, "inspectorTableData", 
					"(" + row + ", " + col +")");
		}
		
    }

}
