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

package org.geogebra.common.gui.layout;

/**
 * interface for DockSplitPane and DockPanel
 * 
 * @author mathieu
 *
 */
public interface DockComponent {

	/**
	 * minimum half size of a panel
	 */
	public static final int MIN_SIZE = 100;

	/**
	 * Update resize weight
	 * 
	 * @return true if it contains a panel that takes new space (currently if
	 *         contains an euclidian view)
	 */
	public boolean updateResizeWeight();

	/**
	 * save divider location (recursively)
	 */
	public void saveDividerLocation();

	/**
	 * update divider location (recursively)
	 * 
	 * @param size
	 *            new size of the component
	 * @param orientation
	 *            orientation of the parent split
	 */
	public void updateDividerLocation(int size, int orientation);

	/**
	 * set visibility of all DockPanel sub components
	 * 
	 * @param visible
	 *            flag
	 */
	public void setDockPanelsVisible(boolean visible);

}
