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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

/**
 * Helper for building distribution functions
 */
public class DistributionFunctionFactory {
	/**
	 * @param cons
	 *            construction
	 * @return function If[x &lt; 0,0,0]
	 */
	public static GeoFunction zeroWhenNegative(Construction cons) {
		return zeroWhenLessThan(new MyDouble(cons.getKernel(), 0), cons, true);
	}

	/**
	 * @param border
	 *            left border of function support
	 * @param cons
	 *            construction
	 * @param sharp
	 *            whether to use &lt; or &lt;=
	 * @return function If[x &lt; border, 0, 0]
	 */
	public static GeoFunction zeroWhenLessThan(ExpressionValue border,
			Construction cons, boolean sharp) {
		Kernel kernel = cons.getKernel();
		FunctionVariable fv = new FunctionVariable(kernel);
		ExpressionNode en = fv.wrap()
				.apply(sharp ? Operation.LESS : Operation.LESS_EQUAL, border)
				.ifElse(new MyDouble(kernel, 0), new MyDouble(kernel, 0));

		return en.buildFunction(fv);
	}
}
