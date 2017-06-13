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
public class GCollapseMenuItem {

	private MenuItem menuItem;
	private FlowPanel itemPanel;
	private boolean expanded;
	private String text;
	private Image imgExpand;
	private Image imgCollapse;

	/**
	 * @param text
	 *            Title
	 * @param expandUrl
	 *            image of expand
	 * @param collapseUrl
	 *            image of collapse
	 * @param expanded
	 *            initial value.
	 * @param cmd
	 *            The command to run.
	 */
	public GCollapseMenuItem(String text, String expandUrl, String collapseUrl,
			boolean expanded,
			final ScheduledCommand cmd) {
		this.text = text;
		imgExpand = new NoDragImage(expandUrl);
		imgCollapse = new NoDragImage(collapseUrl);

		itemPanel = new FlowPanel();
		itemPanel.addStyleName("collapseMenuItem");
		menuItem = new MenuItem(itemPanel.toString(), true,
				new ScheduledCommand() {

					public void execute() {
						toggle();
						cmd.execute();
					}
				});
		setExpanded(expanded);
	}

	/**
	 * Sets the item checked/unchecked.
	 * 
	 * @param value
	 *            to set.
	 */
	public void setExpanded(boolean value) {
		expanded = value;
		itemPanel.clear();
		itemPanel.add(new HTML(text));
		itemPanel.add(expanded ? imgExpand : imgCollapse);
		menuItem.setHTML(itemPanel.toString());
	}



	/**
	 * 
	 * @return The standard menu item with checkmark.
	 */
	public MenuItem getMenuItem() {
		return menuItem;
	}

	/**
	 * 
	 * @return if the menu is expanded or not.
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Toggles the menu - expand/collapse.
	 */
	public void toggle() {
		setExpanded(!expanded);
	}


}
