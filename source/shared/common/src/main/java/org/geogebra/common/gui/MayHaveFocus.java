package org.geogebra.common.gui;

public interface MayHaveFocus {
	boolean focusIfVisible(boolean reverse);

	boolean hasFocus();

	boolean focusNext();

	boolean focusPrevious();

	AccessibilityGroup getAccessibilityGroup();

	AccessibilityGroup.ViewControlId getViewControlId();
}
