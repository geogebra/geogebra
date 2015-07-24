package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.roots.RealRootFunction;

public abstract class Optimizer implements RealRootFunction {
	private NumberValue dep;

	public Optimizer(NumberValue dep) {
		this.dep = dep;
	}

	public abstract GeoElement getGeo();

	public abstract double getValue();

	public abstract boolean hasBounds();

	public abstract double getIntervalMin();

	public abstract double getIntervalMax();

	public abstract void setValue(double old);

	public double evaluate(double x) {
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
