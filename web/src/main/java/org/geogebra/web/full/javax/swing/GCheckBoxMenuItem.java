package org.geogebra.web.full.javax.swing;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Menu item with a checkbox (for new UI use the checkmark version)
 */
public class GCheckBoxMenuItem {

	private ComponentCheckbox checkBox;
	private AriaMenuItem menuItem;
	private FlowPanel itemPanel;

	/**
	 * @param icon - icon
	 * @param text - label
	 * @param app - application
	 */
	public GCheckBoxMenuItem(ResourcePrototype icon, String text, App app) {
		itemPanel = new FlowPanel();
		itemPanel.addStyleName("checkboxItem");
		itemPanel.getElement().appendChild(MainMenu.getImage(icon));
		checkBox = new ComponentCheckbox(app.getLocalization(), false, text, null);

		itemPanel.add(checkBox);
	}

	/**
	 * @param icon - icon
	 * @param text - label
	 * @param cmd - callback
	 * @param app - app
	 */
	public GCheckBoxMenuItem(ResourcePrototype icon, String text, final ScheduledCommand cmd,
			App app) {
		this(icon, text, app);
		setCommand(cmd);
	}

	/**
	 * @param cmd
	 *            command
	 */
	public void setCommand(ScheduledCommand cmd) {
		menuItem = new AriaMenuItem(itemPanel, cmd);
		menuItem.addStyleName("checkboxMenuItem");
		AriaHelper.setRole(menuItem, "menuitemcheckbox");
	}

	/**
	 * @param sel - whether this should be selected
	 */
	public void setSelected(boolean sel) {
		checkBox.setSelected(sel);
		AriaHelper.setChecked(menuItem, sel);
	}

	/**
	 * @return true if check box is checked
	 */
	public boolean isSelected() {
		return checkBox.isSelected();
	}

	/**
	 * @return wrapped item
	 */
	public AriaMenuItem getMenuItem() {
		return menuItem;
	}
}
