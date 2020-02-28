package org.geogebra.web.full.gui.menu;

import com.google.gwt.user.client.ui.Widget;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.gui.menu.impl.DefaultDrawerMenuFactory;
import org.geogebra.common.gui.menu.impl.ExamDrawerMenuFactory;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.menu.action.BaseMenuActionHandlerFactory;
import org.geogebra.web.full.gui.menu.icons.DefaultMenuIconProvider;
import org.geogebra.web.full.gui.menu.icons.MebisMenuIconProvider;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import java.util.List;

/**
 * Controller for the main menu in the apps.
 */
public class MenuViewController {

	private MenuViewListener menuViewListener;

	private FloatingMenuView floatingMenuView;
	private MenuView menuView;

	private Localization localization;
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
	}

	private void createObjects(AppWFull app) {
		localization = app.getLocalization();
		menuIconResource = new MenuIconResource(app.isMebis()
				? MebisMenuIconProvider.INSTANCE : DefaultMenuIconProvider.INSTANCE);
		BaseMenuActionHandlerFactory actionProviderFactory = new BaseMenuActionHandlerFactory(app);
		menuActionRouter = new MenuActionRouter(actionProviderFactory.create());
	}

	private void createViews() {
		floatingMenuView = new FloatingMenuView();
		floatingMenuView.setVisible(false);

		menuView = new MenuView();
		floatingMenuView.add(menuView);
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
		setMenuItemGroups(defaultDrawerMenuFactory.createDrawerMenu().getMenuItemGroups());
	}

	/**
	 * Sets the menu to exam.
	 */
	public void setExamMenu() {
		setMenuItemGroups(examDrawerMenuFactory.createDrawerMenu().getMenuItemGroups());
	}

	/**
	 * Sets the menu visibility.
	 *
	 * @param visible true to show the menu
	 */
	public void setMenuVisible(boolean visible) {
		floatingMenuView.setVisible(visible);
		notifyMenuViewVisibilityChanged(visible);
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

	private void setMenuItemGroups(List<MenuItemGroup> menuItemGroups) {
		menuView.clear();
		for (MenuItemGroup group : menuItemGroups) {
			createMenuItemGroup(group);
		}
	}

	private void createMenuItemGroup(MenuItemGroup menuItemGroup) {
		String titleKey = menuItemGroup.getTitle();
		String title = titleKey == null ? null : localization.getMenu(titleKey);
		MenuItemGroupView view = new MenuItemGroupView(title);
		for (MenuItem menuItem : menuItemGroup.getMenuItems()) {
			createMenuItem(menuItem, view);
		}
		menuView.add(view);
	}

	private void createMenuItem(final MenuItem menuItem, MenuItemGroupView parent) {
		SVGResource icon = menuIconResource.getImageResource(menuItem.getIcon());
		String label = localization.getMenu(menuItem.getLabel());
		MenuItemView view = new MenuItemView(icon, label);
		view.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				setMenuVisible(false);
				menuActionRouter.handleMenuItem(menuItem);
			}
		});
		parent.add(view);
	}
}
