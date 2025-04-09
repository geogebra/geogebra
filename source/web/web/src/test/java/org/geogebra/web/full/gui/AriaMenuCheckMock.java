package org.geogebra.web.full.gui;

public class AriaMenuCheckMock extends AriaMenuItemMock {
	private boolean checked = false;

	public AriaMenuCheckMock(String text) {
		super(text, null, () -> { });
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
