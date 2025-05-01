package org.geogebra.common.gui.layout;

import org.geogebra.common.euclidian.GetViewId;
import org.geogebra.common.gui.SetLabels;

public abstract class DockManager implements SetLabels {

	/**
	 * @return focused euclidian panel
	 */
	public abstract GetViewId getFocusedEuclidianPanel();

	/**
	 * Focus panel with given ID.
	 * @param panel view ID
	 * @return whether focus moved
	 */
	public abstract boolean setFocusedPanel(int panel);

	/**
	 * Unregister a panel.
	 * @param dockPanel dock panel
	 */
	public abstract void unRegisterPanel(DockPanel dockPanel);

	/**
	 * Get a panel for given view.
	 * @param viewId view ID
	 * @return the panel
	 */
	public abstract DockPanel getPanel(int viewId);

	/**
	 * Call onResize for all panels.
	 */
	public void resizePanels() {
		// overridden in Web
	}

	/**
	 * @return number of open views
	 */
	public abstract int getNumberOfOpenViews();

	/**
	 * @return ID of the focused view
	 */
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
