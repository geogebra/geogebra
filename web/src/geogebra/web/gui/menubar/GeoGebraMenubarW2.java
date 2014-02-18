package geogebra.web.gui.menubar;

import geogebra.common.main.Localization;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginAttemptEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.operations.LogInOperation;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.web.html5.Dom;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Temporary sidebar menu for testing.
 * 
 * 
 */

public class GeoGebraMenubarW2 extends GeoGebraMenubarW {

	/**
	 * Appw app
	 */
	private MenuItem signIn;
	private SignedInMenuW signedIn;
	private MenuItem signedInMenu;
	
	private GeoGebraMenubarW2 menubar;

	/**
	 * Constructs the menubar
	 * 
	 * @param app
	 *            application
	 */
	public GeoGebraMenubarW2(AppW app) {
		super(app);
		this.app = app;
		this.menubar = this;
		init();
		addStyleName("menubar2");

	}

	MenuItem drop = null;
	PopupPanel menuPopup;
	SideBarMenuW dropMenu;

	private void createPopupMenu() {
		if (menuPopup == null) {
			menuPopup = new PopupPanel(true);
			dropMenu = new SideBarMenuW(app);
			menuPopup.add(dropMenu);
			menuPopup.addAutoHidePartner(drop.getElement());
			menuPopup.getElement().getStyle().setPadding(0, Unit.PX);
			menuPopup.getElement().getStyle().setProperty("borderRight", "1px solid gray");
			menuPopup.getElement().getStyle().setProperty("borderBottom", "1px solid gray");
			menuPopup.getElement().getStyle().setProperty("borderTop", "none");
			menuPopup.getElement().getStyle().setProperty("borderLeft", "none");
		}
	}

	private void init() {

		this.clearItems();

		drop = addItem("\u2630", true, new Command() {
			

			@Override
            public void execute() {
				if (menuPopup == null) {
					createPopupMenu();
				}
				if (menuPopup.isShowing()) {
					menuPopup.hide();
				} else {
					menuPopup.showRelativeTo(menubar);
				}
			}
		});

		// DropDownMenuW dropDownMenu = new DropDownMenuW(app);
		// MenuItem drop = addItem("\u2630", dropDownMenu);
		//drop.getElement().getStyle().setFontSize(120, Unit.PCT);

		MenuItem ggb = addItem("GeoGebra", true, new Command() {
			@Override
            public void execute() {
				// do nothing
			}
		});
		ggb.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		ggb.getElement().getStyle().setColor("white");
		ggb.addStyleName("menubar2-ggbLogo");
		addItem(ggb);

		if (!app.menubarRestricted()) {
			createSignIn();
		}
	}

	private void createSignIn() {
		signIn = addItem(app.getMenu("SignIn"), getSignInCommand());
		signIn.addStyleName("signIn");

		app.getNetworkOperation().getView().add(new BooleanRenderable() {

			@Override
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

			@Override
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
	 * @param online
	 *            network state of the app renders the online - offline network
	 *            state
	 */
	@Override
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

			@Override
            public void execute() {
				app.getGuiManager().login();
			}
		};
	}

	private native String getNativeEmailSet() /*-{
		if ($wnd.GGW_appengine) {
			return $wnd.GGW_appengine.USER_EMAIL;
		} else
			return "";
	}-*/;

	private void createWindowMenu() {
		WindowMenuW windowMenu = new WindowMenuW(app);
		addItem(app.getMenu("Window"), windowMenu);
	}

	/*
	 * public MenuItem addItem(String name, MenuBar submenu){ String
	 * fontsizeString = app.getGUIFontSize() + "px";
	 * submenu.getElement().getStyle().setProperty("font-size", fontsizeString);
	 * return super.addItem(name, submenu); }
	 */

	/**
	 * Gives back an html source of an enabled menuitem.
	 * 
	 * @param url
	 *            an icon's url
	 * @param text
	 *            menuitem's text
	 * @return html source of a menuitem
	 */
	public static String getMenuBarHtml(String url, String text) {
		// TODO: Resize images for this real size, if it is good.
		text = text.replace("\"", "'");
		return "<img width=\"24\" height=\"24\" alt=\"" + text + "\" src=\""
		        + url + "\" />" + " " + text;
	}

	/**
	 * Gives back an html source of a disabled menuitem.
	 * 
	 * @param url
	 *            an icon's url
	 * @param text
	 *            menuitem's text
	 * @return html source of a menuitem
	 */
	// public static String getMenuBarHtmlGrayout(String url,String text) {
	// //TODO: Resize images for this real size, if it is good.
	// text = text.replace("\"", "'");
	// return
	// "<img width=\"16\" height=\"16\" alt=\""+text+"\" src=\""+url+"\" />"+" "+
	// "<span style=\"color:gray;\">"+text+"</span>";
	// }

	/**
	 * Gives back an html source of a menuitem.
	 * 
	 * @param url
	 *            an icon's url
	 * @param text
	 *            menuitem's text
	 * @param enabled
	 *            true if the menuitem is enabled
	 * @return html source of a menuitem
	 */
	public static String getMenuBarHtml(String url, String text, Boolean enabled) {
		// TODO: Resize images for this real size, if it is good.

		String text2 = text.replace("\"", "'");
		String text3 = (enabled) ? text2 : "<span style=\"color:gray;\">"
		        + text2 + "</span>";
		return "<img class=\"GeoGebraMenuImage\" alt=\"" + text2 + "\" src=\""
		        + url + "\" />" + " " + text3;

	}

	/**
	 * Update the "Edit" menu
	 */

	@Override
    public void updateSelection() {
		if(dropMenu!=null){
			dropMenu.getEditMenu().initActions();
		}
	}

	/**
	 * Updates the menubar.
	 */
	@Override
    public void updateMenubar() {
		if(this.dropMenu != null){
			this.dropMenu.updateMenubar();
		}
	}

	public static void setMenuSelected(MenuItem m, boolean visible) {
		if (visible) {
			m.addStyleName("checked");
		} else {
			m.removeStyleName("checked");
		}
	}

	@Override
    public MenuItem getSignIn() {
		return signIn;
	}

	@Override
    public void success(JSONObject response) {
		renderSignedInState();
	}

	@Override
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

	@Override
    public void render() {
		renderSignInState();
	}

	@Override
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

	@Override
    public void updateFonts() {
		String fontsizeString = app.getGUIFontSize() + "px";
		int imagesize = Math.round(app.getGUIFontSize() * 4 / 3);
		int toolbariconSize = 2 * app.getGUIFontSize();
		// until we have no enough place for the big icons in the toolbar, don't
		// enable to increase too much the size of icons.
		if (toolbariconSize > 45)
			toolbariconSize = 45;

		String innerText = ".GeoGebraMenuBar, .GeoGebraPopupMenu{font-size: "
		        + fontsizeString
		        + " !important}"
		        + ".GeoGebraMenuImage{height: "
		        + imagesize
		        + "px; width: "
		        + imagesize
		        + "px;}"
		        + ".GeoGebraMenuBar input[type=\"checkbox\"], .GeogebraMenuBar input[type=\"radio\"], "
		        + ".GeoGebraPopupMenu input[type=\"checkbox\"], .GeogebraPopupMenu input[type=\"radio\"] "
		        + "{height: " + fontsizeString + "; width: " + fontsizeString
		        + ";}" + ".toolbar_menuitem{font-size: " + fontsizeString
		        + ";}" + ".toolbar_menuitem img{width: " + toolbariconSize
		        + "px;}";

		// Create a new style element for font size changes, and remove the old
		// ones, if already exist.
		// Then add the new element for all GeoGebraWeb applets or application.
		NodeList<Element> fontsizeElements = Dom
		        .getElementsByClassName("GGWFontsize");
		for (int i = 0; i < fontsizeElements.getLength(); i++) {
			fontsizeElements.getItem(i).removeFromParent();
		}
		Element fontsizeElement = DOM.createElement("style");
		fontsizeElement.addClassName("GGWFontsize");
		fontsizeElement.setInnerText(innerText);
		NodeList<Element> geogebrawebElements = Dom
		        .getElementsByClassName("geogebraweb");
		for (int i = 0; i < geogebrawebElements.getLength(); i++) {
			geogebrawebElements.getItem(i).appendChild(fontsizeElement);
		}

	}

}
