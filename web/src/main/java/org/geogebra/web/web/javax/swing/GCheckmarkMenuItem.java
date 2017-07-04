package org.geogebra.web.web.javax.swing;

import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.web.gui.menubar.MenuCommand;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Adds a menu item with a checkmark on its end.
 * 
 * @author laszlo
 * 
 */
public class GCheckmarkMenuItem {

	private MenuItem menuItem;
	private FlowPanel itemPanel;
	private boolean checked;
	private String text;
	private Image checkImg;

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
		this.text = text;
		checkImg = new NoDragImage(checkUrl);
		checkImg.addStyleName("checkImg");
		itemPanel = new FlowPanel();
		itemPanel.addStyleName("checkMarkMenuItem");
		menuItem = new MenuItem(itemPanel.toString(), true, cmd);
		setChecked(checked);
	}

	/**
	 * Sets the item checked/unchecked.
	 * 
	 * @param value
	 *            to set.
	 */
	public void setChecked(boolean value) {
		checked = value;
		itemPanel.clear();
		itemPanel.add(new HTML(text));
		if (checked) {
			itemPanel.add(checkImg);
		}
		menuItem.setHTML(itemPanel.toString());
	}


	/**
	 * 
	 * @return true if item is checked
	 */
	public boolean isChecked() {
		return checked;
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
}
