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
	private AppW app;

	public CASTableW(AppW app){
		super(1,2);
		this.app=app;
		setBorderWidth(1);
		getElement().getStyle().setBorderColor(MyTableW.TABLE_GRID_COLOR.toString());
		getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		editor = new CASTableCellEditorW(this, app);
		insertRow(0,null, false);
	}
	
	public int getRowHeight(int i) {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public void setLabels() {
	    editor.setLabels();
	    
    }

	public GeoCasCell getGeoCasCell(int n) {
	    // TODO Auto-generated method stub
	    return null;
    }

	public App getApplication() {
	    return app;
    }

	public void deleteAllRows() {
	    // TODO Auto-generated method stub
	    
    }

	public void insertRow(int rows, GeoCasCell casCell, boolean b) {
	    // TODO Auto-generated method stub
	    
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
	    // TODO Auto-generated method stub
	    
    }

	public void startEditingRow(int selectedRow) {
	    // TODO Auto-generated method stub
	    
    }

	public CASTableCellEditor getEditor() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public boolean isRowEmpty(int i) {
	    // TODO Auto-generated method stub
	    return false;
    }

	public void insertRow(GeoCasCell casCell, boolean b) {
		int n = getRowCount();
		resize(n+1,2);
	    Widget cellWidget = new CASTableCellW(casCell);
		Widget rowHeader = new RowHeaderWidget(n+1);
		this.setWidget(n, CASTableW.COL_CAS_HEADER, rowHeader);
		 this.getCellFormatter().getElement(n, COL_CAS_HEADER).getStyle()
	        .setBackgroundColor(
	                MyTableW.BACKGROUND_COLOR_HEADER
	                        .toString());
		this.setWidget(n, CASTableW.COL_CAS_CELLS_WEB, cellWidget);
	    
    }

	public void deleteRow(int rowNumber) {
	    // TODO Auto-generated method stub
	    
    }

	public void setRow(int rowNumber, GeoCasCell casCell) {
	    if(rowNumber>=this.getRowCount()){
	    	resize(rowNumber+1,2);
	    }
	    Widget cellWidget = new CASTableCellW(casCell);
	    Widget rowHeader = new RowHeaderWidget(rowNumber+1);
	    this.setWidget(rowNumber, CASTableW.COL_CAS_HEADER, rowHeader);
	    this.getCellFormatter().getElement(rowNumber, COL_CAS_HEADER).getStyle()
        .setBackgroundColor(
                MyTableW.BACKGROUND_COLOR_HEADER
                        .toString());
		this.setWidget(rowNumber, CASTableW.COL_CAS_CELLS_WEB, cellWidget);
    }

}
