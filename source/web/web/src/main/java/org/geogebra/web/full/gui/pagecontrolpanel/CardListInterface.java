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

package org.geogebra.web.full.gui.pagecontrolpanel;

/**
 * Interface for updating card container visuals.
 * 
 * @author laszlo
 *
 */
public interface CardListInterface {
	/** rebuilds the container */
	void update();

	/**
	 * Scroll the panel by diff.
	 * 
	 * @param diff
	 *            to scroll by.
	 */
	void scrollBy(int diff);

	/**
	 * resets the page control panel
	 */
	void reset();

	/**
	 * opens the page control panel
	 */
	void open();

	/**
	 * Update content height.
	 */
	void updateContentPanelHeight();

	/**
	 * Update card indices.
	 * @param index first index to update
	 */
	void updateIndexes(int index);
}

