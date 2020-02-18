package org.geogebra.web.full.gui.menu;

import com.google.gwt.user.client.ui.SimplePanel;

class FloatingMenuView extends SimplePanel {

	private static final String FLOATING_MENU_VIEW_STYLE = "floatingMenuView";

	FloatingMenuView() {
		addStyleName(FLOATING_MENU_VIEW_STYLE);
	}

	@Override
	public void setVisible(boolean visible) {
		setStyleName("transitionIn", visible);
		setStyleName("transitionOut", !visible);
	}
}
