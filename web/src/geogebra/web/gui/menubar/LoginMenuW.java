package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class LoginMenuW extends MenuBar {
	
	private App app;
	private MenuItem loginToGoogle;
	private MenuItem loginToSkyDrive;
	
	public LoginMenuW(App app) {
		super(true);
		this.app = app;
		init();
        addStyleName("GeoGebraMenuBar");
	}
	
	private void init() {
		createLoginToGoogle();
		//TODO:createLoginToSkyDrive();
	}
	
	private void createLoginToSkyDrive() {
		Command c = null;
		String menuHtml = "";
		/*reserve for later, when we will have client side Oauth*/
		if (((AppW) app).getObjectPool().getMySkyDriveApis().signedInToSkyDrive()) {
			c = createCommandForSignedIn();
		} else {
			c = createCommandForNotSignedIn();
			menuHtml = createMenuHtmlForNotSignedIn(app);
		}
		
        loginToGoogle = addItem(menuHtml,true,c);
    }

	private void createLoginToGoogle() {
		
		Command c = null;
		String menuHtml = "";
		/*reserve for later, when we will have client side Oauth*/
		if (((AppW) app).getObjectPool().getMyGoogleApis().signedInToGoogle()) {
			c = createCommandForSignedIn();
		} else {
			c = createCommandForNotSignedIn();
			menuHtml = createMenuHtmlForNotSignedIn(app);
		}
		
        loginToGoogle = addItem(menuHtml,true,c);
    }
	
	Command createCommandForNotSignedIn() {
        return new Command() {
			
			public void execute() {
				((AppW) app).getObjectPool().getMyGoogleApis().loginToGoogle();
			}
		};
    }

	Command createCommandForSignedIn() {
        return new Command() {
			
			public void execute() {
				//Web.AUTH.clearAllTokens();
				((AppW) app).getObjectPool().getMySkyDriveApis().clearAllTokens();
				//TODO:loginToSkyDrive.setHTML(createMenuHtmlForNotSignedIn(app));
				//TODO:loginToSkyDrive.setScheduledCommand(createCommandForNotSignedIn());
				//TODO:((AppW) app).getObjectPool().getGgwMenubar().getMenubar().getFileMenu().getOpenMenu().refreshIfLoggedIntoGoogle(false);
			}
		};
    }
	
	public void setLoggedIntoGoogle(String email, String name) {
		loginToGoogle.setHTML(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString(), email));
		loginToGoogle.setScheduledCommand(createCommandForSignedIn());
		loginToGoogle.setTitle(name);
		((AppW) app).getObjectPool().getGgwMenubar().getMenubar().getFileMenu().getOpenMenu().refreshIfLoggedIntoGoogle(true);
	}
	
	public void setLoggedOutFromGoogle() {
		loginToGoogle.setScheduledCommand(createCommandForNotSignedIn());
	}

	public MenuItem getLoginToGoogle() {
        return loginToGoogle;
    }
	
	static String createMenuHtmlForNotSignedIn(App app) {
        return GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString()
        		, app.getMenu("Login"));
    }

}
