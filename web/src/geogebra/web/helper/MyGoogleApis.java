package geogebra.web.helper;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.web.Web;
import geogebra.web.gui.util.GeoGebraFileChooser;
import geogebra.web.main.AppW;
import geogebra.web.presenter.LoadFilePresenter;
import geogebra.web.util.JSON;

import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MyGoogleApis {
	
	private static boolean firstLogin = true;
	
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

	public static void putNewFileToGoogleDrive(final String fileName,
            final String description, final String fileContent, final GeoGebraFileChooser geogebrafilechooser) {
	   //pack the things.
		JavaScriptObject file = JavaScriptObject.createObject();
		JSON.put(file, "content",fileContent);
		JSON.put(file, "title", fileName);
		JSON.put(file, "description", description);
		JSON.put(file, "mimeType", GeoGebraConstants.GGW_MIME_TYPE);
		JSON.put(file, "resource_id", AppW.currentFileId);
		
		String url = "/svc";
		Method method = AppW.currentFileId.equals("") ? RequestBuilder.POST : RequestBuilder.PUT;
		
		RequestBuilder builder = new RequestBuilder(method, URL.encode(url));
		builder.setHeader("Content-Type", "application/json");
		try {
	        Request request = builder.sendRequest(JSON.stringify(file), new RequestCallback() {
				
				public void onResponseReceived(Request request, Response response) {
					geogebrafilechooser.hide();
					AppW.currentFileId = response.getText().replace("\"", "");
					geogebrafilechooser.saveSuccess(fileName, description, fileContent);
				}
				
				public void onError(Request request, Throwable exception) {
					App.error(exception.getLocalizedMessage());
				}
			});
        } catch (Exception e) {
        	  App.error(e.getLocalizedMessage());
        }
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
	public static native JavaScriptObject getPutFileCallback(String fileName, String description, GeoGebraFileChooser fileChooser) /*-{
	    return function(base64) {
	    	var fName = fileName;
	    	var ds = description;
	    	var chooser = fileChooser;
	    	@geogebra.web.helper.MyGoogleApis::putNewFileToGoogleDrive(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lgeogebra/web/gui/util/GeoGebraFileChooser;)(fName,ds,base64,chooser);
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
		$wnd.console.log("api loaded");
	    $wnd.gapi.client.load('drive', 'v2', function() {
	     $wnd.console.log("drive loaded");
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
							}
						}
					)
		});	
	}-*/;

	public static native void clearAllTokens() /*-{
	    @geogebra.web.helper.MyGoogleApis::firstLogin = false;
	    @geogebra.web.helper.MyGoogleApis::loggedIn = false;
    }-*/;

}
