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

package org.geogebra.common.gui.toolbar;

import java.util.Vector;

/**
 * Part of classic toolbar
 */
public class ToolbarItem {
	private Vector<Integer> menu;
	private Integer mode;

	/**
	 * @param menu
	 *            modes of a submenu
	 */
	public ToolbarItem(Vector<Integer> menu) {
		this.menu = menu;
		this.mode = null;
	}

	/**
	 * @param mode
	 *            single mode
	 */
	public ToolbarItem(Integer mode) {
		this.mode = mode;
	}

	/**
	 * @return modes of a submenu
	 */
	public Vector<Integer> getMenu() {
		return menu;
	}

	/**
	 * @return single mode (null if this represents submenu)
	 */
	public Integer getMode() {
		return mode;
	}

}
