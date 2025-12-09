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
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Optimizer for Minimize[dependent number,number]
 */
public class OptimizerNumeric extends Optimizer {

	private GeoNumeric indep;

	/**
	 * @param dep
	 *            dependent number
	 * @param indep2
	 *            independent number
	 */
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
