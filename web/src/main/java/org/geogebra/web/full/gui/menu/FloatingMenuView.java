package org.geogebra.web.full.gui.menu;

import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.user.client.ui.SimplePanel;

class FloatingMenuView extends SimplePanel {

	private boolean isVisible;

	FloatingMenuView() {
		addStyleName("floatingMenuView");
	}

	@Override
	public void setVisible(boolean visible) {
		isVisible = visible;
		Dom.toggleClass(this, "transitionIn", "transitionOut", visible);
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}
}
