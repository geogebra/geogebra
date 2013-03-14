package geogebra.web.helper;

import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.html5.DynamicScriptElement;
import geogebra.web.main.AppW;
import geogebra.web.presenter.LoadFilePresenter;
import geogebra.web.util.JSON;

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
	
	public boolean loggedIn = false;
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
	
	public void saveFileToGoogleDrive(final String fileName,
            final String description, final String fileContent) {
		JavaScriptObject metaData = JavaScriptObject.createObject();
		JSON.put(metaData,	"title", fileName);
		JSON.put(metaData, "description", description);
		
		handleFileUploadToGoogleDrive(((AppW) app).currentFileId, metaData, fileContent);
		
    }
	
	private native void handleFileUploadToGoogleDrive(String id, JavaScriptObject metaData, String base64) /*-{
		var _this = this;
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
		   		_this.@geogebra.web.helper.MyGoogleApis::updateAfterGoogleDriveSave(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(resp.id, resp.title, resp.description, base64)
		   });
		  }
		  updateFile(id, metaData, base64);
}-*/;
	
	private void updateAfterGoogleDriveSave(String id, String fileName, String description, String content) {
		((DialogManagerW) app.getDialogManager()).getFileChooser().hide();
		((DialogManagerW) app.getDialogManager()).getFileChooser().saveSuccess(fileName, description, content);
		((AppW)app).currentFileId = id;
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
					loadFilePresenter.getApplication().refreshCurrentFileDescriptors(title, description, content);
				}
				
				public void onError(Request request, Throwable exception) {
					App.error(exception.getLocalizedMessage());
				}
			});
        } catch (Exception e) {
	        // TODO: handle exception
        }
	    
    }

	/**
	 * @param fileName name of the File	
	 * @param description Description of the file
	 * @param fileChooser GeoGebraFileChooser
	 * @return javascript function to called back;
	 */
	public native JavaScriptObject getPutFileCallback(String fileName, String description) /*-{
	    var _this = this;
	    return function(base64) {
	    	var fName = fileName;
	    	var ds = description;
	    	_this.@geogebra.web.helper.MyGoogleApis::saveFileToGoogleDrive(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(fName,ds,base64);
	    };
    }-*/;

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
	     $wnd.console.log("driveloaded");
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
		} else if (callBack.equalsIgnoreCase("save")) {
			
		}
	}
	
	private void refreshLoggedInGui(boolean loggedIn) {
			((AppW) app).getObjectPool().getGgwMenubar().getMenubar().getFileMenu().getOpenMenu().refreshIfLoggedIntoGoogle(loggedIn);
    		((AppW) app).getObjectPool().getGgwMenubar().getMenubar().refreshIfLoggedIntoGoogle(loggedIn);
    		((DialogManagerW) app.getDialogManager()).getFileChooser().refreshIfLoggedIntoGoogle(loggedIn);
	}
	private native void setUserEmailAfterLogin() /*-{
		var _this = this;
		$wnd.gapi.client.load('oauth2', 'v2', function() {
          			var request = $wnd.gapi.client.oauth2.userinfo.get();
					request.execute(
						function(resp) {
							if (resp.email) {
								_this.@geogebra.web.helper.MyGoogleApis::loggedIntoGoogleSuccessFull(Ljava/lang/String;Ljava/lang/String;)(resp.name, resp.email);
							}
						}
					)
		});	
	}-*/;

	public native void clearAllTokens() /*-{
	    this.@geogebra.web.helper.MyGoogleApis::firstLogin = false;
	    this.@geogebra.web.helper.MyGoogleApis::loggedIn = false;
    }-*/;
	
	private void processGoogleDriveFileContent(String base64, String description, String title, String id) {
		GeoGebraAppFrame.fileLoader.process(base64);
		GeoGebraAppFrame.fileLoader.getApplication().refreshCurrentFileDescriptors(title, description, base64);
		((AppW)app).currentFileId = id;
	}

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
			_this.@geogebra.web.helper.MyGoogleApis::processGoogleDriveFileContent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(base64, description, title, id);
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
					((AppW) app).getObjectPool().getGgwMenubar().getMenubar().getLogOutFromGoogle().getScheduledCommand().execute();
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
	

}
