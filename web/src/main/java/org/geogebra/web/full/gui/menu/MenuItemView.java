package org.geogebra.web.full.gui.menu;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.main.Localization;

public class MenuItemView extends FlowPanel {

	private static final String MENU_ITEM_VIEW_STYLE = "menuItemView";
	private static final String IMAGE_STYLE = "image";
	private static final String LABEL_STYLE = "label";

	private MenuIconResource resource;

	MenuItemView(MenuItem menuItem, Localization localization) {
		resource = new MenuIconResource();
		addStyleName(MENU_ITEM_VIEW_STYLE);
		createIcon(menuItem.getIcon());
		createLabel(menuItem.getLabel(), localization);
	}

	private void createIcon(Icon icon) {
		Image image = new Image();
		image.addStyleName(IMAGE_STYLE);
		image.setUrl(resource.getImageResource(icon).getSafeUri());
		add(image);
	}

	private void createLabel(String text, Localization localization) {
		Label label = new Label();
		label.setStyleName(LABEL_STYLE);
		label.setText(localization.getMenu(text));
		add(label);
	}
}
