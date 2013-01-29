package geogebra.web.cas.view;

import geogebra.common.cas.view.CASTableCellController;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.main.App;
import geogebra.common.main.KeyCodes;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class CASTableControllerW extends CASTableCellController implements
MouseDownHandler, MouseUpHandler, MouseMoveHandler, ClickHandler, DoubleClickHandler, KeyHandler{

	private CASTableW table;
	private App app;
	public CASTableControllerW(CASTableW consoleTable,App app) {
	    table = consoleTable;
	    this.app = app;
    }

	public void onDoubleClick(DoubleClickEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onClick(ClickEvent event) {
		Cell c = table.getCellForEvent(event);
		if(c!=null)
			table.startEditingRow(c.getRowIndex());
	    
    }

	public void onMouseMove(MouseMoveEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onMouseUp(MouseUpEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onMouseDown(MouseDownEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void keyReleased(KeyEvent e) {
	    if(KeyCodes.translateGWTcode(e.getKeyChar())==KeyCodes.ENTER){
	    	this.handleEnterKey(e, app);
	    }
	    
    }

}
