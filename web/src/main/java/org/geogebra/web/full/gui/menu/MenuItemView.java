package org.geogebra.web.full.gui.menu;

import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.gwtproject.resources.client.ResourcePrototype;

final class MenuItemView {

	static AriaMenuItem create(ResourcePrototype icon, String label, boolean isUserItem) {
		AriaMenuItem item = MainMenu.getMenuBarItem(icon, label, null);
		item.addStyleName("menuItemView");
		item.setStyleName("userMenuItemView", isUserItem);
		if (icon == null) {
			item.addStyleName("noImage");
		}
		return item;
	}
}
