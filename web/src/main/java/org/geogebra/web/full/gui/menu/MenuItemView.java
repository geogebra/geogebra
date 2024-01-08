package org.geogebra.web.full.gui.menu;

import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.resources.client.ResourcePrototype;

class MenuItemView extends AriaMenuItem {

	MenuItemView(ResourcePrototype icon, String label) {
		this(icon, label, false);
	}

	MenuItemView(ResourcePrototype icon, String label, boolean isUserItem) {
		super(MainMenu.getMenuBarHtml(icon, label), true, (Scheduler.ScheduledCommand) null);
		addStyleName("menuItemView");
		setStyleName("userMenuItemView", isUserItem);
		if (icon == null) {
			addStyleName("noImage");
		}
	}
}
