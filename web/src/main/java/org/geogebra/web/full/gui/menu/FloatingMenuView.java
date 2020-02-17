package org.geogebra.web.full.gui.menu;

import com.google.gwt.user.client.ui.SimplePanel;

public class FloatingMenuView extends SimplePanel {

	private static final String FLOATING_MENU_VIEW_STYLE = "floatingMenuView";

	public FloatingMenuView() {
		addStyleName(FLOATING_MENU_VIEW_STYLE);
	}

	public void setVisible(boolean visible) {
		setStyleName("transitionIn", visible);
		setStyleName("transitionOut", !visible);
	}
}
