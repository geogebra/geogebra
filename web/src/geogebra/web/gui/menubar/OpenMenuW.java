package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.EventRenderable;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.dialog.GgbFileInputDialog;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;
import geogebra.web.move.googledrive.events.GoogleDriveLoadedEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class OpenMenuW extends MenuBar implements EventRenderable {
	/**
	 * Application instance
	 */
	private App app;
	private MenuItem openFromGoogleDrive;
	//private MenuItem openFromSkyDrive;
	private MenuItem openURL;

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
		
		 addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.document_open().getSafeUri().asString(), app.getPlain("Open")), true, new Command() {
				
				public void execute() {
		    		GgbFileInputDialog dialog = new GgbFileInputDialog((AppW) app, null);
		    		dialog.setVisible(true);
				}
					
			});
		    
			// this is enabled always
		  openURL = addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.document_open().getSafeUri().asString(),app.getMenu("OpenWebpage")),true,new Command() {
		    	public void execute() {
		    		app.getGuiManager().openURL();
		    	}
		    });	
		openFromGoogleDrive = addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive")),true, getLoginToGoogleCommand());
		//openFromSkyDrive = addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromSkyDrive")),true, getLoginToSkyDriveCommand());
		
		 ((AppW) app).getNetworkOperation().getView().add(new BooleanRenderable() {
				
				public void render(boolean b) {
					renderNetworkOperation(b);
				}
			});
		    
		
		
		((AppW) app).getGoogleDriveOperation().getView().add(this);
		
		if (!((AppW) app).getNetworkOperation().getOnline()) {
			renderNetworkOperation(false);
		}
		
		enableGoogleDrive((((AppW) app).getGoogleDriveOperation().isDriveLoaded()));
	}

	/**
	 * renders the menu concerning online - offline state
	 * @param online online - offline state
	 */
	void renderNetworkOperation(boolean online) {
	    openFromGoogleDrive.setEnabled(online);
	    //openFromSkyDrive.setEnabled(online);
	    openURL.setEnabled(online);
	    
	    if (!online) {
	    	openFromGoogleDrive.setTitle(app.getMenu("YouAreOffline"));    
	    	//openFromSkyDrive.setTitle("YouAreOffline");
	    	openURL.setTitle("YouAreOffline");
	    } else {
	    	openFromGoogleDrive.setTitle("");    
	    	//openFromSkyDrive.setTitle("");
	    	openURL.setTitle("");
	    }
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
			//openFromSkyDrive.setHTML(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.skydrive_icon_16().getSafeUri().asString(), app.getMenu("OpenFromSkyDrive")));
			//openFromSkyDrive.setScheduledCommand(getOpenFromSkyDriveCommand());
			//openFromSkyDrive.setTitle(app.getMenu("LoggedIntoSkyDrive"));
		} else {
			//openFromSkyDrive.setHTML(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromSkyDrive")));
			//openFromSkyDrive.setScheduledCommand(getLoginToSkyDriveCommand());
			//openFromSkyDrive.setTitle(app.getMenu("LogIntoSkyDrive"));
		}
    }

	@Override
    public void renderEvent(BaseEvent event) {
	    if (event instanceof GoogleDriveLoadedEvent) {
	    	enableGoogleDrive((((AppW) app).getGoogleDriveOperation().isDriveLoaded()));
	    }
    }

	private void enableGoogleDrive(boolean enabled) {
	    openFromGoogleDrive.setEnabled(enabled);
    }
}


