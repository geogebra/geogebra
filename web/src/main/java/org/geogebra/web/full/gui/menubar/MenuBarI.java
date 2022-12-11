package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.safehtml.shared.annotations.IsSafeHtml;

/**
 * Shared interface for export and download menu
 *
 */
public interface MenuBarI {

	/**
	 * Hide this after item was clicked (if necessary)
	 */
	void hide();

	/**
	 * @param text
	 *            item heading
	 * @param asHTML
	 *            whether to use html
	 * @param cmd
	 *            callback
	 * @return menu item
	 */
	AriaMenuItem addItem(@IsSafeHtml String text, boolean asHTML,
			ScheduledCommand cmd);

}
