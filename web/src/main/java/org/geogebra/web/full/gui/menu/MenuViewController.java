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
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.menu.action.DefaultMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.ExamMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.MebisMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.MenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.ScientificMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.SuiteMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.icons.DefaultMenuIconProvider;
import org.geogebra.web.full.gui.menu.icons.MebisMenuIconProvider;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.resources.client.impl.ImageResourcePrototype;
import org.gwtproject.safehtml.shared.UriUtils;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Controller for the main menu in the apps.
 */
public class MenuViewController implements EventRenderable, SetLabels, RequiresResize {

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
	private ExamDrawerMenuFactory examDrawerMenuFactory;

	private MenuActionHandlerFactory defaultActionHandlerFactory;
	private MenuActionHandlerFactory examActionHandlerFactory;

	private LogInOperation loginOperation;

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
		onResize();
	}

	private void createObjects(AppWFull app) {
		localization = app.getLocalization();
		frame = app.getAppletFrame();
		loginOperation = app.getLoginOperation();
		menuIconResource = new MenuIconResource(app.isMebis()
				? MebisMenuIconProvider.INSTANCE : DefaultMenuIconProvider.INSTANCE);
	}

	private void createViews() {
		menuPanelGlass = new SimplePanel();
		menuPanelGlass.addStyleName("menuPanelGlass");
		floatingMenuView = new FloatingMenuView();
		floatingMenuView.setVisible(false);

		submenuContainer = new SimplePanel();
		headerView = createHeaderView();
		headerView.getBackButton().removeFromParent();
		menuView = new MenuView(this);
		headeredMenuView = new HeaderedMenuView(menuView);
		headeredMenuView.setHeaderView(headerView);
		headeredMenuView.setTitleHeader(true);
		headeredMenuView.addStyleName("mainMenu");
		submenuContainer.addStyleName("subMenu");

		FlowPanel menuPanelContainer = new FlowPanel();
		menuPanelContainer.addStyleName("menuPanelContainer");
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
		examDrawerMenuFactory = new ExamDrawerMenuFactory(version, app.isSuite());
		examDrawerMenuFactory.setCreatesExitExam(!app.isLockedExam());
	}

	/**
	 * build menu again with new appConfig (needed for suite)
	 * @param app see {@link AppW}
	 */
	public void resetMenuOnAppSwitch(AppW app) {
		GeoGebraConstants.Version version = app.getConfig().getVersion();
		defaultDrawerMenuFactory = createDefaultMenuFactory(app, version);
		if (!GlobalScope.examController.isExamActive()) {
			setDefaultMenu();
		} else {
			setExamMenu();
		}
	}

	private DrawerMenuFactory createDefaultMenuFactory(AppW app,
			GeoGebraConstants.Version version) {
		if (app.isMebis()) {
			return new MebisDrawerMenuFactory(app.getPlatform(), version, app.getLoginOperation(),
					app.enableFileFeatures());
		} else {
			boolean addAppSwitcher = app.isSuite();
			String versionStr = GeoGebraConstants.getVersionString6();
			return new DefaultDrawerMenuFactory(
					app.getPlatform(),
					version, app.getLocalization().getPlainDefault("VersionA",
					"Version %0", versionStr),
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
		} else if (app.isSuite()) {
			defaultActionHandlerFactory = new SuiteMenuActionHandlerFactory(app);
		} else if (app.isMebis()) {
			defaultActionHandlerFactory = new MebisMenuActionHandlerFactory(app);
		} else {
			defaultActionHandlerFactory = new DefaultMenuActionHandlerFactory(app);
		}
		examActionHandlerFactory = new ExamMenuActionHandlerFactory(app);
	}

	private boolean shouldCreateExamEntry(AppW app) {
		return app.getConfig().hasExam() && app.getLAF().isOfflineExamSupported();
	}

	private boolean hasLoginButton(AppW app) {
		return app.getConfig().getVersion() != GeoGebraConstants.Version.SCIENTIFIC
				&& !app.isMebis()
				&& app.enableOnlineFileFeatures();
	}

	private void registerListeners(AppW app) {
		app.addWindowResizeListener(this);
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

	public boolean isSubMenu(Widget widget) {
		return submenuContainer.getWidget() == widget;
	}

	/**
	 * Sets the menu visibility.
	 * @param visible true to show the menu
	 */
	public void setMenuVisible(boolean visible) {
		if (visible != floatingMenuView.isVisible()) {
			floatingMenuView.setVisible(visible);
			updateFocus();
			notifyMenuViewVisibilityChanged(visible);
		}
	}

	private void updateFocus() {
		hideSubmenuAndMoveFocus();
		if (floatingMenuView.isVisible()) {
			menuView.selectItem(0);
			menuView.getSelectedItem().getElement().focus();
		}
		setMenuTransition(menuView, floatingMenuView.isVisible());
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
			if (!isLastGroupOfGroupList(group, menuItemGroups)) {
				menuView.add(BaseWidgetFactory.INSTANCE.newDivider());
			}
		}
	}

	private boolean isLastGroupOfGroupList(MenuItemGroup group,
			List<MenuItemGroup> menuItemGroups) {
		return menuItemGroups.get(menuItemGroups.size() - 1).equals(group);
	}

	void showSubmenu(HeaderedMenuView headeredSubmenu) {
		submenuContainer.setWidget(headeredSubmenu);
		setSubmenuVisibility(true);

	}

	void hideSubmenuAndMoveFocus() {
		if (submenuContainer.getWidget() != null) {
			setSubmenuVisibility(false);
			menuView.selectItem(menuView.getSelectedIndex());
			menuView.getSelectedItem().getElement().focus();
		}
	}

	private void setSubmenuVisibility(boolean visible) {
		setMenuTransition(submenuContainer, visible);
		setMenuTransition(headeredMenuView, !visible);
	}

	private void setMenuTransition(Widget widget, boolean transitionIn) {
		Dom.toggleClass(widget, "transitionIn", "transitionOut", transitionIn);
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
		for (MenuItem menuItem : menuItemGroup.getMenuItems()) {
			MenuItemView item = createMenuItemView(menuItem);
			item.setScheduledCommand(() -> menuActionRouter.handleMenuItem(menuItem));
			menuView.addItem(item);
		}
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
		GeoGebraTubeUser user = loginOperation.getModel().getLoggedInUser();
		if (user == null) {
			return AppResources.INSTANCE.empty();
		}
		return new ImageResourcePrototype("user_icon",
				UriUtils.fromString(user.getImageURL()),
				0, 0, 36, 36, false, false);
	}

	@Override
	public void onResize() {
		headerView.setVisible(frame.hasSmallWindowOrCompactHeader()
				&& activeMenu.getTitle() != null);
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent || event instanceof LogOutEvent) {
			setDefaultMenu();
		}
	}

	@Override
	public void setLabels() {
		updateMenu();
	}
}
