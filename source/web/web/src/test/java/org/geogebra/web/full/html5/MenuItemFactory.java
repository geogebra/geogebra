package org.geogebra.web.full.html5;

import java.util.List;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.AriaMenuItemMock;
import org.geogebra.web.full.gui.ContextMenuItemFactory;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
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
		return new AriaMenuItemMock("TEXTTOOLBAR", null, () -> {});
	}

	@Override
	public AriaMenuItem newAriaMenuItem(String text, ResourcePrototype icon, AriaMenuBar submenu) {
		return new AriaMenuItemMock(text, icon, submenu);
	}

	@Override
	public AriaMenuItem newAriaMenuItem(ResourcePrototype icon, String text,
			Scheduler.ScheduledCommand cmd) {
		return new AriaMenuItemMock(text, icon, cmd);
	}

	@Override
	public GCheckmarkMenuItem newCheckmarkMenuItem(ResourcePrototype icon,
			String title, boolean checked, Scheduler.ScheduledCommand command) {
		return new GCheckmarkMenuItemMock(title, checked);
	}
}
