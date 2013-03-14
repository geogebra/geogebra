package geogebra.web.helper;

import geogebra.common.main.App;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.html5.DynamicScriptElement;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Document;

public class MySkyDriveApis {
	
	private App app;
	boolean skyDriveApiLoaded = false;
	boolean signedIn = false;
	private String callBack = null;
	
	public MySkyDriveApis(App app) {
		this.app = app;
		goForSkyDriveApi();
	}

	private void goForSkyDriveApi() {
		DynamicScriptElement script = (DynamicScriptElement) Document.get().createScriptElement();
		script.setSrc("//js.live.net/v5.0/wl.js");
		script.addLoadHandler(new ScriptLoadCallback() {
			
			public void onLoad() {
				skyDriveApiLoaded  = true;
			}
		});
		Document.get().getBody().appendChild(script);
    }

	public boolean signedInToSkyDrive() {
	    return signedIn;
    }
	
	public native void clearAllTokens() /*-{
		$wnd.WL.logout();
	}-*/;

	public void logout() {
		clearAllTokens();
	    refreshLoggedInGui(false);
    }
	
	private void refreshLoggedInGui(boolean loggedIn) {
		((AppW) app).getObjectPool().getGgwMenubar().getMenubar().getFileMenu().getOpenMenu().refreshIfLoggedIntoSkyDrive(loggedIn);
		((AppW) app).getObjectPool().getGgwMenubar().getMenubar().refreshIfLoggedIntoSkyDrive(loggedIn);
		((DialogManagerW) app.getDialogManager()).getFileChooser().refreshIfLoggedIntoSkyDrive(loggedIn);
}

	public void setCaller(String caller) {
		callBack = caller;
    }

	public native void loginToSkyDrive() /*-{
	    var _this = this;
	    if (this.@geogebra.web.helper.MySkyDriveApis::skyDriveApiLoaded) {
	    	$wnd.WL.init({ client_id: @geogebra.common.GeoGebraConstants::SKYDRIVE_CLIENT_ID, redirect_uri: @geogebra.common.GeoGebraConstants::SYDRIVE_REDIRECT_URI });
			$wnd.WL.Event.subscribe("auth.login", onLogin);
			$wnd.WL.Event.subscribe("auth.sessionChange", onSessionChange);
			
			var session = $wnd.WL.getSession();
			if (session) {
			    $wnd.console.log("You are already signed in!");
			} else {
			    $wnd.WL.login({ scope: "wl.signin wl.basic wl.skydrive" });
			}
			
			function onLogin() {
			    var session = $wnd.WL.getSession();
			    if (session) {
			        $wnd.console.log("You are signed in!");
			    }
			}
			 
			function onSessionChange() {
			    var session = $wnd.WL.getSession();
			    if (session) {
			        $wnd.console.log("Your session has changed.");
			    }
			}
	    }
	}-*/;

}
