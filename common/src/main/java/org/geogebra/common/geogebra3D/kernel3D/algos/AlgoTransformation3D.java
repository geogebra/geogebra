package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Class for static methods used for 3D transformations
 * 
 * @author mathieu
 *
 */
public class AlgoTransformation3D {

	/**
	 * set GeoFunction to GeoCurveCartesian3D
	 * 
	 * @param kernel
	 *            kernel
	 * @param geoFun
	 *            x->f(x) function
	 * @param curve
	 *            t->(x,y,z) curve
	 */
	static final public void toGeoCurveCartesian(Kernel kernel,
			GeoFunction geoFun, GeoCurveCartesian3D curve) {
		FunctionVariable t = new FunctionVariable(kernel, "t");
		FunctionVariable x = geoFun.getFunction().getFunctionVariable();
		ExpressionNode yExp = (ExpressionNode) ((ExpressionNode) geoFun
				.getFunction().getExpression().deepCopy(kernel)).replace(x, t);
		Function[] fun = new Function[3];
		fun[0] = new Function(new ExpressionNode(kernel, t), t);
		fun[1] = new Function(yExp, t);
		fun[2] = new Function(new ExpressionNode(kernel, 0), t);
		curve.setFun(fun);
		if (geoFun.hasInterval()) {
			curve.setInterval(geoFun.getIntervalMin(), geoFun.getIntervalMax());
		} else {
			double min = kernel.getXminForFunctions();
			double max = kernel.getXmaxForFunctions();
			curve.setInterval(min, max);
			// curve.setHideRangeInFormula(true);
		}
	}

}
