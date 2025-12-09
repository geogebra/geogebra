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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.matrix.Coords3;

/**
 * Surface with parametric equation z=f(x1,x2,...,xn)
 * 
 * @author Mathieu
 */
public interface SurfaceEvaluable {

	/**
	 * numerical value for dx, dy, etc. when computing numerical derivative
	 * vector
	 */
	static final public double NUMERICAL_DELTA = Kernel.STANDARD_PRECISION;

	/**
	 * level of detail (speed/quality)
	 */
	public enum LevelOfDetail {
		/** level of detail : speed */
		SPEED,
		/** level of detail : quality */
		QUALITY
	}

	/**
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 * @param point
	 *            point set for parameters u, v
	 */
	public void evaluatePoint(double u, double v, Coords3 point);

	/**
	 * 
	 * @param p
	 *            point where the normal is computed
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 * @param normal
	 *            normal. WARNING: the normal may not have norm=1
	 * @return true if the normal is defined
	 */
	public boolean evaluateNormal(Coords3 p, double u, double v,
			Coords3 normal);

	/**
	 * @param i
	 *            index of parameter
	 * @return minimal value for i-th parameter
	 */
	public double getMinParameter(int i);

	/**
	 * @param i
	 *            index of parameter
	 * @return maximal value for i-th parameter
	 */
	public double getMaxParameter(int i);

	/**
	 * set derivatives (if not already done)
	 */
	public void setDerivatives();

	/**
	 * reset derivatives
	 */
	public void resetDerivatives();

	/**
	 * @return the level of detail (for drawing)
	 */
	public LevelOfDetail getLevelOfDetail();

	/**
	 * set the level of detail (for drawing)
	 * 
	 * @param lod
	 *            level of detail
	 */
	public void setLevelOfDetail(LevelOfDetail lod);

	/**
	 * @return whether this is defined
	 */
	public boolean isDefined();
}
