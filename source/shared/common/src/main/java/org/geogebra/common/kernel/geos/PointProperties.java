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

package org.geogebra.common.kernel.geos;

/**
 * Element with point style and point size (point or list)
 */
public interface PointProperties {
	/**
	 * Sets the point size
	 * 
	 * @param size
	 *            new point size
	 */
	void setPointSize(int size);

	/**
	 * Returns the point size
	 * 
	 * @return point size
	 */
	int getPointSize();

	/**
	 * Sets the point style
	 * 
	 * @param type
	 *            point style
	 */
	void setPointStyle(int type);

	/**
	 * Returns the point style
	 * 
	 * @return point style
	 */
	int getPointStyle();

	/**
	 * Updates and repaints the object
	 */
	void updateRepaint();

	/**
	 * @return whether point settings should be shown
	 */
	boolean showPointProperties();
}