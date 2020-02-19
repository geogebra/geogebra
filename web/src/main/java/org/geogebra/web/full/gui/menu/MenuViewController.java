package org.geogebra.web.full.gui.menu;

import com.google.gwt.user.client.ui.Widget;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.gui.menu.impl.DefaultDrawerMenuFactory;
import org.geogebra.common.gui.menu.impl.ExamDrawerMenuFactory;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.menu.action.DefaultMenuActionHandler;
import org.geogebra.web.full.gui.menu.icons.DefaultMenuIconProvider;
import org.geogebra.web.full.gui.menu.icons.MebisMenuIconProvider;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.resources.SVGResource;

import java.util.List;

public class MenuViewController {

	private MenuViewListener menuViewListener;

	private FloatingMenuView floatingMenuView;
	private MenuView menuView;

	private Localization localization;
	private MenuIconResource menuIconResource;
	private MenuActionRouter menuActionRouter;

	private DrawerMenuFactory defaultDrawerMenuFactory;
	private DrawerMenuFactory examDrawerMenuFactory;

	public MenuViewController(AppWFull app) {
		createObjects(app);
		createViews();
		createFactories(app);
		setDefaultMenu();
	}

	private void createObjects(AppWFull app) {
		localization = app.getLocalization();
		menuIconResource = new MenuIconResource(app.isMebis() ?
				MebisMenuIconProvider.INSTANCE : DefaultMenuIconProvider.INSTANCE);
		menuActionRouter = new MenuActionRouter(new DefaultMenuActionHandler(app));
	}

	private void createViews() {
		floatingMenuView = new FloatingMenuView();
		floatingMenuView.setVisible(false);

		menuView = new MenuView();
		floatingMenuView.add(menuView);
	}

	private void createFactories(App app) {
		GeoGebraConstants.Version version = app.getConfig().getVersion();
		defaultDrawerMenuFactory = new DefaultDrawerMenuFactory(app.getPlatform(),
				version, null, true);
		examDrawerMenuFactory = new ExamDrawerMenuFactory(version);
	}

	public void setMenuViewListener(MenuViewListener menuViewListener) {
		this.menuViewListener = menuViewListener;
	}

	public Widget getView() {
		return floatingMenuView;
	}

	public void setDefaultMenu() {
		setMenuItemGroups(defaultDrawerMenuFactory.createDrawerMenu().getMenuItemGroups());
	}

	public void setExamMenu() {
		setMenuItemGroups(examDrawerMenuFactory.createDrawerMenu().getMenuItemGroups());
	}

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
