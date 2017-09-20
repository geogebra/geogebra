package org.geogebra.web.web.javax.swing;

import org.geogebra.web.web.gui.menubar.MenuCommand;

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
	 * @param cmd
	 *            - command to execute
	 */
	public void setCommand(MenuCommand cmd) {
		menuItem.setCommand(cmd);
	}

	public void setCommand(Command command) {
		menuItem.setCommand(command);
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
