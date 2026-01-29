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

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.javax.swing.InlineTextToolbar;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.resources.client.ResourcePrototype;

/**
 * Factory to create popup menus.
 * @author laszlo
 */
public class ContextMenuItemFactory {

	/**
	 * @return list box, to be mocked
	 */
	public GPopupMenuW newPopupMenu(AppW app) {
		return new GPopupMenuW(app);
	}

	/**
	 * @param text
	 *            content
	 * @param icon
	 *            icon
	 * @param cmd
	 *            command to run when clicked
	 * @return a new AriaMenuItem instance.
	 *
	 */
	public AriaMenuItem newAriaMenuItem(ResourcePrototype icon, String text, ScheduledCommand cmd) {
		return MainMenu.getMenuBarItem(icon, text, cmd);
	}

	/**
	 * @param icon icon
	 * @param text text
	 * @param cmd command to run when clicked
	 * @return a new AriaMenuItem instance.
	 *
	 */
	public AriaMenuItem newAriaMenuItem(IconSpec icon, String text, ScheduledCommand cmd) {
		return MainMenu.getMenuBarItem(icon, text, cmd);
	}

	/**
	 *
	 * @param text menu text
	 * @param icon icon
	 * @param submenu Submenu if any.
	 * @return a new AriaMenuItem instance.
	 */
	public AriaMenuItem newAriaMenuItem(String text,
			@CheckForNull ResourcePrototype icon, AriaMenuBar submenu) {
		return new AriaMenuItem(text, icon, submenu);
	}

	/**
	 *
	 * @param inlines the drawable texts.
	 * @param app the application.
	 * @return toolbar for texts, sub/superscript, list styles.
	 */
	public AriaMenuItem newInlineTextToolbar(List<HasTextFormat> inlines, App app) {
		InlineTextToolbar toolbar = new InlineTextToolbar(inlines, app);
		AriaMenuItem toolbarItem = new AriaMenuItem(toolbar.getItem(), () -> {});
		toolbarItem.setStyleName("inlineTextToolbar");
		return toolbarItem;
	}

	/**
	 * @param icon icon
	 * @param title the title of the item.
	 * @param checked if the item should be checked by default
	 * @param command command
	 * @return the new checkmark capable item.
	 */
	public GCheckmarkMenuItem newCheckmarkMenuItem(IconSpec icon,
			String title, boolean checked, ScheduledCommand command) {
		return new GCheckmarkMenuItem(icon, title, checked, command);
	}
}
