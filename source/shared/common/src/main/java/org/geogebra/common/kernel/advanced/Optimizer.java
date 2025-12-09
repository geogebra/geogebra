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

package org.geogebra.common.kernel.advanced;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Common class for minimizing a function over number interval or path
 *
 */
public abstract class Optimizer implements UnivariateFunction {
	private NumberValue dep;

	/**
	 * @param dep
	 *            dependent number
	 */
	public Optimizer(NumberValue dep) {
		this.dep = dep;
	}

	/**
	 * @return optimization result
	 */
	public abstract GeoElement getGeo();

	/**
	 * @return function value at current param
	 */
	public abstract double getValue();

	/**
	 * @return whether the number is restricted to an interval or point is
	 *         restricted to path
	 */
	public abstract boolean hasBounds();

	/**
	 * @return min parameter
	 */
	public abstract double getIntervalMin();

	/**
	 * @return max parameter
	 */
	public abstract double getIntervalMax();

	/**
	 * @param old
	 *            parameter value
	 */
	public abstract void setValue(double old);

	@Override
	public double value(double x) {
		if ((dep != null) && (getGeo() != null)) {
			setValue(x);
			getGeo().updateCascade();
			// result = geodep.getDouble();
			// if type
			return dep.getDouble();
		}
		return Double.NaN;
		// if variables are ok
	}
}
