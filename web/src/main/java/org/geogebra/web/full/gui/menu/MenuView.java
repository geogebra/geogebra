package org.geogebra.web.full.gui.menu;

import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;

class MenuView extends AriaMenuBar {
	protected MenuViewController controller;

	MenuView(MenuViewController controller) {
		super();
		addStyleName("menuView");
		this.controller = controller;
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONKEYDOWN
				&& event.getKeyCode() == KeyCodes.KEY_ESCAPE) {
			if (controller.isSubMenu(getParent())) {
				controller.hideSubmenuAndMoveFocus();
			} else {
				controller.setMenuVisible(false);
			}
			event.stopPropagation();
			return;
		}
		super.onBrowserEvent(event);
	}
}
