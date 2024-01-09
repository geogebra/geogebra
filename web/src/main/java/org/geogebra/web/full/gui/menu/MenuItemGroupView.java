package org.geogebra.web.full.gui.menu;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.gwtproject.user.client.ui.Label;

class MenuItemGroupView extends AriaMenuItem {

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
