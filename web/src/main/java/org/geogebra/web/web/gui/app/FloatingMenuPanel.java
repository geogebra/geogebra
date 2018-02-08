package org.geogebra.web.web.gui.app;

import com.google.gwt.user.client.ui.FlowPanel;

public class FloatingMenuPanel extends FlowPanel {
	private GGWMenuBar menu;

	public FloatingMenuPanel(GGWMenuBar menu) {
		addStyleName("floatingMenu");
		this.menu = menu;
		add(menu);
	}

	/**
	 * focus in deferred way.
	 */
	public void focusDeferred() {
		menu.focusDeferred();
	}

	@Override
	public void setVisible(boolean b) {
		menu.setVisible(b);
	}

}