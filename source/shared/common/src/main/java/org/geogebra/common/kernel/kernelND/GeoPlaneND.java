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

import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * simple plane interface for all geos that can be considered as a plane (3D
 * plane, polygons, ...)
 * 
 * @author mathieu
 *
 */
public interface GeoPlaneND extends GeoCoordSys2D, EquationValue, LinearEquationRepresentable {

	/**
	 * sets the fading for the "ends" of the plane
	 * 
	 * @param fading
	 *            fading
	 */
	public void setFading(float fading);

	/**
	 * 
	 * @return the fading for the "ends" of the plane
	 */
	public float getFading();

	/**
	 * create a 2D view of this plane
	 */
	public void createView2D();

	/**
	 * @param coords
	 *            point
	 * @return coords of normal projection of given point
	 */
	@Override
	public Coords[] getNormalProjection(Coords coords);

	/**
	 * @param h3d
	 *            other plane
	 * @return distance
	 */
	public double distanceWithSign(GeoPlaneND h3d);

}
