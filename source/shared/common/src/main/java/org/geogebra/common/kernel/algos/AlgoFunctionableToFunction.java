package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;

/**
 * Converts lines, conics to function
 * 
 * @author Zbynek
 * @deprecated instead of converting line to function with this algo please make
 *             sure the receiver can handle both functions and lines
 */
@Deprecated
public class AlgoFunctionableToFunction extends AlgoElement {

	private GeoFunction outputFunction;
	private GeoFunctionable functionable;

	/**
	 * @param construction
	 *            construction
	 * @param functionable
	 *            geo to be converted to a function
	 */
	public AlgoFunctionableToFunction(Construction construction,
			GeoFunctionable functionable) {
		super(construction);
		this.functionable = functionable;
		Function expr = functionable.getFunction();
		outputFunction = new GeoFunction(cons, expr);
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { functionable.toGeoElement() };
		setOnlyOutput(outputFunction);
		setDependencies();
	}

	@Override
	public void compute() {
		ExpressionNode newExpression = functionable.getFunction()
				.getFunctionExpression();
		outputFunction.getFunction().setExpression(newExpression);
		outputFunction.getFunction().initFunction();
	}

	@Override
	public GetCommand getClassName() {
		return Algos.Expression;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return functionable.getLabel(tpl);
	}

	/**
	 * @return output
	 */
	public GeoFunction getFunction() {
		return outputFunction;
	}

}
