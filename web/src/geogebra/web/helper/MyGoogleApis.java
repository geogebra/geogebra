package geogebra.web.helper;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.web.Web;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.util.AlertDialog;
import geogebra.web.gui.util.GeoGebraFileChooser;
import geogebra.web.main.AppW;
import geogebra.web.presenter.LoadFilePresenter;
import geogebra.web.util.JSON;

import java.util.Date;

import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MyGoogleApis {
	
	private static boolean firstLogin = true;
	private static long tokenExpiresAt;
	
	public static boolean loggedIn = false;
	public static boolean driveLoaded = false;
	
	public static AuthRequest createNewAuthRequest() {
		return new AuthRequest(GeoGebraConstants.GOOGLE_AUTH_URL, GeoGebraConstants.GOOGLE_CLIENT_ID)
		.withScopes(GeoGebraConstants.USERINFO_EMAIL_SCOPE,GeoGebraConstants.USERINFO_PROFILE_SCOPE,GeoGebraConstants.DRIVE_SCOPE);
	}

	public static void executeApi(String urlWithToken,
            final GoogleApiCallback googleApiCallback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(urlWithToken));
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					App.error(exception.getLocalizedMessage());
				}
				
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						googleApiCallback.success(response.getText());
					} else {
						googleApiCallback.failure(response.getStatusText());
					}
				}
			});
        } catch (Exception e) {
	       App.error(e.getLocalizedMessage());
        }
    }

	/**
	 * Looks that if we have a google drive url
	 */
	public static void handleURL() {
	    if (Window.Location.getParameter("state") != null) {
	    	JavaScriptObject state = JSON.parse(Window.Location.getParameter("state"));
	    	String action = JSON.get(state, "action");
	    	String parentId = JSON.get(state, "parentId");
	    	String code = Window.Location.getParameter("code");
	    	if (action != null && parentId != null) {
	    		Web.gdAsync.fileCreated(action, parentId, code,  new AsyncCallback<Boolean>() {
					
					public void onSuccess(Boolean result) {
						
					}
					
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
	    	}
	    }
    }

	public static void saveFileToGoogleDrive(final String fileName,
            final String description, final String fileContent) {
		JavaScriptObject metaData = JavaScriptObject.createObject();
		JSON.put(metaData,	"title", fileName);
		JSON.put(metaData, "description", description);
		
		handleFileUploadToGoogleDrive(AppW.currentFileId, metaData, fileContent);
		
    }
	
	private static native void handleFileUploadToGoogleDrive(String id, JavaScriptObject metaData, String base64) /*-{
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
		   		@geogebra.web.helper.MyGoogleApis::updateAfterGoogleDriveSave(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(resp.id, resp.title, resp.description, base64)
		   });
		  }
		  updateFile(id, metaData, base64);
}-*/;
	
	private static void updateAfterGoogleDriveSave(String id, String fileName, String description, String content) {
		GeoGebraFileChooser.INSTANCE.hide();
		AppW.currentFileId = id;
		GeoGebraFileChooser.INSTANCE.saveSuccess(fileName, description, content);
	}

	private static native String getFileIdOrNull() /*-{
	    if ($wnd.GGW_appengine && $wnd.GGW_appengine.FILE_IDS[0] !== "") {
	    	return $wnd.GGW_appengine.FILE_IDS[0];
	    }
	    return null;
    }-*/;

	public static void getFileFromGoogleDrive(
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
	public static native JavaScriptObject getPutFileCallback(String fileName, String description) /*-{
	    return function(base64) {
	    	var fName = fileName;
	    	var ds = description;
	    	@geogebra.web.helper.MyGoogleApis::saveFileToGoogleDrive(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(fName,ds,base64);
	    };
    }-*/;

	public static boolean signedInToGoogle() {
	    //AuthRequest r = createNewAuthRequest();
	    //if (Web.AUTH.expiresIn(r) > 0) {
	    //	return true;
	    //}
	    return MyGoogleApis.loggedIn;
	}

	public native static void loadGoogleDrive() /*-{
	    $wnd.gapi.client.load('drive', 'v2', function() {
	     @geogebra.web.helper.MyGoogleApis::driveLoaded = true;
        });
    }-*/;
	
	public native static void loginToGoogle() /*-{
		var config = {'client_id': 	@geogebra.common.GeoGebraConstants::GOOGLE_CLIENT_ID,
	            	'scope': 	@geogebra.common.GeoGebraConstants::DRIVE_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::USERINFO_EMAIL_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::USERINFO_PROFILE_SCOPE + " " +
	            				@geogebra.common.GeoGebraConstants::PLUS_ME_SCOPE,
	            	 'immediate': false};
	    if (!@geogebra.web.helper.MyGoogleApis::firstLogin) {
	    	config.max_auth_age = 0;
	    }
		$wnd.gapi.auth.authorize(config,
	            	 function (resp) {
	            	 	if (!resp.error) {
	            	 		@geogebra.web.helper.MyGoogleApis::setUserEmailAfterLogin()();
	            	 		@geogebra.web.helper.MyGoogleApis::setExpiresAt(Ljava/lang/String;)(resp.expires_in);
	            	 	}
	            	 }
	       );
	}-*/;
	
	private static native void setUserEmailAfterLogin() /*-{
		$wnd.gapi.client.load('oauth2', 'v2', function() {
          			var request = $wnd.gapi.client.oauth2.userinfo.get();
					request.execute(
						function(resp) {
							if (resp.email) {
								@geogebra.web.gui.menubar.GeoGebraMenubarW::setLoggedIntoGoogle(Ljava/lang/String;Ljava/lang/String;)(resp.name, resp.email);
								@geogebra.web.helper.MyGoogleApis::loggedIn = true;
								@geogebra.web.helper.MyGoogleApis::initGoogleTokenChecking()();
							}
						}
					)
		});	
	}-*/;

	public static native void clearAllTokens() /*-{
	    @geogebra.web.helper.MyGoogleApis::firstLogin = false;
	    @geogebra.web.helper.MyGoogleApis::loggedIn = false;
    }-*/;
	
	private static void processGoogleDriveFileContent(String base64, String description, String title, String id) {
		GeoGebraAppFrame.fileLoader.process(base64);
		GeoGebraAppFrame.fileLoader.getApplication().refreshCurrentFileDescriptors(title, description, base64);
		AppW.currentFileId = id;
	}

	/**
	 * @param currentFileName fileName
	 * @param description File Description
	 * @param title File Title
	 */
	public static native void loadFromGoogleFile(String currentFileName, String description, String title, String id) /*-{
		
		
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
			@geogebra.web.helper.MyGoogleApis::processGoogleDriveFileContent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(base64, description, title, id);
		});
    }-*/;
	
	static AnimationHandle checker;
	
	private static void initGoogleTokenChecking() {
		checker = AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			
			public void execute(double timestamp) {
				long current = new Date().getTime();
				if (current > tokenExpiresAt) {
					AlertDialog.get("your token expired - please log in again").show();
					GeoGebraMenubarW.loginToGoogle.getScheduledCommand().execute();
					checker.cancel();
					return;
				}
				AnimationScheduler.get().requestAnimationFrame(this);
			}
		});
		
	}
	
	private static void setExpiresAt(String expires_in) {
		long current = new Date().getTime();
		tokenExpiresAt = current + (Integer.parseInt(expires_in) * 1000);
	}
	

}
