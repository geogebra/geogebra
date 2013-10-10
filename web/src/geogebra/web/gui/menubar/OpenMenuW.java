package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.EventRenderable;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.dialog.GgbFileInputDialog;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;
import geogebra.web.move.googledrive.events.GoogleLogOutEvent;
import geogebra.web.move.googledrive.events.GoogleLoginEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class OpenMenuW extends MenuBar implements EventRenderable {
	/**
	 * Application instance
	 */
	private App app;
	private MenuItem openFromGoogleDrive;
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
		openFromGoogleDrive = addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("OpenFromGoogleDrive")),true, getOpenFromGoogleDriveCommand());
		
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


