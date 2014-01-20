package geogebra.web.move.googledrive.operations;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.operations.BaseOperation;
import geogebra.common.move.views.BaseEventView;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.util.DynamicScriptElement;
import geogebra.html5.util.JSON;
import geogebra.html5.util.URL;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.main.AppW;
import geogebra.web.move.googledrive.events.GoogleDriveLoadedEvent;
import geogebra.web.move.googledrive.events.GoogleLogOutEvent;
import geogebra.web.move.googledrive.events.GoogleLoginEvent;
import geogebra.web.move.googledrive.models.GoogleDriveModelW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
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
	private JavaScriptObject googleDriveURL;

	
	/**
	 * creates new google drive operation instance
	 * @param app Application
	 */
	public GoogleDriveOperationW (AppW app) {
		
		this.app = app;
		setView(new BaseEventView());
		setModel(new GoogleDriveModelW());
		
		app.getLoginOperation().getView().add(this);
		getView().add(this);
		
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

	private static void fetchScript() {
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
	 * @param immediate wheter to force login popup open
	 */
	public native void login(boolean immediate) /*-{
		var _this = this,
			config = {'client_id': 	@geogebra.common.GeoGebraConstants::GOOGLE_CLIENT_ID,
	            	'scope': 	@geogebra.common.GeoGebraConstants::DRIVE_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::USERINFO_EMAIL_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::USERINFO_PROFILE_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::PLUS_ME_SCOPE,
	            	 'immediate': immediate};
	    config.max_auth_age = 0;
		$wnd.gapi.auth.authorize(config,
	            	 function (resp) {
	            	 		_this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::authorizeCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(resp);
	            	 	}
	           
	       );
	}-*/;
	
	private void authorizeCallback(JavaScriptObject resp) {
		if (resp == null || JSON.get(resp, "error") != null) {
			this.loggedIn = false;
			onEvent(new GoogleLoginEvent(false));
		} else {
			this.loggedIn = true;
			onEvent(new GoogleLoginEvent(true));
		}
	}

    public void renderEvent(BaseEvent event) {
    	App.debug("event: " + event.toString());
    	if (event instanceof GoogleDriveLoadedEvent) {
    		checkIfOpenedFromGoogleDrive();
    		return;
    	}
    	if (event instanceof GoogleLoginEvent) {
    		if (((GoogleLoginEvent) event).isSuccessFull()) {
    			checkIfFileMustbeOpenedFromGoogleDrive();
    		} else {
    			if ("open".equals(getAction())) {
    				login(false);
    			} else if (getModel().lastLoggedInFromGoogleDrive()) {
    				login(false);
    			}    		
    		}
    		return;
    	}
	    if (event instanceof LoginEvent) {
	    	if (((LoginEvent) event).isSuccessful()) {
	    		String type = app.getLoginOperation().getModel().getLoggedInUser().getIdentifier();
	    		if (type.indexOf("www.google.com") != -1) {
	    			if (this.isDriveLoaded) {
	    				this.login(true);
	    				this.getModel().setLoggedInFromGoogleDrive(true);
	    			} else {
	    				getView().add(new EventRenderable() {
							
							public void renderEvent(BaseEvent loadevent) {
								if (loadevent instanceof GoogleDriveLoadedEvent) {
									login(true);
				    				GoogleDriveOperationW.this.getModel().setLoggedInFromGoogleDrive(true);
								}
								
							}
						});
	    			}
	    		} else {
    				GoogleDriveOperationW.this.getModel().setLoggedInFromGoogleDrive(false);
	    		}
	    	} else {
	    		logOut();
	    	}
	    	return;
	    }
	    if (event instanceof LogOutEvent) {
	    	logOut();
	    	return;
	    }
	    
    }

	private void checkIfFileMustbeOpenedFromGoogleDrive() {
	   if ("open".equals(getAction())) {
		   openFileFromGoogleDrive(googleDriveURL);
	   }
    }

	private native void openFileFromGoogleDrive(JavaScriptObject descriptors) /*-{
		var id = descriptors["ids"] ? descriptors["ids"][0] : undefined,
			_this = this;
			request;
		if ( id !== undefined) {
			request = $wnd.gapi.client.drive.files.get({
				fileId : id
			});
			request.execute(function(resp) {
				_this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::loadFromGoogleFile(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(resp.downloadUrl, resp.description, resp.title, resp.id);
			});
		}
    }-*/;

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
		    xhr.responseType = "blob";
		    xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
		    xhr.onload = function() {
		      callback(xhr.response);
		    };
		    xhr.onerror = function() {
		      callback(null);
		    };
		    xhr.send();
		  } else {
		    callback(null);
		  }
		}
		
		downloadFile(currentFileName,function (content) {
			var reader = new FileReader();
			reader.addEventListener("loadend", function(e) {
				if (e.target.result.indexOf("UEsDBBQ") === 0) {
					_this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::processGoogleDriveFileContentAsBase64(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(e.target.result, description, title, id);
				} else {
					_this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::processGoogleDriveFileContentAsBinary(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(content, description, title, id);
				}
			});
			reader.readAsText(content);
		});
    }-*/;
	
	private void processGoogleDriveFileContentAsBase64(String base64, String description, String title, String id) {
		app.loadGgbFileAsBase64Again(base64);
		postprocessFileLoading(description, title, id);
	}

	private void postprocessFileLoading(String description,
            String title, String id) {
	    app.refreshCurrentFileDescriptors(title, description);
		app.setCurrentFileId(id);
    }
	
	private void processGoogleDriveFileContentAsBinary(JavaScriptObject binary, String description, String title, String id) {
		app.loadGgbFileAsBinaryAgain(binary);
		postprocessFileLoading(description, title, id);
	}

	
	/**
	 * logs out from Google Drive (this means, removes the possibilities to interact with Google Drive)
	 */
	public void logOut() {
		this.onEvent(new GoogleLogOutEvent());
		this.getModel().setLoggedInFromGoogleDrive(false);
	}
	
	/**
	 * @param fileName name of the File	
	 * @param description Description of the file
	 * @return javascript function to called back;
	 */
	public native JavaScriptObject getPutFileCallback(String fileName, String description) /*-{
	    var _this = this;
	    return function(base64) {
	    	var fName = fileName,
	    		ds = description;
	    	_this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::saveFileToGoogleDrive(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(fName,ds,base64);
	    };
    }-*/;
	
	private void saveFileToGoogleDrive(final String fileName,
            final String description, final String fileContent) {
		JavaScriptObject metaData = JavaScriptObject.createObject();
		JSON.put(metaData,	"title", fileName);
		JSON.put(metaData, "description", description);
		if (!fileName.equals(app.getFileName())) {
			app.setCurrentFileId(null);
		}
		if ((getFolderId() != null) && !"".equals(getFolderId())) {
			JavaScriptObject folderId = JavaScriptObject.createObject();
			JSON.put(folderId, "id", getFolderId());
			JsArray<JavaScriptObject> parents = (JsArray<JavaScriptObject>) JavaScriptObject.createArray();
			parents.push(folderId);
			JSON.put(metaData, "parents", parents);
		}
		AppW.debug(metaData);
		handleFileUploadToGoogleDrive(app.getCurrentFileId(), metaData, fileContent);		
    }
	
	private native void handleFileUploadToGoogleDrive(String id, JavaScriptObject metaData, String base64) /*-{
	var _this = this,
		fId = id ? id : "";
	function updateFile(fileId, fileMetadata, fileData) {
	  var boundary = '-------314159265358979323846';
	  var delimiter = "\r\n--" + boundary + "\r\n";
	  var close_delim = "\r\n--" + boundary + "--";
	  var contentType = @geogebra.common.GeoGebraConstants::GGW_MIME_TYPE;
	  var base64Data = fileData;
	  var multipartRequestBody =
	        delimiter +
	        'Content-Type: application/json\r\n\r\n' +
	        JSON.stringify(fileMetadata) +
	        delimiter +
	        'Content-Type: ' + contentType + '\r\n' +
	        'Content-Transfer-Encoding: base64\r\n' +
	        '\r\n' +
	        base64Data +
	        close_delim;
	   var method = (fileId ? 'PUT' : 'POST');
	   var request = $wnd.gapi.client.request({
	        'path': '/upload/drive/v2/files/' + fileId,
	        'method': method,
	        'params': {'uploadType': 'multipart', 'alt': 'json'},
	        'headers': {
	          'Content-Type': 'multipart/mixed; boundary="' + boundary + '"'
	        },
	        'body': multipartRequestBody});
	    
	   request.execute(function(resp) {
	   		if (!resp.error) {
	   			_this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::updateAfterGoogleDriveSave(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(resp.id, resp.title, resp.description)
	   		} else {
	   			_this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::showUploadError()();
	   		}
	   });
	  }
	  updateFile(fId, metaData, base64);
	}-*/;
	
	private void showUploadError() {
		((DialogManagerW) app.getDialogManager()).getFileChooser().hide();
		((DialogManagerW) app.getDialogManager()).getAlertDialog().get(app.getLocalization().getMenu("UserNotAuthenticatedToWriteThisFile"));
	}
	private void updateAfterGoogleDriveSave(String id, String fileName, String description) {
		((DialogManagerW) app.getDialogManager()).getFileChooser().hide();
		((DialogManagerW) app.getDialogManager()).getFileChooser().saveSuccess(fileName, description);
		app.setCurrentFileId(id);
	}

	private void checkIfOpenedFromGoogleDrive() {
		String state = URL.getQueryParameterAsString("state");
		App.debug(state);
		if (!"".equals(state)) {
			googleDriveURL = JSON.parse(state);
			AppW.debug(googleDriveURL);
			if (!this.loggedIn) {
				login(true);
			}
			
		}
	}
	
	private String getFolderId() {
		String folderId = null;
		if (googleDriveURL != null) {
			folderId = JSON.get(googleDriveURL, "folderId");
		}
		return folderId;
	}
	
	private String getAction() {
		String action = null;
		if (googleDriveURL != null) {
			action = JSON.get(googleDriveURL, "action");
		}
		return action;
	}
	
	

	

}
