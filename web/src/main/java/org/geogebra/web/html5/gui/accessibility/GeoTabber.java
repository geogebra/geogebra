package org.geogebra.web.html5.gui.accessibility;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.MayHaveFocus;
import org.geogebra.common.main.App;

public class GeoTabber implements MayHaveFocus {

	private final App app;
	private boolean selected;

	public GeoTabber(App app) {
		this.app = app;
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		app.getSelectionManager().clearSelectedGeos(false);
		if (reverse) {
			selected = app.getSelectionManager().selectPreviousGeo();
		} else {
			selected = app.getSelectionManager().selectNextGeo();
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
		selected = app.getSelectionManager().selectNextGeo();
		return selected;
	}

	@Override
	public boolean focusPrevious() {
		selected = app.getSelectionManager().selectPreviousGeo();
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
