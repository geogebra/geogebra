package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;

public class AlgoConicToFunction extends AlgoElement {

	private GeoFunction g;
	private GeoFunctionable f;

	public AlgoConicToFunction(Construction c, GeoFunctionable f) {
		super(c);
		this.f = f;
		Function expr = f.getFunction(false);
		g = new GeoFunction(cons, expr);
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { f.toGeoElement() };
		setOnlyOutput(g);
		setDependencies();
	}

	@Override
	public void compute() {
		ExpressionNode newExpression = f.getFunction(false)
				.getFunctionExpression();
		g.getFunction().setExpression(newExpression);
		g.getFunction().initFunction();
	}

	@Override
	public GetCommand getClassName() {
		return Algos.Expression;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return f.getLabel(tpl);
	}

	public GeoFunction getFunction() {
		return g;
	}

}
