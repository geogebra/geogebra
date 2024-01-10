package org.geogebra.web.full.gui.menu;

import org.geogebra.web.html5.gui.Shades;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.SimplePanel;

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
			controller.setMenuVisible(!isVisible());
		}
		super.onBrowserEvent(event);
	}

	private void createDivider() {
		SimplePanel widget = new SimplePanel();
		widget.addStyleName("divider");
		widget.addStyleName(Shades.NEUTRAL_300.getName());
		add(widget);
	}
}
