/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.menu;

import java.util.List;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.gui.menu.impl.DefaultDrawerMenuFactory;
import org.geogebra.common.gui.menu.impl.ExamDrawerMenuFactory;
import org.geogebra.common.gui.menu.impl.ExternalDrawerMenuFactory;
import org.geogebra.common.gui.menu.impl.MebisDrawerMenuFactory;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.StringUtil;
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
import org.geogebra.web.full.gui.menu.icons.MenuIconResource;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
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
		menuIconResource = new MenuIconResource(app.isUsingFontAwesome()
				? new MebisMenuIconProvider() : new DefaultMenuIconProvider());
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
		examDrawerMenuFactory = new ExamDrawerMenuFactory(version, app.isSuite());
		examDrawerMenuFactory.setCreatesExitExam(!app.isLockedExam());
		if (!GlobalScope.examController.isExamActive()) {
			setDefaultMenu();
		} else {
			setExamMenu();
		}
	}

	private DrawerMenuFactory createDefaultMenuFactory(AppW app,
			GeoGebraConstants.Version version) {
		if (app.isByCS()) {
			return new MebisDrawerMenuFactory(app.getPlatform(), version, app.getLoginOperation(),
					app.enableFileFeatures());
		} else {
			boolean addAppSwitcher = app.isSuite();
			String versionStr = GeoGebraConstants.getVersionString6();
			String versionString = app.getLocalization().getPlainDefault("VersionA",
					"Version %0", versionStr);
			if (app.getLAF().hasHelpMenu()) {
				return new DefaultDrawerMenuFactory(
						app.getPlatform(),
						version, versionString,
						hasLoginButton(app) ? app.getLoginOperation() : null,
						shouldCreateExamEntry(app),
						app.enableFileFeatures(),
						addAppSwitcher,
						shouldCreateSwitchCalcEntry(app));
			}
			return new ExternalDrawerMenuFactory(
					app.getPlatform(),
					version, versionString,
					hasLoginButton(app) ? app.getLoginOperation() : null,
					shouldCreateExamEntry(app),
					app.enableFileFeatures(),
					addAppSwitcher,
					shouldCreateSwitchCalcEntry(app));
		}
	}

	private void createActionHandlerFactories(AppWFull app) {
		GeoGebraConstants.Version version = app.getConfig().getVersion();
		if (version == GeoGebraConstants.Version.SCIENTIFIC && !app.isSuite()) {
			defaultActionHandlerFactory = new ScientificMenuActionHandlerFactory(app);
		} else if (app.isSuite()) {
			defaultActionHandlerFactory = new SuiteMenuActionHandlerFactory(app);
		} else if (app.isByCS()) {
			defaultActionHandlerFactory = new MebisMenuActionHandlerFactory(app);
		} else {
			defaultActionHandlerFactory = new DefaultMenuActionHandlerFactory(app);
		}
		examActionHandlerFactory = new ExamMenuActionHandlerFactory(app);
	}

	private boolean shouldCreateExamEntry(AppW app) {
		return (app.getConfig().hasExam() && app.getLAF().isOfflineExamSupported())
				|| !StringUtil.empty(app.getAppletParameters().getParamExamLaunchURL());
	}

	private boolean shouldCreateSwitchCalcEntry(AppW app) {
		ExamType examType = ExamType.byName(app.getAppletParameters().getParamFeatureSet());
		return examType == null || GlobalScope.getEnabledSubAppsFor(examType).size() > 1;
	}

	private boolean hasLoginButton(AppW app) {
		return (app.getConfig().getVersion() != GeoGebraConstants.Version.SCIENTIFIC
				|| app.isSuite())
				&& !app.isByCS()
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

	/**
	 * TODO move the caller into controller
	 * @param widget widget
	 * @return whether the widget is the submenu
	 */
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
			menuView.focusSelectedItem();
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
				menuView.add(BaseWidgetFactory.INSTANCE.newDivider(false));
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
			submenuContainer.setWidget(null);
			menuView.selectItem(menuView.getSelectedIndex());
			menuView.focusSelectedItem();
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
		HeaderView headerView = new HeaderView(frame.getApp());
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
			AriaMenuItem item = createMenuItemView(menuItem);
			item.setScheduledCommand(() -> menuActionRouter.handleMenuItem(menuItem));
			menuView.addItem(item);
		}
	}

	private AriaMenuItem createMenuItemView(MenuItem menuItem) {
		if (menuItem.getIcon() == Icon.USER_ICON) {
			return MenuItemView.create(getUserImage(), menuItem.getLabel());
		} else {
			IconSpec icon = menuIconResource.getImageResource(menuItem.getIcon());
			String label = localization.getMenu(menuItem.getLabel());
			return MenuItemView.create(icon, label);
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
