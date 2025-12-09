/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.functioninspector;

import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.full.gui.view.functioninspector.GridModel.DataCell;
import org.geogebra.web.full.gui.view.functioninspector.GridModel.IGridListener;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.gwtproject.user.client.ui.FlexTable;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class InspectorTableW extends FlexTable implements IGridListener {
	private static final int HEADER_ROW = 0;
	private GridModel model;
	private int selectedRow;
	private AutoCompleteTextFieldW cellEditor;
	
	/**
	 * @param app
	 *            application
	 * @param col
	 *            number of columns
	 * @param blurAndEnterHandler called when Enter pressed or blur occurs while editing cells
	 */
	public InspectorTableW(App app, int col, Runnable blurAndEnterHandler) {
		super();
		setStyleName("inspectorTable");
		setWidth("100%");
		setModel(new GridModel(col, this));
		selectedRow = 1;
		RowFormatter rf = getRowFormatter();
		rf.setStyleName(HEADER_ROW, "inspectorTableHeader");
		InputPanelW input = new InputPanelW(app, -1, false);
		cellEditor = input.getTextComponent();
		cellEditor.addStyleName("inspectorTableEditor");
		
		addClickHandler(event -> {
			Cell cell = getCellForEvent(event);
			if (cell == null) {
				return;
			}
			updateRowStyle(cell, "selected");
		});
		
		cellEditor.addEnterPressHandler(blurAndEnterHandler);
	}

	/**
	 * @return value of edited cell or null if can't be parsed
	 */
	public Double getDoubleEdited() {
		if (cellEditor == null) {
			return null;
		}

		Double value = null;
		try {
			value = Double.parseDouble(cellEditor.getText());
		} catch (NumberFormatException e) {
			// not a number
		}
		return value;
	}

	private void setEditorInCell(int row, int col) {
		if (row == -1 || col == -1) {
			return;
		}
		String data = model.getData(row - 1, col);
		cellEditor.setText(data);
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
	
	/**
	 * Remove selected row style.
	 */
	public void clearSelectedRowStyle() {
		if (selectedRow < getRowCount()) {
			// making sure it is not removed meanwile
			getRowFormatter().setStyleName(selectedRow, "");
		}
	}

	@Override
	public void updateHeader(int col, String title) {
		setCellWidget(HEADER_ROW, col, "inspectorTableHeader", title);
	}

	@Override
	public void updateDataCell(int row, int col, DataCell value) {
		// Cells at row 0 are headers.
		updateCell(row + 1, col, value);
	}
		
	protected void updateCell(int row, int col, DataCell value) {
		Widget widget = getWidget(row, col);
		
		if (widget != null) {
			if (widget == cellEditor) {
				cellEditor.setText(value.toString());
			} else {
				((Label) widget).setText(value.toString());
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

	@Override
	public void addRow(List<DataCell> row) {
		int numRows = getRowCount();
		int col = 0;
		for (DataCell cell: row) {
			setCellWidget(numRows, col, "inspectorTableData", cell);
			col++;
		}
	}

	@Override
	public void setHeaders(String[] headers) {
		int col = 0;
		for (String title: headers) {
			updateHeader(col, title);
			col++;
		}
	}

	/**
	 * @return selected row index
	 */
	public int getSelectedRow() {
		return selectedRow;
	}

	/**
	 * @param idx
	 *            selected row index
	 */
	public void setSelectedRow(int idx) {
		RowFormatter rf = getRowFormatter();
		clearSelectedRowStyle();
		this.selectedRow = idx + 1; // 0 is the header
		rf.setStyleName(selectedRow, "selected");
	}

	private void removeLastCell(int row) {
		removeCell(row, model.getColumnCount());
	}

	@Override
	public void removeLastRow() {
		removeRow(getRowCount() - 1);
	}

	@Override
	public void removeColumn() {
		for (int row = 0; row < getRowCount(); row++) {
			removeLastCell(row);
		}
	}

	@Override
	public void appendColumn(String name) {
		int col = getCellCount(HEADER_ROW);
		addCell(HEADER_ROW);
		setCellWidget(HEADER_ROW, col, "inspectorTableHeader", name);
		for (int row = 1; row < getRowCount(); row++) {
			setCellWidget(row, col, "inspectorTableData", "");
		}
	}

	/**
	 * Make a cell editable.
	 * 
	 * @param row
	 *            row
	 * @param col
	 *            column
	 */
	public void setCellEditable(int row, int col) {
		model.setCellEditable(row, col);
		setEditorInCell(row + 1, col);
	}

}
