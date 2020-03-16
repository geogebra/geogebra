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
	public boolean focusIfVisible() {
		this.selected = !app.getKernel().getConstruction().isEmpty();
		app.getSelectionManager().clearSelectedGeos(false);
		app.getSelectionManager().selectNextGeo(app.getActiveEuclidianView());
		return selected;
	}

	@Override
	public boolean hasFocus() {
		return selected;
	}

	@Override
	public boolean focusNext() {
		selected = app.getSelectionManager().selectNextGeo(app.getActiveEuclidianView());
		return selected;
	}

	@Override
	public boolean focusPrevious() {
		selected = app.getSelectionManager().selectLastGeo(app.getActiveEuclidianView());
		return selected;
	}

	@Override
	public AccessibilityGroup getAccessibilityGroup() {
		return AccessibilityGroup.GEO_ELEMENT;
	}

	@Override
	public int getViewId() {
		return -1;
	}

	public void setFocused(boolean b) {
		this.selected = b;
	}
}
