package geogebra.web.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.gui.menubar.OptionsMenu;
import geogebra.common.main.App;
import geogebra.web.Web;
import geogebra.web.gui.images.AppResources;
import geogebra.web.helper.GoogleApiCallback;
import geogebra.web.helper.MyGoogleApis;
import geogebra.web.util.JSON;

import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
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


public class GeoGebraMenubarW extends MenuBar {
	
	
		private App app;
		private FileMenuW fileMenu;
		private EditMenuW editMenu;
		private HelpMenuW helpMenu;
		private OptionsMenuW optionsMenu;

		/**
		 * public static to add relative position for the descriptions.
		 */
		public static MenuItem loginToGoogle;
		private MenuItem linktoggb;

		public GeoGebraMenubarW(App app) {
	        super();
	        this.app = app;
	        init();
	        addStyleName("GeoGebraMenuBar");
	        
        }

		private void init() {

			createFileMenu(!getNativeEmailSet().equals(""));
			
			createEditMenu();
			
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
					App.error("Request failed" + " " + reason.getMessage());
                }

				public void onSuccess(String token) {
					MyGoogleApis.executeApi(GeoGebraConstants.API_USERINFO + token,new GoogleApiCallback() {
						
						public void success(String responseText) {
							JavaScriptObject answer = JSON.parse(responseText);
							loginToGoogle.setHTML(getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString(), JSON.get(answer,"email")));
							loginToGoogle.setCommand(createCommandForSignedIn());
						}
						
						public void failure(String failureText) {
							App.error(failureText);
							
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
	                       App.error("Request failed" + " " + reason.getMessage());
                        }

						public void onSuccess(String token) {
	                       MyGoogleApis.executeApi(GeoGebraConstants.API_USERINFO + token,new GoogleApiCallback() {
							
							public void success(String responseText) {
								JavaScriptObject answer = JSON.parse(responseText);
								loginToGoogle.setHTML(getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString(),JSON.get(answer,"email")));
								loginToGoogle.setCommand(createCommandForSignedIn());
								Web.oaAsync.triggerLoginToGoogle(new AsyncCallback<Boolean>() {
									
									public void onSuccess(Boolean result) {
										App.debug(result);
										
									}
									
									public void onFailure(Throwable caught) {
										App.error(caught.getLocalizedMessage());
										
									}
								});
							}
							
							public void failure(String failureText) {
								App.error(failureText);
								
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
	        fileMenu = new FileMenuW(app, enabled);
			addItem(app.getMenu("File"),fileMenu);
        }

		private void createEditMenu() {
			editMenu = new EditMenuW(app);
			addItem(app.getMenu("Edit"), editMenu);
		}
		
		private void createHelpMenu() {
	        helpMenu = new HelpMenuW(app);
			addItem(app.getMenu("Help"),helpMenu);
        }
		
		private void createOptionsMenu() {
			optionsMenu = new OptionsMenuW(app);
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
		
		/**
		 * Update the "Edit" menu
		 */

		public void updateSelection() {
			editMenu.initActions();	        
        }

		public void updateMenubar() {
			App.debug("implementation needed - just finishing");
			OptionsMenu.update();        
        }

		public static void setMenuSelected(MenuItem m,
                boolean visible) {
	        if (visible) {
	        	m.addStyleName("checked");
	        } else {
	        	m.removeStyleName("checked");
	        }
        }
		
}
