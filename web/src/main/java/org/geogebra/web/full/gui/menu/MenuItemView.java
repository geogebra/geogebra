package org.geogebra.web.full.gui.menu;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.resources.client.ResourcePrototype;

class MenuItemView extends AriaMenuItem {

	private static final String MENU_ITEM_VIEW_STYLE = "menuItemView";
	private static final String USER_MENU_ITEM_VIEW_STYLE = "userMenuItemView";

	MenuItemView(ResourcePrototype icon, String label) {
		this(icon, label, false);
	}

	MenuItemView(ResourcePrototype icon, String label, boolean isUserItem) {
		super(MainMenu.getMenuBarHtml(icon, label), true, (Scheduler.ScheduledCommand) null);
		//super(icon, label, isUserItem ? 36 : 24, isUserItem ? 36 : 24);
		addStyleName(MENU_ITEM_VIEW_STYLE);
		setStyleName(USER_MENU_ITEM_VIEW_STYLE, isUserItem);
	}
}
