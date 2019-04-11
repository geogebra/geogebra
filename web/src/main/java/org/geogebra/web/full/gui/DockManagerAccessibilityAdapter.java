package org.geogebra.web.full.gui;

import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract;
import org.geogebra.web.html5.gui.accessibility.PerspectiveAccessibilityAdapter;
import org.geogebra.web.html5.gui.accessibility.EuclidianViewAccessibiliyAdapter;
import org.geogebra.web.html5.main.AppW;

public class DockManagerAccessibilityAdapter implements PerspectiveAccessibilityAdapter {

	private AppW app;

	public DockManagerAccessibilityAdapter(AppW app) {
		this.app = app;
	}

	@Override
	public int nextID(int viewID) {
		DockManagerW dm = (DockManagerW) app.getGuiManager().getLayout()
				.getDockManager();
		return nextID(dm.getPanels(), viewID);
	}

	@Override
	public int prevID(int viewID) {
		DockManagerW dm = (DockManagerW) app.getGuiManager().getLayout()
				.getDockManager();
		DockPanelW[] reversePanels = new DockPanelW[dm.getPanels().length];
		for (int i = 0; i < reversePanels.length; i++) {
			reversePanels[i] = dm.getPanels()[dm.getPanels().length - 1 - i];
		}
		return nextID(reversePanels, viewID);
	}

	private static int nextID(DockPanelW[] panels, int viewID) {
		boolean returnNext = viewID == -1;
		for (DockPanelW panel : panels) {
			EuclidianDockPanelWAbstract ev = checkEuclidianViewWithZoomButtons(
					panel);
			if (ev != null && returnNext) {
				return ev.getViewId();
			}
			if (ev != null && ev.getViewId() == viewID) {
				returnNext = true;
			}
		}
		return -1;
	}

	private static EuclidianDockPanelWAbstract checkEuclidianViewWithZoomButtons(
			DockPanelW panel) {
		if (!(panel instanceof EuclidianDockPanelWAbstract)) {
			return null;
		}

		EuclidianDockPanelWAbstract ev = (EuclidianDockPanelWAbstract) panel;

		boolean zoomButtons = ev.isAttached() && ev.hasZoomButtons();
		return zoomButtons ? ev : null;
	}

	@Override
	public EuclidianDockPanelWAbstract getEuclidianPanel(int viewId) {
		return (EuclidianDockPanelWAbstract) app.getGuiManager().getLayout()
				.getDockManager().getPanel(viewId);
	}

	@Override
	public EuclidianViewAccessibiliyAdapter getEVPanelWitZoomButtons(
			int viewID) {
		return checkEuclidianViewWithZoomButtons(getEuclidianPanel(viewID));
	}
}
