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

package org.geogebra.web.full.javax.swing;

import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.resources.client.ResourcePrototype;

/**
 * Builds a menu item with optional icon, text and a checkmark
 */
public class GCheckmarkMenuItem {
	protected final GCheckMarkPanel panel;
	private final AriaMenuItem menuItem;

	/**
	 * @param img icon
	 * @param text title
	 * @param checked initial value
	 * @param cmd command to run
	 */
	public GCheckmarkMenuItem(ResourcePrototype img, String text, boolean checked,
			final ScheduledCommand cmd) {
		panel = new GCheckMarkPanel(text, img, checked);
		menuItem = newMenuItem(img == null);
		AriaHelper.setRole(menuItem, "menuitemcheckbox");
		AriaHelper.setChecked(menuItem, checked);
		setCommand(cmd);
	}

	/**
	 * @param img icon
	 * @param text title
	 * @param checked initial value
	 * @param cmd command to run
	 */
	public GCheckmarkMenuItem(IconSpec img, String text, boolean checked,
			final ScheduledCommand cmd) {
		panel = new GCheckMarkPanel(text, img, checked);
		menuItem = newMenuItem(img == null);
		AriaHelper.setRole(menuItem, "menuitemcheckbox");
		AriaHelper.setChecked(menuItem, checked);
		setCommand(cmd);
	}

	/**
	 * @param text title
	 * @param checked initial value
	 * @param cmd command to run
	 */
	public GCheckmarkMenuItem(String text, boolean checked, final ScheduledCommand cmd) {
		this((ResourcePrototype) null, text, checked, cmd);
	}

	protected AriaMenuItem newMenuItem(boolean hasIcon) {
		AriaMenuItem mi = new AriaMenuItem(panel, (ScheduledCommand) null);
		if (hasIcon) {
			mi.addStyleName("no-image");
		}
		return mi;
	}

	/**
	 * @param command to execute
	 */
	public void setCommand(ScheduledCommand command) {
		menuItem.setScheduledCommand(command);
	}

	public AriaMenuItem getMenuItem() {
		return menuItem;
	}

	/**
	 * Sets the state of the checkmark
	 * @param checked true if checked
	 */
	public void setChecked(boolean checked) {
		panel.setChecked(checked);
	}
}
