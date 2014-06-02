package geogebra.web.gui.view.functioninspector;

import geogebra.web.gui.view.functioninspector.GridModel.IGridListener;

import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;


public class InspectorTableW extends FlexTable implements IGridListener {
	private static final int HEADER_ROW = 0;
	private GridModel model;
	private int selectedRow;
	public InspectorTableW(int col) {
		super();
		setStyleName("inspectorTable");
		setWidth("100%");
		setModel(new GridModel(col, this));
		createHeader();
		selectedRow = 1;
	}

	private void createHeader() {
		for (int col=0; col < getModel().getColumnCount(); col++) {
			updateHeader(col, "");
		}
	}

	public void updateHeader(int col, String title) {
		setCellLabel(HEADER_ROW, col, "InspectorTableHeader", "");
    }

	public void updateCell(int row, int col, String value) {
		Label label = (Label)getWidget(row, col);
		if (label != null) {
			label.setText(value);
		} else {
			setCellLabel(row, col, "inspectorTableData", value);
		}

	}

	protected void setCellLabel(int row, int col, String style, String value) {
		Label label = new Label(value);
		label.setStyleName(style);
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
			setCellLabel(numRows + 1 , col, "inspectorTableData", cellText);
			col++;
		}
    }

	public void setHeaders(String[] headers) {
		int col = 0;
		for (String cell: headers) {
			Label label = new Label(cell);
			setWidget(0, col, label);
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
	    removeCell(row, model.getColumnCount() - 1);
    }

	public void appendColumn(String name) {
		int col = getCellCount(0);
		updateHeader(col, name);
		
    }

}
