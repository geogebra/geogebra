package org.geogebra.web.html5.gui.accessibility;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.MayHaveFocus;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;

public class GeoTabber implements MayHaveFocus {

	private final App app;
	private boolean selected;
	private final SelectionManager selectionManager;

	/**
	 * @param app Application
	 */
	public GeoTabber(App app) {
		this.app = app;
		selectionManager = app.getSelectionManager();
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		selectionManager.clearSelectedGeos(false);
		if (reverse) {
			selected = selectionManager.selectPreviousGeo();
		} else {
			selected = selectionManager.selectNextGeo();

		}
		if (selected) {
			app.getActiveEuclidianView().requestFocus();
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
