package geogebra.web.gui;

import com.google.gwt.user.client.Command;
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
    }

	private void addAxesAndGridCheckBoxes() {
	    MenuItem cbShowAxes = addAction(((AppW)app).getGuiManager().getShowAxesAction(), GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.axes().getSafeUri().asString(), app.getMenu("Axes")), app.getMenu("Axes"));
	    ((AppW)app).setShowAxesSelected(cbShowAxes);
	    
	    
	    MenuItem cbShowGrid = addAction(((AppW)app).getGuiManager().getShowGridAction(), GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.grid().getSafeUri().asString(), app.getMenu("Grid")), app.getMenu("Grid"));
	    ((AppW)app).setShowGridSelected(cbShowGrid);
	}

	

}
