package geogebra.web.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.Application;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;
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
			
			MenuItem loginToGoogle = addItem("Login to Google",new Command() {
				
				public void execute() {
					final AuthRequest req = new AuthRequest(GeoGebraConstants.GOOGLE_AUTH_URL, GeoGebraConstants.GOOGLE_CLIENT_ID)
		            .withScopes(GeoGebraConstants.PLUS_ME_SCOPE);

		        // Calling login() will display a popup to the user the first time it is
		        // called. Once the user has granted access to the application,
		        // subsequent calls to login() will not display the popup, and will
		        // immediately result in the callback being given the token to use.
		        AUTH.login(req, new Callback<String, Throwable>() {
		          public void onSuccess(String token) {
		            Window.alert("Got an OAuth token:\n" + token + "\n"
		                + "Token expires in " + AUTH.expiresIn(req) + " ms\n");
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
