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

package org.geogebra.common.awt;

/** Class for integer tuples **/
public class GPointWithZ extends GPoint {
	/** z-coordinate **/
	public int z;

	/**
	 * Point (0,0,0)
	 */
	public GPointWithZ() {
		z = 0;
	}

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public GPointWithZ(int x, int y, int z) {
		super(x, y);
		this.z = z;
	}

	/**
	 * Set x and y at the same time
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public void setLocation(int x, int y, int z) {
		super.setLocation(x, y);
		this.z = z;
	}

	/**
	 * Take both coords from a point
	 * 
	 * @param p
	 *            point
	 */
	public void setLocation(GPointWithZ p) {
		super.setLocation(p);
		this.z = p.z;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public int hashCode() {
		return (super.hashCode() << 16) ^ z;
	}

	@Override
	public boolean equals(Object o) {
		// super also check z-coord
		return super.equals(o);
	}

	/**
	 * @param d
	 *            another 3D point
	 * @return distance to that point
	 */
	public double distance(GPointWithZ d) {
		return Math.sqrt((x - d.x) * (x - d.x) + (y - d.y) * (y - d.y)
				+ (z - d.z) * (z - d.z));
	}

	/**
	 * @param dx
	 *            x-coord of other point
	 * @param dy
	 *            y-coord of other point
	 * @param dz
	 *            z-coord of other point
	 * @return distance to (dx, dy, dz)
	 */
	public double distance(double dx, double dy, double dz) {
		return (int) Math.sqrt((x - dx) * (x - dx) + (y - dy) * (y - dy)
				+ (z - dz) * (z - dz));
	}

	@Override
	public String toString() {
		return x + " : " + y + " : " + z;
	}

}
