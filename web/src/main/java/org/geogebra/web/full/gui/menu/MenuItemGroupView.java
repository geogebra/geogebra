package org.geogebra.web.full.gui.menu;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

class MenuItemGroupView extends FlowPanel {

	MenuItemGroupView(String title) {
		setStyleName("menuItemGroupView");
		createTitle(title);
	}

	private void createTitle(String text) {
		Label label = BaseWidgetFactory.INSTANCE.newSecondaryText(text, "groupLabel");
		label.setVisible(text != null);
		add(label);
	}
}
