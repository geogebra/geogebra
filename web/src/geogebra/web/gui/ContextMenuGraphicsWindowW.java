package geogebra.web.gui;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.menubar.MyActionListener;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class ContextMenuGraphicsWindowW extends ContextMenuGeoElementW implements MyActionListener{

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
        
        wrappedPopup.addSeparator();
        
        // zoom for both axes
        MenuBar zoomMenu = new MenuBar(true);
        MenuItem zoomMenuItem = new MenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.zoom16().getSafeUri().asString(), app.getMenu("Zoom")), true, zoomMenu);
        wrappedPopup.addItem(zoomMenuItem);
        addZoomItems(zoomMenu);
        
        RadioButtonMenuBar yaxisMenu = app.getFactory().newRadioButtonMenuBar(app);
        addAxesRatioItems(yaxisMenu);
        
		app.addMenuItem(wrappedPopup,
		        app.getEmptyIconFileName(),
		        app.getPlain("xAxis") + " : " + app.getPlain("yAxis"), true,
		        yaxisMenu);

        MenuItem miShowAllObjectsView = new MenuItem(app.getPlain("ShowAllObjects"), new Command() {
			
			public void execute() {
				setViewShowAllObject();
			}

		});
        wrappedPopup.addItem(miShowAllObjectsView);
        
        MenuItem miStandardView = new MenuItem(app.getPlain("StandardView"), new Command() {
			
			public void execute() {
				setStandardView();
			}
		});
        wrappedPopup.addItem(miStandardView);
        
        if(!ev.isZoomable()){
        	zoomMenuItem.setEnabled(false);
        	((MenuItem) yaxisMenu).setEnabled(false);
        	miShowAllObjectsView.setEnabled(false);
        	miStandardView.setEnabled(false);
        }
        
        if(ev.isLockedAxesRatio()){
        	((MenuItem) yaxisMenu).setEnabled(false);
        }
        
        //addMiProperties();
               
    }
	
	private void addMiProperties() {
	    MenuItem miProperties = new MenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.view_properties16().getSafeUri().asString(), app.getPlain("DrawingPad") + " ..."), true, new Command() {
			
			public void execute() {
				showOptionsDialog();
			}
		});
	    miProperties.setEnabled(false); //TMP AG
	    wrappedPopup.addItem(miProperties);
    }

	protected void showOptionsDialog() {
		app.getGuiManager().setShowView(true, App.VIEW_PROPERTIES);
    }

	protected void setStandardView() {
	    app.setStandardView();
    }

	protected void setViewShowAllObject() {
        app.setViewShowAllObjects();
    }

	private void addAxesRatioItems(RadioButtonMenuBar menu) {
		
		double scaleRatio = ((EuclidianView)app.getActiveEuclidianView()).getScaleRatio(); 
		
		MenuItem mi;
		String[] items = {};
		String[] actionCommands = {};
		
		boolean separatorAdded = false;
		StringBuilder sb = new StringBuilder();
		 for (int i=0, j=0; i < axesRatios.length; i++, j++) {                        
	            // build text like "1 : 2"
	            sb.setLength(0);
	            if (axesRatios[i] > 1.0) {                                 
	                sb.append((int) axesRatios[i]);
	                sb.append(" : 1");
	                if (! separatorAdded) {
	                    //((MenuBar) menu).addSeparator();
	                	actionCommands[j] = "0.0";
	                	items[j++] = "---";
	                    separatorAdded = true;
	                }
	                
	            } else { // factor 
	            	if (axesRatios[i] == 1){
	                	//((MenuBar) menu).addSeparator();
	            		actionCommands[j] = "0.0";
	            		items[j++] = "---";
	            	}
	                sb.append("1 : "); 
	                sb.append((int) (1.0 / axesRatios[i]));                               
	            } 

	            
	            items[j] = sb.toString();
	            actionCommands[j] = ""+axesRatios[i];
		 }
		int selPos = 0;
		while ((selPos < actionCommands.length) &&
				!Kernel.isEqual(Double.parseDouble(actionCommands[selPos]), scaleRatio)) {
			selPos++;
		}
		
		App.debug("selPos: " +selPos);
		menu.addRadioButtonMenuItems(this, items, actionCommands,  selPos, false);
	 
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
					wrappedPopup.hide();
				}
	          });
	          menu.addItem(mi);
	    }
	    
    }
	
	private void zoom(double zoomFactor) {
        app.zoom(px, py, zoomFactor);       
    }

	private void addAxesAndGridCheckBoxes() {
//	    MenuItem cbShowAxes = addAction(((AppW)app).getGuiManager().getShowAxesAction(), GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.axes().getSafeUri().asString(), app.getMenu("Axes")), app.getMenu("Axes"));
//		SafeHtml cbHtml = SafeHtmlUtils.fromSafeConstant(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.axes().getSafeUri().asString(), app.getMenu("Axes")));
	
		String htmlString = GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.axes().getSafeUri().asString(), app.getMenu("Axes"));
		GCheckBoxMenuItem cbShowAxes = new GCheckBoxMenuItem(htmlString, ((AppW)app).getGuiManager().getShowAxesAction());
		
	    ((AppW)app).setShowAxesSelected(cbShowAxes);
	    wrappedPopup.addItem(cbShowAxes);
	    
	    
//	    MenuItem cbShowGrid = addAction(((AppW)app).getGuiManager().getShowGridAction(), GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.grid().getSafeUri().asString(), app.getMenu("Grid")), app.getMenu("Grid"));
		htmlString = GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.grid().getSafeUri().asString(), app.getMenu("Grid"));
		GCheckBoxMenuItem cbShowGrid = new GCheckBoxMenuItem(htmlString, ((AppW)app).getGuiManager().getShowGridAction());

	    ((AppW)app).setShowGridSelected(cbShowGrid);
	    wrappedPopup.addItem(cbShowGrid);

	}

    public void actionPerformed(String command) {
        try {   
            //zoomYaxis(Double.parseDouble(e.getActionCommand()));
        	zoomYaxis(Double.parseDouble(command));
        } catch (Exception ex) {
        }       
    }  
	

}
