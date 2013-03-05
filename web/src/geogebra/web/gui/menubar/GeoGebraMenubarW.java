package geogebra.web.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

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


public class GeoGebraMenubarW extends MenuBar {
	
	
		private static AppW app;
		private static FileMenuW fileMenu;
		private EditMenuW editMenu;
		private HelpMenuW helpMenu;
		private OptionsMenuW optionsMenu;

		/**
		 * public static to add relative position for the descriptions.
		 */
		public static MenuItem loginToGoogle;
		private MenuItem linktoggb;

		/**
		 * Constructs the menubar
		 * @param app application
		 */
		public GeoGebraMenubarW(AppW app) {
	        super();
	        this.app = app;
	        init();
	        addStyleName("GeoGebraMenuBar");
	        
        }

	private void init() {

		createFileMenu();

		createEditMenu();

		createViewMenu();

		// Creation of Options Menu
		createOptionsMenu(); // Later we'll put back.

		createWindowMenu();

		// Creation of Help Menu
		createHelpMenu();

		createLinkToGGBT();

		createLoginToGoogle();
	}

		private void createLoginToGoogle() {
			
			Command c = null;
			String menuHtml = "";
			/*reserve for later, when we will have client side Oauth*/
			if (((AppW) app).getMyGoogleApis().signedInToGoogle()) {
				c = createCommandForSignedIn();
			} else {
				c = createCommandForNotSignedIn();
				menuHtml = createMenuHtmlForNotSignedIn();
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

		static String createMenuHtmlForNotSignedIn() {
	        return getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString()
	        		, app.getMenu("Login"));
        }
		
		private native String getNativeEmailSet() /*-{
			if($wnd.GGW_appengine){
				return $wnd.GGW_appengine.USER_EMAIL;
			}
			else return "";
		}-*/;

		

		static Command createCommandForNotSignedIn() {
	        return new Command() {
				
				public void execute() {
					((AppW) app).getMyGoogleApis().loginToGoogle();
				}
			};
        }

		static Command createCommandForSignedIn() {
	        return new Command() {
				
				public void execute() {
					//Web.AUTH.clearAllTokens();
					((AppW) app).getMyGoogleApis().clearAllTokens();
					loginToGoogle.setHTML(createMenuHtmlForNotSignedIn());
					loginToGoogle.setScheduledCommand(createCommandForNotSignedIn());
					fileMenu.refreshIfLoggedIntoGoogle(false);
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

		private void createFileMenu() {
	        fileMenu = new FileMenuW(app);
			addItem(app.getMenu("File"),fileMenu);
        }

		private void createEditMenu() {
			editMenu = new EditMenuW(app);
			addItem(app.getMenu("Edit"), editMenu);
		}
		
		private void createViewMenu() {
			ViewMenuW viewMenu = new ViewMenuW(app);
			addItem(app.getMenu("View"), viewMenu);
		}
		
		private void createHelpMenu() {
	        helpMenu = new HelpMenuW(app);
			addItem(app.getMenu("Help"),helpMenu);
        }
		
		private void createOptionsMenu() {
			optionsMenu = new OptionsMenuW(app);
			addItem(app.getMenu("Options"), optionsMenu);
		}
		
		private void createWindowMenu() {
			WindowMenuW windowMenu = new WindowMenuW(app);
			addItem(app.getMenu("Window"), windowMenu);
		}
				
		/**
		 * Gives back an html source of an enabled menuitem.
		 * @param url an icon's url
		 * @param text menuitem's text
		 * @return html source of a menuitem
		 */
		public static String getMenuBarHtml(String url,String text) {		
			//TODO: Resize images for this real size, if it is good.
			return "<img width=\"16\" height=\"16\" alt=\""+text+"\" src=\""+url+"\" />"+" "+text;
		}

		/**Gives back an html source of a disabled menuitem.
		 * @param url an icon's url
		 * @param text menuitem's text
		 * @return html source of a menuitem
		 */
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

		/**
		 * Updates the menubar.
		 */
		public void updateMenubar() {
			App.debug("implementation needed - just finishing");
			app.getOptionsMenu().update();        
        }

		public static void setMenuSelected(MenuItem m,
                boolean visible) {
	        if (visible) {
	        	m.addStyleName("checked");
	        } else {
	        	m.removeStyleName("checked");
	        }
        }
		
		public static void setLoggedIntoGoogle(String email, String name) {
			loginToGoogle.setHTML(getMenuBarHtml(AppResources.INSTANCE.drive_icon_16().getSafeUri().asString(), email));
			loginToGoogle.setScheduledCommand(createCommandForSignedIn());
			loginToGoogle.setTitle(name);
			fileMenu.refreshIfLoggedIntoGoogle(true);
		}
		
		public static void setLoggedOutFromGoogle() {
			loginToGoogle.setScheduledCommand(createCommandForNotSignedIn());
		}
		
}
