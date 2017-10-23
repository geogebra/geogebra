package org.geogebra.web.web.gui.menubar;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;
import com.google.gwt.user.client.ui.MenuItem;

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
	public MenuItem addItem(@IsSafeHtml String text, boolean asHTML,
			ScheduledCommand cmd);

}
