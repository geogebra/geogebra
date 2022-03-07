package org.geogebra.web.full.html5;

import org.geogebra.web.full.gui.AriaMenuItemMock;

public class AriaMenuCheckMock extends AriaMenuItemMock {
	private boolean checked = false;

	public AriaMenuCheckMock(String html) {
		super(html, false, () -> { });
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
