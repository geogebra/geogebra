package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * Sum command helper for functions
 *
 */
public class FunctionFold implements FoldComputer {

	private GeoFunction resultFun;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geogebra.common.kernel.algos.FoldComputer#getTemplate(org.geogebra.
	 * common.kernel.Construction)
	 */
	@Override
	public GeoElement getTemplate(Construction cons, GeoClass listElement) {
		return this.resultFun = new GeoFunction(cons);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geogebra.common.kernel.algos.FoldComputer#add(org.geogebra.common.
	 * kernel.geos.GeoElement, org.geogebra.common.plugin.Operation)
	 */
	@Override
	public void add(GeoElement geoElement, Operation op) {
		resultFun = GeoFunction.add(resultFun, resultFun,
				(GeoFunctionable) geoElement, op);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geogebra.common.kernel.algos.FoldComputer#setFrom(org.geogebra.common
	 * .kernel.geos.GeoElement, org.geogebra.common.kernel.Kernel)
	 */
	@Override
	public void setFrom(GeoElement geoElement, Kernel kernel) {
		if (!geoElement.isRealValuedFunction()) {
			resultFun.setUndefined();
			return;
		}

		Function fun1 = ((GeoFunctionable) geoElement).getFunction();

		FunctionVariable x1 = fun1.getFunction().getFunctionVariable();
		FunctionVariable x = new FunctionVariable(kernel);

		ExpressionNode left = fun1.getFunctionExpression()
				.getCopy(fun1.getKernel());

		Function f = new Function(left.replace(x1, x).wrap(), x);

		resultFun.setFunction(f);
		resultFun.setDefined(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geogebra.common.kernel.algos.FoldComputer#check(org.geogebra.common.
	 * kernel.geos.GeoElement)
	 */
	@Override
	public boolean check(GeoElement geoElement) {
		return geoElement.isRealValuedFunction();
	}

	@Override
	public void finish() {
		// nothing to do
	}

}
