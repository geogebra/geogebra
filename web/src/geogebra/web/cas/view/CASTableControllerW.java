package geogebra.web.cas.view;

import geogebra.common.awt.GPoint;
import geogebra.common.cas.view.CASTableCellController;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerInterfaceW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.NativeEvent;
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
		GuiManagerInterfaceW gm = app.getGuiManager();
		if (app.getToolbar() != null) gm.setActiveToolbarId(App.VIEW_CAS);		
		
		CASTableW table = view.getConsoleTable();
		Cell c = table.getCellForEvent(event);
		if(c==null)
			return;
		if(c.getCellIndex()==CASTableW.COL_CAS_CELLS_WEB){
			int rowIndex = c.getRowIndex();
			table.startEditingRow(rowIndex);
		}
	    
    }

	public void onMouseMove(MouseMoveEvent event) {
		GPoint p = view.getConsoleTable().getPointForEvent(event);
		CASTableW table = view.getConsoleTable();
		if (p == null || p.getX() != CASTableW.COL_CAS_HEADER || startSelectRow < 0) {
			return;
		}
		if (event.isShiftKeyDown()) {
			table.addSelectedRows(startSelectRow, p.getY());
		}
		event.stopPropagation();
	    
    }

	public void onMouseUp(MouseUpEvent event) {
		GPoint p = view.getConsoleTable().getPointForEvent(event);
		CASTableW table = view.getConsoleTable();
		if (p == null || p.getX() != CASTableW.COL_CAS_HEADER || startSelectRow < 0) {
			return;
		}
		table.cancelEditing();
		event.stopPropagation();
		if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT){
			for(Integer item : table.getSelectedRows()){
				if (item.equals(startSelectRow)) return;
			}
			table.setSelectedRows(startSelectRow, startSelectRow);
			return;
		}
		if (event.isControlKeyDown()){
			table.addSelectedRows(startSelectRow, p.getY());
		} else {
			table.setSelectedRows(startSelectRow, p.getY());
		}
    }

	public void onMouseDown(MouseDownEvent event) {
		
		//Remove context menu (or other popups), if it's visible.
		((GuiManagerW)app.getGuiManager()).removePopup();
		
		CASTableW table = view.getConsoleTable();
		GPoint p = table.getPointForEvent(event);
		if (p == null || p.getX() != CASTableW.COL_CAS_HEADER) {
			this.startSelectRow = -1;
			return;
		}
		if (!event.isShiftKeyDown()) {
			this.startSelectRow = p.getY();
		} else if (event.isControlKeyDown()) {
			table.addSelectedRows(startSelectRow, p.getY());
		} else {
			table.setSelectedRows(startSelectRow, p.getY());
		}
	    event.stopPropagation();
    }

	public void keyReleased(KeyEvent e) {
	    if(e.isEnterKey()){
	    	this.handleEnterKey(e, app);
	    }
	    
    }

}
