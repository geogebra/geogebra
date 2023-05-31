package org.geogebra.common.gui;

import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;

public class GeoTabber implements MayHaveFocus {

	private boolean selected;
	private final SelectionManager selectionManager;

	/**
	 * @param app Application
	 */
	public GeoTabber(App app) {
		selectionManager = app.getSelectionManager();
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		selectionManager.clearSelectedGeos(false);
		// selectNext / selectPrevious are responsible also for moving the focus -- they have
		// to decide if it goes to inputbox or canvas. We should not move focus here.
		if (reverse) {
			selected = selectionManager.selectPreviousGeo();
		} else {
			selected = selectionManager.selectNextGeo();
		}
		return selected;
	}

	@Override
	public boolean hasFocus() {
		return selected;
	}

	@Override
	public boolean focusNext() {
		selected = selectionManager.selectNextGeo();
		return selected;
	}

	@Override
	public boolean focusPrevious() {
		selected = selectionManager.selectPreviousGeo();
		return selected;
	}

	@Override
	public AccessibilityGroup getAccessibilityGroup() {
		return AccessibilityGroup.GEO_ELEMENT;
	}

	@Override
	public AccessibilityGroup.ViewControlId getViewControlId() {
		return null;
	}

	public void setFocused(boolean b) {
		this.selected = b;
	}
}
