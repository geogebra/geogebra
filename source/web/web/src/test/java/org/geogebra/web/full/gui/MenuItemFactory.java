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

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.menubar.GCheckmarkMenuItemMock;
import org.geogebra.web.full.gui.menubar.GPopupMenuWMock;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.resources.client.ResourcePrototype;

public class MenuItemFactory extends ContextMenuItemFactory {
	public MenuItemFactory(AppW app) {
		super();
	}

	@Override
	public GPopupMenuW newPopupMenu(AppW app) {
		return new GPopupMenuWMock(app);
	}

	@Override
	public AriaMenuItem newInlineTextToolbar(List<HasTextFormat> inlines, App app) {
		return new AriaMenuItemMock("TEXTTOOLBAR", (ResourcePrototype) null, () -> {});
	}

	@Override
	public AriaMenuItem newAriaMenuItem(String text, ResourcePrototype icon, AriaMenuBar submenu) {
		return new AriaMenuItemMock(text, icon, submenu);
	}

	@Override
	public AriaMenuItem newAriaMenuItem(IconSpec icon, String text,
			Scheduler.ScheduledCommand cmd) {
		return new AriaMenuItemMock(text, icon, cmd);
	}

	@Override
	public AriaMenuItem newAriaMenuItem(ResourcePrototype icon, String text,
			Scheduler.ScheduledCommand cmd) {
		return new AriaMenuItemMock(text, icon, cmd);
	}

	@Override
	public GCheckmarkMenuItem newCheckmarkMenuItem(IconSpec icon,
			String title, boolean checked, Scheduler.ScheduledCommand command) {
		return new GCheckmarkMenuItemMock(title, checked);
	}
}
