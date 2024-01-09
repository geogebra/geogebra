package org.geogebra.web.full.gui.menu;

import org.geogebra.web.html5.gui.Shades;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.gwtproject.user.client.ui.SimplePanel;

class MenuView extends AriaMenuBar {

	MenuView() {
		super();
		addStyleName("menuView");
	}

	private void createDivider() {
		SimplePanel widget = new SimplePanel();
		widget.addStyleName("divider");
		widget.addStyleName(Shades.NEUTRAL_300.getName());
		add(widget);
	}
}
