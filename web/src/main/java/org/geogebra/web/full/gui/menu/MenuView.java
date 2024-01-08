package org.geogebra.web.full.gui.menu;

import org.geogebra.web.html5.gui.Shades;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.VerticalPanel;
import org.gwtproject.user.client.ui.Widget;

class MenuView extends FlowPanel {

	private static final String MENU_VIEW_STYLE = "menuView";
	private static final String DIVIDER_STYLE = "divider";

	MenuView() {
		addStyleName(MENU_VIEW_STYLE);
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
		widget.addStyleName(DIVIDER_STYLE);
		widget.addStyleName(Shades.NEUTRAL_300.getName());
		super.add(widget);
	}
}
