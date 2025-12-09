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

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Locateable;

/**
 * Interface for GeoElements that have an absolute screen position (GeoImage,
 * GeoText, GeoNumeric)
 */
public interface AbsoluteScreenLocateable extends Locateable {
	/**
	 * @param x
	 *            x offset (in pixels)
	 * @param y
	 *            y offset (in pixels)
	 */
	public void setAbsoluteScreenLoc(int x, int y);

	/**
	 * 
	 * @return x offset (in pixels)
	 */
	public int getAbsoluteScreenLocX();

	/**
	 * 
	 * @return y offset (in pixels)
	 */
	public int getAbsoluteScreenLocY();

	/**
	 * 
	 * @param x
	 *            real world x coordinate
	 * @param y
	 *            real world y coordinate
	 */
	public void setRealWorldLoc(double x, double y);

	/**
	 * 
	 * @return real world x-coordinate
	 */
	public double getRealWorldLocX();

	/**
	 * 
	 * @return real world y-coordinate
	 */
	public double getRealWorldLocY();

	/**
	 * @param flag
	 *            true to make abs position active
	 */
	public void setAbsoluteScreenLocActive(boolean flag);

	/**
	 * @return true iff abs position is active
	 */
	public boolean isAbsoluteScreenLocActive();

	/**
	 * E.g. GeoNumeric implements this, but not all numbers can have abs.
	 * location
	 * 
	 * @return true if this element can have absolute screen location
	 */
	public boolean isAbsoluteScreenLocateable();

	/**
	 * @param view
	 *            view
	 * @return height in given view, including label
	 */
	public int getTotalHeight(EuclidianViewInterfaceCommon view);

	/**
	 * @param view
	 *            view
	 * @return width in given view, including label
	 */
	public int getTotalWidth(EuclidianViewInterfaceCommon view);

	/**
	 * @return whether this is an interactive element
	 */
	boolean isFurniture();

	/**
	 * @return tue if bounding box / corners need recomputing
	 */
	default boolean needsUpdatedBoundingBox() {
		return false;
	}

}
