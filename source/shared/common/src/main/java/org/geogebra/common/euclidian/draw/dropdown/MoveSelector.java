package org.geogebra.common.euclidian.draw.dropdown;

/**
 * Object with selection that can move both vertically and horizontally.
 */
public interface MoveSelector {
	/**
	 * Moves dropdown selector up or down by one item.
	 *
	 * @param down
	 *            Sets if selection indicator should move down or up.
	 */
	void moveSelectorVertical(boolean down);

	/**
	 * Moves the selector horizontally, if dropdown has more columns than one.
	 *
	 * @param left
	 *            Indicates that selector should move left or right.
	 */
	void moveSelectorHorizontal(boolean left);
}
