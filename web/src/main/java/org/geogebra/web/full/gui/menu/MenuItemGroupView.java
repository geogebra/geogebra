package org.geogebra.web.full.gui.menu;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.VerticalPanel;

class MenuItemGroupView extends VerticalPanel {

	private static final String MENU_ITEM_GROUP_VIEW_STYLE = "menuItemGroupView";
	private static final String GROUP_LABEL_STYLE = "groupLabel";

	MenuItemGroupView(String title) {
		setStyleName(MENU_ITEM_GROUP_VIEW_STYLE);
		setHorizontalAlignment(ALIGN_LOCALE_START);
		createTitle(title);
	}

	private void createTitle(String text) {
		Label label = BaseWidgetFactory.INSTANCE.newSecondaryText(text, GROUP_LABEL_STYLE);
		label.setVisible(text != null);
		add(label);
	}
}
