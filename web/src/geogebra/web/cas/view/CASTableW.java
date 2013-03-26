package geogebra.web.cas.view;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GPoint;
import geogebra.common.cas.view.CASTable;
import geogebra.common.cas.view.CASTableCellEditor;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;
import geogebra.web.gui.view.spreadsheet.MyTableW;
import geogebra.web.main.AppW;

import java.util.TreeSet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

public class CASTableW extends Grid implements CASTable {

	public static final int COL_CAS_CELLS_WEB = 1;
	public static final int COL_CAS_HEADER = 0;
	private CASTableCellEditorW editor;
	private CASTableCellW editing;
	private AppW app;
	private int[] selectedRows = new int[0];

	public CASTableW(AppW app, CASTableControllerW ml) {
		super(0, 2);
		this.app = app;
		setBorderWidth(1);
		getElement().getStyle().setBorderColor(
		        MyTableW.TABLE_GRID_COLOR.toString());
		getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		editor = new CASTableCellEditorW(this, app, ml);
		insertRow(0, null, false);
	}

	public void setLabels() {
		editor.setLabels();

	}

	public GeoCasCell getGeoCasCell(int n) {
		Widget w = getWidget(n, COL_CAS_CELLS_WEB);
		if (w instanceof CASTableCellW) {
			return ((CASTableCellW) w).getCASCell();
		}
		return null;
	}

	public App getApplication() {
		return app;
	}

	public void deleteAllRows() {
		resize(0, 2);
	}

	public void insertRow(int rows, GeoCasCell casCell, boolean b) {
		int n = rows;
		if (n >= getRowCount())
			resize(n + 1, 2);
		else
			this.insertRow(n);
		Widget cellWidget = new CASTableCellW(casCell);
		Widget rowHeader = new RowHeaderWidget(n + 1,casCell);
		setWidget(n, CASTableW.COL_CAS_HEADER, rowHeader);
		getCellFormatter()
		        .getElement(n, COL_CAS_HEADER)
		        .getStyle()
		        .setBackgroundColor(MyTableW.BACKGROUND_COLOR_HEADER.toString());
		this.setWidget(n, CASTableW.COL_CAS_CELLS_WEB, cellWidget);
	}

	public int[] getSelectedRows() {
		return selectedRows;
	}

	public int getSelectedRow() {
		if(selectedRows.length<1)
			return -1;
		return selectedRows[0];
	}

	public void stopEditing() {
		if (editing != null)
			editing.stopEditing();
		editing = null;

	}

	public void cancelEditing() {
		if (editing != null)
			editing.cancelEditing();
		editing = null;

	}

	public void startEditingRow(int n) {
		Widget w = getWidget(n, COL_CAS_CELLS_WEB);

		if (w == editing)
			return;
		setSelectedRows(n,n);
		cancelEditing();
		if (w instanceof CASTableCellW) {
			editing = (CASTableCellW) w;
			editing.startEditing(editor.getWidget());
		}

	}

	public CASTableCellEditor getEditor() {
		return editor;
	}

	public void deleteRow(int rowNumber) {
		removeRow(rowNumber);
	}

	public void setRow(int rowNumber, GeoCasCell casCell) {
		if (rowNumber >= this.getRowCount()) {
			resize(rowNumber + 1, 2);
		}
		Widget cellWidget = new CASTableCellW(casCell);
		Widget rowHeader = new RowHeaderWidget(rowNumber + 1,casCell);
		setWidget(rowNumber, CASTableW.COL_CAS_HEADER, rowHeader);
		getCellFormatter()
		        .getElement(rowNumber, COL_CAS_HEADER)
		        .getStyle()
		        .setBackgroundColor(MyTableW.BACKGROUND_COLOR_HEADER.toString());
		setWidget(rowNumber, CASTableW.COL_CAS_CELLS_WEB, cellWidget);
	}

	public boolean isEditing() {
		return editing != null;
	}

	public GPoint getPointForEvent(MouseEvent event) {
		Element td = getEventTargetCell(Event.as(event.getNativeEvent()));
		if (td == null) {
			return null;
		}

		int row = TableRowElement.as(td.getParentElement())
		        .getSectionRowIndex();
		int column = TableCellElement.as(td).getCellIndex();
		return new GPoint(column, row);
	}

	public void setSelectedRows(int from, int to) {
		selectedRows = new int[0];
		addSelectedRows(from, to);
	}

	public void addSelectedRows(int a, int b) {
		int from = Math.min(a, b);
		int to = Math.max(a, b);
		if(from < 0)
			return;
		for(int i=0;i<getRowCount();i++){
			markRowSelected(i,false);
		}
		TreeSet<Integer> newSelectedRows = new TreeSet<Integer>();
		//add old rows 
		for (int i = 0; i < selectedRows.length; i++) {
			newSelectedRows.add(selectedRows[i]);
			markRowSelected(selectedRows[i],true);
		}
		//add new rows
		for (int i = from; i <= to; i++) {
			newSelectedRows.add(i);
			markRowSelected(i,true);
		}
		int j = 0;
		selectedRows = new int[newSelectedRows.size()];
		for(int row:newSelectedRows){
			selectedRows[j++]=row;
		}
	}

	private void markRowSelected(int rowNumber, boolean b) {
		GColor color = b?GColor.GRAY:MyTableW.BACKGROUND_COLOR_HEADER;
		getCellFormatter()
        .getElement(rowNumber, COL_CAS_HEADER)
        .getStyle()
        .setBackgroundColor(color.toString());
    }

}
