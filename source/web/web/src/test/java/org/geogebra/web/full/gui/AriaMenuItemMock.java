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

package org.geogebra.web.full.gui;

import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.resources.client.ResourcePrototype;

public class AriaMenuItemMock extends AriaMenuItem {
	private String text;

	/** New menu item mock */
	public AriaMenuItemMock(String text, ResourcePrototype icon, AriaMenuBar submenu) {
		super(text, icon, submenu);
		setTextContent(text);
	}

	/** New menu item mock */
	public AriaMenuItemMock(String text, ResourcePrototype icon, Scheduler.ScheduledCommand cmd) {
		super(text, icon, cmd);
		setTextContent(text);
	}

	@Override
	public void setTextContent(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}
}
