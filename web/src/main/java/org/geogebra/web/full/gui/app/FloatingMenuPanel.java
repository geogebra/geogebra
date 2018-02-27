package org.geogebra.web.full.gui.app;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Floating menu panel
 */
public class FloatingMenuPanel extends FlowPanel {
	private GGWMenuBar menu;

	/**
	 * @param menu
	 *            main menu
	 */
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
		super.setVisible(b);
		menu.setVisible(b);
	}

}