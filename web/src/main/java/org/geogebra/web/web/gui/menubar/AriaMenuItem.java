package org.geogebra.web.web.gui.menubar;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.SimplePanel;

public class AriaMenuItem extends SimplePanel {
	ScheduledCommand cmd;
	private AriaMenuBar submenu;
	public AriaMenuItem(String text, boolean asHTML, ScheduledCommand cmd) {
		super(Document.get().createLIElement());
		setContent(text, asHTML);
		this.cmd = cmd;
	}

	public AriaMenuItem(String text, boolean asHTML, AriaMenuBar submenu) {
		super(Document.get().createLIElement());
		setContent(text, asHTML);
		this.submenu = submenu;
	}

	private void setContent(String text, boolean asHTML) {
		if (asHTML) {
			getElement().setInnerHTML(text);
		} else {
			getElement().setInnerText(text);
		}
		getElement().setClassName("gwt-MenuItem listMenuItem");
		getElement().setAttribute("role", "menuitem");
		getElement().setTabIndex(0);

	}

	public ScheduledCommand getScheduledCommand() {
		return cmd;
	}

	public void setEnabled(boolean online) {
		// TODO Auto-generated method stub

	}

	public void setScheduledCommand(ScheduledCommand cmd) {
		this.cmd = cmd;
	}

	public AriaMenuBar getSubMenu() {
		return submenu;
	}

	public String getHTML() {
		return getElement().getInnerHTML();
	}

	public void setHTML(String string) {
		setContent(string, true);
	}
}
