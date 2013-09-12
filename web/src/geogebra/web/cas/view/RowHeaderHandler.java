package geogebra.web.cas.view;

import geogebra.common.awt.GPoint;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

public class RowHeaderHandler implements MouseUpHandler{

//	public void onClick(ClickEvent event) {
//		App.debug("click on header");
//	    if(event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT){
//	    	App.debug("rightclick");
//	    	RowHeaderPopupMenuW popupMenu = new RowHeaderPopupMenuW();
//	    	popupMenu.show(new GPoint());
//	    }
//	    
//    }
	
	private AppW app;
	private RowHeaderWidget rowHeader;
	private CASTableW table;

	public RowHeaderHandler(AppW appl, CASTableW casTableW, RowHeaderWidget rowHeaderWidget){
		super();
		app = appl;
		rowHeader = rowHeaderWidget;
		table = casTableW;
	}

	public void onMouseUp(MouseUpEvent event) {
	    if(event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT){
	    	int releasedRow = rowHeader.getIndex();
	    	if(!table.isSelectedIndex(releasedRow)){
	    		table.setSelectedRows(releasedRow,  releasedRow);
	    	}
			if(table.getSelectedRows().length>0){    	
		    	// Don't istantiate RowHeaderPopupMenuW() directly. Use guimanager for this,
		    	// because it must store in GuiManagerW.currentPopup - in this way the popup will hide
		    	// when a newer popup will be shown.
		    	RowHeaderPopupMenuW popupMenu = app.getGuiManager().getCASContextMenu(rowHeader, table);
		    	popupMenu.show(new GPoint(event.getClientX(), event.getClientY()));
			}
	    }    
	    
    }
}
