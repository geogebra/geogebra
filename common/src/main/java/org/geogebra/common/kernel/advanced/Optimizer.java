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
