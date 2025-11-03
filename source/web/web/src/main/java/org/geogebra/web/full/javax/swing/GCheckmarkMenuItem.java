package org.geogebra.web.full.javax.swing;

import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.resources.client.ResourcePrototype;

/**
 * Builds a menu item with optional icon, text and a checkmark
 */
public class GCheckmarkMenuItem {
	protected final GCheckMarkPanel panel;
	private final AriaMenuItem menuItem;

	/**
	 * @param img icon
	 * @param text title
	 * @param checked initial value
	 * @param cmd command to run
	 */
	public GCheckmarkMenuItem(ResourcePrototype img, String text, boolean checked,
			final ScheduledCommand cmd) {
		panel = new GCheckMarkPanel(text, img, checked);
		menuItem = newMenuItem(img == null);
		AriaHelper.setRole(menuItem, "menuitemcheckbox");
		AriaHelper.setChecked(menuItem, checked);
		setCommand(cmd);
	}

	/**
	 * @param text title
	 * @param checked initial value
	 * @param cmd command to run
	 */
	public GCheckmarkMenuItem(String text, boolean checked, final ScheduledCommand cmd) {
		this(null, text, checked, cmd);
	}

	protected AriaMenuItem newMenuItem(boolean hasIcon) {
		AriaMenuItem mi = new AriaMenuItem(panel, (ScheduledCommand) null);
		if (hasIcon) {
			mi.addStyleName("no-image");
		}
		return mi;
	}

	/**
	 * @param command to execute
	 */
	public void setCommand(ScheduledCommand command) {
		menuItem.setScheduledCommand(command);
	}

	public AriaMenuItem getMenuItem() {
		return menuItem;
	}

	/**
	 * Sets the state of the checkmark
	 * @param checked true if checked
	 */
	public void setChecked(boolean checked) {
		panel.setChecked(checked);
	}
}
