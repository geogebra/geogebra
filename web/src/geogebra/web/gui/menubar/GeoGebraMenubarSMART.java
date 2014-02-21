package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.html5.css.GuiResources;
import geogebra.web.gui.app.GGWFrameLayoutPanel;
import geogebra.web.main.AppW;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.StackPanel;

/**
 * Sidebar menu for SMART
 * 
 * 
 */

public class GeoGebraMenubarSMART extends FlowPanel implements GeoGebraMenuW, WindowResizeListener {
	
	/**
	 * Appw app
	 */
	/*private MenuItem signIn;
	private SignedInMenuW signedIn;
	private MenuItem signedInMenu;*/
	
	private AppW app;
	
	private StackPanel menuPanel;
	private ViewMenuW viewMenu;
	private FileMenuW fileMenu;
	private HelpMenuW helpMenu;
	private OptionsMenuW optionsMenu;
	private EditMenuW editMenu;
	private PerspectivesMenuW perspectivesMenu;
	private Runnable closeCallback;

	/**
	 * Constructs the menubar
	 * 
	 * @param app
	 *            application
	 */
	public GeoGebraMenubarSMART(AppW app) {
		this.addStyleName("menubarSMART");
		this.app = app;
		this.closeCallback = closeCallback;
		init();
	}

	private void init() {
		this.createFileMenu();
		this.createPerspectivesMenu();
		this.createEditMenu();
		this.createViewMenu();
		this.createOptionsMenu();
		this.createHelpMenu();
		
		this.menuPanel = new StackPanel();
		this.menuPanel.addStyleName("menuPanel");
		
		this.menuPanel.add(fileMenu, setHTML(GuiResources.INSTANCE.menu_icon_file(), "File"), true);
		this.menuPanel.add(editMenu, setHTML(GuiResources.INSTANCE.menu_icon_edit(), "Edit"), true);
		this.menuPanel.add(perspectivesMenu, setHTML(GuiResources.INSTANCE.menu_icon_perspectives_algebra(), "Perspectives"), true);
		this.menuPanel.add(viewMenu, setHTML(GuiResources.INSTANCE.menu_icon_view(), "View"), true);
		this.menuPanel.add(optionsMenu, setHTML(GuiResources.INSTANCE.menu_icon_options(), "Options"), true);
		this.menuPanel.add(helpMenu, setHTML(GuiResources.INSTANCE.menu_icon_help(), "Help"), true);

	    this.add(menuPanel);
	    
	    onWindowResized(0, 0);
	}
	
	private String setHTML(ImageResource img, String s){
		//return  "<img src=\""+img.getSafeUri().asString()+"\" /><span style= \"font-size:80% \"  >" + s + "</span>";
		return  "<img src=\""+img.getSafeUri().asString()+"\" /><span>" + s + "</span>";
	}
	
	
	
	private void createFileMenu() {
		fileMenu = new FileMenuW(app, true, closeCallback);
	}

	private void createPerspectivesMenu() {
		perspectivesMenu = new PerspectivesMenuW(app);
	}

	private void createEditMenu() {
		editMenu = new EditMenuW(app);
	}
	
	private void createViewMenu() {

		viewMenu = (app.isApplet()) ? new ViewMenuW(app) : new ViewMenuApplicationW(app);
	}
	
	private void createHelpMenu() {
		helpMenu = new HelpMenuW(app);
	}

	private void createOptionsMenu() {
		optionsMenu = new OptionsMenuW(app);
	}

	public EditMenuW getEditMenu() {
	    return editMenu;
    }

	public void updateMenubar() {
		app.getOptionsMenu().update();
		if (!app.isApplet()) {
			((ViewMenuApplicationW) viewMenu).update();
		}
    }
	
	public void updateSelection() {
		if(this != null){
			getEditMenu().initActions();
		}
	}

	@Override
    public MenuItem getSignIn() {
		return null;
    }
	
	public static void setMenuSelected(MenuItem m, boolean visible) {
		if (visible) {
			m.addStyleName("checked");
		} else {
			m.removeStyleName("checked");
		}
	}

	@Deprecated
    @Override
	public void onWindowResized(int width, int height) {
		App.debug("resize");
		int menuHeight = Window.getClientHeight() - GGWFrameLayoutPanel.COMMAND_LINE_HEIGHT - GGWFrameLayoutPanel.TOOLBAR_HEIGHT;
	    this.setHeight(menuHeight + "px");
    }

	/*MenuItem drop = null;
	FlowPanel menuPanel;
	SideBarMenuW dropMenu;

	private void initMenuPanel() {
		menuPanel = new FlowPanel();
		dropMenu = new SideBarMenuW(app, new Runnable(){
			@Override
            public void run() {
				//do nothing
            }});
		this.dropMenu.addStyleName("menuPanel");
		menuPanel.add(dropMenu);
		this.menuPanel.setVisible(true);
	}

	private void init() {
		this.clearItems();
		this.initMenuPanel();
		this.getElement().appendChild(this.menuPanel.getElement());
		
		
		
		
		
		this.clearItems();

		drop = addItem("\u2630", true, new Command() {
			

			@Override
            public void execute() {
				if (menuPanel == null) {
					initMenuPanel();
				}
				if (menuPanel.isVisible()) {
					menuPanel.setVisible(false);
				} else {
					menuPanel.setVisible(true);
				}
			}
		});
		*/
		

		// DropDownMenuW dropDownMenu = new DropDownMenuW(app);
		// MenuItem drop = addItem("\u2630", dropDownMenu);
		//drop.getElement().getStyle().setFontSize(120, Unit.PCT);

		/*MenuItem ggb = addItem("GeoGebra", true, new Command() {
			@Override
            public void execute() {
				// do nothing
			}
		});
		ggb.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		ggb.getElement().getStyle().setColor("white");
		ggb.addStyleName("menubar2-ggbLogo");
		addItem(ggb);*/

		/*if (!app.menubarRestricted()) {
			createSignIn();
		}
		
	}*/

//	private native String getNativeEmailSet() /*-{
//		if ($wnd.GGW_appengine) {
//			return $wnd.GGW_appengine.USER_EMAIL;
//		} else
//			return "";
//	}-*/;

	//private void createWindowMenu() {
		//WindowMenuW windowMenu = new WindowMenuW(app);
		//addItem(app.getMenu("Window"), windowMenu);
	//}

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
//	public static String getMenuBarHtml(String url, String text) {
//		// TODO: Resize images for this real size, if it is good.
//		text = text.replace("\"", "'");
//		return "<img width=\"24\" height=\"24\" alt=\"" + text + "\" src=\""
//		        + url + "\" />" + " " + text;
//	}

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
//	public static String getMenuBarHtml(String url, String text, Boolean enabled) {
//		// TODO: Resize images for this real size, if it is good.
//
//		String text2 = text.replace("\"", "'");
//		String text3 = (enabled) ? text2 : "<span style=\"color:gray;\">"
//		        + text2 + "</span>";
//		return "<img class=\"GeoGebraMenuImage\" alt=\"" + text2 + "\" src=\""
//		        + url + "\" />" + " " + text3;
//
//	}

	/**
	 * Update the "Edit" menu
	 */

	/*@Override
    public void updateSelection() {
		if(dropMenu!=null){
			dropMenu.getEditMenu().initActions();
		}
	}*/

//	public static void setMenuSelected(MenuItem m, boolean visible) {
//		if (visible) {
//			m.addStyleName("checked");
//		} else {
//			m.removeStyleName("checked");
//		}
//	}

	/*@Override
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
	}*/

//	private void renderSignedInState() {
//		if (signedInMenu == null) {
//			/*createSignedInMenu();*/
//		} else {
//			signedIn.refreshstate();
//		}
//		signedInMenu.setVisible(true);
//		signIn.setVisible(false);
//	}

	/*@Override
    public void render() {
		renderSignInState();
	}*/

	/*@Override
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

	}*/

}
