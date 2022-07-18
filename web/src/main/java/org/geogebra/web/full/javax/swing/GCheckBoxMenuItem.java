package org.geogebra.web.full.javax.swing;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Menu item with a checkbox (for new UI use the checkmark version)
 */
public class GCheckBoxMenuItem {

	private ComponentCheckbox checkBox;
	private AriaMenuItem menuItem;
	private HorizontalPanel itemPanel;
	private boolean selected;
	private App app;
	private boolean isHtml;
	private String text;
	private boolean forceCheckbox = false;

	/**
	 * @param text
	 *            label
	 * @param isHtml
	 *            whether do treat text as raw HTML
	 * @param app
	 *            application
	 */
	public GCheckBoxMenuItem(String text,
			boolean isHtml, App app) {
		this.text = text;
		this.isHtml = isHtml;
		this.app = app;

		itemPanel = new HorizontalPanel();
		checkBox = new ComponentCheckbox(app.getLocalization(), false, text, null);
		if (!app.isUnbundled()) {
			itemPanel.add(checkBox);
		}
	}

	/**
	 * @param text
	 *            label
	 * @param cmd
	 *            callback
	 * @param isHtml
	 *            whether to use text as HTML
	 * @param app
	 *            app
	 */
	public GCheckBoxMenuItem(String text, final ScheduledCommand cmd,
			boolean isHtml, App app) {
		this(text, isHtml, app);
		setCommand(cmd);
	}

	/**
	 * @param cmd
	 *            command
	 */
	public void setCommand(ScheduledCommand cmd) {
		menuItem = new AriaMenuItem(itemPanel.toString(), true, cmd);
	}

	/**
	 * @param sel
	 *            whether this should be selected
	 * @param menu
	 *            parent menu to update
	 */
	public void setSelected(boolean sel, AriaMenuBar menu) {
		selected = sel;
		checkBox.setSelected(sel);
		String html = itemPanel.toString();
		menuItem.setHTML(html);
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

	/*private void setText(String text) {
		Widget w = isHtml ? new HTML(text) : new Label(text);
		itemPanel.add(w);
	}*/

	/**
	 * @param forceCheckbox
	 *            whether this is a checkbox rather than toggle menu item
	 */
	/*public void setForceCheckbox(boolean forceCheckbox) {
		this.forceCheckbox = forceCheckbox;
		checkBox.setVisible(!isToggleMenu());
	}*/
}
