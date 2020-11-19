package org.geogebra.web.full.gui.menubar;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Radio button widget for the RadioButton menu
 *
 * @author gabor
 */
public class GradioButtonBase {

	/**
	 * radiobutton for the radiobutton menu
	 */
	RadioButton radio;
	/**
	 * panel to hold the button and the menu's html
	 */
	FlowPanel itemPanel;
	private String command;

	/**
	 * @param html
	 *            Html of the menu
	 * @param cmd
	 *            command associated with this item
	 * @param groupName
	 *            group name
	 */
	public GradioButtonBase(String html, String cmd, String groupName) {
		radio = new RadioButton(groupName);
		command = cmd;
		itemPanel = new FlowPanel();
		itemPanel.add(radio);
		itemPanel.add(new HTML(html));
    }

	/**
	 * @return gets the safe html of the menuitem
	 */
	public String getSafeHtmlString() {
		return itemPanel.getElement().getInnerHTML();
	}

	/**
	 * @return command associated with this radio item
	 */
	public String getActionCommand() {
		return command;
	}
}
