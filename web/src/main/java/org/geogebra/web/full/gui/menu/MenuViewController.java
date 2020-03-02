package org.geogebra.web.full.gui.menu;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.gui.menu.impl.DefaultDrawerMenuFactory;
import org.geogebra.common.gui.menu.impl.ExamDrawerMenuFactory;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.menu.action.DefaultMenuActionHandler;
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
public class MenuViewController implements ResizeHandler {

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
		addHandler();
		onResize(null);
	}

	private void createObjects(AppWFull app) {
		localization = app.getLocalization();
		frame = app.getAppletFrame();
		menuIconResource = new MenuIconResource(app.isMebis()
				? MebisMenuIconProvider.INSTANCE : DefaultMenuIconProvider.INSTANCE);
		menuActionRouter = new MenuActionRouter(new DefaultMenuActionHandler(app),
				this, localization);
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

	private void createFactories(AppW app) {
		GeoGebraConstants.Version version = app.getConfig().getVersion();
		defaultDrawerMenuFactory = new DefaultDrawerMenuFactory(app.getPlatform(),
				version, hasLoginButton(app) ? app.getLoginOperation() : null, app.isExam());
		examDrawerMenuFactory = new ExamDrawerMenuFactory(version);
	}

	private boolean hasLoginButton(AppW app) {
		return app.getConfig().getVersion() != GeoGebraConstants.Version.SCIENTIFIC
				&& (!app.isMebis())
				&& app.enableFileFeatures()
				&& app.getLAF().hasLoginButton();
	}

	private void addHandler() {
		Window.addResizeHandler(this);
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
		setDrawerMenu(defaultDrawerMenuFactory.createDrawerMenu());
	}

	/**
	 * Sets the menu to exam.
	 */
	public void setExamMenu() {
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
		SVGResource icon = menuItem.getIcon() != null
				? menuIconResource.getImageResource(menuItem.getIcon()) : null;
		String label = localization.getMenu(menuItem.getLabel());
		MenuItemView view = new MenuItemView(icon, label);
		view.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				menuActionRouter.handleMenuItem(menuItem);
			}
		});
		parent.add(view);
	}

	@Override
	public void onResize(ResizeEvent event) {
		headerView.setVisible(frame.hasSmallWindowOrCompactHeader());
	}
}
