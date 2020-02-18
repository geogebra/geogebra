package org.geogebra.web.full.gui.menu;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.main.Localization;

import java.util.List;

class MenuItemGroupView extends VerticalPanel {

	private static final String MENU_ITEM_GROUP_VIEW_STYLE = "menuItemGroupView";
	private static final String GROUP_LABEL_STYLE = "groupLabel";

	MenuItemGroupView(MenuItemGroup menuItemGroup, Localization localization,
					  MenuIconResource menuIconResource) {
		setStyleName(MENU_ITEM_GROUP_VIEW_STYLE);
		setHorizontalAlignment(ALIGN_LOCALE_START);
		createTitle(menuItemGroup.getTitle(), localization);
		createMenuItems(menuItemGroup.getMenuItems(), localization, menuIconResource);
	}

	private void createTitle(String title, Localization localization) {
		Label label = new Label();
		label.setText(localization.getMenu(title));
		label.setStyleName(GROUP_LABEL_STYLE);
		add(label);
	}

	private void createMenuItems(List<MenuItem> menuItems, Localization localization,
								 MenuIconResource menuIconResource) {
		for (MenuItem menuItem : menuItems) {
			createMenuItem(menuItem, localization, menuIconResource);
		}
	}

	private void createMenuItem(MenuItem menuItem, Localization localization,
								MenuIconResource menuIconResource) {
		MenuItemView view = new MenuItemView(menuItem, localization, menuIconResource);
		add(view);
	}
}
