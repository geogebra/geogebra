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

package org.geogebra.common.kernel.kernelND;

/**
 * Axis in euclidian view (or euclidian view 3D)
 *
 */
public interface GeoAxisND extends GeoLineND {
	/** xAxis id (for XML) */
	public static final int X_AXIS = 0;
	/** yAxis id (for XML) */
	public static final int Y_AXIS = 1;
	/** zAxis id (for XML) */
	public static final int Z_AXIS = 2;
	/** xAxis3D id (for XML) */
	public static final int X_AXIS_3D = X_AXIS;
	/** yAxis3D id (for XML) */
	public static final int Y_AXIS_3D = Y_AXIS;
	/** zAxis3D id (for XML) */
	public static final int Z_AXIS_3D = Z_AXIS;

	/** @return axis unit */
	public String getUnitLabel();

	/** @return tick style */
	public int getTickStyle();

	/** @return whether numbers should be displayed */
	public boolean getShowNumbers();

	/** @return tick size in pixels */
	public int getTickSize();

	/** @return axis id */
	public int getType();

	/**
	 * 
	 * @param colored
	 *            iff axis is drawn colored in 3D view
	 */
	public void setColoredFor3D(boolean colored);

}
