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

import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;

class MenuView extends AriaMenuBar {
	protected MenuViewController controller;

	MenuView(MenuViewController controller) {
		super();
		addStyleName("menuView");
		this.controller = controller;
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONKEYDOWN
				&& event.getKeyCode() == KeyCodes.KEY_ESCAPE) {
			if (controller.isSubMenu(getParent())) {
				controller.hideSubmenuAndMoveFocus();
			} else {
				controller.setMenuVisible(false);
			}
			event.stopPropagation();
			return;
		}
		super.onBrowserEvent(event);
	}
}
