package org.geogebra.desktop.main;

/**
 * move DockBar out of App so that minimal applets work
 * 
 * @author michael
 *
 */
public interface DockBarInterface {
	/**
	 * @return whether the DockBar is visible
	 */
	boolean isVisible();

	/**
	 * @return whether the DockBar is oriented to the east
	 */
	boolean isEastOrientation();

	/**
	 * Sets the visibility of the DockBar.
	 *
	 * @param b
	 *            whether the DockBar should be visible
	 */
	void setVisible(boolean b);

	/**
	 * Sets the labels for the buttons on the DockBar.
	 */
	void setLabels();

	/**
	 * Show the popup.
	 */
	void showPopup();

	/**
	 * @return whether button bar is shown
	 */
	boolean isShowButtonBar();

	/**
	 * @param eastOrientation whether the docker bar should be oriented east
	 */
	void setEastOrientation(boolean eastOrientation);

	/**
	 * @param selected whether to show the button bar
	 */
	void setShowButtonBar(boolean selected);

	/**
	 * Hide the popup.
	 */
	void hidePopup();

}
