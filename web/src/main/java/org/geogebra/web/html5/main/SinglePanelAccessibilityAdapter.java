package org.geogebra.web.html5.main;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.accessibility.EuclidianViewAccessibiliyAdapter;
import org.geogebra.web.html5.gui.accessibility.PerspectiveAccessibilityAdapter;

public class SinglePanelAccessibilityAdapter
		implements PerspectiveAccessibilityAdapter,
		EuclidianViewAccessibiliyAdapter {
	private AppW app;

	public SinglePanelAccessibilityAdapter(AppW app) {
		this.app = app;
	}

	@Override
	public int nextID(int i) {
		return i == -1 ? App.VIEW_EUCLIDIAN : -1;
	}

	@Override
	public int prevID(int i) {
		return nextID(i);
	}

	@Override
	public EuclidianViewAccessibiliyAdapter getEuclidianPanel(int viewID) {
		return viewID == -1 ? null : this;
	}

	@Override
	public EuclidianViewAccessibiliyAdapter getEVPanelWitZoomButtons(
			int viewID) {
		if (app.getZoomPanel() != null && app.getZoomPanel().hasButtons()) {
			return getEuclidianPanel(viewID);
		}
		return null;
	}

	@Override
	public boolean focusSpeechRecBtn() {
		return false;
	}

	@Override
	public void focusNextGUIElement() {
		if (app.getZoomPanel() != null) {
			app.getZoomPanel().focusFirstButton();
		}
	}

	@Override
	public void focusLastZoomButton() {
		if (app.getZoomPanel() != null) {
			app.getZoomPanel().focusLastButton();
		}
	}

	@Override
	public boolean focusSettings() {
		return false;
	}

	@Override
	public boolean focusResetButton() {
		if (app.showResetIcon() && getEuclidianView() instanceof EuclidianViewW) {
			EuclidianViewW view = (EuclidianViewW) getEuclidianView();
			view.focusResetIcon();
			return true;
		}
		return false;
	}

	@Override
	public EuclidianView getEuclidianView() {
		return app.getEuclidianView1();
	}

}
