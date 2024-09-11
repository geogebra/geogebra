package org.geogebra.web.full.gui;

import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.resources.client.ResourcePrototype;

public class AriaMenuItemMock extends AriaMenuItem {
	private String text;

	/** New menu item mock */
	public AriaMenuItemMock(String text, ResourcePrototype icon, AriaMenuBar submenu) {
		super(text, icon, submenu);
		setTextContent(text);
	}

	/** New menu item mock */
	public AriaMenuItemMock(String text, ResourcePrototype icon, Scheduler.ScheduledCommand cmd) {
		super(text, icon, cmd);
		setTextContent(text);
	}

	@Override
	public void setTextContent(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}
}
