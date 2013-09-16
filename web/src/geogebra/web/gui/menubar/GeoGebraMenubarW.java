package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.EventRenderable;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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


public class GeoGebraMenubarW extends MenuBar implements EventRenderable {
	
	
		/**
		 * Appw app
		 */
		AppW app;
		private FileMenuW fileMenu;
		private EditMenuW editMenu;
		private HelpMenuW helpMenu;
		private OptionsMenuW optionsMenu;
		private MenuItem signIn;
		private MenuItem linktoggb;
		private ViewMenuW viewMenu;
		private SignedInMenuW signedIn;
		private MenuItem signedInMenu;

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

		if (!app.menubarRestricted()) {
			createFileMenu();
		}
		
		createEditMenu();

		createViewMenu();

		// Creation of Options Menu
		createOptionsMenu(); // Later we'll put back.

		createWindowMenu();

		// Creation of Help Menu
		createHelpMenu();
		
		if (!app.menubarRestricted()) {
			createSignIn();
		}
		
		createLinkToGGBT();
		
	}

	
	
	private void createSignIn() {
	   signIn = addItem(app.getMenu("signIn"), getSignInCommand());
	   signIn.addStyleName("signIn");
	   
	   app.getOfflineOperation().getView().add(new BooleanRenderable() {
			
			public void render(boolean b) {
				renderNetworkOperation(b);
			}
		});
	    
	    // this methods should be called only from AppWapplication or AppWapplet
	   app.getLoginOperation().getView().add(this);
	   
	   if (!app.getOfflineOperation().getOnline()) {
		   renderNetworkOperation(false);
	   }
    }
	
	private void createSignedInMenu() {
		  signedIn = new SignedInMenuW(app);
	      signedInMenu = addItem(app.getMenu("SignedIn"), signedIn);
	      signedInMenu.addStyleName("signIn");
	}

	/**
	 * @param online network state of the app
	 * renders the online - offline network state
	 */
	void renderNetworkOperation(boolean online) {
	    signIn.setEnabled(online);
	    if (!online) {
	    	signIn.setTitle(app.getMenu("YouAreOffline"));
	    } else {
	    	signIn.setTitle("");
	    }
    }

	private ScheduledCommand getSignInCommand() {
	 
	    return new ScheduledCommand() {
			
			public void execute() {
				app.getGuiManager().login();
			}
		};
    }

	private native String getNativeEmailSet() /*-{
		if($wnd.GGW_appengine){
			return $wnd.GGW_appengine.USER_EMAIL;
		}
		else return "";
	}-*/;

		

		

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
			viewMenu = new ViewMenuW(app);
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
			text = text.replace("\"", "'");
			return "<img width=\"16\" height=\"16\" alt=\""+text+"\" src=\""+url+"\" />"+" "+text;
		}

		/**Gives back an html source of a disabled menuitem.
		 * @param url an icon's url
		 * @param text menuitem's text
		 * @return html source of a menuitem
		 */
		public static String getMenuBarHtmlGrayout(String url,String text) {		
			//TODO: Resize images for this real size, if it is good.
			text = text.replace("\"", "'");
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
			viewMenu.update();
			
        }

		public static void setMenuSelected(MenuItem m,
                boolean visible) {
	        if (visible) {
	        	m.addStyleName("checked");
	        } else {
	        	m.removeStyleName("checked");
	        }
        }
		
		public FileMenuW getFileMenu() {
			return fileMenu;
		}
		
		public MenuItem getSignIn() {
			return signIn;
		}

		public void success(JSONObject response) {
				renderSignedInState();     
        }

		public void fail(JSONObject resonse) {
	       renderSignInState();
        }

		private void renderSignInState() {
			if (signedInMenu != null) {
				signedInMenu.setVisible(false);
			}
			signIn.setVisible(true);
        }
		
		private void renderSignedInState() {
			if (signedInMenu == null) {
				createSignedInMenu();
			} else {
				signedIn.refreshstate();
			}
			signedInMenu.setVisible(true);
			signIn.setVisible(false);
		}
		

		public void render() {
			renderSignInState();
        }

		public void renderEvent(BaseEvent event) {
	        // TODO Auto-generated method stub
	        
        }
		
}
