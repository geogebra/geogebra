package geogebra.web.javax.swing;

import geogebra.common.main.App;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Popup menu for web.
 * @author Judit Elias
 */
public class JPopupMenuW extends geogebra.common.javax.swing.GPopupMenu{


	protected PopupPanel popupPanel;
	protected MenuBar popupMenu;
	private int popupMenuSize = 0;

	public JPopupMenuW(){
		popupPanel = new PopupPanel();		
		popupMenu = new MenuBar(true);
		popupMenu.setAutoOpen(true);
		popupPanel.add(popupMenu);
	}
	
//	public void add(MenuItem mi) {
//	    impl.addItem(mi);
//	    
//    }
	
	public void setVisible(boolean v){
		popupPanel.setVisible(v);
	}
	
	public void show(Canvas c, int x, int y) {
			
		int xr = c.getAbsoluteLeft() + x;
		int yr = c.getAbsoluteTop() + y;
		
		//c.getAbsoluteRight() - wrappedPopup.getOffsetWidth())
		//wrappedPopup.setPopupPosition(xr, yr);
		popupPanel.show();
		
		
		App.debug("x: " + x);
		App.debug("y: " + y);
		App.debug("xr: " + xr);
		App.debug("yr: " + yr);
		App.debug("wrappedPopup.getOffsetWidth(): " + popupPanel.getOffsetWidth());
		App.debug("c.getOffsetWidth(): " + c.getOffsetWidth());
		App.debug("c.getAbsoluteLeft(): " + c.getAbsoluteLeft());
		
		popupPanel.setPopupPosition(Math.min(xr, c.getAbsoluteLeft() + c.getOffsetWidth() - popupPanel.getOffsetWidth()), yr);

	}
	
	public void removeFromParent(){
		popupPanel.removeFromParent();
	}
	
	public void clearItems(){
		popupMenu.clearItems();
	}
	
	public int getComponentCount(){
		return popupMenuSize;
	}
	
	public void addSeparator(){
		popupMenu.addSeparator();
	}
	
	public void addItem(MenuItem item) {
	    popupMenu.addItem(item);
    }
	
	public MenuBar getPopupMenu(){
		return popupMenu;
	}
	
	public MenuItem add(Command action, String html, String text) {
		MenuItem mi;
	    if (html != null) {
	    	mi = new MenuItem(html, true, action);
	    } else {
	    	mi = new MenuItem(text, action);
	    }
	    popupMenu.addItem(mi); 
	    popupMenuSize++;
	    return mi;
    }

}
