package org.geogebra.common.gui.layout;

import org.geogebra.common.euclidian.GetViewId;
import org.geogebra.common.gui.SetLabels;

public abstract class DockManager implements SetLabels {

	public abstract GetViewId getFocusedEuclidianPanel();

	public abstract boolean setFocusedPanel(int panel);

	public abstract void unRegisterPanel(DockPanel dockPanel);

	public abstract DockPanel getPanel(int ViewId);

	/**
	 * Call onResize for all panels.
	 */
	public void resizePanels() {
		// overridden in Web
	}

	public abstract int getNumberOfOpenViews();

	public abstract int getFocusedViewId();

	/**
	 * Ensure that one of the panels has focus.
	 */
	public void ensureFocus() {
		// overridden in Web
	}

	/**
	 * Puts AV bellow to EV if app height is bigger than width (portrait), or EV
	 * next to AV otherwise (landscape).
	 * 
	 * @param force
	 *            TODO
	 */
	public void adjustViews(boolean force) {
		// overridden in Web
	}
}
