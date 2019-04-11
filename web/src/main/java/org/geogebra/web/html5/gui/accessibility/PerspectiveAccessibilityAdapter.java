package org.geogebra.web.html5.gui.accessibility;

public interface PerspectiveAccessibilityAdapter {

	int nextID(int i);

	int prevID(int i);

	EuclidianViewAccessibiliyAdapter getEuclidianPanel(int viewId);

	EuclidianViewAccessibiliyAdapter getEVPanelWitZoomButtons(int viewID);

}
