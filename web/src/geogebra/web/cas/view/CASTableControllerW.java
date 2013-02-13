package geogebra.web.cas.view;

import geogebra.common.awt.GPoint;
import geogebra.common.cas.view.CASTableCellController;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class CASTableControllerW extends CASTableCellController implements
MouseDownHandler, MouseUpHandler, MouseMoveHandler, ClickHandler, DoubleClickHandler, KeyHandler{

	private CASViewW view;
	private AppW app;
	private int startSelectRow;
	public CASTableControllerW(CASViewW casViewW,AppW app) {
	    view = casViewW;
	    this.app = app;
    }

	public void onDoubleClick(DoubleClickEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onClick(ClickEvent event) {
		GuiManagerW gm = app.getGuiManager();
		gm.setActiveToolbarId(App.VIEW_CAS);		
		
		CASTableW table = view.getConsoleTable();
		Cell c = table.getCellForEvent(event);
		if(c==null)
			return;
		if(c.getCellIndex()==CASTableW.COL_CAS_CELLS_WEB)
			table.startEditingRow(c.getRowIndex());
	    
    }

	public void onMouseMove(MouseMoveEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onMouseUp(MouseUpEvent event) {
		event.preventDefault();
		GPoint p = view.getConsoleTable().getPointForEvent(event);
		CASTableW table = view.getConsoleTable();
		if(p.getX()!=CASTableW.COL_CAS_HEADER || startSelectRow<0)
			return;
		table.cancelEditing();
		if(event.isControlKeyDown()){
			table.addSelectedRows(startSelectRow,p.getY());
		}else{
			table.setSelectedRows(startSelectRow,p.getY());
		}
		event.stopPropagation();
	    
    }

	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		CASTableW table = view.getConsoleTable();
		GPoint p = table.getPointForEvent(event);
		if(p.getX()!=CASTableW.COL_CAS_HEADER)
			this.startSelectRow = -1;
		if(!event.isShiftKeyDown()){
			this.startSelectRow = p.getY();
		}else if(event.isControlKeyDown()){
			table.addSelectedRows(startSelectRow,p.getY());
		}else{
			table.setSelectedRows(startSelectRow,p.getY());
		}
	    event.stopPropagation();
    }

	public void keyReleased(KeyEvent e) {
	    if(e.getKeyChar()==KeyCodes.KEY_ENTER ||e.getKeyChar()==10){
	    	this.handleEnterKey(e, app);
	    }
	    
    }

}
