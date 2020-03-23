package org.geogebra.web.full.gui.menu;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.gui.menu.impl.DefaultDrawerMenuFactory;
import org.geogebra.common.gui.menu.impl.ExamDrawerMenuFactory;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.menu.action.DefaultMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.ExamMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.MenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.action.ScientificMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.icons.DefaultMenuIconProvider;
import org.geogebra.web.full.gui.menu.icons.MebisMenuIconProvider;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import java.util.List;

/**
 * Controller for the main menu in the apps.
 */
public class MenuViewController implements ResizeHandler, EventRenderable {

	private MenuViewListener menuViewListener;

	private FloatingMenuView floatingMenuView;
	private HeaderedMenuView headeredMenuView;
	private HeaderView headerView;
	private MenuView menuView;

	private Localization localization;
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
	 *
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
		floatingMenuView = new FloatingMenuView();
		floatingMenuView.setVisible(false);

		headerView = createHeaderView();
		headerView.getBackButton().removeFromParent();
		menuView = new MenuView();
		headeredMenuView = new HeaderedMenuView(menuView);
		headeredMenuView.setHeaderView(headerView);
		headeredMenuView.setTitleHeader(true);
		floatingMenuView.setWidget(headeredMenuView);
	}

	private void createFactories(AppWFull app) {
		createDrawerMenuFactories(app);
		createActionHandlerFactories(app);
	}

	private void createDrawerMenuFactories(AppW app) {
		GeoGebraConstants.Version version = app.getConfig().getVersion();
		defaultDrawerMenuFactory =
				new DefaultDrawerMenuFactory(
						app.getPlatform(),
						version,
						hasLoginButton(app) ? app.getLoginOperation() : null,
						shouldCreateExamEntry(app));
		examDrawerMenuFactory = new ExamDrawerMenuFactory(version);
	}

	private void createActionHandlerFactories(AppWFull app) {
		GeoGebraConstants.Version version = app.getConfig().getVersion();
		if (version == GeoGebraConstants.Version.SCIENTIFIC) {
			defaultActionHandlerFactory = new ScientificMenuActionHandlerFactory(app);
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
	}

	/**
	 * Set the menu view listener.
	 *
	 * @param menuViewListener listener
	 */
	public void setMenuViewListener(MenuViewListener menuViewListener) {
		this.menuViewListener = menuViewListener;
	}

	/**
	 * Get the menu view.
	 *
	 * @return view
	 */
	public Widget getView() {
		return floatingMenuView;
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
		setHeaderCaption(drawerMenu.getTitle());
		setMenuItemGroups(menuView,
				drawerMenu.getMenuItemGroups());
	}

	/**
	 * Sets the menu visibility.
	 *
	 * @param visible true to show the menu
	 */
	public void setMenuVisible(boolean visible) {
		if (visible != floatingMenuView.isVisible()) {
			floatingMenuView.setVisible(visible);
			notifyMenuViewVisibilityChanged(visible);
			if (visible) {
				hideSubmenu();
			}
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

	void setMenuItemGroups(MenuView menuView, List<MenuItemGroup> menuItemGroups) {
		menuView.clear();
		for (MenuItemGroup group : menuItemGroups) {
			createMenuItemGroup(menuView, group);
		}
	}

	void showSubmenu(HeaderedMenuView headeredSubmenu) {
		floatingMenuView.clear();
		floatingMenuView.add(headeredSubmenu);
	}

	void hideSubmenu() {
		if (headeredMenuView.getParent() == null) {
			floatingMenuView.clear();
			floatingMenuView.add(headeredMenuView);
		}
	}

	HeaderView createHeaderView() {
		HeaderView headerView = new HeaderView();
		headerView.setElevated(false);
		headerView.setCompact(true);
		return headerView;
	}

	private void setHeaderCaption(String title) {
		String localizedTitle = localization.getMenu(title);
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
				0, 0,	36, 36, false, false);
	}

	@Override
	public void onResize(ResizeEvent event) {
		headerView.setVisible(frame.hasSmallWindowOrCompactHeader());
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
}
