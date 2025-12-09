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
 * Simple interface for geos that have 4 coords (3D points and vectors, 3D
 * planes, ...)
 * 
 * @author mathieu
 *
 */
public interface GeoCoords4D {

	/**
	 * sets the coords
	 * 
	 * @param x
	 *            x-ccord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param w
	 *            w-coord (homogeneous)
	 */
	public void setCoords(double x, double y, double z, double w);

}
