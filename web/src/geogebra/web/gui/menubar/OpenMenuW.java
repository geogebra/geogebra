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
	private App app;
	private MenuItem openFromGoogleDrive;
	private MenuItem openFromSkyDrive;

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
		openFromGoogleDrive = addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive")),true, getLoginToGoogleCommand());
		openFromSkyDrive = addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromSkyDrive")),true, getLoginToSkyDriveCommand());
	}
	
	private Command getOpenFromGoogleDriveCommand() {
		return new Command() {
			
			public void execute() {
				//Window.open("https://drive.google.com/?tab=co&authuser=0#search/.ggb", "_blank", "");
				((GuiManagerW) app.getGuiManager()).openFromGoogleDrive();
			}
		};
	}
	
	public Command getOpenFromSkyDriveCommand() {
		return new Command() {
			
			public void execute() {
				((GuiManagerW) app.getGuiManager()).openFromSkyDrive();
				
			}
		};
	}
	
	private Command getLoginToGoogleCommand() {
		return new Command() {
			
			public void execute() {
				((AppW) app).getObjectPool().getMyGoogleApis().setCaller("open");
				((AppW) app).getObjectPool().getMyGoogleApis().loginToGoogle();
				
			}
		};
	}
	
	private Command getLoginToSkyDriveCommand() {
		return new Command() {
			public void execute() {
				((AppW) app).getObjectPool().getMySkyDriveApis().setCaller("open");
				((AppW) app).getObjectPool().getMySkyDriveApis().loginToSkyDrive();
				
			}
		};
	}
	
	public void refreshIfLoggedIntoGoogle(boolean loggedIn) {
		if (loggedIn) {
			openFromGoogleDrive.setHTML(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive")));
			openFromGoogleDrive.setScheduledCommand(getOpenFromGoogleDriveCommand());
			openFromGoogleDrive.setTitle(app.getMenu("LoggedIntoGoogleDrive"));
		} else {
			openFromGoogleDrive.setHTML(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive")));
			openFromGoogleDrive.setScheduledCommand(getLoginToGoogleCommand());
			openFromGoogleDrive.setTitle(app.getMenu("LogIntoGoogleDrive"));
		}
	}

	public void refreshIfLoggedIntoSkyDrive(boolean loggedIn) {
		if (loggedIn) {
			openFromSkyDrive.setHTML(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.skydrive_icon_16().getSafeUri().asString(), app.getMenu("OpenFromSkyDrive")));
			openFromSkyDrive.setScheduledCommand(getOpenFromSkyDriveCommand());
			openFromSkyDrive.setTitle(app.getMenu("LoggedIntoSkyDrive"));
		} else {
			openFromSkyDrive.setHTML(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromSkyDrive")));
			openFromSkyDrive.setScheduledCommand(getLoginToSkyDriveCommand());
			openFromSkyDrive.setTitle(app.getMenu("LogIntoSkyDrive"));
		}
    }
}
