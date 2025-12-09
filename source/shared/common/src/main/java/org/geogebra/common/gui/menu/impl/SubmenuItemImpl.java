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

package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.gui.menu.SubmenuItem;

class SubmenuItemImpl extends AbstractMenuItem implements SubmenuItem {

	private final MenuItemGroup group;
	private final String bottomText;

	SubmenuItemImpl(Icon icon, String label, String bottomText, ActionableItem... items) {
		super(icon, label);
		this.bottomText = bottomText;
		group = new MenuItemGroupImpl(items);
	}

	@Override
	public MenuItemGroup getGroup() {
		return group;
	}

	@Override
	public String getBottomText() {
		return bottomText;
	}
}
