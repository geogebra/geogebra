package org.geogebra.web.html5.gui.util;

import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Collection of widgets, only one is shown
 *
 */
public class CardPanel extends FlowPanel {

	/**
	 * Show only selected card.
	 * 
	 * @param idx
	 *            selected index
	 */
	public void setSelectedIndex(int idx) {
		for (int i = 0; i < getWidgetCount(); i++) {
			getWidget(i).setVisible(i == idx);
		}
	}

}
