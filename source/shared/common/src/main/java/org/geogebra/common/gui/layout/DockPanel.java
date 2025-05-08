package org.geogebra.common.gui.layout;

import org.geogebra.common.annotation.MissingDoc;

/**
 * @author judit interface for geogebra.gui.layout.DockPanel
 * 
 */
public interface DockPanel {

	@MissingDoc
	String getToolbarString();

	@MissingDoc
	String getDefaultToolbarString();

	@MissingDoc
	int getViewId();

	/**
	 * Close this panel permanently.
	 */
	public void closePanel();

	/**
	 * change the visibility of the DockPanel
	 * 
	 * @param visible
	 *            visibility
	 */
	public void setVisible(boolean visible);

	/**
	 * 
	 * @return true if set visible
	 */
	public boolean isVisible();

	@MissingDoc
	void deferredOnResize();

	/**
	 * update navigation bar
	 */
	public void updateNavigationBar();
}
