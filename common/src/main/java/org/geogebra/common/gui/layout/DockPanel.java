package org.geogebra.common.gui.layout;

import org.geogebra.common.awt.Component;

/**
 * @author judit interface for geogebra.gui.layout.DockPanel
 * 
 */
public interface DockPanel extends Component {

	String getToolbarString();

	boolean isOpenInFrame();

	String getDefaultToolbarString();

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

	void deferredOnResize();

	/**
	 * update navigation bar
	 */
	public void updateNavigationBar();
}
