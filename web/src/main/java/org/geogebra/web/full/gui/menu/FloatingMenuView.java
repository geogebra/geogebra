package org.geogebra.web.full.gui.menu;

import org.gwtproject.user.client.ui.SimplePanel;

class FloatingMenuView extends SimplePanel {

	private static final String FLOATING_MENU_VIEW_STYLE = "floatingMenuView";
	private static final String TRANSITION_IN_STYLE = "transitionIn";
	private static final String TRANSITION_OUT_STYLE = "transitionOut";

	private boolean isVisible;

	FloatingMenuView() {
		addStyleName(FLOATING_MENU_VIEW_STYLE);
	}

	@Override
	public void setVisible(boolean visible) {
		isVisible = visible;
		setStyleName(TRANSITION_IN_STYLE, visible);
		setStyleName(TRANSITION_OUT_STYLE, !visible);
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}
}
