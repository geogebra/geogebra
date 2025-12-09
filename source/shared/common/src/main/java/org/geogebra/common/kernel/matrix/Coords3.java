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

package org.geogebra.common.kernel.matrix;

/**
 * class for 3 floats (x, y, z)
 * 
 * @author mathieu
 *
 */
public abstract class Coords3 {

	/**
	 * set values
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	abstract public void set(float x, float y, float z);

	/**
	 * set values
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	abstract public void set(double x, double y, double z);

	/**
	 * returns false if one value equals NaN
	 * 
	 * @return false if one value equals NaN
	 */
	abstract public boolean isDefined();

	/**
	 * @return true if not a final (constant) undefined
	 */
	public boolean isNotFinalUndefined() {
		return true;
	}

	/**
	 * @return true if a final (constant) undefined
	 */
	public boolean isFinalUndefined() {
		return false;
	}

	/**
	 * returns a copy of the vector
	 * 
	 * @return a copy of the vector
	 */
	abstract public Coords3 copyVector();

	/**
	 * add values of v inside this
	 * 
	 * @param v
	 *            vector
	 */
	abstract public void addInside(Coords3 v);

	/**
	 * multiply all values by v
	 * 
	 * @param v
	 *            factor
	 */
	abstract public void mulInside(float v);

	/**
	 * multiply all values by v
	 * 
	 * @param v
	 *            factor
	 */
	abstract public void mulInside(double v);

	/**
	 * multiply values by a/b/c
	 * 
	 * @param a
	 *            x factor
	 * @param b
	 *            y factor
	 * @param c
	 *            z factor
	 * 
	 */
	abstract public void mulInside(double a, double b, double c);

	/**
	 * normalize this (if norm != 0)
	 */
	abstract public void normalizeIfPossible();

	/**
	 * set this to v normalized or (0, 0, 0) if v=0
	 * 
	 * @param v
	 *            vector
	 */
	final public void setNormalizedIfPossible(Coords v) {
		double vx = v.getX();
		double vy = v.getY();
		double vz = v.getZ();

		double f = 1 / Math.sqrt(vx * vx + vy * vy + vz * vz);

		if (Double.isNaN(f)) {
			set(0, 0, 0);
		} else {
			set(vx * f, vy * f, vz * f);
		}

	}

	/**
	 * 
	 * @return x coord
	 */
	abstract public double getXd();

	/**
	 * 
	 * @return x coord
	 */
	abstract public double getYd();

	/**
	 * 
	 * @return x coord
	 */
	abstract public double getZd();

	/**
	 * 
	 * @return x coord
	 */
	abstract public float getXf();

	/**
	 * 
	 * @return x coord
	 */
	abstract public float getYf();

	/**
	 * 
	 * @return x coord
	 */
	abstract public float getZf();

}
