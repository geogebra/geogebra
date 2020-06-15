package org.geogebra.web.full.gui;

import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;

import com.google.gwt.core.client.Scheduler;

public class AriaMenuItemMock extends AriaMenuItem {
	private String text;

	public AriaMenuItemMock() {
	}

	public AriaMenuItemMock(String text, boolean asHtml, AriaMenuBar submenu) {
		super(text, asHtml, submenu);
		setContent(text, false);
	}

	public AriaMenuItemMock(String text, boolean asHTML, Scheduler.ScheduledCommand cmd) {
		super(text, asHTML, cmd);
		setContent(text, false);
	}

	@Override
	public void setContent(String text, boolean asHTML) {
		this.text = text;
		super.setContent(text, asHTML);
	}

	@Override
	public String getHTML() {
		return text;
	}
}
