package org.geogebra.web.html5.gui.util;

import com.google.gwt.user.client.ui.FlowPanel;

public class CardPanel extends FlowPanel {
	private int selectedIndex;

	public CardPanel() {
		selectedIndex = 0;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int idx) {
		for (int i = 0; i < getWidgetCount(); i++) {
			getWidget(i).setVisible(i == idx);
		}
		selectedIndex = idx;
	}

}
