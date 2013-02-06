package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.dialog.GgbFileInputDialog;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
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

	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.document_open().getSafeUri().asString(), app.getMenu("Open")), true, new Command() {
			
			public void execute() {
	    		GgbFileInputDialog dialog = new GgbFileInputDialog((AppW) app, null);
	    		dialog.setVisible(true);
			}
				
		});
	    
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
	    
		if (!((AppW)app).getNativeEmailSet().equals("")){
			addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive")),true,new Command() {
			
				public void execute() {
					Window.open("https://drive.google.com/?tab=co&authuser=0#search/.ggb", "_blank", "");
				}
			});
		}
		else
			addItem(GeoGebraMenubarW.getMenuBarHtmlGrayout(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive")),true,new Command() {
				public void execute() {
					//do nothing
				}
			});

	}

}
