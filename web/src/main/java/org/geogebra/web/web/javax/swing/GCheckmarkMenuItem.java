package org.geogebra.web.web.javax.swing;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.NoDragImage;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;

public class GCheckmarkMenuItem {

	MenuItem menuItem;
	FlowPanel itemPanel;
	boolean selected;
	private App app;
	String text;
	private Image img;

	/**
	 * 
	 * @param text
	 * @param url
	 * @param selected
	 * @param cmd
	 * @param app
	 */
	public GCheckmarkMenuItem(String text, String url, boolean selected,
			final ScheduledCommand cmd,
			App app) {
		this.text = text;
		this.app = app;
		img = new NoDragImage(url);
		itemPanel = new FlowPanel();
		itemPanel.addStyleName("checkMarkMenuItem");
		menuItem = new MenuItem(itemPanel.toString(), true, cmd);
		setSelected(selected);
	}

	public void setSelected(boolean sel) {
		selected = sel;
		itemPanel.clear();
		itemPanel.add(new HTML(text));
		if (selected) {
			itemPanel.add(img);
		}
		menuItem.setHTML(itemPanel.toString());
	}


	/**
	 * 
	 * @return true if item is checked
	 */
	public boolean isSelected() {
		return selected;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

}
