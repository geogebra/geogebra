/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
