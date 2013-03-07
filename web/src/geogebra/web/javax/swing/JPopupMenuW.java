package geogebra.web.javax.swing;

import geogebra.common.main.App;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	
	private void addHideCommandFor(MenuItem item){
		MenuBar submenu = item.getSubMenu();
		if (submenu==null){
			final ScheduledCommand oldCmd = item.getScheduledCommand();
			ScheduledCommand cmd = new ScheduledCommand(){
				public void execute() {
					oldCmd.execute();
					popupPanel.hide();
	            }
			};
			item.setScheduledCommand(cmd);
		} else {
//			CloseHandler<PopupPanel> closehandler = new CloseHandler<PopupPanel>(){
//				public void onClose(CloseEvent<PopupPanel> event) {
//	                App.debug("popuppanel closed");
//                }
//			}; 
//			submenu.addCloseHandler(closehandler);
//			submenu.addHandler(closehandler, CloseEvent.getType());			
			
			submenu.addHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
	                App.debug("onclick happened");
	                popupPanel.hide();
                }
			}, ClickEvent.getType());
		}		
	}
	
	public void addItem(final MenuItem item) {
		addHideCommandFor(item);
	    popupMenu.addItem(item);
	    popupMenuSize++;
    }
	
	public MenuBar getPopupMenu(){
		return popupMenu;
	}
	
//	public MenuItem add(final Command action, String html, String text) {
//		MenuItem mi;
//		Command cmd = new Command(){
//			public void execute() {
//				action.execute();
//				popupPanel.hide();
//			}		
//		};
//		
//	    if (html != null) {
//	    	mi = new MenuItem(html, true, cmd);
//	    } else {
//	    	mi = new MenuItem(text, cmd);
//	    }
//	    popupMenu.addItem(mi); 
//	    popupMenuSize++;
//	    return mi;
//    }

	public void addItem(GCheckBoxMenuItem item) {
	    addItem(item.getMenuItem());
	    
    }

//	public void addItem(String string, boolean asHtml, MenuBar submenu) {
//		popupMenu.addItem(string, asHtml, submenu);
//	    
//    }

	public void hide(){
		popupPanel.hide();
	}
}
