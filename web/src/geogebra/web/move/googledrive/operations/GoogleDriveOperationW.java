package geogebra.web.move.googledrive.operations;

import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.operations.BaseOperation;
import geogebra.common.move.views.BaseEventView;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.util.DynamicScriptElement;
import geogebra.html5.util.JSON;
import geogebra.web.main.AppW;
import geogebra.web.move.googledrive.events.GoogleDriveLoadedEvent;
import geogebra.web.move.googledrive.events.GoogleLoginEvent;
import geogebra.web.move.googledrive.models.GoogleDriveModelW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;

/**
 * @author gabor
 *
 *	Operational class for Google Drive Api
 */
public class GoogleDriveOperationW extends BaseOperation<EventRenderable> implements EventRenderable {
	
	private static final String GoogleApiJavaScriptSrc = "https://apis.google.com/js/client.js?onload=GGW_loadGoogleDrive";
	private boolean isDriveLoaded;
	private AppW app;
	private boolean loggedIn;

	
	/**
	 * creates new google drive operation instance
	 */
	public GoogleDriveOperationW (AppW app) {
		
		this.app = app;
		setView(new BaseEventView());
		setModel(new GoogleDriveModelW());
		
		app.getLoginOperation().getView().add(this);
		
	}
	
	@Override
    public GoogleDriveModelW getModel() {
		return (GoogleDriveModelW) super.getModel();
	}
	
	/**
	 * @return the logged in user name
	 */
	public String getUserName() {
		return getModel().getUserName();
	}
	
	/**
	 * Go for the google drive url, and fetch the script
	 */
	public void initGoogleDriveApi() {
		createGoogleApiCallbackFunction();
		fetchScript();
	}

	private void fetchScript() {
		DynamicScriptElement script = (DynamicScriptElement) Document.get().createScriptElement();
		script.setSrc(GoogleApiJavaScriptSrc);
		Document.get().getBody().appendChild(script);
    }

	private native void createGoogleApiCallbackFunction() /*-{
		var _this = this;
		$wnd.GGW_loadGoogleDrive = function() {
			_this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::loadGoogleDrive()();
			
		}
    }-*/;
	
	private native void loadGoogleDrive() /*-{
		var _this = this;
	    $wnd.gapi.client.load('drive', 'v2', function() {
	     _this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::googleDriveLoaded()();
        });
	}-*/;
	
	private void googleDriveLoaded() {
		this.isDriveLoaded = true;
		onEvent(new GoogleDriveLoadedEvent());
	}

	/**
	 * @return if google drive loaded or not
	 */
	public boolean isDriveLoaded() {
	    return isDriveLoaded;
    }
	
	/**
	 * logs in the user to Google
	 */
	public native void login() /*-{
		var _this = this,
			config = {'client_id': 	@geogebra.common.GeoGebraConstants::GOOGLE_CLIENT_ID,
	            	'scope': 	@geogebra.common.GeoGebraConstants::DRIVE_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::USERINFO_EMAIL_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::USERINFO_PROFILE_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::PLUS_ME_SCOPE,
	            	 'immediate': true};
	    config.max_auth_age = 0;
		$wnd.gapi.auth.authorize(config,
	            	 function (resp) {
	            	 		_this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::authorizeCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(resp);
	            	 	}
	           
	       );
	}-*/;
	
	private void authorizeCallback(JavaScriptObject resp) {
		if (JSON.get(resp, "error") != null) {
			this.loggedIn = false;
			onEvent(new GoogleLoginEvent(false));
		} else {
			this.loggedIn = true;
			onEvent(new GoogleLoginEvent(true));
		}
	}

    public void renderEvent(BaseEvent event) {
	    if (event instanceof LoginEvent) {
	    	if (((LoginEvent) event).isSuccessful()) {
	    		String type = app.getLoginOperation().getModel().getLoggedInUser().getIdentifier();
	    		if (type.indexOf("www.google.com") != -1) {
	    			if (this.isDriveLoaded) {
	    				this.login();
	    			} else {
	    				getView().add(new EventRenderable() {
							
							public void renderEvent(BaseEvent loadevent) {
								if (loadevent instanceof GoogleDriveLoadedEvent) {
									login();
								}
								
							}
						});
	    			}
	    		}
	    	}
	    }
	    
    }

	/**
	 * @return if the user is logged into google
	 */
	public boolean isLoggedIntoGoogle() {
	  return loggedIn;
    }

	/**
	 * @param currentFileName name of the file
	 * @param description description of the file
	 * @param title title
	 * @param id id of the file
	 */
	public native void loadFromGoogleFile(String currentFileName,
            String description, String title, String id) /*-{
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
			_this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::processGoogleDriveFileContent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(base64, description, title, id);
		});
    }-*/;
	
	private void processGoogleDriveFileContent(String base64, String description, String title, String id) {
		app.loadGgbFileAgain(base64);
		app.refreshCurrentFileDescriptors(title, description, base64);
		app.currentFileId = id;
	}
	
	

	

}
