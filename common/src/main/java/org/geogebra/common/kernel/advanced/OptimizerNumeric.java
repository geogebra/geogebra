package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

public class OptimizerNumeric extends Optimizer {

	private GeoNumeric indep;

	public OptimizerNumeric(NumberValue dep, GeoNumeric indep2) {
		super(dep);
		this.indep = indep2;
	}

	@Override
	public GeoElement getGeo() {
		return indep;
	}

	@Override
	public double getValue() {
		return indep.getValue();
	}

	@Override
	public boolean hasBounds() {
		return indep.getIntervalMaxObject() != null
				&& indep.getIntervalMinObject() != null;
	}

	@Override
	public double getIntervalMin() {
		return indep.getIntervalMin();
	}

	@Override
	public double getIntervalMax() {
		return indep.getIntervalMax();
	}

	@Override
	public void setValue(double old) {
		indep.setValue(old);

	}

}
