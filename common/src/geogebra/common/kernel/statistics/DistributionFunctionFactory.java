package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyNumberPair;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.plugin.Operation;

public class DistributionFunctionFactory {
	public static GeoFunction zeroWhenNegative(Construction cons){
		Kernel kernel = cons.getKernel();
		FunctionVariable fv = new FunctionVariable(kernel);
		ExpressionNode en = new ExpressionNode(kernel,
				new MyNumberPair(kernel,fv.wrap().lessThan(0),new MyDouble(kernel,0)),Operation.IF_ELSE,new MyDouble(kernel,0));
		
		return en.buildFunction(fv);
	}
}
