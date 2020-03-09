package org.geogebra.web.full.cas.view;

import java.util.Collections;
import java.util.TreeSet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.cas.view.CASTable;
import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;

/**
 * CAS table
 */
public class CASTableW extends Grid implements CASTable {
	/** column index of content */
	public static final int COL_CAS_CELLS_WEB = 1;
	/** column index of marbles */
	public static final int COL_CAS_HEADER = 0;
	private CASTableCellEditor editor;
	private CASTableCellW editing;
	private AppW app;
	private int[] selectedRows = new int[0];
	private CASTableControllerW ml;
	private CASViewW view;

	/**
	 * @param app
	 *            application
	 * @param controller
	 *            controller
	 * @param casViewW
	 *            view
	 */
	public CASTableW(AppW app, CASTableControllerW controller,
			CASViewW casViewW) {
		super(0, 2);
		this.app = app;
		this.ml = controller;

		addStyleName("CAS-table");
		insertRow(0, null, false);
		view = casViewW;
	}

	@Override
	public void setLabels() {
		if (hasEditor()) {
			getEditor().setLabels();
		}
	}

	@Override
	public GeoCasCell getGeoCasCell(int n) {
		if (n >= 0 && this.getRowCount() > n) {
			Widget w = getWidget(n, COL_CAS_CELLS_WEB);
			if (w instanceof CASTableCellW) {
				return ((CASTableCellW) w).getCASCell();
			}
		}
		return null;
	}

	@Override
	public App getApplication() {
		return app;
	}

	@Override
	public void deleteAllRows() {
		resize(0, 2);
	}

	@Override
	public void insertRow(int rows, GeoCasCell casCell, boolean b) {
		int n = rows;
		if (n >= getRowCount()) {
			resize(n + 1, 2);
		} else {
			this.insertRow(n);
		}
		// update keys (rows) in arbitrary constant table
		updateAfterInsertArbConstTable(rows);
		CASTableCellW cellWidget = new CASTableCellW(casCell, app);
		Widget rowHeader = new RowHeaderWidget(this, n + 1, casCell,
		        (AppW) getApplication());

		addOutputListener(cellWidget);

		setWidget(n, CASTableW.COL_CAS_HEADER, rowHeader);
		getCellFormatter().addStyleName(n, COL_CAS_HEADER, "cas_header");

		setWidget(n, CASTableW.COL_CAS_CELLS_WEB, cellWidget);

		if (n < getRowCount() - 1) {
			// Let increase the labels below the n. row.
			resetRowNumbers(n + 1);
			// tell construction about new GeoCasCell if it is not at the
			// end
			app.getKernel().getConstruction().setCasCellRow(casCell, rows);
		}
	}

	private void addOutputListener(CASTableCellW cellWidget) {
		Widget outputWidget = cellWidget.getOutputWidget();
		outputWidget.addDomHandler(ml, MouseUpEvent.getType());
		outputWidget.addBitlessDomHandler(ml, TouchEndEvent.getType());
		ClickStartHandler.initDefaults(outputWidget, true, true);
	}

	/**
	 * Updates arbitraryConstantTable in construction.
	 * 
	 * @param row
	 *            row index (starting from 0) where cell insertion is done
	 */
	private void updateAfterInsertArbConstTable(int row) {
		if (app.getKernel().getConstruction().getArbitraryConsTable()
				.size() > 0) {
			// find last row number
			Integer max = Collections.max(app.getKernel().getConstruction()
					.getArbitraryConsTable().keySet());
			for (int key = max; key >= row; key--) {
				MyArbitraryConstant myArbConst = app.getKernel()
						.getConstruction()
					.getArbitraryConsTable().get(key);
				if (myArbConst != null
					&& !app.getKernel().getConstruction().isCasCellUpdate()
					&& !app.getKernel().getConstruction().isFileLoading()
					&& app.getKernel().getConstruction().isNotXmlLoading()) {
					app.getKernel().getConstruction().getArbitraryConsTable()
						.remove(key);
					app.getKernel().getConstruction().getArbitraryConsTable()
						.put(key + 1, myArbConst);
				}
			}
		}
	}

	@Override
	public void resetRowNumbers(int from) {
		RowHeaderWidget nextHeader;
		for (int i = from; i < getRowCount(); i++) {
			nextHeader = (RowHeaderWidget) this.getWidget(i, COL_CAS_HEADER);
			nextHeader.setLabel(i + 1);
		}
	}

	@Override
	public int[] getSelectedRows() {
		return selectedRows;
	}

	@Override
	public int getSelectedRow() {
		if (selectedRows.length < 1) {
			return -1;
		}
		return selectedRows[0];
	}

	@Override
	public void stopEditing() {
		if (editing != null) {
			editing.stopEditing();
		}
		editing = null;
	}

	/**
	 * Stop editing without comitting changes
	 */
	public void cancelEditing() {
		if (editing != null) {
			editing.cancelEditing();
		}
		editing = null;
	}

	@Override
	public void startEditingRow(int n) {
		startEditingRow(n, null);
	}

	private void startEditingRow(int n, String newText) {
		if (n == 0) {
			setFirstRowFront(true);
		}
		Widget w = getWidget(n, COL_CAS_CELLS_WEB);

		if (w == editing && newText == null) {
			getEditor().ensureEditing();
			return;
		}
		setSelectedRows(n, n);
		// cancelEditing();
		stopEditing();
		Log.debug(n + ":" + (w == null ? "null" : w.getClass()));
		if (w instanceof CASTableCellW) {
			// App.debug("cell found");
			GeoCasCell casCell = this.getGeoCasCell(n);
			boolean asText = casCell != null && casCell.isUseAsText();
			editing = (CASTableCellW) w;
			((CASEditorW) getEditor()).resetInput();
			((CASEditorW) getEditor())
					.setAutocomplete(editing.getCASCell() == null
							|| !editing.getCASCell().isUseAsText());
			editing.startEditing(((CASEditorW) getEditor()),
					newText, asText);
		}
	}

	@Override
	public CASTableCellEditor getEditor() {
		if (editor == null) {
			editor = new CASLaTeXEditor(app, ml);
			editor.setPixelRatio(app.getPixelRatio());
		}
		return editor;
	}

	@Override
	public void deleteRow(int rowNumber) {
		removeRow(rowNumber);
		resetRowNumbers(rowNumber);
		// update keys (rows) in arbitrary constant table
		updateAfterDeleteArbConstTable(rowNumber);
	}

	/**
	 * Updates arbitraryConstantTable in construction.
	 * 
	 * @param row
	 *            row index (starting from 0) where cell is deleted
	 */
	private void updateAfterDeleteArbConstTable(int row) {
		MyArbitraryConstant arbConst = app.getKernel().getConstruction()
				.getArbitraryConsTable().remove(row);
		if (arbConst != null) {
			for (GeoNumeric geoNum : arbConst.getConstList()) {
				app.getKernel().getConstruction()
						.removeFromConstructionList(geoNum);
				app.getKernel().getConstruction().removeLabel(geoNum);
				app.getKernel().notifyRemove(geoNum);
			}
		}
		if (app.getKernel().getConstruction().getArbitraryConsTable()
				.size() > 0) {
			// find last row number
			Integer max = Collections.max(app.getKernel().getConstruction()
				.getArbitraryConsTable().keySet());
			for (int key = row + 1; key <= max; key++) {
				MyArbitraryConstant myArbConst = app.getKernel()
						.getConstruction()
					.getArbitraryConsTable().get(key);
				if (myArbConst != null) {
					app.getKernel().getConstruction().getArbitraryConsTable()
						.remove(key);
					app.getKernel().getConstruction().getArbitraryConsTable()
						.put(key - 1,
						myArbConst);
				}
			}
		}
	}

	@Override
	public void setRow(int rowNumber, GeoCasCell casCell) {
		if (rowNumber < 0) {
			return;
		}
		if (rowNumber >= this.getRowCount()) {
			resize(rowNumber + 1, 2);
		}
		if (casCell.isUseAsText() && editing != null) {
			editing.setInput();
		}

		CASTableCellW cellWidget = new CASTableCellW(casCell, app);
		Widget rowHeader = new RowHeaderWidget(this, rowNumber + 1, casCell,
				(AppW) getApplication());

		addOutputListener(cellWidget);

		setWidget(rowNumber, CASTableW.COL_CAS_HEADER, rowHeader);
		setWidget(rowNumber, CASTableW.COL_CAS_CELLS_WEB, cellWidget);
		if (casCell.isUseAsText()) {
			cellWidget.setFont();
			cellWidget.setColor();
		}
	}

	private void setRowSelected(int rowNumber, boolean selected) {
		if (selected) {
			getCellFormatter().getElement(rowNumber, COL_CAS_HEADER)
					.addClassName("selected");
		} else {
			getCellFormatter().getElement(rowNumber, COL_CAS_HEADER)
					.removeClassName("selected");
		}
	}

	@Override
	public boolean isEditing() {
		return editing != null;
	}

	/**
	 * Convert event into cell coordinates
	 * 
	 * @param event
	 *            mouise / touch event
	 * @return (column, row)
	 */
	public GPoint getPointForEvent(HumanInputEvent<?> event) {
		Element td = getEventTargetCell(Event.as(event.getNativeEvent()));
		if (td == null) {
			return null;
		}

		int row = TableRowElement.as(td.getParentElement())
				.getSectionRowIndex();
		int column = TableCellElement.as(td).getCellIndex();
		return new GPoint(column, row);
	}

	/**
	 * @param from
	 *            min selected row
	 * @param to
	 *            max selected row
	 */
	public void setSelectedRows(int from, int to) {
		selectedRows = new int[0];
		addSelectedRows(from, to);
		view.getCASStyleBar().setSelectedRow(getGeoCasCell(from));
	}

	/**
	 * @param a
	 *            min or max selected row
	 * @param b
	 *            min or max selected row
	 */
	public void addSelectedRows(int a, int b) {
		int from = Math.min(a, b);
		int to = Math.max(a, b);
		if (from < 0) {
			return;
		}
		for (int i = 0; i < getRowCount(); i++) {
			markRowSelected(i, false);
		}
		TreeSet<Integer> newSelectedRows = new TreeSet<>();
		// add old rows
		for (int i = 0; i < selectedRows.length; i++) {
			newSelectedRows.add(selectedRows[i]);
			markRowSelected(selectedRows[i], true);
		}
		// add new rows
		for (int i = from; i <= to; i++) {
			newSelectedRows.add(i);
			markRowSelected(i, true);
		}
		int j = 0;
		selectedRows = new int[newSelectedRows.size()];
		for (int row : newSelectedRows) {
			selectedRows[j++] = row;
		}
	}

	private void markRowSelected(int rowNumber, boolean b) {
		setRowSelected(rowNumber, b);
	}

	/**
	 * @return CAS view
	 */
	public CASViewW getCASView() {
		return view;
	}

	/**
	 * @param row
	 *            row index
	 * @return whether row is among the selectted ones
	 */
	public boolean isSelectedIndex(int row) {
		for (Integer item : getSelectedRows()) {
			if (item.equals(row)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return currently edited row index
	 */
	public int getEditingRow() {
		if (isEditing()) {
			return getSelectedRows()[0];
		}
		return -1;
	}

	/**
	 * @return currently edited cell
	 */
	public CASTableCellW getEditingCell() {
		return editing;
	}

	/**
	 * @param value
	 *            whether first row is selected
	 */
	public void setFirstRowFront(boolean value) {
		CellFormatter cellFormatter = getCellFormatter();
		if (value) {
			cellFormatter.addStyleName(0, COL_CAS_CELLS_WEB,
					"CAS_table_first_row_selected");
		} else {
			cellFormatter.removeStyleName(0, COL_CAS_CELLS_WEB,
					"CAS_table_first_row_selected");
		}
	}

	/**
	 * @param event
	 *            mouse / touch event
	 * @return clicked cell
	 */
	public CASTableCellW getCasCellForEvent(HumanInputEvent<?> event) {
		Element td = getEventTargetCell(Event.as(event.getNativeEvent()));
		if (td == null) {
			return null;
		}

		int row = TableRowElement.as(td.getParentElement())
		        .getSectionRowIndex();
		int column = TableCellElement.as(td).getCellIndex();
		Widget widget = getWidget(row, column);
		if (!(widget instanceof CASTableCellW)) {
			return null;
		}
		return (CASTableCellW) widget;
	}

	/**
	 * Return value for {@link HTMLTable#getCellForEvent}.
	 */
	public class MyCell extends HTMLTable.Cell {
		/**
		 * @param rowIndex
		 *            row
		 * @param cellIndex
		 *            column
		 */
		public MyCell(int rowIndex, int cellIndex) {
			super(rowIndex, cellIndex);
		}
	}

	/**
	 * Given a click event, return the Cell that was clicked or touched, or null
	 * if the event did not hit this table. The cell can also be null if the
	 * click event does not occur on a specific cell.
	 * 
	 * @param event
	 *            A click event of indeterminate origin
	 * @return The appropriate cell, or null
	 */
	public MyCell getCellForEvent(HumanInputEvent<?> event) {
		Element td = getEventTargetCell(Event.as(event.getNativeEvent()));
		if (td == null) {
			return null;
		}

		int row = TableRowElement.as(td.getParentElement())
				.getSectionRowIndex();
		int column = TableCellElement.as(td).getCellIndex();
		return new MyCell(row, column);
	}

	/**
	 * @param i
	 *            cell index
	 * @param cellInput
	 *            cell input
	 */
	public void setCellInput(int i, String cellInput) {
		GeoCasCell casCell = getGeoCasCell(i);
		if (casCell != null && cellInput != null && cellInput.length() > 0) {
			// casCell.setInput(toBeCopied);
			// casCell.setLaTeXInput(null);
			startEditingRow(i, cellInput);
		}
	}

	@Override
	public boolean hasEditor() {
		return this.editor != null;
	}

	@Override
	public boolean keepEditing(boolean failure, int rowNum) {
		if (failure) {
			Widget widget = getWidget(rowNum, COL_CAS_CELLS_WEB);
			if (widget instanceof CASTableCellW) {
				((CASTableCellW) widget).showError();
			}
		}
		return false;
	}

	/**
	 * @param event
	 *            mouse / touch event that moved the caret
	 */
	public void adjustCaret(HumanInputEvent<?> event) {
		if (editor != null) {
			((CASEditorW) editor).adjustCaret(event);
		}
	}
}
