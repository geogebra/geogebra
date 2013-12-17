package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginAttemptEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.operations.LogInOperation;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.EventRenderable;
import geogebra.web.gui.images.AppResources;
import geogebra.web.html5.Dom;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
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

		if (!app.isApplet()) createWindowMenu();

		// Creation of Help Menu
		createHelpMenu();
		
		if (!app.menubarRestricted()) {
			createSignIn();
		}
		
		createLinkToGGBT();
		
	}

	
	
	private void createSignIn() {
	   signIn = addItem(app.getMenu("SignIn"), getSignInCommand());
	   signIn.addStyleName("signIn");
	   
	   app.getNetworkOperation().getView().add(new BooleanRenderable() {
			
			public void render(boolean b) {
				renderNetworkOperation(b);
			}
		});
	    
	    // this methods should be called only from AppWapplication or AppWapplet
	   LogInOperation loginOp = app.getLoginOperation();
	   loginOp.getView().add(this);
	   if (loginOp.isLoggedIn()) {
		   onLogin(true, loginOp.getModel().getLoggedInUser());
	   }
	   
	   if (!app.getNetworkOperation().getOnline()) {
		   renderNetworkOperation(false);
	   }
    }
	
	private void onLogin(boolean successful, GeoGebraTubeUser user) {
		Localization loc = app.getLocalization();

		if (successful) {
			signIn.setScheduledCommand(getSignOutCommand());
			signIn.setText(loc.getPlain("SignedInAsA", user.getUserName()));
		} else {
			signIn.setScheduledCommand(getSignInCommand());
			signIn.setText(loc.getMenu("SignInError"));
		}
	    
    }

	private ScheduledCommand getSignOutCommand() {
	    return new ScheduledCommand() {
			
	    	public void execute() {
				app.getLoginOperation().performLogOut();
			}
		};
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
			viewMenu = (app.isApplet())? new ViewMenuW(app) : new ViewMenuApplicationW(app);
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
		/*
		public MenuItem addItem(String name, MenuBar submenu){
			String fontsizeString = app.getGUIFontSize() + "px";
			submenu.getElement().getStyle().setProperty("font-size", fontsizeString);
			return super.addItem(name, submenu);
		}*/
				
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
//		public static String getMenuBarHtmlGrayout(String url,String text) {		
//			//TODO: Resize images for this real size, if it is good.
//			text = text.replace("\"", "'");
//			return "<img width=\"16\" height=\"16\" alt=\""+text+"\" src=\""+url+"\" />"+" "+
//					"<span style=\"color:gray;\">"+text+"</span>";
//		}
		
		/**Gives back an html source of a menuitem.
		 * @param url an icon's url
		 * @param text menuitem's text
		 * @param enabled true if the menuitem is enabled
		 * @return html source of a menuitem
		 */
		public static String getMenuBarHtml(String url,String text, Boolean enabled) {		
			//TODO: Resize images for this real size, if it is good.
			
			String text2 = text.replace("\"", "'");
			String text3 = (enabled) ? text2 :  "<span style=\"color:gray;\">"+text2+"</span>";
			return	"<img class=\"GeoGebraMenuImage\" alt=\""+text2+"\" src=\""+url+"\" />"+" "+ text3;
					
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
			if (!app.isApplet()){
				((ViewMenuApplicationW)viewMenu).update();
			}
			
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
			if (event instanceof LoginAttemptEvent) {
				signIn.setText(app.getMenu("SignInProgress"));
			} else if (event instanceof LogOutEvent) {
				signIn.setText(app.getMenu("SignIn"));
				signIn.setScheduledCommand(getSignInCommand());
				
			} else if (event instanceof LoginEvent) {
				LoginEvent loginEvent = (LoginEvent) event;
				onLogin(loginEvent.isSuccessful(), loginEvent.getUser());
			}
        }

		
		public void updateFonts() {
			String fontsizeString = app.getGUIFontSize() + "px";
			NodeList<Element> fontsizeElements = Dom.getElementsByClassName("GGWFontsize");
			for(int i=0; i<fontsizeElements.getLength(); i++){
				fontsizeElements.getItem(i).removeFromParent();
			}
			Element fontsizeElement = DOM.createElement("style");
			fontsizeElement.addClassName("GGWFontsize");
			int imagesize = Math.round(app.getGUIFontSize() * 4/3);
			String innerText = ".GeoGebraMenuBar{font-size: "+fontsizeString+" !important}"+
			".GeoGebraMenuImage{height: "+imagesize+"px; width: "+imagesize+"px;}";
			fontsizeElement.setInnerText(innerText);
			
			NodeList<Element> geogebrawebElements = Dom.getElementsByClassName("geogebraweb");
			for(int i=0; i<geogebrawebElements.getLength(); i++){
				geogebrawebElements.getItem(i).appendChild(fontsizeElement);
			}
        }
		
}
