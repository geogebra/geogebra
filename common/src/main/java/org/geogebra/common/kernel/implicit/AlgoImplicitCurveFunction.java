package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;

/**
 * Implicit curve
 */
public class AlgoImplicitCurveFunction extends AlgoElement {
	private GeoFunctionNVar func;
	private GeoImplicitCurve implicitCurve;

	/**
	 * Construct a new {@link AlgoImplicitCurveFunction}
	 * 
	 * @param c
	 *            {@link Construction}
	 * @param label
	 *            Label
	 * @param func
	 *            function
	 */
	public AlgoImplicitCurveFunction(Construction c, String label,
			GeoFunctionNVar func) {
		super(c);
		this.func = func;
		this.implicitCurve = new GeoImplicitCurve(cons);
		setInputOutput();
		compute();
		implicitCurve.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { func };
		setOutputLength(1);
		setOutput(0, implicitCurve);
		setDependencies();
	}

	@Override
	public void compute() {
		if (func.getFunctionVariables().length > 2) {
			implicitCurve.setUndefined();
			return;
		}
		try {
			implicitCurve.setDefined();
			ExpressionNode lhs = func.getFunctionExpression();
			ExpressionNode rhs = new ExpressionNode(kernel, 0.0);
			Equation eqn = new Equation(kernel, lhs, rhs);
			implicitCurve.fromEquation(eqn);
		} catch (Exception ex) {
			implicitCurve.setUndefined();
		}
	}

	@Override
	public GetCommand getClassName() {
		return Commands.ImplicitCurve;
	}

	/**
	 * 
	 * @return {@link GeoImplicitCurve} for the function
	 */
	public GeoImplicitCurve getImplicitCurve() {
		return implicitCurve;
	}
}
