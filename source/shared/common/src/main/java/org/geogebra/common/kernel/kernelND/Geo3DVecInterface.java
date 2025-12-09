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
 * Interface for 3D vectors (not to be confused with GeoVec3D)
 */
public interface Geo3DVecInterface extends GeoVecInterface {
	/**
	 * @param vec
	 *            other vector
	 * @return true if this vector and other vector have same coordinates
	 */
	public boolean isEqual(Geo3DVecInterface vec);

	/**
	 * @return x-coord
	 */
	@Override
	public double getX();

	/**
	 * @return y-coord
	 */
	@Override
	public double getY();

	/**
	 * @return z-coord
	 */
	@Override
	public double getZ();

	/**
	 * @return length
	 */
	public double length();

	/**
	 * @return (Math.round(x), Math.round(y), Math.round(z))
	 */
	public Geo3DVecInterface round();

	/**
	 * @return (Math.floor(x), Math.floor(y), Math.floor(z))
	 */
	public Geo3DVecInterface floor();

	/**
	 * @return (Math.ceil(x), Math.ceil(y), Math.ceil(z))
	 */
	public Geo3DVecInterface ceil();

	/**
	 * @return Math.atan2(y,x)
	 */
	public double arg();

	/**
	 * @param double1
	 *            coefficient
	 */
	public void mult(double double1);

	/**
	 * @param mode
	 *            coordinates mode
	 */
	public void setMode(int mode);

}
