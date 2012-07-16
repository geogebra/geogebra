package geogebra.web.gui;


import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import geogebra.common.gui.ContextMenuGeoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.main.AppW;
import geogebra.web.openjdk.awt.geom.Point;

/**
 * @author gabor
 * 
 * ContextMenuGeoElement for Web
 *
 */
public class ContextMenuGeoElementW extends ContextMenuGeoElement {
	
	protected PopupPanel wrappedPopup;
	protected MenuBar popupMenu;
	
	/**
	 * Creates new context menu
	 * @param app application
	 */
	ContextMenuGeoElementW(AppW app) {
		this.app = app;    
		this.wrappedPopup = new PopupPanel();
		this.popupMenu = new MenuBar(true);
		wrappedPopup.add(popupMenu);
	}
	
	/** Creates new MyPopupMenu for GeoElement
	 * @param app application
	 * @param geos selected elements
	 * @param location screen position
	 */
	public ContextMenuGeoElementW(AppW app, ArrayList<GeoElement> geos, Point location) {
		this(app);
		this.geos = geos;
		geo = geos.get(0);

		String title;

		if (geos.size() == 1) {
			title = getDescription(geo);
		} else {
			title = app.getPlain("Selection");
		}
		setTitle(title);        

		/*AG continue here...if (app.getGuiManager().showView(App.VIEW_ALGEBRA)) {
			addPointItems();
			addLineItems();
			addVectorItems();
			addConicItems();
			addNumberItems();	
			addUserInputItem();
			
			addViewForValueStringItems();
				
		}
		
		//TODO remove the condition when ggb version >= 5
		if (app.getKernel().getManager3D()!=null)
			addPlaneItems();


		
		
		if (wrappedPopup.getComponentCount() > 2)
			wrappedPopup.addSeparator();
		addForAllItems();*/
	}

	private void addAction(Command action, String html, String text) {
		MenuItem mi;
	    if (html != null) {
	    	mi = new MenuItem(html, true, action);
	    } else {
	    	mi = new MenuItem(text, action);
	    }
	    popupMenu.addItem(mi);  
    }

	private void setTitle(String str) {
	    MenuItem title = new MenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), str),
	    		true, new Command() {
					
					public void execute() {
						wrappedPopup.setVisible(false);
					}
				});
	    popupMenu.addItem(title);
    }
	
	public PopupPanel getWrappedPopup() {
	    return wrappedPopup;
    }
	
	public void show(Canvas c, int x, int y) {
		
	}
	
	
	
}
