package geogebra.web.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.AbstractApplication;
import geogebra.web.Web;
import geogebra.web.gui.images.AppResources;
import geogebra.web.helper.GoogleApiCallback;
import geogebra.web.helper.MyGoogleApis;
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
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor
 * 
 * Creates a menubar for GeoGebraWeb
 *
 */


public class GeoGebraMenubar extends MenuBar {
	
	
		private AbstractApplication app;
		private FileMenu fileMenu;
		private HelpMenu helpMenu;
		private OptionsMenu optionsMenu;

		/**
		 * public static to add relative position for the descriptions.
		 */
		public static MenuItem loginToGoogle;
		private MenuItem linktoggb;

		public GeoGebraMenubar(AbstractApplication app) {
	        super();
	        this.app = app;
	        init();
	        addStyleName("GeoGebraMenuBar");
	        
        }

		private void init() {

			createFileMenu(!getNativeEmailSet().equals(""));
			
			//Creation of Options Menu
			 createOptionsMenu(); // Later we'll put back.
			//Creation of Help Menu
			createHelpMenu();
			
			createLinkToGGBT();
			
			createLoginToGoogle();
        }

		private void createLoginToGoogle() {
			
			Command c = null;
			String menuHtml = "";
			/*reserve for later, when we will have client side Oauth
			if (signedInToGoogle()) {
				c = createCommandForSignedIn();
				//will be handled by callback
				createMenuHtmlForSignedIn();
			} else {
				c = createCommandForNotSignedIn();
				menuHtml = createMenuHtmlForNotSignedIn();
			}*/
			String email = getNativeEmailSet();
			if (email.equals("")) {
				c = createLoginCommand();
				menuHtml = getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString(), app.getMenu("Login"));
			} else {
				menuHtml =  getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString(), email);
				c = createLogOutCommand();
			}
			
	        loginToGoogle = addItem(menuHtml,true,c);
	        loginToGoogle.addStyleName("logintogoogle");
        }

		private Command createLogOutCommand() {
	        // TODO Auto-generated method stub
	        return new Command() {
				
				public void execute() {
					Window.Location.replace(GeoGebraConstants.APPENGINE_REDIRECT_URL+"?user_act=logged_out");
				}
			};
        }

		private Command createLoginCommand() {
	        // TODO Auto-generated method stub
	        return new Command() {
				
				public void execute() {
					Window.Location.replace(GeoGebraConstants.APPENGINE_REDIRECT_URL);
				}
			};
        }

		String createMenuHtmlForNotSignedIn() {
	        return getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString()
	        		, "Login to Google");
        }
		
		private native String getNativeEmailSet() /*-{
			return $wnd.GGW_appengine.USER_EMAIL;
		}-*/;

		private void createMenuHtmlForSignedIn() {
	        AuthRequest r = MyGoogleApis.createNewAuthRequest();
	        Web.AUTH.login(r, new Callback<String, Throwable>() {

				public void onFailure(Throwable reason) {
					AbstractApplication.error("Request failed" + " " + reason.getMessage());
                }

				public void onSuccess(String token) {
					MyGoogleApis.executeApi(GeoGebraConstants.API_USERINFO + token,new GoogleApiCallback() {
						
						public void success(String responseText) {
							JavaScriptObject answer = JSON.parse(responseText);
							loginToGoogle.setHTML(getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString(), JSON.get(answer,"email")));
							loginToGoogle.setCommand(createCommandForSignedIn());
						}
						
						public void failure(String failureText) {
							AbstractApplication.error(failureText);
							
						}
                       });
                }
			});
	        
        }

		private static boolean signedInToGoogle() {
	        AuthRequest r = MyGoogleApis.createNewAuthRequest();
	        if (Web.AUTH.expiresIn(r) > 0) {
	        	return true;
	        }
	        return false;
        }

		Command createCommandForNotSignedIn() {
	        // TODO Auto-generated method stub
	        return new Command() {
				
				public void execute() {
					final AuthRequest req = MyGoogleApis.createNewAuthRequest();
					Web.AUTH.login(req, new Callback<String, Throwable>() {

						public void onFailure(Throwable reason) {
	                       AbstractApplication.error("Request failed" + " " + reason.getMessage());
                        }

						public void onSuccess(String token) {
	                       MyGoogleApis.executeApi(GeoGebraConstants.API_USERINFO + token,new GoogleApiCallback() {
							
							public void success(String responseText) {
								JavaScriptObject answer = JSON.parse(responseText);
								loginToGoogle.setHTML(getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString(),JSON.get(answer,"email")));
								loginToGoogle.setCommand(createCommandForSignedIn());
								Web.oaAsync.triggerLoginToGoogle(new AsyncCallback<Boolean>() {
									
									public void onSuccess(Boolean result) {
										AbstractApplication.debug(result);
										
									}
									
									public void onFailure(Throwable caught) {
										AbstractApplication.error(caught.getLocalizedMessage());
										
									}
								});
							}
							
							public void failure(String failureText) {
								AbstractApplication.error(failureText);
								
							}
	                       });
                        }
					});
				}
			};
        }

		Command createCommandForSignedIn() {
	        return new Command() {
				
				public void execute() {
					Web.AUTH.clearAllTokens();
					loginToGoogle.setHTML(createMenuHtmlForNotSignedIn());
					loginToGoogle.setCommand(createCommandForNotSignedIn());
				}
			};
        }

		private void createLinkToGGBT() {
	        linktoggb = addItem(getMenuBarHtml(AppResources.INSTANCE.GeoGebraTube().getSafeUri().asString(),""),true, new Command() {
				
				public void execute() {
					Window.open("http://geogebratube.org", "", "");
				}
			});
			linktoggb.setStyleName("linktoggbtube");
			linktoggb.setTitle("Go to GeoGebraTube");
        }

		private void createFileMenu(boolean enabled) {
	        fileMenu = new FileMenu(app, enabled);
			addItem(app.getMenu("File"),fileMenu);
        }
		
		private void createHelpMenu() {
	        helpMenu = new HelpMenu(app);
			addItem(app.getMenu("Help"),helpMenu);
        }
		
		private void createOptionsMenu() {
			optionsMenu = new OptionsMenu(app);
			addItem(app.getMenu("Options"), optionsMenu);
		}
		
		public static String getMenuBarHtml(String url,String text) {		
			//TODO: Resize images for this real size, if it is good.
			return "<img width=\"16\" height=\"16\" alt=\""+text+"\" src=\""+url+"\" />"+" "+text;
		}

		public static String getMenuBarHtmlGrayout(String url,String text) {		
			//TODO: Resize images for this real size, if it is good.
			return "<img width=\"16\" height=\"16\" alt=\""+text+"\" src=\""+url+"\" />"+" "+
					"<span style=\"color:gray;\">"+text+"</span>";
		}
	
}
