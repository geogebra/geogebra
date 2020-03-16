package org.geogebra.common.gui;

public interface MayHaveFocus {
	boolean focusIfVisible();

	boolean hasFocus();

	boolean focusNext();

	boolean focusPrevious();

	AccessibilityGroup getAccessibilityGroup();

	int getViewId();
}
