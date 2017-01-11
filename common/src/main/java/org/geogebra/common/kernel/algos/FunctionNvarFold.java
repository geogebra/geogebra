package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * Sum helper for nvar functions
 *
 */
public class FunctionNvarFold implements FoldComputer {

	private GeoFunctionNVar result;

	@Override
	public GeoElement getTemplate(Construction cons, GeoClass listElement) {
		return this.result = new GeoFunctionNVar(cons);
	}

	@Override
	public void add(GeoElement geoElement, Operation op) {
		FunctionNVar fn = GeoFunction
				.operationSymb(op, result, (FunctionalNVar) geoElement)
				.deepCopy(geoElement.getKernel());
		fn.setExpression(AlgoDependentFunction
				.expandFunctionDerivativeNodes(fn.getExpression(), true)
				.wrap());
		result.setFunction(fn);
		this.result.setDefined(true);

	}

	@Override
	public void setFrom(GeoElement geoElement, Kernel kernel) {
		this.result.set(geoElement);
		this.result.setDefined(true);
	}

	@Override
	public boolean check(GeoElement geoElement) {
		return geoElement instanceof FunctionalNVar;
	}

	@Override
	public void finish() {
		this.result.setDefined(true);
	}

}
