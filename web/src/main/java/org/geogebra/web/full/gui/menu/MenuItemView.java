package org.geogebra.web.full.gui.menu;

import org.geogebra.web.html5.gui.view.button.StandardButton;

import com.google.gwt.resources.client.ResourcePrototype;

class MenuItemView extends StandardButton {

	private static final String MENU_ITEM_VIEW_STYLE = "menuItemView";
	private static final String USER_MENU_ITEM_VIEW_STYLE = "userMenuItemView";

	MenuItemView(ResourcePrototype icon, String label) {
		this(icon, label, false);
	}

	MenuItemView(ResourcePrototype icon, String label, boolean isUserItem) {
		super(icon, label, isUserItem ? 36 : 24, isUserItem ? 36 : 24);
		addStyleName(MENU_ITEM_VIEW_STYLE);
		setStyleName(USER_MENU_ITEM_VIEW_STYLE, isUserItem);
	}
}
