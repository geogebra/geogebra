package org.geogebra.web.full.html5;

import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;

public class GCheckmarkMenuItemMock extends GCheckmarkMenuItem {
	private AriaMenuCheckMock menuCheck;

	/**
	 * @param title title
	 * @param checked whether it's checked initially
	 */
	public GCheckmarkMenuItemMock(String title, boolean checked) {
		super(null, title, checked, null);
		setChecked(checked);
	}

	@Override
	protected AriaMenuItem newMenuItem() {
		menuCheck = new AriaMenuCheckMock(panel.getText());
		return menuCheck;
	}

	@Override
	public void setChecked(boolean value) {
		super.setChecked(value);
		menuCheck.setChecked(value);
	}

	public boolean isChecked() {
		return menuCheck.isChecked();
	}
}