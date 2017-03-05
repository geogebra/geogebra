package org.geogebra.common.gui.layout;

import org.geogebra.common.euclidian.GetViewId;
import org.geogebra.common.gui.SetLabels;

public abstract class DockManager implements SetLabels {

	public abstract GetViewId getFocusedEuclidianPanel();

	public abstract boolean setFocusedPanel(int panel);

	public abstract void unRegisterPanel(DockPanel dockPanel);

	public abstract DockPanel getPanel(int ViewId);

	public void resizePanels() {
		// TODO Auto-generated method stub

	}

	public abstract int getNumberOfOpenViews();

	public abstract int getFocusedViewId();

	public void ensureFocus() {
		// TODO Auto-generated method stub
	}

	/**
	 * Puts AV bellow to EV if app height is bigger than width (portrait), or EV
	 * next to AV otherwise (landscape).
	 * 
	 */
	public void adjustViews() {
		// overridden in Web
	}
}
