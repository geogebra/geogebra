package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoFunction;

public class DistributionFunctionFactory {
	public static GeoFunction zeroWhenNegative(Construction cons) {
		return zeroWhenLessThan(new MyDouble(cons.getKernel(), 0), cons);
	}

	public static GeoFunction zeroWhenLessThan(ExpressionValue border,
			Construction cons) {
		Kernel kernel = cons.getKernel();
		FunctionVariable fv = new FunctionVariable(kernel);
		ExpressionNode en = fv.wrap().lessThan(border)
				.ifElse(new MyDouble(kernel, 0), new MyDouble(kernel, 0));

		return en.buildFunction(fv);
	}
}
