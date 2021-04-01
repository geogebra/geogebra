package org.geogebra.web.full.gui;

import java.util.List;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.main.App;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.javax.swing.InlineTextToolbar;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * Factory to create popup menus.
 * @author laszlo
 */
public class ContextMenuFactory {

	/**
	 * @return list box, to be mocked
	 */
	public GPopupMenuW newPopupMenu(AppW app) {
		return new GPopupMenuW(app);
	}

	/**
	 * @param text
	 *            content
	 * @param asHTML
	 *            whether to use it as raw HTML
	 * @param cmd
	 *            command to run when clicked
	 * @return a new AriaMenuItem instance.
	 *
	 */
	public AriaMenuItem newAriaMenuItem(String text, boolean asHTML, ScheduledCommand cmd) {
		return new AriaMenuItem(text, asHTML, cmd);
	}

	/**
	 *
	 * @param text menu text
	 * @param asHtml indicates if text is a html one.
	 * @param submenu Submenu if any.0
	 * @return a new AriaMenuItem instance.
	 */
	public AriaMenuItem newAriaMenuItem(String text, boolean asHtml, AriaMenuBar submenu) {
		return new AriaMenuItem(text, asHtml, submenu);
	}

	/**
	 *
	 * @param inlines the drawable texts.
	 * @param app the application.
	 * @return toolbar for texts, sub/superscript, list styles.
	 */
	public InlineTextToolbar newInlineTextToolbar(List<HasTextFormat> inlines, App app) {
		return new InlineTextToolbar(inlines, new AriaMenuItem(), app);
	}

	/**
	 *
	 * @param title the title of the item.
	 * @param checked if the item should be checked by default
	 * @return the new checkmark capable item.
	 */
	public GCheckmarkMenuItem newCheckmarkMenuItem(String title, boolean checked) {
		return new GCheckmarkMenuItem(title, checked);
	}
}
