package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.css.GuiResources;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.dialog.GgbFileInputDialog;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;
import geogebra.web.move.googledrive.events.GoogleLogOutEvent;
import geogebra.web.move.googledrive.events.GoogleLoginEvent;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class OpenMenuW extends MenuBar implements EventRenderable {
	/**
	 * Application instance
	 */
	private App app;
	private MenuItem openFromGoogleDrive;
	private MenuItem openFromGGT;

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
		
		 addItem(GeoGebraMenubarW.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_open().getSafeUri().asString(), app.getPlain("Open"), true), true, new Command() {
				
				public void execute() {
		    		GgbFileInputDialog dialog = new GgbFileInputDialog((AppW) app, null);
		    		dialog.setVisible(true);
				}
					
			});
		    
			// this is enabled always
		 
		openFromGoogleDrive = addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive"), true),true, getOpenFromGoogleDriveCommand());
		openFromGGT = addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.GeoGebraTube().getSafeUri().asString(), app.getMenu("OpenFromGeoGebraTube"), true),true, getOpenFromGeoGebraTubeCommand());
		
		 ((AppW) app).getNetworkOperation().getView().add(new BooleanRenderable() {
				
				public void render(boolean b) {
					renderNetworkOperation(b);
				}
			});
		    
		
		
		((AppW) app).getGoogleDriveOperation().getView().add(this);
		
		if (!((AppW) app).getNetworkOperation().getOnline()) {
			renderNetworkOperation(false);
		}
		
		enableGoogleDrive((((AppW) app).getGoogleDriveOperation().isLoggedIntoGoogle()));
	}

	private ScheduledCommand getOpenFromGeoGebraTubeCommand() {
		return new Command() {
					
					public void execute() {
						((GuiManagerW) app.getGuiManager()).openFromGGT();;
					}
		};
	}

	/**
	 * renders the menu concerning online - offline state
	 * @param online online - offline state
	 */
	void renderNetworkOperation(boolean online) {
	    openFromGoogleDrive.setEnabled(online);
	    //openFromSkyDrive.setEnabled(online);
	    openFromGGT.setEnabled(online);
	    
	    if (!online) {
	    	openFromGoogleDrive.setTitle(app.getMenu("YouAreOffline")); 
	    	openFromGGT.setTitle(app.getMenu("YouAreOffline"));
	    	//openFromSkyDrive.setTitle("YouAreOffline");
	    } else {
	    	openFromGoogleDrive.setTitle("");
	    	openFromGGT.setTitle("");
	    	//openFromSkyDrive.setTitle("");
	    }
    }
	
	private Command getOpenFromGoogleDriveCommand() {
		return new Command() {
			
			public void execute() {
				((GuiManagerW) app.getGuiManager()).openFromGoogleDrive();
			}
		};
	}



    public void renderEvent(BaseEvent event) {
	    if (event instanceof GoogleLoginEvent) {
	    	enableGoogleDrive(((GoogleLoginEvent) event).isSuccessFull());
	    } else if (event instanceof GoogleLogOutEvent) {
	    	enableGoogleDrive(false);
	    }
    }

	private void enableGoogleDrive(boolean enabled) {
	    openFromGoogleDrive.setEnabled(enabled);
    }
}


