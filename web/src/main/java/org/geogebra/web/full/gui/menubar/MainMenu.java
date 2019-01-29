package org.geogebra.web.full.gui.menubar;

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
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.gui.TabHandler;
import org.geogebra.web.html5.gui.laf.MainMenuI;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.SharedResources;
import org.geogebra.web.shared.ggtapi.LoginOperationW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sidebar menu for SMART
 * 
 * 
 */
public class MainMenu extends FlowPanel implements MainMenuI, EventRenderable,
		BooleanRenderable, TabHandler, KeyDownHandler {

	/**
	 * Appw app
	 */
	/*
	 * private MenuItem signIn; private SignedInMenuW signedIn; private MenuItem
	 * signedInMenu;
	 */

	AppW app;

	/**
	 * Panel with menus
	 */
	AriaStackPanel menuPanel;
	private ViewMenuW viewMenu;
	private FileMenuW fileMenu;
	private DownloadMenuW downloadMenu;
	private HelpMenuW helpMenu;
	private ToolsMenuW toolsMenu;
	private EditMenuW editMenu;

	private PerspectivesMenuW perspectivesMenu;
	private PerspectivesMenuUnbundledW perspectiveMenuUnbundled;
	// private boolean leftSide = false;

	public boolean smallScreen = false;
	/**
	 * Menus
	 */
	ArrayList<GMenuBar> menus;
	/**
	 * text list of menu items
	 */
	ArrayList<String> menuTitles = new ArrayList<>();
	/**
	 * img list of menu items
	 */
	ArrayList<SVGResource> menuImgs = new ArrayList<>();
	/** user menu */
	GMenuBar userMenu;
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
	 * Constructs the menubar
	 * 
	 * @param app
	 *            application
	 */
	public MainMenu(AppW app) {
		if (!app.isUnbundledOrWhiteboard()) {
			this.addStyleName("menubarSMART");
		}
		signInMenu = new GMenuBar("signin", app);
		this.app = app;
		init();
	}

	private void init() {
		if (app.getLoginOperation() == null) {
			app.initSignInEventFlow(new LoginOperationW(app),
					ArticleElement.isEnableUsageStats());
		}
		this.app.getLoginOperation().getView().add(this);
		final boolean exam = app.isExam();
		if (app.enableFileFeatures()) {
			this.createFileMenu();
		}

		if (app.isUnbundledOrWhiteboard()) {
			this.createDownloadAsMenu();
		}

		if (app.isUnbundledOrWhiteboard()) {
			this.createDownloadAsMenu();
		}

		boolean enableGraph = !exam || app.enableGraphing();
		if (enableGraph && !app.has(Feature.MOW_BURGER_MENU_CLEANUP)) {
			this.createPerspectivesMenu();
			this.createEditMenu();
			this.createViewMenu();
		}
		this.createOptionsMenu();
		if (enableGraph) {
			this.createToolsMenu();
		}
		this.menus = new ArrayList<>();
		if (!exam) {
			this.createHelpMenu();
			this.createUserMenu();
			if (app.enableFileFeatures()) {
				menus.add(fileMenu);
			}
			if (!app.has(Feature.MOW_BURGER_MENU_CLEANUP)) {
				menus.add(editMenu);
			}
			if (app.isUnbundledOrWhiteboard()) {
				menus.add(downloadMenu);
			}
			if (!app.has(Feature.MOW_BURGER_MENU_CLEANUP)) {
				menus.add(perspectivesMenu);
			}
			if (!app.isUnbundledOrWhiteboard()) {
				menus.add(viewMenu);
				menus.add(settingsMenu);
				menus.add(toolsMenu);
			}
			if (!app.has(Feature.MOW_BURGER_MENU_CLEANUP)) {
				menus.add(helpMenu);
			}
			if (app.enableFileFeatures()) {
				menus.add(signInMenu);
			}

		} else {
			this.menus.add(fileMenu);
			this.menus.add(settingsMenu);
		}
		menuTitles.clear();
		menuImgs.clear();

		smallScreen = app.isUnbundled()
				&& app.shouldHaveSmallScreenLayout();

		initAriaStackPanel();
		if (!app.isUnbundled() && !app.isWhiteboardActive()) {
			this.menuPanel.addStyleName("menuPanel");
		} else if (smallScreen) {
			initLogoMenu();
		}

		if (app.enableFileFeatures()) {
			if (app.isUnbundledOrWhiteboard()) {
				menus.add(fileMenu);
				this.menuPanel
						.add(fileMenu,
								getExpandCollapseHTML(
										app.isWhiteboardActive()
												? MaterialDesignResources.INSTANCE
														.file()
												: MaterialDesignResources.INSTANCE
														.insert_file_black(),
										"File"),
								true);
			} else {
				this.menuPanel.add(fileMenu, getHTML(
						MaterialDesignResources.INSTANCE.insert_file_black(),
						"File"), true);
			}
		}
		if (enableGraph) {
			if (app.isUnbundledOrWhiteboard()
					&& !app.has(Feature.MOW_BURGER_MENU_CLEANUP)) {
				createEditMenu();
				menus.add(editMenu);
				this.menuPanel.add(editMenu,
						getExpandCollapseHTML(
								MaterialDesignResources.INSTANCE.edit_black(),
								"Edit"),
						true);
			} else if (!app.has(Feature.MOW_BURGER_MENU_CLEANUP)) {
				this.menuPanel.add(editMenu,
						getHTML(MaterialDesignResources.INSTANCE.edit_black(),
								"Edit"),
						true);
			}

			if (app.isUnbundledOrWhiteboard()) {
				menus.add(downloadMenu);
				this.menuPanel.add(downloadMenu,
						getExpandCollapseHTML(app.isWhiteboardActive()
								? MaterialDesignResources.INSTANCE.download()
								: MaterialDesignResources.INSTANCE
										.file_download_black(),
								"DownloadAs"),
						true);
				if (!app.has(Feature.MOW_BURGER_MENU_CLEANUP)) {
					this.menuPanel.add(perspectiveMenuUnbundled,
							getExpandCollapseHTML(
									MaterialDesignResources.INSTANCE
											.geogebra_black(),
									"Apps"),
							true);
				}
			} else {
				this.menuPanel
						.add(perspectivesMenu,
								getHTML(MaterialDesignResources.INSTANCE
										.geogebra_black(), "Perspectives"),
								true);
			}

			if (!app.isUnbundledOrWhiteboard()) {
				this.menuPanel.add(viewMenu,
						getHTML(MaterialDesignResources.INSTANCE.home_black(),
								"View"),
						true);
			}
		}

		if (!app.isUnbundledOrWhiteboard()) {
			this.menuPanel
					.add(settingsMenu,
							getHTML(MaterialDesignResources.INSTANCE.gear(),
									app.getLocalization().getMenu("Settings")),
							true);
		} else {
			this.menuPanel.add(settingsMenu,
					getSingleMenuHTML(MaterialDesignResources.INSTANCE.gear(),
							app.getLocalization().getMenu("Settings")),
					true);

		}
		if (!app.getLAF().isSmart() && enableGraph
				&& !app.isUnbundledOrWhiteboard()) {
			this.menuPanel.add(
					toolsMenu, getHTML(
							app.isUnbundled()
									? MaterialDesignResources.INSTANCE
											.tools_black()
									: GuiResources.INSTANCE.menu_icon_tools(),
							"Tools"),
					true);
		}
		if (!exam) {
			if (!app.has(Feature.MOW_BURGER_MENU_CLEANUP)) {
				if (app.isUnbundledOrWhiteboard()) {
					this.menuPanel.add(helpMenu,
							getExpandCollapseHTML(
									SharedResources.INSTANCE.icon_help_black(),
									"Help"),
							true);
				} else {
					this.menuPanel.add(helpMenu,
							getHTML(SharedResources.INSTANCE.icon_help_black(),
									"Help"),
							true);
				}
			}
			if (app.getNetworkOperation().isOnline()) {
				render(true);
			}
			app.getNetworkOperation().getView().add(this);
		}
		this.add(menuPanel);
	}

	private void initLogoMenu() {
		AppType appType = app.getSettings().getToolbarSettings().getType();
		SVGResource icon = appType.equals(AppType.GRAPHING_CALCULATOR)
				? MaterialDesignResources.INSTANCE.graphing()
				: (appType.equals(AppType.GRAPHER_3D)
						? MaterialDesignResources.INSTANCE.graphing3D()
						: MaterialDesignResources.INSTANCE.geometry());
		logoMenu = new GMenuBar("", app);
		logoMenu.setStyleName("logoMenu");
		this.menuPanel.add(logoMenu,
				getHTML(icon,
						appType.equals(AppType.GRAPHING_CALCULATOR)
								? app.getLocalization()
										.getMenu("GeoGebraGraphingCalculator")
								: appType.equals(AppType.GRAPHER_3D)
										? app.getLocalization()
												.getMenu("GeoGebra3DGrapher")
										: app.getLocalization()
												.getMenu("GeoGebraGeometry")),
				true);
	}

	private void initAriaStackPanel() {
		this.menuPanel = new AriaStackPanel() {
			@Override
			public void showStack(int index) {
				if (smallScreen && index == 0) {
					super.showStack(1);
					expandStack(1);
				} else {
					if (app.isUnbundledOrWhiteboard()) {
						int selected = getSelectedIndex();
						collapseStack(getSelectedIndex());
						if (selected == index) {
							closeAll();
							return;
						}
						expandStack(index);
					}
					super.showStack(index);
				}

				dispatchOpenEvent();

				if (smallScreen && index == 0) {
					app.getGuiManager().setDraggingViews(
							isViewDraggingMenu(menus.get(1)), false);
				} else if (index < menus.size()) {
					app.getGuiManager().setDraggingViews(
							isViewDraggingMenu(menus.get(index)), false);
				}
			}

			@Override
			public void onBrowserEvent(Event event) {
				int eventType = DOM.eventGetType(event);
				Element target = DOM.eventGetTarget(event);
				int index = findDividerIndex(target);
				if (!app.isExam() && eventType == Event.ONMOUSEOUT) {
					if (index != getSelectedIndex()) {
						getMenuAt(getSelectedIndex()).selectItem(null);
					}
				} else if (eventType == Event.ONCLICK) {
					// check if SignIn was clicked
					// if we are offline, the last item is actually Help
					if (app.getNetworkOperation().isOnline()
							&& !app.getLoginOperation().isLoggedIn()
							&& index >= 0
							&& this.getWidget(index) == signInMenu) {
						app.getLoginOperation().showLoginDialog();
						app.toggleMenu();
						return;
					} else if (index >= 0) {
						if (this.getWidget(index) == logoMenu) {
							app.toggleMenu();
							return;
						}
						if (this.getWidget(index) == settingsMenu) {
							app.getDialogManager().showPropertiesDialog(
									OptionType.GLOBAL, null);
							app.toggleMenu();
							return;
						}
					}
					if (index != -1) {
						showStack(index);
					}
				}
				super.onBrowserEvent(event);
			}

			private void setExpandStyles(int index) {
				GMenuBar mi = getMenuAt(index);
				mi.getElement().removeClassName("collapse");
				mi.getElement().addClassName("expand");
			}

			private void setCollapseStyles(int index) {
				GMenuBar mi = getMenuAt(index);
				mi.getElement().removeClassName("expand");
				mi.getElement().addClassName("collapse");
			}

			private void setStackText(int index, boolean expand) {
				int step = smallScreen ? 1 : 0;
				if (index < step || index - step >= menuImgs.size()) {
					return;
				}

				SVGResource img = menuImgs.get(index - step);
				GMenuBar menu = getMenuAt(index);
				String title = menu.getMenuTitle().substring(0, 1).toUpperCase()
						+ menu.getMenuTitle().substring(1);
				if (menu == userMenu && app.getLoginOperation().isLoggedIn()) {
					title = app.getLoginOperation().getUserName();
				}

				if (menu == settingsMenu) {
					setStackText(index, getHTML(img, title), title, expand);
					return;
				}

				String menuText = expand ? getHTMLExpand(img, title)
						: getHTMLCollapse(img, title);

				setStackText(index, menuText, title, expand);

				if (expand) {
					setExpandStyles(index);
				} else {
					setCollapseStyles(index);
				}

			}

			private void expandStack(int index) {
				setStackText(index, false);
			}

			private void collapseStack(int index) {
				setStackText(index, true);
			}

			/**
			 * @param ariaLabel
			 *            for compatibility with AriaStackPanel
			 * @param expanded
			 *            for compatibility with AriaStackPanel
			 */
			public void setStackText(int index, @IsSafeHtml String text,
					String ariaLabel, Boolean expanded) {
				super.setStackText(index, text);
				setAriaLabel(index, ariaLabel, expanded);
			}

			@Override
			public void add(Widget w, @IsSafeHtml String stackText,
					boolean asHTML) {
				add(w);
				int index = getWidgetCount() - 1;
				setStackText(index, stackText, getMenuAt(index).getMenuTitle(),
						null);
			}

			@Override
			public void reset() {
				collapseStack(getSelectedIndex());
				for (int i = 1; i < menuPanel.getWidgetCount(); i++) {
					getMenuAt(i).selectItem(null);
				}
			}
		};

		menuPanel.addDomHandler(this, KeyDownEvent.getType());
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
		if (!hasLoginButton()) {
			return;
		}
		removeUserSignIn();
		if (online && app.getLoginOperation().isLoggedIn()) {
			addUserMenu();
		} else if (online) {
			addSignInMenu();
		}
	}

	private boolean hasLoginButton() {
		return app.enableFileFeatures() && (app.getLoginOperation() == null
				|| app.getLAF().hasLoginButton());
	}

	private void removeUserSignIn() {
		if (this.signInMenu != null) {
			this.menuPanel.removeStack(this.signInMenu);
		}
		if (this.userMenu != null) {
			this.menuPanel.removeStack(this.userMenu);
		}
	}

	private void createUserMenu() {
		this.userMenu = new GMenuBar("user", app);
		if (app.isUnbundledOrWhiteboard()) {
			this.userMenu.addStyleName("matStackPanel");
		} else {
			this.userMenu.addStyleName("GeoGebraMenuBar");
		}

		this.userMenu.addItem(
				getMenuBarHtml(
						MaterialDesignResources.INSTANCE.signout_black(),
						app.getLocalization().getMenu("SignOut")),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.getLoginOperation().showLogoutUI();
						app.getLoginOperation().performLogOut();
					}
				});
	}

	/**
	 * @param img
	 *            - menu item image
	 * @param s
	 *            - menu item title
	 * @return html code for a single menu item
	 */
	String getHTML(ResourcePrototype img, String s) {
		return "<img src=\"" + ImgResourceHelper.safeURI(img)
				+ (img instanceof SVGResource ? "" : "\" style=\"opacity:1")
				+ "\" draggable=\"false\"><span>"
				+ app.getLocalization().getMenu(s) + "</span>";
	}

	/**
	 * @param img
	 *            - menu item image
	 * @param s
	 *            - menu item title
	 * @return html code for an expandable menu item
	 */
	String getHTMLExpand(SVGResource img, String s) {
		return "<img src=\"" + img.getSafeUri().asString()
				+ "\" draggable=\"false\" aria-hidden=\"true\"><span>"
				+ app.getLocalization().getMenu(s) + "</span>" + "<img src=\""
				+ MaterialDesignResources.INSTANCE.expand_black().getSafeUri()
						.asString()
				+ "\" class=\"expandImg\" draggable=\"false\""
				+ " aria-label=\"expand\" role=\"button\">";
	}

	/**
	 * @param img
	 *            - menu item img
	 * @param s
	 *            - menu item title
	 * @return html code for menu item
	 */
	String getHTMLCollapse(SVGResource img, String s) {
		return "<img src=\"" + (img == null ? "-" : img.getSafeUri().asString())
				+ "\" draggable=\"false\" aria-hidden=\"true\"><span>"
				+ app.getLocalization().getMenu(s) + "</span>" + "<img src=\""
				+ MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString()
				+ "\" class=\"collapseImg\" draggable=\"false\""
				+ " aria-label=\"collapse\" role=\"button\">";
	}

	private String getExpandCollapseHTML(SVGResource img, String s) {
		menuTitles.add(s);
		menuImgs.add(img);
		return getHTMLExpand(img, s);
	}

	private String getSingleMenuHTML(SVGResource img, String s) {
		menuTitles.add(s);
		menuImgs.add(img);
		return getHTML(img, s);
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
		settingsMenu = new GMenuBar("settings", app);
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
		if (viewMenu != null) {
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
		if (this.getEditMenu() != null) {
			getEditMenu().invalidate();
		}
	}

	/**
	 * Focus a submenu (the last selected one if possible)
	 */
	public void focus() {
		int index = Math.max(menuPanel.getSelectedIndex(), 0);
		if (this.menus.get(index) != null) {
			this.menus.get(index).focus();
		}
	}

	/**
	 * @param w
	 *            submenu
	 * @param left
	 *            arrow direction
	 */
	public static void addSubmenuArrow(AriaMenuBar w, boolean left) {
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

	/**
	 * @param url
	 *            image URL
	 * @param str
	 *            item text
	 * @param enabled
	 *            whether the item is enabled (otherwise it's grayed)
	 * @return menu item as HTML
	 */
	public static String getMenuBarHtml(String url, String str,
			boolean enabled) {
		String text2 = str.replace("\"", "'");
		String text3 = (enabled) ? text2
				: "<span style=\"color:gray;\">" + text2 + "</span>";
		return "<img class=\"GeoGebraMenuImage menuImg\" alt=\"" + text2
				+ "\" src=\"" + url
				+ "\" draggable=\"false\" aria-hidden=\"true\">" + text3;
	}

	/**
	 * @param url
	 *            image URL
	 * @param str
	 *            item text
	 * @return menu item as HTML
	 */
	public static String getMenuBarHtml(String url, String str) {
		String text = str.replace("\"", "'");
		return "<img class=\"menuImg\" width=\"16\" height=\"16\" alt=\"" + text
				+ "\" src=\"" + url
				+ "\" draggable=\"false\" aria-hidden=\"true\">" + text;
	}

	/**
	 * @param m
	 *            item
	 * @param checked
	 *            whether it's checked
	 */
	public static void setMenuSelected(AriaMenuItem m, boolean checked) {
		if (checked) {
			m.addStyleName("checked");
		} else {
			m.removeStyleName("checked");
		}
	}

	/**
	 * sets the height of the menu
	 * 
	 * @param height
	 *            int
	 */
	public void updateHeight(int height) {
		this.setHeight(height + "px");
	}

	@Override
	public void renderEvent(final BaseEvent event) {
		if (!hasLoginButton()) {
			return;
		}

		if (event instanceof LoginEvent
				&& ((LoginEvent) event).isSuccessful()) {
			removeUserSignIn();
			addUserMenu();
			this.userMenu.setVisible(false);
		} else if (event instanceof LogOutEvent) {
			removeUserSignIn();
			addSignInMenu();
			this.signInMenu.setVisible(false);
		}
	}

	private void addSignInMenu() {
		this.menuPanel.add(this.signInMenu,
				getHTML(MaterialDesignResources.INSTANCE.signin_black(),
						app.getLocalization().getMenu("SignIn")),
				true);
	}

	private void addUserMenu() {
		if (app.isUnbundledOrWhiteboard()) {
			this.menuPanel.add(this.userMenu,
					getExpandCollapseHTML(
							MaterialDesignResources.INSTANCE.person_black(),
							app.getLoginOperation().getUserName()),
					true);
		} else {
			this.menuPanel.add(this.userMenu,
					getHTML(MaterialDesignResources.INSTANCE.person_black(),
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
			if (app.isUnbundledOrWhiteboard()) {
				index--;
			}
			if (index < 0 || index > menus.size() - 1) {
				index = 0;
			}
			app.dispatchEvent(
					new org.geogebra.common.plugin.Event(EventType.OPEN_MENU,
							null, menus.get(index).getMenuTitle()));
		}
	}

	/**
	 * Focuses the first item of the Main Menu
	 */
	public void focusFirst() {
		if (menuPanel.getSelectedIndex() != 0) {
			menuPanel.showStack(0);
			getMenuAt(0).focus();
		}
	}

	@Override
	public boolean onTab(Widget source, boolean shiftDown) {
		if (source instanceof GMenuBar) {
			GMenuBar submenu = (GMenuBar) source;

			if (shiftDown) {
				selectPreviousItem(submenu);
			} else if (!selectNextItem(submenu)) {
				app.toggleMenu();
				app.getAccessibilityManager().focusMenu();
			}
			return true;
		}
		return false;
	}

	private void focusStack(int index) {
		if (menuPanel != null) {
			menuPanel.focusHeader(index);
		}
	}

	/**
	 * Selects the next item of the menu.
	 * 
	 * @param menu
	 *            to select in.
	 * @return true if the next item is not the same as it is already selected.
	 * 
	 */
	boolean selectNextItem(GMenuBar menu) {
		if (menu == null) {
			return false;
		}

		if (menu.isLastItemSelected() || menu.isEmpty()
				|| menuPanel.isCollapsed()) {
			menu.selectItem(null);
			int nextIdx = menuPanel.getLastSelectedIndex() + 1;
			if (nextIdx < menuPanel.getWidgetCount()) {
				menuPanel.showStack(nextIdx);
				focusStack(nextIdx);
			} else {
				return false;
			}
		} else {
			menu.moveSelectionDown();
		}
		return true;
	}

	/**
	 * Selects the previous item of the menu.
	 * 
	 * @param menu
	 *            to select in.
	 * @return true if the previous item is not the same as it is already
	 *         selected.
	 */
	boolean selectPreviousItem(GMenuBar menu) {
		if (menu == null) {
			return false;
		}

		if (menu.isFirstItemSelected() || menu.isEmpty()
				|| menuPanel.isCollapsed()) {
			menu.selectItem(null);
			int prevIdx = menuPanel.getLastSelectedIndex() - 1;
			if (prevIdx != -1) {
				menuPanel.showStack(prevIdx);
				focusStack(prevIdx);
			} else {
				return false;
			}
		} else {
			menu.moveSelectionUp();
		}
		return true;
	}

	/**
	 * Gets the menu at given id from StackPanel
	 * 
	 * @param stackIdx
	 *            the index
	 * @return the widget at given index if it is GMenubar instance or null
	 *         otherwise.
	 */
	GMenuBar getMenuAt(int stackIdx) {
		int idx = stackIdx > -1 && stackIdx < menuPanel.getWidgetCount()
				? stackIdx : 0;
		Widget w = menuPanel.getWidget(idx);
		if (w instanceof GMenuBar) {
			return (GMenuBar) w;
		}
		return null;
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int key = event.getNativeKeyCode();
		GMenuBar mi = getMenuAt(menuPanel.getLastSelectedIndex());

		if (key == KeyCodes.KEY_TAB) {
			if (mi != null) {
				onTab(mi, event.isShiftKeyDown());
				event.preventDefault();
				event.stopPropagation();
			}
		} else if (key == KeyCodes.KEY_UP) {
			selectPreviousItem(mi);
		} else if (key == KeyCodes.KEY_DOWN) {
			selectNextItem(mi);
		}
	}

	@Override
	public void setVisible(boolean visible) {
		if (!visible) {
			menuPanel.reset();
		}
		super.setVisible(visible);
	}

	/**
	 * @param imgRes
	 *            image
	 * @param name
	 *            localized text
	 * @return HTML
	 */
	public static String getMenuBarHtml(final ResourcePrototype imgRes,
			String name) {
		final String iconString = NoDragImage.safeURI(imgRes);
		return MainMenu.getMenuBarHtml(iconString, name, true);
	}

	/**
	 * @param name
	 *            manu item localized name
	 * @return item HTML
	 */
	public static String getMenuBarHtmlNoIcon(String name) {
		final String iconString = AppResources.INSTANCE.empty().getSafeUri()
				.asString();
		return MainMenu.getMenuBarHtml(iconString, name, true);
	}
}
