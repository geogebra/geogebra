package geogebra.web.cas.view;

import geogebra.common.cas.view.CASTableCellController;
import geogebra.common.cas.view.CASView;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.GuiManager;
import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerW;

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
	private App app;
	public CASTableControllerW(CASViewW casViewW,App app) {
	    view = casViewW;
	    this.app = app;
    }

	public void onDoubleClick(DoubleClickEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onClick(ClickEvent event) {
		GuiManager gm = app.getGuiManager();
		if(gm.getActiveToolbarId()!=App.VIEW_CAS){
			((GuiManagerW)gm).setActiveToolbarId(App.VIEW_CAS);
			gm.setToolBarDefinition(CASView.TOOLBAR_DEFINITION);
			gm.clearInputbar();
			gm.updateToolbar();
		}
		
		CASTableW table = view.getConsoleTable();
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
	    if(e.getKeyChar()==KeyCodes.KEY_ENTER ||e.getKeyChar()==10){
	    	this.handleEnterKey(e, app);
	    }
	    
    }

}
