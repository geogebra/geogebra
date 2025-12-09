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

package org.geogebra.common.gui.menu;

import java.io.Serializable;
import java.util.List;

import javax.annotation.CheckForNull;

/**
 * A model describing a menu item group. Each group can have
 * an optional title, and has at least a single menu item.
 */
public interface MenuItemGroup extends Serializable {

	/**
	 * Get the title of the menu item group.
	 *
	 * @return title
	 */
	@CheckForNull String getTitle();

	/**
	 * Get the menu items of this group.
	 *
	 * @return menu items
	 */
	List<MenuItem> getMenuItems();
}
