package org.geogebra.web.web.javax.swing;

import org.geogebra.web.html5.gui.util.NoDragImage;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
	private Image img;

	/**
	 * 
	 * @param text
	 *            Title
	 * @param url
	 *            Chekmark url
	 * @param checked
	 *            initial value.
	 * @param cmd
	 *            The command to run.
	 */
	public GCheckmarkMenuItem(String text, String url, boolean checked,
			final ScheduledCommand cmd) {
		this.text = text;
		img = new NoDragImage(url);
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
			itemPanel.add(img);
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

}
