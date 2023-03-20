package org.geogebra.web.full.gui.menu;

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
		Label label = new Label();
		label.setStyleName(GROUP_LABEL_STYLE);
		label.setText(text);
		label.setVisible(text != null);
		add(label);
	}
}
