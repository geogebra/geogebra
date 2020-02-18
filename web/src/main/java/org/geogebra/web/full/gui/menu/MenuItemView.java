package org.geogebra.web.full.gui.menu;

import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.resources.SVGResource;

class MenuItemView extends StandardButton {

	private static final String MENU_ITEM_VIEW_STYLE = "menuItemView";

	MenuItemView(SVGResource icon, String label) {
		super(icon, label, 24, null);
		addStyleName(MENU_ITEM_VIEW_STYLE);
	}
}
