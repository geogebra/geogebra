package org.geogebra.web.full.html5;

import org.geogebra.web.full.gui.AriaMenuItemMock;

import com.google.gwt.core.client.Scheduler;

public class AriaMenuCheckMock extends AriaMenuItemMock {
	private boolean checked = false;
	public AriaMenuCheckMock(String html) {
		super(html, false, new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {

			}
		});
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
