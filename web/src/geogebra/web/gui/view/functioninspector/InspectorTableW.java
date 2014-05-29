package geogebra.web.gui.view.functioninspector;

import geogebra.web.gui.view.functioninspector.GridModel.IGridListener;

import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;


public class InspectorTableW extends FlexTable implements IGridListener {
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
			Label label = new Label("column" + col);
			label.setStyleName("InspectorTableHeader");
			setWidget(0, col, label);
		}
	}

	public void updateHeader(int col, String title) {
		updateCell(col, 0, title);
	}

	public void updateCell(int col, int row, String value) {
		Label label = (Label)getWidget(row, col);
		label.setText(value);

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
		for (String cell: row) {
			Label label = new Label(cell);
			setWidget(numRows, col, label);
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

}
