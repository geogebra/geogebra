package org.geogebra.web.full.html5;

import java.util.List;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.AriaMenuItemMock;
import org.geogebra.web.full.gui.ContextMenuFactory;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.javax.swing.InlineTextToolbar;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;

public class MenuFactory extends ContextMenuFactory {
	public MenuFactory(AppW app) {
		super();
	}

	@Override
	public GPopupMenuW newPopupMenu(AppW app) {
		return new GPopupMenuWMock(app);
	}

	@Override
	public InlineTextToolbar newInlineTextToolbar(List<HasTextFormat> inlines, App app) {
		return new InlineTextToolbarMock(inlines, app);
	}

	@Override
	public AriaMenuItem newAriaMenuItem(String text, boolean asHTML, Scheduler.ScheduledCommand cmd) {
		return new AriaMenuItemMock(text, asHTML, cmd);
	}

	@Override
	public AriaMenuItem newAriaMenuItem(String text, boolean asHtml, AriaMenuBar submenu) {
		return new AriaMenuItemMock(text, asHtml, submenu);
	}

	@Override
	public GCheckmarkMenuItem newCheckmarkMenuItem(String title,
			boolean checked) {
		return new GCheckmarkMenuItemMock(title, checked);
	}
}
