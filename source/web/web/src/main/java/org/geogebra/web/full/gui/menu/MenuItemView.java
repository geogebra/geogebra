/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.menu;

import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.gwtproject.resources.client.ResourcePrototype;

final class MenuItemView {

	static AriaMenuItem create(ResourcePrototype icon, String label) {
		AriaMenuItem item = MainMenu.getMenuBarItem(icon, label, null);
		item.addStyleName("menuItemView");
		item.addStyleName("userMenuItemView");
		if (icon == null) {
			item.addStyleName("noImage");
		}
		return item;
	}

	static AriaMenuItem create(IconSpec icon, String label) {
		AriaMenuItem item = MainMenu.getMenuBarItem(icon, label, null);
		item.addStyleName("menuItemView");
		if (icon == null) {
			item.addStyleName("noImage");
		}
		return item;
	}
}
