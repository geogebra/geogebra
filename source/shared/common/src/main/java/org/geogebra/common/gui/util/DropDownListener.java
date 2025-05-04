package org.geogebra.common.gui.util;

/**
 * Dropdown event listener.
 */
public interface DropDownListener {
	/**
	 * Handle click.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	void onClick(int x, int y);

	/**
	 * Handle scroll.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	void onScroll(int x, int y);
}
