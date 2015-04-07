package org.geogebra.web.web.gui.view.functioninspector;

import java.util.List;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;
import org.geogebra.web.web.gui.view.functioninspector.GridModel.DataCell;
import org.geogebra.web.web.gui.view.functioninspector.GridModel.IGridListener;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class InspectorTableW extends FlexTable implements IGridListener {
	private static final int HEADER_ROW = 0;
	private static final String TABLE_PREFIX = "[INSPECTOR_TABLE]";
	private GridModel model;
	private int selectedRow;
	private int editRow;
	private int editCol;
	private AutoCompleteTextFieldW cellEditor;
	private KeyHandler keyHandler;
	private BlurHandler blurHandler;
	
	public InspectorTableW(AppW app, int col) {
		super();
		setStyleName("inspectorTable");
		setWidth("100%");
		setModel(new GridModel(col, this));
		selectedRow = 1;
		editRow = -1;
		editCol = -1;
		RowFormatter rf = getRowFormatter();
		rf.setStyleName(HEADER_ROW, "inspectorTableHeader");
		InputPanelW input = new InputPanelW(null, app, -1, false);
		cellEditor = input.getTextComponent();
		cellEditor.setStyleName("inspectorTableEditor");
		keyHandler = null;
		blurHandler = null;
		
		addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				Cell cell = getCellForEvent(event);
				if (cell == null) {
					return;
				}
				updateRowStyle(cell, "selected");
			}
		});
		
		cellEditor.addKeyHandler(new KeyHandler() {
			
			public void keyReleased(KeyEvent e) {
				if (keyHandler != null) {
					keyHandler.keyReleased(e);
				}
			}
		});
		
		cellEditor.addBlurHandler(new BlurHandler() {
			
			public void onBlur(BlurEvent event) {
				if (blurHandler != null) {
					blurHandler.onBlur(event);
				}
			}
		});
		
	}

	public Double getDoubleEdited() {
		if (cellEditor == null) {
			return null;
		}
		
		Double value = null;
		try {
			value = Double.parseDouble(cellEditor.getText());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return value;
	}
	private void setEditorInCell(int row, int col) {
		if (row == -1 || col == -1) {
			return;
		}
		DataCell data = model.getData(row - 1, col);
		cellEditor.setText(data == null ? "": data.toString());
		setWidget(row, col, cellEditor);
	}
	
	private void updateRowStyle(Cell cell, String style) {
		
		if (cell == null) {
			return;
		}
	
		int row  = cell.getRowIndex();
		if (row == HEADER_ROW) {
			// Header row cannot be selected.
			return;
			
		}
		
		clearSelectedRowStyle();
		RowFormatter rf = getRowFormatter();
		rf.setStyleName(row, style);
		selectedRow = row;	
	}
	
	public void clearSelectedRowStyle() {
		if (selectedRow < getRowCount()) {
			// making sure it is not removed meanwile
			getRowFormatter().setStyleName(selectedRow, "");
		}
		
	}
	public void updateHeader(int col, String title) {
		setCellWidget(HEADER_ROW, col, "inspectorTableHeader", title);
    }
	
	
	public void updateDataCell(int row, int col, DataCell value) {
		// Cells at row 0 are headers.
		updateCell(row + 1, col, value);
	}
		
	protected void updateCell(int row, int col, DataCell value) {
		//App.debug(TABLE_PREFIX + "updating cell at row: " + row 
		//		+ " col: " + col);
		Widget widget = getWidget(row, col);
		
		
		if (widget != null) {
			if (widget == cellEditor) {
				((AutoCompleteTextFieldW)widget).setText(value.toString());
			} else {
				((Label)widget).setText(value.toString());
			}
		} else {
			setCellWidget(row, col, "inspectorTableData", value);
		}

	}

	protected void setCellWidget(int row, int col, String style, DataCell cell) {
		Widget w = null;
		if (cell.isEditable()) {
			cellEditor.setText(cell.toString());
			w = cellEditor;
		} else {
			w = new Label(cell.toString());
		}

		getCellFormatter().setStyleName(row, col, style);
		setWidget(row, col, w);
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

	public void setSelectedRow(int idx) {
		RowFormatter rf = getRowFormatter();
		clearSelectedRowStyle();
	    this.selectedRow = idx + 1; // 0 is the header
		rf.setStyleName(selectedRow, "selected");
    }

	public void removeLastCell(int row) {
	    removeCell(row, model.getColumnCount());
    }

	public void removeLastRow() {
	    removeRow(getRowCount() -1);
	  //  App.debug("[TABLE] rowCount: " + getRowCount());
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
			setCellWidget(row, col, "inspectorTableData", "");
		}
		
    }

	public void setCellEditable(int row, int col) {
	    model.setCellEditable(row, col);
	    setEditorInCell(row + 1, col);
	}

	public KeyHandler getKeyHandler() {
	    return keyHandler;
    }

	public void addKeyHandler(KeyHandler keyHandler) {
	    this.keyHandler = keyHandler;
    }

	public BlurHandler getBlurHandler() {
	    return blurHandler;
    }

	public void addBlurHandler(BlurHandler blurHandler) {
	    this.blurHandler = blurHandler;
    }

}
