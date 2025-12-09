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

package org.geogebra.common.kernel.optimization;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * Converts f into -f
 *
 */
public class NegativeRealRootFunction implements UnivariateFunction {

	private UnivariateFunction f;

	/**
	 * @param f
	 *            function to negate (=multiply by -1)
	 */
	public NegativeRealRootFunction(UnivariateFunction f) {
		this.f = f;
	}

	@Override
	final public double value(double x) {
		return -f.value(x);
	}

}
