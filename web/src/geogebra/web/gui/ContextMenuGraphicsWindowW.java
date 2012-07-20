package geogebra.web.gui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.main.AppW;

public class ContextMenuGraphicsWindowW extends ContextMenuGeoElementW {

	private double px;
	private double py;

	ContextMenuGraphicsWindowW(AppW app) {
	    super(app);
    }

	public ContextMenuGraphicsWindowW(AppW app, double px, double py) {
	    this(app);
	    
	    this.px = px;
	    this.py = py;
	    
	    EuclidianViewInterfaceCommon ev= app.getActiveEuclidianView();
        if(ev.getEuclidianViewNo()==2){
        	setTitle(app.getPlain("DrawingPad2"));
        } else {
        	setTitle(app.getPlain("DrawingPad"));
        }
        
        addAxesAndGridCheckBoxes();
        
        popupMenu.addSeparator();
        
        // zoom for both axes
        MenuBar zoomMenu = new MenuBar(true);
        MenuItem zoomMenuItem = new MenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.zoom16().getSafeUri().asString(), app.getMenu("Zoom")), true, zoomMenu);
        popupMenu.addItem(zoomMenuItem);
        addZoomItems(zoomMenu);
    }

	private void addZoomItems(MenuBar menu) {
	    int perc;
	    
	    MenuItem mi;
	    boolean separatorAdded = false;
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < zoomFactors.length; i++) {
	    	perc = (int) (zoomFactors[i] * 100.0);
	    	// build text like "125%" or "75%"
	          sb.setLength(0);
	          if (perc > 100) {           
	               
	          } else {
	              if (! separatorAdded) {
	                  menu.addSeparator();
	                  separatorAdded = true;
	              }         
	          }                           
	          sb.append(perc);
	          sb.append('%'); 
	          final int index = i;
	          mi = new MenuItem(sb.toString(), new Command() {
				
				public void execute() {
					zoom(zoomFactors[index]);
				}
	          });
	          menu.addItem(mi);
	    }
	    
    }
	
	private void zoom(double zoomFactor) {
        app.zoom(px, py, zoomFactor);       
    }

	private void addAxesAndGridCheckBoxes() {
	    MenuItem cbShowAxes = addAction(((AppW)app).getGuiManager().getShowAxesAction(), GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.axes().getSafeUri().asString(), app.getMenu("Axes")), app.getMenu("Axes"));
	    ((AppW)app).setShowAxesSelected(cbShowAxes);
	    
	    
	    MenuItem cbShowGrid = addAction(((AppW)app).getGuiManager().getShowGridAction(), GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.grid().getSafeUri().asString(), app.getMenu("Grid")), app.getMenu("Grid"));
	    ((AppW)app).setShowGridSelected(cbShowGrid);
	}

	

}
