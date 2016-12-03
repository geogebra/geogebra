package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;

/**
 * Algorithm for ImplicitDerivative[f(x,y)]
 *
 */
public class AlgoImplicitDerivative extends AlgoElement implements UsesCAS {

	private GeoFunctionNVar result;
	private FunctionalNVar functional;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param functional
	 *            function of two variables
	 */
	public AlgoImplicitDerivative(Construction cons, String label,
			FunctionalNVar functional) {
		super(cons);
		this.functional = functional;
		this.result = new GeoFunctionNVar(cons);
		setInputOutput();
		compute();
		result.setLabel(label);

	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
		if (functional instanceof GeoFunctionNVar)
			input = new GeoElement[] { (GeoFunctionNVar) functional };
		if (functional instanceof GeoFunction)
			input = new GeoElement[] { (GeoFunction) functional };
		setDependencies();

	}

	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	@Override
	public void compute() {
		StringTemplate tpl = StringTemplate.prefixedDefault;
		StringBuilder sb = new StringBuilder(80);
		sb.append("ImplicitDerivative(");
		sb.append(functional.toValueString(tpl));
		sb.append(")");

		try {
			String functionOut = kernel.evaluateCachedGeoGebraCAS(
					sb.toString(), arbconst);
			if (functionOut == null || functionOut.length() == 0) {
				result.setUndefined();
			} else {
				// read result back into function
				result.set(kernel.getAlgebraProcessor().evaluateToFunctionNVar(
						functionOut, true));
			}
		} catch (Throwable e) {
			result.setUndefined();
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.ImplicitDerivative;
	}

	/**
	 * @return resulting derivative
	 */
	public GeoFunctionNVar getResult() {
		return result;
	}

	

}
