package org.geogebra.web.web.gui.menubar;

import java.util.ArrayList;

import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.html5.gui.laf.MainMenuI;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.browser.SignInButton;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.StackPanel;

/**
 * Sidebar menu for SMART
 * 
 * 
 */
public class MainMenu extends FlowPanel implements MainMenuI, EventRenderable, BooleanRenderable {
	
	/**
	 * Appw app
	 */
	/*private MenuItem signIn;
	private SignedInMenuW signedIn;
	private MenuItem signedInMenu;*/
	
	AppW app;
	
	/**
	 * Panel with menus
	 */
	StackPanel menuPanel;
	private ViewMenuW viewMenu;
	private FileMenuW fileMenu;
	private DownloadMenuW downloadMenu;
	private HelpMenuW helpMenu;
	private OptionsMenuW optionsMenu;
	private ToolsMenuW toolsMenu;
	private EditMenuW editMenu;

	private PerspectivesMenuW perspectivesMenu;
	private PerspectivesMenuUnbundledW perspectiveMenuUnbundled;
	// private boolean leftSide = false;
	/**
	 * Menus
	 */
	ArrayList<GMenuBar> menus;
	/**
	 * text list of menu items
	 */
	ArrayList<String> menuTitles = new ArrayList<String>();
	/**
	 * img list of menu items
	 */
	ArrayList<SVGResource> menuImgs = new ArrayList<SVGResource>();
	private GMenuBar userMenu;
	/** sign in menu */
	final GMenuBar signInMenu;
	/**
	 * simple logo menu item
	 */
	GMenuBar logoMenu;
	/**
	 * simple settings menu item
	 */
	GMenuBar settingsMenu;
	/**
	 * simple language menu item
	 */
	GMenuBar languageMenu;

	/**
	 * Constructs the menubar
	 * 
	 * @param app
	 *            application
	 */
	public MainMenu(AppW app) {
		if (!app.isUnbundled() && !app.isWhiteboardActive()) {
			this.addStyleName("menubarSMART");
		}
		signInMenu = new GMenuBar(true, "signin", app);
		// leftSide = app.isWhiteboardActive() || app.isUnbundled();

		this.app = app;
		init();
	}

	private void init() {
		this.app.getLoginOperation().getView().add(this);
		final boolean exam = app.isExam();
		if(app.enableFileFeatures()){
			this.createFileMenu();
		}
		if (app.isUnbundled()) {
			this.createDownloadAsMenu();
		}
		
		boolean enableGraph = !exam || app.enableGraphing();
		if (enableGraph){
			this.createPerspectivesMenu();
			this.createEditMenu();
			this.createViewMenu();
		}
		this.createOptionsMenu();
		if (enableGraph) {
			this.createToolsMenu();
		}
		this.menus = new ArrayList<GMenuBar>();
		if (!exam) {
			this.createHelpMenu();
			this.createUserMenu();
			if (app.enableFileFeatures()) {
				menus.add(fileMenu);
			}
			
			menus.add(editMenu);
			if (app.isUnbundled()) {
				menus.add(downloadMenu);
			}
			menus.add(perspectivesMenu);
			if (!app.isUnbundled() && !app.isWhiteboardActive()) {
				menus.add(viewMenu);
			}

			menus.add(optionsMenu);
			menus.add(toolsMenu);
			menus.add(helpMenu);

			
			if (app.enableFileFeatures()) {
				menus.add(signInMenu);
			}

		} else {
			this.menus.add(fileMenu);
			this.menus.add(optionsMenu);
		}
		menuTitles.clear();
		menuImgs.clear();
		
		initKeyListener();
		initStackPanel();
		initLogoMenu();

		if(app.enableFileFeatures()){	
			if (app.isUnbundled()) {
				this.menuPanel.add(fileMenu,
						getHTMLCollapse(
								MaterialDesignResources.INSTANCE
										.insert_file_black(),
						"File"), true);
				// fileMenu.getElement().removeClassName("collapse");
				// fileMenu.getElement().addClassName("expand");
				menuTitles.add("File");
				menuImgs.add(
						MaterialDesignResources.INSTANCE.insert_file_black());
			} else {
				this.menuPanel.add(fileMenu,
						getHTML(app.isWhiteboardActive()
								? MaterialDesignResources.INSTANCE
										.insert_file_black()
								: GuiResources.INSTANCE.menu_icon_file(),
								"File"),
						true);
			}
		}
		if (enableGraph) {
			if (app.isUnbundled()) {
				this.menuPanel.add(editMenu,
						getExpandCollapseHTML(
								MaterialDesignResources.INSTANCE.edit_black(),
								"Edit"),
						true);
			} else {
				this.menuPanel.add(editMenu,
						getHTML(app.isWhiteboardActive()
								? MaterialDesignResources.INSTANCE.edit_black()
								: GuiResources.INSTANCE.menu_icon_edit(),
								"Edit"),
					true);
			}

			if (app.isUnbundled()) {
				this.menuPanel.add(downloadMenu, getExpandCollapseHTML(
						MaterialDesignResources.INSTANCE.file_download_black(),
						"DownloadAs"), true);
				this.menuPanel.add(perspectiveMenuUnbundled,
						getExpandCollapseHTML(
								MaterialDesignResources.INSTANCE
										.geogebra_black(),
								"Apps"),
						true);
			} else if (!app.isWhiteboardActive()) {
				this.menuPanel
					.add(perspectivesMenu,
							getHTML(app.isUnbundled()
										? MaterialDesignResources.INSTANCE
												.geogebra_black()
									: GuiResources.INSTANCE
												.menu_icon_perspectives(),
										"Perspectives"),
							true);
			} else if (app.isWhiteboardActive()) {
				this.menuPanel
						.add(perspectiveMenuUnbundled,
								getHTML(MaterialDesignResources.INSTANCE
										.geogebra_black(), "Perspectives"),
								true);
			}

			if (!app.isUnbundled() && !app.isWhiteboardActive()) {
				this.menuPanel.add(viewMenu,
						getHTML(app.isUnbundled()
								? MaterialDesignResources.INSTANCE.home_black()
							: GuiResources.INSTANCE.menu_icon_view(), "View"),
					true);
			}

		}

		if (!app.isUnbundled()) {
			this.menuPanel.add(optionsMenu,
					getHTML(app.isWhiteboardActive()
							? MaterialDesignResources.INSTANCE.gere()
						: GuiResources.INSTANCE.menu_icon_options(),
							app.isWhiteboardActive()
								? app.getLocalization().getMenu("Settings")
								: "Options"),
				true);
		} else {
			settingsMenu = new GMenuBar(true, "", app);
			this.menuPanel.add(settingsMenu,
					getHTML(MaterialDesignResources.INSTANCE.gere(),
							app.getLocalization().getMenu("Settings")),
					true);
			menuTitles.add("Settings");
			menuImgs.add(MaterialDesignResources.INSTANCE.gere());
			languageMenu = new GMenuBar(true, "", app);
			this.menuPanel.add(languageMenu,
					getHTML(MaterialDesignResources.INSTANCE.language_black(),
							app.getLocalization().getMenu("Language")),
					true);
			menuTitles.add("Language");
			menuImgs.add(MaterialDesignResources.INSTANCE.language_black());
		}
		if (!app.getLAF().isSmart() && enableGraph && !app.isUnbundled()
				&& !app.isWhiteboardActive()) {
			this.menuPanel.add(toolsMenu,
					getHTML(app.isUnbundled()
							? MaterialDesignResources.INSTANCE.tools_black()
							: GuiResources.INSTANCE.menu_icon_tools(), "Tools"),
					true);
		}
		if (!exam) {
			if (app.isUnbundled()) {
				this.menuPanel.add(helpMenu, getExpandCollapseHTML(
								MaterialDesignResources.INSTANCE
								.icon_help_black(),
						"Help"), true);
			} else {
				this.menuPanel.add(helpMenu,
						getHTML(app.isWhiteboardActive()
								? MaterialDesignResources.INSTANCE
										.icon_help_black()
								: GuiResources.INSTANCE.menu_icon_help(),
								"Help"),
					true);
			}
			if(app.getNetworkOperation().isOnline()){
				render(true);
			}
			app.getNetworkOperation().getView().add(this);
		}
	    this.add(menuPanel);	    
	}

	private void initLogoMenu() {
		if (!app.isUnbundled()) {
			if (app.isWhiteboardActive()) {
				ImageResource icon = MaterialDesignResources.INSTANCE
						.whiteboard();
				logoMenu = new GMenuBar(true, "", app);
				logoMenu.setStyleName("logoMenu");
				this.menuPanel.add(logoMenu,
						getHTML(icon,
								"GeoGebra " + app.getLocalization()
										.getMenu("Perspective.Whiteboard")),
						true);
			} else {
				this.menuPanel.addStyleName("menuPanel");
			}
		} else {
			SVGResource icon = app.getSettings().getToolbarSettings()
					.getType().equals(AppType.GRAPHING_CALCULATOR)
							? MaterialDesignResources.INSTANCE.graphing()
							: MaterialDesignResources.INSTANCE.geometry();
			logoMenu = new GMenuBar(true, "", app);
			logoMenu.setStyleName("logoMenu");
			this.menuPanel.add(logoMenu, getHTML(icon,
					app.getSettings().getToolbarSettings().getType()
							.equals(AppType.GRAPHING_CALCULATOR)
									? app.getLocalization().getMenu(
											"GeoGebraGraphingCalculator")
									: app.getLocalization()
											.getMenu("GeoGebraGeometry")),
					true);
		}

	}

	private void initStackPanel() {
		this.menuPanel = new StackPanel() {
			@Override
			public void showStack(int index) {
				if (app.isUnbundled() && index == 0) {
					super.showStack(1);
				} else {
					super.showStack(index);
					if (app.isUnbundled()) {
						setStackText(index,
								getHTMLCollapse(menuImgs.get(index - 1),
										menuTitles.get(index - 1)),
								true);
						menus.get(index - 1).getElement()
								.removeClassName("collapse");
						menus.get(index - 1).getElement()
								.addClassName("expand");
					}
				}
				dispatchOpenEvent();
				if (app.isUnbundled() && index == 0) {
					app.getGuiManager().setDraggingViews(
							isViewDraggingMenu(menus.get(1)), false);
				} else {
					app.getGuiManager().setDraggingViews(
							isViewDraggingMenu(menus.get(index)), false);
				}
			}

			@Override
			public void onBrowserEvent(Event event) {

				if (!app.isExam() && DOM.eventGetType(event) == Event.ONCLICK) {
					Element target = DOM.eventGetTarget(event);
					int index = findDividerIndex(target);
					// check if SignIn was clicked
					// if we are offline, the last item is actually Help
					if (app.getNetworkOperation().isOnline()
							&& !app.getLoginOperation().isLoggedIn()
							&& index >= 0
							&& this.getWidget(index) == signInMenu) {
						((SignInButton) app.getLAF().getSignInButton(app))
								.login();
						app.toggleMenu();
						return;
					} else if (index >= 0) {
						if (this.getWidget(index) == logoMenu) {
							app.toggleMenu();
							return;
						}
						if (this.getWidget(index) == settingsMenu
								&& app.has(Feature.GLOBAL_SETTINGS)) {
							app.getDialogManager().showPropertiesDialog(
									OptionType.GLOBAL, null);
							app.toggleMenu();
							return;
						}
						if (this.getWidget(index) == languageMenu) {
							app.showLanguageGUI();
							return;
						}
					}

					if (index != -1) {

						if (index == this.getSelectedIndex()) {
							closeAll(this);
							if (app.isUnbundled()) {
								setStackText(index,
										getHTMLExpand(menuImgs.get(index - 1),
												menuTitles.get(index - 1)),
										true);
								menus.get(index - 1).getElement()
										.removeClassName("expand");
								menus.get(index - 1).getElement()
										.addClassName("collapse");
							}
							return;
						}
						if (app.isUnbundled() && this.getSelectedIndex() > 0) {
							setStackText(this.getSelectedIndex(), getHTMLExpand(
									menuImgs.get(this.getSelectedIndex() - 1),
									menuTitles
											.get(this.getSelectedIndex() - 1)),
									true);
							menus.get(getSelectedIndex() - 1).getElement()
									.removeClassName("expand");
							menus.get(getSelectedIndex() - 1).getElement()
									.addClassName("collapse");
						}
						showStack(index);
					}
				}
				super.onBrowserEvent(event);
			}

			// violator pattern from
			// https://code.google.com/archive/p/google-web-toolkit/issues/1188
			private native void closeAll(StackPanel stackPanel) /*-{
		          stackPanel.@com.google.gwt.user.client.ui.StackPanel::setStackVisible(IZ)(stackPanel. @com.google.gwt.user.client.ui.StackPanel::visibleStack, false);
		          stackPanel.@com.google.gwt.user.client.ui.StackPanel::visibleStack = -1; 
		     }-*/;

			private int findDividerIndex(Element elemSource) {
				Element elem = elemSource;
				while (elem != null && elem != getElement()) {
					String expando = elem.getPropertyString("__index");
					if (expando != null) {
						// Make sure it belongs to me!
						int ownerHash = elem.getPropertyInt("__owner");
						if (ownerHash == hashCode()) {
							// Yes, it's mine.
							return Integer.parseInt(expando);
						}
						// It must belong to some nested StackPanel.
						return -1;
					}
					elem = DOM.getParent(elem);
				}
				return -1;
			}

		};

	}

	private void initKeyListener() {
		for (int i = 0; i < menus.size(); i++) {
			final int next = (i + 1) % menus.size();
			final int previous = (i - 1 + menus.size()) % menus.size();
			final int index = i;
			this.menus.get(i).addDomHandler(new KeyDownHandler() {

				@Override
				public void onKeyDown(KeyDownEvent event) {
					int keyCode = event.getNativeKeyCode();
					// First / last below are not intuitive -- note that default
					// handler of
					// down skipped already from last to first
					if (keyCode == KeyCodes.KEY_DOWN) {
						if (menus.get(index).isFirstItemSelected()) {
							menuPanel.showStack(next);
							menus.get(next).focus();
						}

					}
					if (keyCode == KeyCodes.KEY_UP) {
						if (menus.get(index).isLastItemSelected()) {
							menuPanel.showStack(previous);
							menus.get(previous).focus();
						}
					}
					if (keyCode == KeyCodes.KEY_ESCAPE) {
						app.toggleMenu();
						((GuiManagerW) app.getGuiManager()).getToolbarPanel()
								.selectMenuButton(-1);
					}

				}
			}, KeyDownEvent.getType());
		}

	}

	/**
	 * @param menu
	 *            menu
	 * @return whether dragging views should be enabled for this menu
	 */
	protected boolean isViewDraggingMenu(GMenuBar menu) {
		return menu == perspectivesMenu || menu == viewMenu;
	}

	@Override
	public void render(boolean online) {
		if(!app.enableFileFeatures()){
			return;
		}
		if (online && app.getLoginOperation().isLoggedIn()) {
			loggedIn = true;
			addUserMenu();
		} else if(online){
			addSignInMenu();
		} else {
			loggedIn = false;
			if(this.signInMenu != null){
				this.menuPanel.remove(this.signInMenu);
			}
			if(this.userMenu != null){
				this.menuPanel.remove(this.userMenu);
			}
		}
    }

	private void createUserMenu() {
		this.userMenu = new GMenuBar(true, "user", app);
		if (app.isUnbundled() || app.isWhiteboardActive()) {
			this.userMenu.addStyleName("matStackPanel");
		} else {
			this.userMenu.addStyleName("GeoGebraMenuBar");
		}
		this.userMenu.addItem(
				getMenuBarHtml(
						app.isUnbundled() || app.isWhiteboardActive()
								? MaterialDesignResources.INSTANCE
										.signout_black().getSafeUri().asString()
								: GuiResources.INSTANCE.menu_icon_sign_out().getSafeUri()
								.asString(),
						app.getLocalization().getMenu("SignOut"), true),
				true, new MenuCommand(app) {

			@Override
            public void doExecute() {
				app.getLoginOperation().performLogOut();
			}
		});
    }

	private String getHTML(ResourcePrototype img, String s) {
		//return  "<img src=\""+img.getSafeUri().asString()+"\" /><span style= \"font-size:80% \"  >" + s + "</span>";
		return "<img src=\"" + ImgResourceHelper.safeURI(img)
				+ "\" draggable=\"false\"><span>"
				+ app.getLocalization().getMenu(s) + "</span>";
	}
	
	/**
	 * @param img
	 *            - menu item img
	 * @param s
	 *            - menu item title
	 * @return html code for menu item
	 */
	String getHTMLExpand(SVGResource img, String s) {
		return "<img src=\"" + img.getSafeUri().asString()
				+ "\" draggable=\"false\"><span>"
				+ app.getLocalization().getMenu(s) + "</span>"
				+ "<img src=\"" + MaterialDesignResources.INSTANCE
						.expand_black().getSafeUri().asString()
				+ "\" class=\"expandImg\" draggable=\"false\">";
	}

	/**
	 * @param img
	 *            - menu item img
	 * @param s
	 *            - menu item title
	 * @return html code for menu item
	 */
	String getHTMLCollapse(SVGResource img, String s) {
		return "<img src=\"" + img.getSafeUri().asString()
				+ "\" draggable=\"false\"><span>"
				+ app.getLocalization().getMenu(s) + "</span>" + "<img src=\""
				+ MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString()
				+ "\" class=\"collapseImg\" draggable=\"false\">";
	}

	private String getExpandCollapseHTML(SVGResource img, String s) {
		menuTitles.add(s);
		menuImgs.add(img);
		return getHTMLExpand(img, s);
	}

	private void createFileMenu() {
		fileMenu = new FileMenuW(app);
	}

	private void createDownloadAsMenu() {
		downloadMenu = new DownloadMenuW(app);
	}

	private void createPerspectivesMenu() {
		perspectivesMenu = new PerspectivesMenuW(app);
		perspectiveMenuUnbundled = new PerspectivesMenuUnbundledW(app);
	}

	private void createEditMenu() {
		editMenu = new EditMenuW(app);
	}
	
	private void createViewMenu() {
		if (!app.isUnbundled()) {
			viewMenu = new ViewMenuW(app);
		}
	}
	
	private void createHelpMenu() {
		helpMenu = new HelpMenuW(app);
	}

	private void createOptionsMenu() {
		optionsMenu = new OptionsMenuW(app);
	}

	private void createToolsMenu() {
		toolsMenu = new ToolsMenuW(app);
	}

	private EditMenuW getEditMenu() {
	    return editMenu;
    }


	/**
	 * Update all submenus that depend on file content
	 */
	public void updateMenubar() {
		if(app.hasOptionsMenu()){
			app.getOptionsMenu(null).update();
		}
		if(viewMenu != null){
			viewMenu.update();
		}

		if (this.getEditMenu() != null) {
			getEditMenu().update();
		}
    }
	


	/**
	 * Update on selection change
	 */
	public void updateSelection() {
		if(this.getEditMenu()!=null){
			getEditMenu().invalidate();
		}
	}

	public void focus(){
		int index= Math.max(menuPanel.getSelectedIndex(),0);
		if (this.menus.get(index) != null) {
			this.menus.get(index).focus();
		}
	}

	public static void addSubmenuArrow(MenuBar w) {
		addSubmenuArrow(w, false);
	}

	public static void addSubmenuArrow(MenuBar w, boolean left) {
		w.addStyleName(left ? "subMenuRightSide" : "subMenuLeftSide");
		FlowPanel arrowSubmenu = new FlowPanel();
		arrowSubmenu.addStyleName("arrowSubmenu");
		NoDragImage arrow = left
				? new NoDragImage(GuiResources.INSTANCE.arrow_submenu_left()
						.getSafeUri().asString())
				: new NoDragImage(GuiResources.INSTANCE.arrow_submenu_right()
						.getSafeUri().asString());
		arrowSubmenu.add(arrow);
		w.getElement().appendChild(arrowSubmenu.getElement());
    }

	public static String getMenuBarHtml(String url, String str, boolean enabled) {
		String text2 = str.replace("\"", "'");
		String text3 = (enabled) ? text2 :  "<span style=\"color:gray;\">"+text2+"</span>";
		return "<img class=\"GeoGebraMenuImage menuImg\" alt=\"" + text2
				+ "\" src=\""
				+ url + "\" draggable=\"false\">" + text3;
    }

	
	public static String getMenuBarHtml(String url, String str) {
		String text = str.replace("\"", "'");
		return "<img class=\"menuImg\" width=\"16\" height=\"16\" alt=\"" + text
				+ "\" src=\""
				+ url + "\" draggable=\"false\">"
				+ text;
    }

	public static String getMenuBarHtmlImgLast(String str, String url) {
		String text = str.replace("\"", "'");
		return text + "<img class=\"menuImg\" width=\"16\" height=\"16\" alt=\""
				+ text + "\" src=\"" + url + "\" draggable=\"false\">";
	}
	public static void setMenuSelected(MenuItem m, boolean visible) {
		if (visible) {
			m.addStyleName("checked");
		} else {
			m.removeStyleName("checked");
		}
	}
	
	/**
	 * sets the height of the menu
	 * @param height int
	 */
	public void updateHeight(int height) {
		this.setHeight(height + "px");
    }

	private boolean loggedIn = false;
	@Override
	public void renderEvent(final BaseEvent event) {
		if(!app.enableFileFeatures()){
			return;
		}
		if (event instanceof LoginEvent && ((LoginEvent) event).isSuccessful()) {
			if (loggedIn) {
				this.menuPanel.remove(this.userMenu);
			}
			loggedIn = true;
			this.menuPanel.remove(this.signInMenu);
			addUserMenu();
			this.userMenu.setVisible(false);
		} else if (event instanceof LogOutEvent) {
			this.menuPanel.remove(this.userMenu);
			loggedIn = false;
			addSignInMenu();
			this.signInMenu.setVisible(false);
		}
	}

    private void addSignInMenu() {
		this.menuPanel.add(this.signInMenu,
				getHTML(app.isUnbundled() || app.isWhiteboardActive()
						? MaterialDesignResources.INSTANCE.signin_black()
						: GuiResources.INSTANCE.menu_icon_sign_in(),
						app.getLocalization().getMenu("SignIn")),
				true);
    }

    private void addUserMenu() {
		if (app.isUnbundled()) {
			this.menuPanel.add(this.userMenu,
					getExpandCollapseHTML(
							MaterialDesignResources.INSTANCE.person_black(),
							app.getLoginOperation().getUserName()),
					true);
		} else {
			this.menuPanel
				.add(this.userMenu,
							getHTML(app.isWhiteboardActive()
									? MaterialDesignResources.INSTANCE
											.person_black()
									: GuiResources.INSTANCE
									.menu_icon_signed_in_f(),
									app.getLoginOperation().getUserName()),
						true);
		}
    }

	/**
	 * Inform client listener about opening the menu
	 */
	public void dispatchOpenEvent() {
		if (menuPanel != null) {
			int index = menuPanel.getSelectedIndex();
			if (app.isUnbundled()) {
				index--;
			}
			if (index < 0 || index > menus.size() - 1) {
				index = 0;
			}
			app.dispatchEvent(new org.geogebra.common.plugin.Event(
					EventType.OPEN_MENU, null,
					menus.get(index).getMenuTitle()));
		}
	}

}
