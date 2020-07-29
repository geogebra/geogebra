package org.geogebra.web.full.javax.swing;

import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;

/**
 * Adds a menu item with a checkmark on its end.
 * 
 * @author laszlo
 * 
 */
public class GCheckmarkMenuItem extends GCheckMarkPanel {

	private AriaMenuItem menuItem;

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
	public GCheckmarkMenuItem(String text, SVGResource checkUrl,
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
	public GCheckmarkMenuItem(String text, SVGResource checkUrl,
			boolean checked) {
		super(text, checkUrl, checked, null);
	}

	/**
	 * 
	 * @return The standard menu item with checkmark.
	 */
	public AriaMenuItem getMenuItem() {
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
		menuItem = newMenuItem();
		menuItem.getElement().setAttribute("role", "checkbox");
	}

	protected AriaMenuItem newMenuItem() {
		return new AriaMenuItem(getHTML(), true, getCmd());
	}

	@Override
	protected void updateContents() {
		menuItem.setHTML(getHTML());
	}

	@Override
	public void setChecked(boolean value) {
		super.setChecked(value);
		menuItem.getElement().setAttribute("aria-checked", isChecked() + "");
	}

}
