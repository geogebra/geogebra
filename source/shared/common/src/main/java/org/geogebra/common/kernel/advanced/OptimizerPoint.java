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

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Optimizer for Minimize[dependent number, point on path]
 *
 */
public class OptimizerPoint extends Optimizer {

	private GeoPointND indep;

	/**
	 * @param dep
	 *            dependent number
	 * @param indep
	 *            independent point
	 */
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
