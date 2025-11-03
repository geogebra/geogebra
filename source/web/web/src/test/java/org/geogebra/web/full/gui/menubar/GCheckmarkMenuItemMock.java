package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.gui.AriaMenuCheckMock;
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
	protected AriaMenuItem newMenuItem(boolean hasIcon) {
		menuCheck = new AriaMenuCheckMock(panel.getText());
		return menuCheck;
	}

	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
		menuCheck.setChecked(checked);
	}

	public boolean isChecked() {
		return menuCheck.isChecked();
	}
}