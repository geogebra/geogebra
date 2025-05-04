package org.geogebra.common.gui;

/**
 * Focusable component.
 */
public interface MayHaveFocus {
	/**
	 * Focus first or last part of this component, if visible.
	 * @param reverse whether focus comes from reverse tabbing.
	 * @return success
	 */
	boolean focusIfVisible(boolean reverse);

	/**
	 * @return whether any part of this component has focus
	 */
	boolean hasFocus();

	/**
	 * Focus the next part.
	 * @return success
	 */
	boolean focusNext();

	/**
	 * Focus the previous part.
	 * @return success
	 */
	boolean focusPrevious();

	/**
	 * @return accessibility group
	 */
	AccessibilityGroup getAccessibilityGroup();

	/**
	 * @return view control ID
	 */
	AccessibilityGroup.ViewControlId getViewControlId();
}
