/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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