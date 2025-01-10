package org.geogebra.web.full.gui.menu;

import java.util.Collections;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.SubmenuItem;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.menu.action.MenuActionHandler;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.gwtproject.user.client.ui.Label;

class MenuActionRouter {

	private MenuActionHandler menuActionHandler;
	private MenuViewController menuViewController;
	private Localization localization;

	MenuActionRouter(MenuActionHandler menuActionHandler,
					 MenuViewController menuViewController,
					 Localization localization) {
		this.menuActionHandler = menuActionHandler;
		this.menuViewController = menuViewController;
		this.localization = localization;
	}

	void handleMenuItem(MenuItem menuItem) {
		if (menuItem instanceof ActionableItem) {
			handleAction(((ActionableItem) menuItem).getAction());
		} else if (menuItem instanceof SubmenuItem) {
			handleSubmenu((SubmenuItem) menuItem);
		}
	}

	private void handleAction(Action action) {
		menuActionHandler.executeMenuAction(action);
		menuViewController.setMenuVisible(false);
	}

	private void handleSubmenu(SubmenuItem submenuItem) {
		final MenuView menuView = new MenuView(menuViewController);
		menuViewController.setMenuItemGroups(menuView,
				Collections.singletonList(submenuItem.getGroup()));
		HeaderView headerView = menuViewController.createHeaderView();
		headerView.setCaption(localization.getMenu(submenuItem.getLabel()));
		headerView.getBackButton().addFastClickHandler(source ->
				menuViewController.hideSubmenuAndMoveFocus());
		HeaderedMenuView submenu = new HeaderedMenuView(menuView);
		if (submenuItem.getBottomText() != null) {
			Label version = BaseWidgetFactory.INSTANCE.newDisabledText(
					submenuItem.getBottomText(), "versionNumber");
			submenu.add(version);
		}
		submenu.setHeaderView(headerView);
		menuViewController.showSubmenu(submenu);
		menuView.selectItem(0);
		menuView.getSelectedItem().getElement().focus();
	}
}
