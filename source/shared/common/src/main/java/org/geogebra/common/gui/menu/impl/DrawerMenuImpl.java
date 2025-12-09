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

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.MenuItemGroup;

class DrawerMenuImpl implements DrawerMenu {

	private String title;
	private List<MenuItemGroup> groups;

	DrawerMenuImpl(String title, MenuItemGroup... groups) {
		this(title, Arrays.asList(groups));
	}

	DrawerMenuImpl(String title, List<MenuItemGroup> groups) {
		this.title = title;
		this.groups = groups;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public List<MenuItemGroup> getMenuItemGroups() {
		return groups;
	}
}
