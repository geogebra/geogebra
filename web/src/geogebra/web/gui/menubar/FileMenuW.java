package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends MenuBar {
	
	/** Application */
	App app;
	private OpenMenuW openMenu;
	
	/**
	 * @param app application
	 */
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

	    openMenu = new OpenMenuW(app);
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.document_open().getSafeUri().asString(), app.getPlain("Open")),true, openMenu);
	   
		
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.document_save().getSafeUri().asString(), app.getMenu("SaveAs")),true,new Command() {
		
			public void execute() {
				app.getGuiManager().save();
			}
		});			
			

		// this is enabled always
	    MenuItem uploadToGGT = addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.export_small().getSafeUri().asString(),app.getMenu("Share")),true,new Command() {
	    	public void execute() {
	    		app.uploadToGeoGebraTube();
	    	}
	    });
	    
	    if (!((AppW) app).getOfflineOperation().getOnline()) {
	    	uploadToGGT.setEnabled(false);
	    	uploadToGGT.setTitle(app.getMenu("YouAreOffline"));
	    	
	    }
	    
		

	}

	/**
	 * @return Open submenu
	 */
	public OpenMenuW getOpenMenu() {
	   return openMenu;
    }
	
	

}
