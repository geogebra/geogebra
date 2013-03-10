package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.dialog.GgbFileInputDialog;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class OpenMenuW extends MenuBar {
	/**
	 * Application instance
	 */
	App app;
	private MenuItem openFromGoogleDrive;

	/**
	 * Constructs the "Open" menu
	 * @param app Application instance
	 */
	public OpenMenuW(App app) {

		super(true);
		this.app = app;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}

	private void initActions() {
		
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
		
		
		if (((AppW) app).getObjectPool().getMyGoogleApis().signedInToGoogle()){
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
