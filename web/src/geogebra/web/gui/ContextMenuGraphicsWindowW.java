package geogebra.web.gui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
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
        
        MenuBar yaxisMenu = new MenuBar(true);
        MenuItem yaxisMenuItem = new MenuItem(app.getPlain("xAxis") + " : " 
        							+ app.getPlain("yAxis"), yaxisMenu);
        addAxesRatioItems(yaxisMenu);
        popupMenu.addItem(yaxisMenuItem);
        
        MenuItem miShowAllObjectsView = new MenuItem(app.getPlain("ShowAllObjects"), new Command() {
			
			public void execute() {
				setViewShowAllObject();
			}

		});
        popupMenu.addItem(miShowAllObjectsView);
        
        MenuItem miStandardView = new MenuItem(app.getPlain("StandardView"), new Command() {
			
			public void execute() {
				setStandardView();
			}
		});
        popupMenu.addItem(miStandardView);
        
        if(!ev.isZoomable()){
        	zoomMenuItem.setEnabled(false);
        	yaxisMenuItem.setEnabled(false);
        	miShowAllObjectsView.setEnabled(false);
        	miStandardView.setEnabled(false);
        }
        
        if(ev.isLockedAxesRatio()){
        	yaxisMenuItem.setEnabled(false);
        }
        
        addMiProperties();
               
    }
	
	private void addMiProperties() {
	    MenuItem miProperties = new MenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.view_properties16().getSafeUri().asString(), app.getPlain("DrawingPad") + " ..."), true, new Command() {
			
			public void execute() {
				showOptionsDialog();
			}
		});
	    miProperties.setEnabled(false); //TMP AG
	    popupMenu.addItem(miProperties);
    }

	protected void showOptionsDialog() {
		app.getGuiManager().setShowView(true, App.VIEW_PROPERTIES);
		Window.alert("Later...");
	    
    }

	protected void setStandardView() {
	    app.setStandardView();
    }

	protected void setViewShowAllObject() {
        app.setViewShowAllObjects();
    }

	private void addAxesRatioItems(MenuBar menu) {
		double scaleRatio = ((EuclidianView)app.getActiveEuclidianView()).getScaleRatio(); 
		
		MenuItem mi;
		
		boolean separatorAdded = false;
		StringBuilder sb = new StringBuilder();
		 for (int i=0; i < axesRatios.length; i++) {                        
	            // build text like "1 : 2"
	            sb.setLength(0);
	            if (axesRatios[i] > 1.0) {                                 
	                sb.append((int) axesRatios[i]);
	                sb.append(" : 1");
	                if (! separatorAdded) {
	                    menu.addSeparator();
	                    separatorAdded = true;
	                }
	                
	            } else { // factor 
	            	if (axesRatios[i] == 1) 
	                	menu.addSeparator(); 
	                sb.append("1 : "); 
	                sb.append((int) (1.0 / axesRatios[i]));                               
	            } 
	            //TODO: it is terrible, should be used ONE listener for each menuItem, this kills the memory, if GWT changes this 
	            // get it right!
	            final int index = i;
	            mi = new MenuItem(sb.toString(), new Command() {
					
					public void execute() {
						zoomYaxis(axesRatios[index]);
					}
				});
	            GeoGebraMenubarW.setMenuSelected(mi, Kernel.isEqual(axesRatios[i], scaleRatio));
	            menu.addItem(mi);
		 }
	    
    }

	protected void zoomYaxis(double axesRatio) {
		app.zoomAxesRatio(axesRatio);    	
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
	        //TODO: it is terrible, should be used ONE listener for each menuItem, this kills the memory, if GWT changes this 
	            // get it right!
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
