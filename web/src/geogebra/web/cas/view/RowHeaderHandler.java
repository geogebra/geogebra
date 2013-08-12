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

	public RowHeaderHandler(AppW appl){
		super();
		app = appl;
	}

	public void onMouseUp(MouseUpEvent event) {
	    if(event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT){

//TODO	    	
//			if(!rowHeader.isSelectedIndex(releasedRow)){
//				rowHeader.setSelectedIndex(releasedRow);
//			}
//			if(rowHeader.getSelectedIndices().length>0){
	    	
	    	
	    	// Don't istantiate RowHeaderPopupMenuW() directly. Use guimanager for this,
	    	// because it must store in GuiManagerW.currentPopup - in this way the popup will hide
	    	// when a newer popup will be shown.
	    	RowHeaderPopupMenuW popupMenu = app.getGuiManager().getCASContextMenu();
	    	popupMenu.show(new GPoint(event.getClientX(), event.getClientY()));
	    }    
	    
    }
}
