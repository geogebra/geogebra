package org.geogebra.web.full.gui.menu;

import java.util.List;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.gui.menu.impl.DefaultDrawerMenuFactory;
import org.geogebra.common.gui.menu.impl.ExamDrawerMenuFactory;
import org.geogebra.common.gui.menu.impl.MebisDrawerMenuFactory;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.menu.action.DefaultMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.ExamMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.MebisMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.MenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.ScientificMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.SuiteMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.icons.DefaultMenuIconProvider;
import org.geogebra.web.full.gui.menu.icons.MebisMenuIconProvider;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Controller for the main menu in the apps.
 */
public class MenuViewController implements ResizeHandler, EventRenderable, SetLabels {

	private static final String MENU_PANEL_GLASS = "menuPanelGlass";
	private static final String MENU_PANEL_CONTAINER_STYLE = "menuPanelContainer";
	private static final String MAIN_MENU_STYLE = "mainMenu";
	private static final String SUB_MENU_STYLE = "subMenu";
	private static final String TRANSITION_IN_STYLE = "transitionIn";
	private static final String TRANSITION_OUT_STYLE = "transitionOut";

	private MenuViewListener menuViewListener;

	private SimplePanel menuPanelGlass;
	private SimplePanel submenuContainer;
	private FloatingMenuView floatingMenuView;
	private HeaderedMenuView headeredMenuView;
	private HeaderView headerView;
	private MenuView menuView;
	private DrawerMenu activeMenu;

	private LocalizationW localization;
	private GeoGebraFrameW frame;
	private MenuIconResource menuIconResource;
	private MenuActionRouter menuActionRouter;

	private DrawerMenuFactory defaultDrawerMenuFactory;
	private DrawerMenuFactory examDrawerMenuFactory;

	private MenuActionHandlerFactory defaultActionHandlerFactory;
	private MenuActionHandlerFactory examActionHandlerFactory;

	private GeoGebraTubeUser user;

	/**
	 * Creates a MenuViewController.
	 * @param app app
	 */
	public MenuViewController(AppWFull app) {
		createObjects(app);
		createViews();
		createFactories(app);
		setDefaultMenu();
		registerListeners(app);
		onResize(null);
	}

	private void createObjects(AppWFull app) {
		localization = app.getLocalization();
		frame = app.getAppletFrame();
		menuIconResource = new MenuIconResource(app.isMebis()
				? MebisMenuIconProvider.INSTANCE : DefaultMenuIconProvider.INSTANCE);
	}

	private void createViews() {
		menuPanelGlass = new SimplePanel();
		menuPanelGlass.addStyleName(MENU_PANEL_GLASS);
		floatingMenuView = new FloatingMenuView();
		floatingMenuView.setVisible(false);

		submenuContainer = new SimplePanel();
		headerView = createHeaderView();
		headerView.getBackButton().removeFromParent();
		menuView = new MenuView();
		headeredMenuView = new HeaderedMenuView(menuView);
		headeredMenuView.setHeaderView(headerView);
		headeredMenuView.setTitleHeader(true);
		headeredMenuView.addStyleName(MAIN_MENU_STYLE);
		submenuContainer.addStyleName(SUB_MENU_STYLE);

		FlowPanel menuPanelContainer = new FlowPanel();
		menuPanelContainer.addStyleName(MENU_PANEL_CONTAINER_STYLE);
		menuPanelContainer.add(headeredMenuView);
		menuPanelContainer.add(submenuContainer);

		floatingMenuView.add(menuPanelContainer);
		menuPanelGlass.add(floatingMenuView);
		setSubmenuVisibility(false);
	}

	private void createFactories(AppWFull app) {
		createDrawerMenuFactories(app);
		createActionHandlerFactories(app);
	}

	private void createDrawerMenuFactories(AppW app) {
		GeoGebraConstants.Version version = app.getConfig().getVersion();
		defaultDrawerMenuFactory = createDefaultMenuFactory(app, version);
		examDrawerMenuFactory = new ExamDrawerMenuFactory(version);
	}

	/**
	 * build menu again with new appConfig (neede for suite)
	 * @param app see {@link AppW}
	 */
	public void resetMenuOnAppSwitch(AppW app) {
		GeoGebraConstants.Version version = app.getConfig().getVersion();
		defaultDrawerMenuFactory =  new DefaultDrawerMenuFactory(
				app.getPlatform(),
				version,
				hasLoginButton(app) ? app.getLoginOperation() : null,
				shouldCreateExamEntry(app),
				app.enableFileFeatures(),
				true);
		setDefaultMenu();
	}

	private DrawerMenuFactory createDefaultMenuFactory(AppW app,
			GeoGebraConstants.Version version) {
		if (app.isMebis()) {
			return new MebisDrawerMenuFactory(app.getPlatform(), version, app.getLoginOperation());
		} else {
			boolean addAppSwitcher = version.equals(GeoGebraConstants.Version.SUITE);
			return new DefaultDrawerMenuFactory(
					app.getPlatform(),
					version,
					hasLoginButton(app) ? app.getLoginOperation() : null,
					shouldCreateExamEntry(app),
					app.enableFileFeatures(),
					addAppSwitcher);
		}
	}

	private void createActionHandlerFactories(AppWFull app) {
		GeoGebraConstants.Version version = app.getConfig().getVersion();
		if (version == GeoGebraConstants.Version.SCIENTIFIC) {
			defaultActionHandlerFactory = new ScientificMenuActionHandlerFactory(app);
		} else if (version == GeoGebraConstants.Version.SUITE) {
			defaultActionHandlerFactory = new SuiteMenuActionHandlerFactory(app);
		} else if (app.isMebis()) {
			defaultActionHandlerFactory = new MebisMenuActionHandlerFactory(app);
		} else {
			defaultActionHandlerFactory = new DefaultMenuActionHandlerFactory(app);
		}
		examActionHandlerFactory = new ExamMenuActionHandlerFactory(app);
	}

	private boolean shouldCreateExamEntry(AppW app) {
		return app.getConfig().hasExam() && !app.isExam() && app.getLAF().isOfflineExamSupported();
	}

	private boolean hasLoginButton(AppW app) {
		return app.getConfig().getVersion() != GeoGebraConstants.Version.SCIENTIFIC
				&& (!app.isMebis())
				&& app.enableFileFeatures()
				&& app.getLAF().hasLoginButton();
	}

	private void registerListeners(AppW app) {
		Window.addResizeHandler(this);
		app.getLoginOperation().getView().add(this);
		localization.registerLocalizedUI(this);
	}

	/**
	 * Set the menu view listener.
	 * @param menuViewListener listener
	 */
	public void setMenuViewListener(MenuViewListener menuViewListener) {
		this.menuViewListener = menuViewListener;
	}

	/**
	 * Get the menu view.
	 * @return view
	 */
	public Widget getView() {
		return menuPanelGlass;
	}

	/**
	 * Sets the menu to default.
	 */
	public void setDefaultMenu() {
		menuActionRouter =
				new MenuActionRouter(defaultActionHandlerFactory.create(), this, localization);
		setDrawerMenu(defaultDrawerMenuFactory.createDrawerMenu());
	}

	/**
	 * Sets the menu to exam.
	 */
	public void setExamMenu() {
		menuActionRouter =
				new MenuActionRouter(examActionHandlerFactory.create(), this, localization);
		setDrawerMenu(examDrawerMenuFactory.createDrawerMenu());
	}

	private void setDrawerMenu(DrawerMenu drawerMenu) {
		activeMenu = drawerMenu;
		updateMenu();
	}

	private void updateMenu() {
		updateHeaderCaption();
		updateMenuItemGroups();
	}

	/**
	 * Sets the menu visibility.
	 * @param visible true to show the menu
	 */
	public void setMenuVisible(boolean visible) {
		if (visible != floatingMenuView.isVisible()) {
			floatingMenuView.setVisible(visible);
			notifyMenuViewVisibilityChanged(visible);
			hideSubmenu();
		}
	}

	private void notifyMenuViewVisibilityChanged(boolean visible) {
		if (menuViewListener != null) {
			if (visible) {
				menuViewListener.onMenuOpened();
			} else {
				menuViewListener.onMenuClosed();
			}
		}
	}

	private void updateMenuItemGroups() {
		setMenuItemGroups(menuView, activeMenu.getMenuItemGroups());
	}

	void setMenuItemGroups(MenuView menuView, List<MenuItemGroup> menuItemGroups) {
		menuView.clear();
		for (MenuItemGroup group : menuItemGroups) {
			createMenuItemGroup(menuView, group);
		}
	}

	void showSubmenu(HeaderedMenuView headeredSubmenu) {
		submenuContainer.setWidget(headeredSubmenu);
		setSubmenuVisibility(true);
	}

	void hideSubmenu() {
		if (submenuContainer.getWidget() != null) {
			setSubmenuVisibility(false);
		}
	}

	private void setSubmenuVisibility(boolean visible) {
		setMenuTransition(submenuContainer, visible);
		setMenuTransition(headeredMenuView, !visible);
	}

	private void setMenuTransition(Widget widget, boolean transitionIn) {
		widget.setStyleName(TRANSITION_IN_STYLE, transitionIn);
		widget.setStyleName(TRANSITION_OUT_STYLE, !transitionIn);
	}

	HeaderView createHeaderView() {
		HeaderView headerView = new HeaderView();
		headerView.setElevated(false);
		headerView.setCompact(true);
		return headerView;
	}

	private void updateHeaderCaption() {
		String localizedTitle = localization.getMenu(activeMenu.getTitle());
		headerView.setCaption(localizedTitle);
	}

	private void createMenuItemGroup(MenuView menuView, MenuItemGroup menuItemGroup) {
		String titleKey = menuItemGroup.getTitle();
		String title = titleKey == null ? null : localization.getMenu(titleKey);
		MenuItemGroupView view = new MenuItemGroupView(title);
		for (MenuItem menuItem : menuItemGroup.getMenuItems()) {
			createMenuItem(menuItem, view);
		}
		menuView.add(view);
	}

	private void createMenuItem(final MenuItem menuItem, MenuItemGroupView parent) {
		MenuItemView view = createMenuItemView(menuItem);
		view.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				menuActionRouter.handleMenuItem(menuItem);
			}
		});
		parent.add(view);
	}

	private MenuItemView createMenuItemView(MenuItem menuItem) {
		if (menuItem.getIcon() == Icon.USER_ICON) {
			return new MenuItemView(getUserImage(), menuItem.getLabel(), true);
		} else {
			SVGResource icon = menuItem.getIcon() != null
					? menuIconResource.getImageResource(menuItem.getIcon()) : null;
			String label = localization.getMenu(menuItem.getLabel());
			return new MenuItemView(icon, label);
		}
	}

	private ImageResource getUserImage() {
		return new ImageResourcePrototype(user.getUserName(),
				UriUtils.fromString(user.getImageURL()),
				0, 0, 36, 36, false, false);
	}

	@Override
	public void onResize(ResizeEvent event) {
		headerView.setVisible(frame.hasSmallWindowOrCompactHeader()
				&& activeMenu.getTitle() != null);
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent) {
			user = ((LoginEvent) event).getUser();
		} else {
			user = null;
		}
		if (event instanceof LoginEvent || event instanceof LogOutEvent) {
			setDefaultMenu();
		}
	}

	@Override
	public void setLabels() {
		updateMenu();
	}
}
