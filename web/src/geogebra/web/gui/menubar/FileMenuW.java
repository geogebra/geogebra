package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.dialog.GgbFileInputDialog;
import geogebra.web.gui.images.AppResources;
import geogebra.web.helper.MyGoogleApis;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class FileMenuW extends MenuBar {
	
	private App app;
	private MenuItem openFromGoogleDrive;
	
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
	    
		if (MyGoogleApis.signedInToGoogle()){
			openFromGoogleDrive = addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive")),true,getOpenFromGoogleDriveCommand());
		}
		else
			openFromGoogleDrive = addItem(GeoGebraMenubarW.getMenuBarHtmlGrayout(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive")),true,new Command() {
				public void execute() {
					//do nothing
				}
			});

	}
	
	private Command getOpenFromGoogleDriveCommand() {
		return new Command() {
			
			public void execute() {
				//Window.open("https://drive.google.com/?tab=co&authuser=0#search/.ggb", "_blank", "");
				((GuiManagerW) app.getGuiManager()).openFromGoogleDrive();
			}
		};
	}
	
	public void refreshIfLoggedIntoGoogle(boolean loggedIn) {
		if (loggedIn) {
			openFromGoogleDrive.setHTML(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive")));
			openFromGoogleDrive.setScheduledCommand(getOpenFromGoogleDriveCommand());
		} else {
			openFromGoogleDrive.setHTML(GeoGebraMenubarW.getMenuBarHtmlGrayout(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive")));
			openFromGoogleDrive.setScheduledCommand(new Command() {
				
				public void execute() {
					//do nothing
				}
			});
		}
	}

}
