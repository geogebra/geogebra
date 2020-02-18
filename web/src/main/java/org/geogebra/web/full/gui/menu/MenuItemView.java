package org.geogebra.web.full.gui.menu;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import org.geogebra.web.resources.SVGResource;

class MenuItemView extends FlowPanel {

	private static final String MENU_ITEM_VIEW_STYLE = "menuItemView";
	private static final String IMAGE_STYLE = "image";
	private static final String LABEL_STYLE = "label";

	MenuItemView(SVGResource icon, String label) {
		addStyleName(MENU_ITEM_VIEW_STYLE);
		createIcon(icon);
		createLabel(label);
	}

	private void createIcon(SVGResource icon) {
		Image image = new Image();
		image.addStyleName(IMAGE_STYLE);
		image.setUrl(icon.getSafeUri());
		add(image);
	}

	private void createLabel(String text) {
		Label label = new Label();
		label.setStyleName(LABEL_STYLE);
		label.setText(text);
		add(label);
	}
}
