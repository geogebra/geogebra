package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class OptimizerPoint extends Optimizer {

	private GeoPointND indep;

	public OptimizerPoint(NumberValue dep, GeoPointND indep) {
		super(dep);
		this.indep = indep;
	}

	@Override
	public GeoElement getGeo() {
		return indep.toGeoElement();
	}

	@Override
	public double getValue() {
		return indep.getPathParameter().getT();
	}

	@Override
	public boolean hasBounds() {
		return indep.getPath() != null;
	}

	@Override
	public double getIntervalMin() {
		return indep.getPath().getMinParameter();
	}

	@Override
	public double getIntervalMax() {
		return indep.getPath().getMaxParameter();
	}

	@Override
	public void setValue(double old) {
		indep.getPathParameter().setT(old);
		indep.getPath().pathChanged(indep);
		indep.updateCoords();

	}

}
