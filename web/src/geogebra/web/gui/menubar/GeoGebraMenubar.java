package geogebra.web.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.Application;
import geogebra.web.util.JSON;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor
 * 
 * Creates a menubar for GeoGebraWeb
 *
 */


public class GeoGebraMenubar extends MenuBar {
	
		private static final Auth AUTH = Auth.get();
	
		private AbstractApplication app;
		private FileMenu fileMenu;

		private MenuItem loginToGoogle;

		public GeoGebraMenubar(AbstractApplication app) {
	        super();
	        this.app = app;
	        init();
	        addStyleName("GeoGebraMenuBar");
	        
        }

		private void init() {

			//file
			fileMenu = new FileMenu(app);
			addItem(app.getMenu("File"),fileMenu);
			MenuItem linktoggb = addItem(getMenuBarHtml(AppResources.INSTANCE.GeoGebraTube().getSafeUri().asString(),""),true, new Command() {
				
				public void execute() {
					Window.open("http://geogebratube.org", "", "");
				}
			});
			linktoggb.setStyleName("linktoggbtube");
			linktoggb.setTitle("Go to GeoGebraTube");
			
			loginToGoogle = addItem("Login to Google",new Command() {
				
				public void execute() {
					final AuthRequest req = new AuthRequest(GeoGebraConstants.GOOGLE_AUTH_URL, GeoGebraConstants.GOOGLE_CLIENT_ID)
		            .withScopes(GeoGebraConstants.USERINFO_EMAIL_SCOPE,GeoGebraConstants.USERINFO_PROFILE_SCOPE,GeoGebraConstants.DRIVE_SCOPE);
		        // Calling login() will display a popup to the user the first time it is
		        // called. Once the user has granted access to the application,
		        // subsequent calls to login() will not display the popup, and will
		        // immediately result in the callback being given the token to use.
			        AUTH.login(req, new Callback<String, Throwable>() {
			          public void onSuccess(String token) {
			        	  
			        	  String url = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" 
			        	            + token;
			        	  RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
			        	  try {
			        		  Request request = builder.sendRequest(null, new RequestCallback() {
			        		    public void onError(Request request, Throwable exception) {
			        		       // Couldn't connect to server (could be timeout, SOP violation, etc.)     
			        		    }
	
			        		    public void onResponseReceived(Request request, Response response) {
			        		      if (200 == response.getStatusCode()) {
			        		    	  JavaScriptObject answer = JSON.parse(response.getText());
			        		          loginToGoogle.setHTML(JSON.get(answer,"email"));
			        		      } else {
			        		        // Handle the error.  Can get the status text from response.getStatusText()
			        		      }
			        		    }       
			        		  });
			        		} catch (RequestException e) {
			        		  // Couldn't connect to server        
			        		}
			          }
	
			          public void onFailure(Throwable caught) {
			            Window.alert("Error:\n" + caught.getMessage());
			          }
			        });
				}
			});
        }
		
		public static String getMenuBarHtml(String url,String text) {
			return "<img width=\"16\" height=\"16\" alt=\""+text+"\" src=\""+url+"\" />"+" "+text;
		}
	
}
