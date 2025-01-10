package org.geogebra.web.full.gui.menu;

import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.gwtproject.resources.client.ResourcePrototype;

final class MenuItemView {

	static AriaMenuItem create(ResourcePrototype icon, String label) {
		AriaMenuItem item = MainMenu.getMenuBarItem(icon, label, null);
		item.addStyleName("menuItemView");
		item.addStyleName("userMenuItemView");
		if (icon == null) {
			item.addStyleName("noImage");
		}
		return item;
	}

	static AriaMenuItem create(IconSpec icon, String label) {
		AriaMenuItem item = MainMenu.getMenuBarItem(icon, label, null);
		item.addStyleName("menuItemView");
		if (icon == null) {
			item.addStyleName("noImage");
		}
		return item;
	}
}
