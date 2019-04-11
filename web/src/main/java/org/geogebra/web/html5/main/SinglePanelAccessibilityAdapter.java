package org.geogebra.web.html5.main;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.accessibility.EuclidianViewAccessibiliyAdapter;
import org.geogebra.web.html5.gui.accessibility.PerspectiveAccessibilityAdapter;

public class SinglePanelAccessibilityAdapter
		implements PerspectiveAccessibilityAdapter,
		EuclidianViewAccessibiliyAdapter {
	private AppW app;

	public SinglePanelAccessibilityAdapter(AppW app) {
		this.app = app;
	}

	public int nextID(int i) {
		return i == -1 ? App.VIEW_EUCLIDIAN : -1;
	}

	public int prevID(int i) {
		return nextID(i);
	}

	public EuclidianViewAccessibiliyAdapter getEuclidianPanel(int viewID) {
		return viewID == -1 ? null : this;
	}

	public EuclidianViewAccessibiliyAdapter getEVPanelWitZoomButtons(
			int viewID) {
		if (app.getZoomPanel() != null && app.getZoomPanel().hasButtons()) {
			return getEuclidianPanel(viewID);
		}
		return null;
	}

	public boolean focusSpeechRecBtn() {
		return false;
	}

	public void focusNextGUIElement() {
		if (app.getZoomPanel() != null) {
			app.getZoomPanel().focusFirstButton();
		}
	}

	public void focusLastZoomButton() {
		if (app.getZoomPanel() != null) {
			app.getZoomPanel().focusLastButton();
		}
	}

	public boolean focusSettings() {
		return false;
	}

	public EuclidianView getEuclidianView() {
		return app.getEuclidianView1();
	}

}
