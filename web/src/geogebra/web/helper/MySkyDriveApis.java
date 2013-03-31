package geogebra.web.helper;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.html5.FormData;
import geogebra.web.html5.XHR2;
import geogebra.web.main.AppW;

import java.util.Date;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class MySkyDriveApis {
	
	private App app;
	boolean skyDriveApiLoaded = false;
	private long tokenExpiresAt;
	boolean signedIn = false;
	private String callBack = null;
	private boolean loggedIn;
	private String loggedInUser;
	
	public MySkyDriveApis(App app) {
		this.app = app;
		goForSkyDriveApi();
	}

	private void goForSkyDriveApi() {
		
		ScriptNameSpace nameSpace = (ScriptNameSpace)	Document.get().createIFrameElement();
		nameSpace.renameNameSpace(GeoGebraConstants.SKYDRIVE_API_URL, "WL", "WindowsLive", new ScriptLoadCallback() {	
			public void onLoad() {
				skyDriveApiLoaded  = true;
			}
		});
		Document.get().getBody().appendChild(nameSpace);
    }

	public boolean signedInToSkyDrive() {
	    return signedIn;
    }
	
	public native void clearAllTokens() /*-{
		$wnd.WindowsLive.logout();
	}-*/;

	public void logout() {
		clearAllTokens();
	    refreshLoggedInGui(false);
    }
	
	public native JavaScriptObject getPutFileCallback(String fileName, String description) /*-{
    var _this = this;
    return function(base64) {
    	var fName = fileName;
    	var ds = description;
    	_this.@geogebra.web.helper.MySkyDriveApis::saveFileToSkyDrive(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(fName,ds,base64);
    };
	}-*/;
	
	private void saveFileToSkyDrive(String fileName, String description, String base64) {
		
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
	    	$wnd.WindowsLive.init({ client_id: @geogebra.common.GeoGebraConstants::SKYDRIVE_CLIENT_ID, redirect_uri: @geogebra.common.GeoGebraConstants::SKYDRIVE_REDIRECT_URI });
			$wnd.WindowsLive.Event.subscribe("auth.login", onLogin);
			$wnd.WindowsLive.Event.subscribe("auth.sessionChange", onSessionChange);
			
			var session = $wnd.WindowsLive.getSession();
			if (session) {
			  	this.@geogebra.web.helper.MySkyDriveApis::setUserEmailAfterLogin()();
			  	this.@geogebra.web.helper.MySkyDriveApis::setExpiresAt(Ljava/lang/String;)(session.expires_in);
			} else {
			    $wnd.WindowsLive.login({ scope: "wl.signin wl.basic wl.skydrive" });
			}
			
			function onLogin() {
			    var session = $wnd.WindowsLive.getSession();
			    if (session) {
			    _this.@geogebra.web.helper.MySkyDriveApis::setUserEmailAfterLogin()();
			  	_this.@geogebra.web.helper.MySkyDriveApis::setExpiresAt(Ljava/lang/String;)(session.expires_in);
			    }
			}
			 
			function onSessionChange() {
			    var session = $wnd.WindowsLive.getSession();
			    if (session) {
			        $wnd.console.log("Your session has changed.");
			    }
			}
	    }
	}-*/;
	
	public native void setUserEmailAfterLogin() /*-{
		var _this = this;
		$wnd.WindowsLive.api({ path: "/me", method: "GET" }).then(
        function (response) {
            _this.@geogebra.web.helper.MySkyDriveApis::loggedIntoSkyDriveSuccessFull(Ljava/lang/String;)(response.name);
        },
        function (response) {
            $wnd.console.log("API call failed: " + JSON.stringify(response.error).replace(/,/g, "\n"));
        }
    	);
    
    
	}-*/;
	
	public void loggedIntoSkyDriveSuccessFull(String name) {
		setLoggedIn(true);
		loggedInUser = name;
		initSkyDriveTokenChecking();
		refreshLoggedInGui(true);
		if (callBack != null) {
			callCallback();
		}
	}
	
	private AnimationHandle checker;
	
	private void initSkyDriveTokenChecking() {
		final MySkyDriveApis _this = this;
		checker = AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			
			public void execute(double timestamp) {
				long current = new Date().getTime();
				if (current > tokenExpiresAt) {
					((DialogManagerW) _this.app.getDialogManager()).getAlertDialog().get(app.getLocalization().getMenu("TimeExpired")).show();
					((AppW) app).getObjectPool().getGgwMenubar().getMenubar().getLogOutFromSkyDrive().getScheduledCommand().execute();
					checker.cancel();
					return;
				}
				AnimationScheduler.get().requestAnimationFrame(this);
			}
		});
	}
	
	private void callCallback() {
		if (callBack.equalsIgnoreCase("open")) {
			((GuiManagerW) app.getGuiManager()).openFromSkyDrive();
		}
	}
	
	private void setExpiresAt(String expires_in) {
		long current = new Date().getTime();
		tokenExpiresAt = current + (Integer.parseInt(expires_in) * 1000);
	}

	public String getLoggedInUser() {
	    return loggedInUser;
    }

	public boolean isLoggedIn() {
	    return loggedIn;
    }

	public void setLoggedIn(boolean loggedIn) {
	    this.loggedIn = loggedIn;
    }
	
	public boolean isLoaded() {
		return skyDriveApiLoaded;
	}

	public void loadFromSkyDrive(String id, String name, String source) {
		XHR2 xhr = (XHR2) XHR2.create();
		xhr.open("POST", GWT.getModuleBaseURL() + GeoGebraConstants.SKYDRIVE_PROXY_URL);
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			
			public void onReadyStateChange(XMLHttpRequest xhr) {
				if (xhr.getReadyState() == xhr.DONE && xhr.getStatus() == 200) {
					App.debug(xhr.getResponseText());
				}
			}
		});
		FormData fd = FormData.create();
		fd.append("source", source);
		xhr.send(fd);
    };

}
