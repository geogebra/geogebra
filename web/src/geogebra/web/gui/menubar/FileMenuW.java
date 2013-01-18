package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

public class FileMenuW extends MenuBar {
	
	private App app;
	
	public FileMenuW(App app) {
	    super(true);
	    this.app = app;
	    addStyleName("GeoGebraMenuBar");
	    initActions();
		update();
	}

	private void update() {
	    // TODO Auto-generated method stub
	    
    }

	private void initActions() {

		// this is enabled always
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("New")),true,new Command() {

			public void execute() {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();
			}
		});

	    /*addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.document_open().getSafeUri().asString(), app.getMenu("Load")), true, new Command() {
			
			public void execute() {
				
			}
				
		});*/
	    
		// this is enabled always
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.document_open().getSafeUri().asString(),app.getMenu("OpenWebpage")),true,new Command() {
	    	public void execute() {
	    		app.getGuiManager().openURL();
	    	}
	    });
		
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.document_save().getSafeUri().asString(), app.getMenu("SaveAs")),true,new Command() {
		
			public void execute() {
				app.getGuiManager().save();
			}
		});			
			

		// this is enabled always
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.export_small().getSafeUri().asString(),app.getMenu("Share")),true,new Command() {
	    	public void execute() {
	    		app.uploadToGeoGebraTube();
	    	}
	    });
    }

}
