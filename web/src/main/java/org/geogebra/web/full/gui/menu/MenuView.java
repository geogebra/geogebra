package org.geogebra.web.full.gui.menu;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.main.Localization;

import java.util.List;

public class MenuView extends VerticalPanel {

	private static final String MENU_VIEW_STYLE = "menuView";
	private static final String DIVIDER_STYLE = "divider";

	private Localization localization;

	public MenuView(Localization localization) {
		this.localization = localization;
		addStyleName(MENU_VIEW_STYLE);
	}

	public void setMenuItemGroups(List<MenuItemGroup> menuItemGroups) {
		clear();
		for (int index = 0; index < menuItemGroups.size(); index++ ) {
			if (index != 0) {
				createDivider();
			}
			MenuItemGroup menuItemGroup = menuItemGroups.get(index);
			createMenuItemGroup(menuItemGroup);
		}
	}

	private void createMenuItemGroup(MenuItemGroup menuItemGroup) {
		MenuItemGroupView view = new MenuItemGroupView(menuItemGroup, localization);
		add(view);
	}

	private void createDivider() {
		SimplePanel widget = new SimplePanel();
		widget.addStyleName(DIVIDER_STYLE);
		add(widget);
	}
}
