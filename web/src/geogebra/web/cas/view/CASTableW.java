package geogebra.web.cas.view;

import geogebra.common.cas.view.CASTable;
import geogebra.common.cas.view.CASTableCellEditor;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CASTableW extends Grid implements CASTable{

	public CASTableW(){
		super(2,1);
		Label retwidget = new Label();
		
		retwidget.setText("Cas view");
		retwidget.getElement().getStyle().setPadding(2, Style.Unit.PX);
		retwidget.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		retwidget.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		
		this.setWidget(0, 0, retwidget);
	}
	
	public int getRowHeight(int i) {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public void setLabels() {
	    // TODO Auto-generated method stub
	    
    }

	public GeoCasCell getGeoCasCell(int n) {
	    // TODO Auto-generated method stub
	    return null;
    }

	public App getApplication() {
	    // TODO Auto-generated method stub
	    return null;
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
		resize(n+1,1);
	    Widget retwidget = new CASTableCellW(casCell);
		
		this.setWidget(n, CASTable.COL_CAS_CELLS, retwidget);
	    
    }

	public void deleteRow(int rowNumber) {
	    // TODO Auto-generated method stub
	    
    }

	public void setRow(int rowNumber, GeoCasCell casCell) {
	    if(rowNumber>=this.getRowCount()){
	    	resize(rowNumber+1,1);
	    }
	    Widget retwidget = new CASTableCellW(casCell);
		
		this.setWidget(rowNumber, CASTable.COL_CAS_CELLS, retwidget);
    }

}
