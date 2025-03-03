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
	 *
	 * @param title the title of the item.
	 * @param checked if the item should be checked by default
	 * @return the new checkmark capable item.
	 */
	public GCheckmarkMenuItem newCheckmarkMenuItem(ResourcePrototype icon,
			String title, boolean checked, ScheduledCommand command) {
		return new GCheckmarkMenuItem(icon, title, checked, command);
	}
}
