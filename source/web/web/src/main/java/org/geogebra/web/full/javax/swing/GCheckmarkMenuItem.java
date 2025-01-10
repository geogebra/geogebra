package org.geogebra.web.full.javax.swing;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.resources.client.ResourcePrototype;

/**
 * Adds a menu item with a checkmark on its end.
 * 
 * @author laszlo
 * 
 */
public class GCheckmarkMenuItem {

	protected final GCheckMarkPanel panel;
	private AriaMenuItem menuItem;

	/**
	 * @param text
	 *            Title
	 * @param checked
	 *            initial value.
	 * @param cmd
	 *            The command to run.
	 */
	public GCheckmarkMenuItem(ResourcePrototype img, String text, boolean checked,
			final ScheduledCommand cmd) {
		panel = new GCheckMarkPanel(text, img,
				MaterialDesignResources.INSTANCE.check_black(), checked);
		menuItem = newMenuItem();
		menuItem.getElement().setAttribute("role", "checkbox");
		AriaHelper.setChecked(menuItem, checked);
		setCommand(cmd);
	}

	protected AriaMenuItem newMenuItem() {
		return new AriaMenuItem(panel, (ScheduledCommand) null);
	}

	/**
	 * @param command
	 *            - command to execute
	 */
	public void setCommand(ScheduledCommand command) {
		menuItem.setScheduledCommand(command);
	}

	public AriaMenuItem getMenuItem() {
		return menuItem;
	}

	public void setChecked(boolean b) {
		panel.setChecked(b);
	}
}
