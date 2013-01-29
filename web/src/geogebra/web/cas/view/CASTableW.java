package geogebra.web.cas.view;

import geogebra.common.cas.view.CASTable;
import geogebra.common.cas.view.CASTableCellEditor;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;
import geogebra.web.gui.view.spreadsheet.MyTableW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

public class CASTableW extends Grid implements CASTable{

	private static final int COL_CAS_CELLS_WEB = 1;
	private static final int COL_CAS_HEADER = 0;
	private CASTableCellEditorW editor;
	private CASTableCellW editing;
	private AppW app;

	public CASTableW(AppW app, CASTableControllerW ml){
		super(0,2);
		this.app=app;
		setBorderWidth(1);
		getElement().getStyle().setBorderColor(MyTableW.TABLE_GRID_COLOR.toString());
		getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		editor = new CASTableCellEditorW(this, app,ml);
		insertRow(0,null, false);
	}

	public void setLabels() {
	    editor.setLabels();
	    
    }

	public GeoCasCell getGeoCasCell(int n) {
	    Widget w = getWidget(n,COL_CAS_CELLS_WEB);
	    if(w instanceof CASTableCellW){
	    	return ((CASTableCellW)w).getCASCell();
	    }
	    return null;
    }

	public App getApplication() {
	    return app;
    }

	public void deleteAllRows() {
	    resize(0,2);
    }

	public void insertRow(int rows, GeoCasCell casCell, boolean b) {
		int n = rows;
		if(n >= getRowCount())
			resize(n+1,2);
		else this.insertRow(n);
	    Widget cellWidget = new CASTableCellW(casCell);
		Widget rowHeader = new RowHeaderWidget(n+1);
		setWidget(n, CASTableW.COL_CAS_HEADER, rowHeader);
		getCellFormatter().getElement(n, COL_CAS_HEADER).getStyle()
	        .setBackgroundColor(
	                MyTableW.BACKGROUND_COLOR_HEADER
	                        .toString());
		this.setWidget(n, CASTableW.COL_CAS_CELLS_WEB, cellWidget);
    }

	public int[] getSelectedRows() {
	    // TODO Auto-generated method stub		
	    return null;
    }

	public int getSelectedRow() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public void stopEditing() {
	    if(editing!=null)
	    	editing.stopEditing();
		editing = null;
	    
    }

	public void startEditingRow(int n) {
		Widget w = getWidget(n,COL_CAS_CELLS_WEB);
		if(w == editing)
			return;
		stopEditing();
	    if(w instanceof CASTableCellW){
	    	editing = (CASTableCellW)w;
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
	    if(rowNumber>=this.getRowCount()){
	    	resize(rowNumber+1,2);
	    }
	    Widget cellWidget = new CASTableCellW(casCell);
	    Widget rowHeader = new RowHeaderWidget(rowNumber+1);
	    setWidget(rowNumber, CASTableW.COL_CAS_HEADER, rowHeader);
	    getCellFormatter().getElement(rowNumber, COL_CAS_HEADER).getStyle()
        .setBackgroundColor(
                MyTableW.BACKGROUND_COLOR_HEADER
                        .toString());
		setWidget(rowNumber, CASTableW.COL_CAS_CELLS_WEB, cellWidget);
    }

	public boolean isEditing() {
	    return editing != null;
    }

}
