package org.geogebra.common.gui;

/**
 * Focusable component.
 */
public interface MayHaveFocus {
	boolean focusIfVisible(boolean reverse);

	boolean hasFocus();

	boolean focusNext();

	boolean focusPrevious();

	AccessibilityGroup getAccessibilityGroup();

	AccessibilityGroup.ViewControlId getViewControlId();
}
