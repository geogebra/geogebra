package org.geogebra.web.full.gui.menu;

import org.geogebra.web.html5.gui.Shades;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

class MenuView extends AriaMenuBar {

	MenuView() {
		super();
		addStyleName("menuView");
	}

	@Override
	public void add(Widget w) {
		if (getChildren().size() > 0) {
			createDivider();
		}
		super.add(w);
	}

	private void createDivider() {
		SimplePanel widget = new SimplePanel();
		widget.addStyleName("divider");
		widget.addStyleName(Shades.NEUTRAL_300.getName());
		super.add(widget);
	}
}
