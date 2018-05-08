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
