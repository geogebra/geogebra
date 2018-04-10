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
