package org.geogebra.web.web.javax.swing;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Adds a menu item with a checkmark on its end.
 * 
 * @author laszlo
 * 
 */
public class GCheckmarkMenuItem extends GCheckMarkPanel {

	private MenuItem menuItem;

	/**
	 * @param text
	 *            Title
	 * @param checkUrl
	 *            image of check mark
	 * @param checked
	 *            initial value.
	 * @param cmd
	 *            The command to run.
	 */
	public GCheckmarkMenuItem(String text, String checkUrl,
			boolean checked,
			final ScheduledCommand cmd) {
		super(text, checkUrl, checked, cmd);
	}

	/**
	 * @param text
	 *            Title
	 * @param checkUrl
	 *            image of check mark
	 * @param checked
	 *            initial value.
	 */
	public GCheckmarkMenuItem(String text, String checkUrl,
			boolean checked) {
		super(text, checkUrl, checked, null);
	}

	/**
	 * 
	 * @return The standard menu item with checkmark.
	 */
	public MenuItem getMenuItem() {
		return menuItem;
	}

	/**
	 * @param command
	 *            - command to execute
	 */
	public void setCommand(Command command) {
		menuItem.setScheduledCommand(command);
	}

	@Override
	protected void createContents() {
		menuItem = new MenuItem(itemPanel.toString(), true, getCmd());
	}

	@Override
	protected void updateContents() {
		menuItem.setHTML(itemPanel.toString());

	}
}
