package org.geogebra.web.full.gui.menu;

import org.gwtproject.user.client.ui.SimplePanel;

class FloatingMenuView extends SimplePanel {

	private boolean isVisible;

	FloatingMenuView() {
		addStyleName("floatingMenuView");
	}

	@Override
	public void setVisible(boolean visible) {
		isVisible = visible;
		setStyleName("transitionIn", visible);
		setStyleName("transitionOut", !visible);
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}
}
