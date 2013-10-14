package geogebra.web.helper;

import geogebra.common.main.App;
import geogebra.html5.util.DynamicScriptElement;
import geogebra.html5.util.JSON;
import geogebra.html5.util.ScriptLoadCallback;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.main.AppW;
import geogebra.web.presenter.LoadFilePresenter;

import java.util.Date;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

public class MyGoogleApis {
	
	private boolean firstLogin = true;
	private long tokenExpiresAt;
	
	private boolean loggedIn = false;
	

	public boolean driveLoaded = false;
	private App app;
	protected boolean googleApiLoaded;
	private String loggedInUser;
	private String loggedInEmail;
	private String callBack = null;

	/**
	 * @param app Application
	 * Instantiates MyGoogleApis that used for communicate with Google Drive
	 */
	public MyGoogleApis(App app) {
	    this.app = app;
	    goForGoogleDriveApi();
    }
	
	
	
	
	


	private native String getFileIdOrNull() /*-{
	    if ($wnd.GGW_appengine && $wnd.GGW_appengine.FILE_IDS[0] !== "") {
	    	return $wnd.GGW_appengine.FILE_IDS[0];
	    }
	    return null;
    }-*/;

	public void getFileFromGoogleDrive(
            String fileId, final LoadFilePresenter loadFilePresenter) {
		String url = "/svc?file_id="+fileId;
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		try {
	        builder.sendRequest(null, new RequestCallback() {
				
				public void onResponseReceived(Request request, Response response) {
					JavaScriptObject data = JSON.parse(response.getText());
					String title = JSON.get(data, "title");
					String content = JSON.get(data, "content");
					String mimeType = JSON.get(data, "mimeType");
					String description = JSON.get(data, "description");
					loadFilePresenter.process(content);
					((AppW)loadFilePresenter.getApplication()).refreshCurrentFileDescriptors(title, description);
				}
				
				public void onError(Request request, Throwable exception) {
					App.error(exception.getLocalizedMessage());
				}
			});
        } catch (Exception e) {
	        // TODO: handle exception
        }
	    
    }

	

	public boolean signedInToGoogle() {
	    return loggedIn;
	}
	
	private native void initGGWObject() /*-{
	var _this = this;
		$wnd.GGW_loadGoogleDrive = function() {
			_this.@geogebra.web.helper.MyGoogleApis::loadGoogleDrive()();
		}
	}-*/;
	
	private void goForGoogleDriveApi() {
		initGGWObject();
		
		DynamicScriptElement script = (DynamicScriptElement) Document.get().createScriptElement();
		script.setSrc("https://apis.google.com/js/client.js?onload=GGW_loadGoogleDrive");
		script.addLoadHandler(new ScriptLoadCallback() {
			
			public void onLoad() {
				googleApiLoaded  = true;
				//MyGoogleApis.loadGoogleDrive()();
			}
		});
		Document.get().getBody().appendChild(script);
	}

	public native void loadGoogleDrive() /*-{
		var _this = this;
	    $wnd.gapi.client.load('drive', 'v2', function() {
	     _this.@geogebra.web.helper.MyGoogleApis::driveLoaded = true;
        });
    }-*/;
	
	public native void loginToGoogle() /*-{
		var _this = this;
		var config = {'client_id': 	@geogebra.common.GeoGebraConstants::GOOGLE_CLIENT_ID,
	            	'scope': 	@geogebra.common.GeoGebraConstants::DRIVE_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::USERINFO_EMAIL_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::USERINFO_PROFILE_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::PLUS_ME_SCOPE,
	            	 'immediate': false};
	    if (!_this.@geogebra.web.helper.MyGoogleApis::firstLogin) {
	    	config.max_auth_age = 0;
	    }
		$wnd.gapi.auth.authorize(config,
	            	 function (resp) {
	            	 	if (!resp.error) {
	            	 		_this.@geogebra.web.helper.MyGoogleApis::setUserEmailAfterLogin()();
	            	 		_this.@geogebra.web.helper.MyGoogleApis::setExpiresAt(Ljava/lang/String;)(resp.expires_in);
	            	 	}
	            	 }
	       );
	}-*/;
	
	private void loggedIntoGoogleSuccessFull(String email, String name) {	
		loggedIn = true;
		loggedInUser = name;
		loggedInEmail = email;
		initGoogleTokenChecking();
		refreshLoggedInGui(true);
		if (callBack != null) {
			callCallback();
		}
	}
	
	private void callCallback() {
		if (callBack.equalsIgnoreCase("open")) {
			((GuiManagerW) app.getGuiManager()).openFromGoogleDrive();
		}
	}
	
	private void refreshLoggedInGui(boolean loggedIn) {
			//((AppW) app).getObjectPool().getGgwMenubar().getMenubar().getFileMenu().getOpenMenu().refreshIfLoggedIntoGoogle(loggedIn);
    		//((DialogManagerW) app.getDialogManager()).getFileChooser().refreshIfLoggedIntoGoogle(loggedIn);
	}
	private native void setUserEmailAfterLogin() /*-{
		var _this = this;
		$wnd.gapi.client.load('oauth2', 'v2', function() {
          			var request = $wnd.gapi.client.oauth2.userinfo.get();
					request.execute(
						function(resp) {
							var name;
							if (resp.email) {
								name = resp.name === undefined ? "" : resp.name;
								_this.@geogebra.web.helper.MyGoogleApis::loggedIntoGoogleSuccessFull(Ljava/lang/String;Ljava/lang/String;)(name, resp.email);
							}
						}
					)
		});	
	}-*/;

	public native void clearAllTokens() /*-{
	    this.@geogebra.web.helper.MyGoogleApis::firstLogin = false;
	    this.@geogebra.web.helper.MyGoogleApis::loggedIn = false;
    }-*/;
	
	

	/**
	 * @param currentFileName fileName
	 * @param description File Description
	 * @param title File Title
	 */
	public native void loadFromGoogleFile(String currentFileName, String description, String title, String id) /*-{
		var _this = this;
		
		function downloadFile(downloadUrl, callback) {
		  if (downloadUrl) {
		    var accessToken = $wnd.gapi.auth.getToken().access_token;
		    var xhr = new $wnd.XMLHttpRequest();
		    xhr.open('GET', downloadUrl);
		    xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
		    xhr.onload = function() {
		      callback(xhr.responseText);
		    };
		    xhr.onerror = function() {
		      callback(null);
		    };
		    xhr.send();
		  } else {
		    callback(null);
		  }
		}
		
		downloadFile(currentFileName,function (base64) {
			//_this.@geogebra.web.helper.MyGoogleApis::processGoogleDriveFileContent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(base64, description, title, id);
		});
    }-*/;
	
	private AnimationHandle checker;
	
	private void initGoogleTokenChecking() {
		final MyGoogleApis _this = this;
		checker = AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			
			public void execute(double timestamp) {
				long current = new Date().getTime();
				if (current > tokenExpiresAt) {
					((DialogManagerW) _this.app.getDialogManager()).getAlertDialog().get(app.getLocalization().getMenu("TimeExpired")).show();
					checker.cancel();
					return;
				}
				AnimationScheduler.get().requestAnimationFrame(this);
			}
		});
		
	}
	
	private void setExpiresAt(String expires_in) {
		long current = new Date().getTime();
		tokenExpiresAt = current + (Integer.parseInt(expires_in) * 1000);
	}
	
	public String getLoggedInUser() {
		return loggedInUser;
	}
	
	public String getLoggedInEmail() {
		return loggedInEmail;
	}

	public void logout() {
		clearAllTokens();
	    refreshLoggedInGui(false);
    }
	
	public void setCaller(String caller) {
		callBack = caller;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	

}
