package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;

/**
 * Computes
 */
public class AlgoImplicitPolyFunction extends AlgoElement {

	private GeoFunctionNVar function; // input
	private GeoImplicit implicitPoly; // output

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param func
	 *            function
	 */
	public AlgoImplicitPolyFunction(Construction c, String label,
			GeoFunctionNVar func) {
		super(c);
		function = func;
		implicitPoly = kernel.newImplicitPoly(cons);
		setInputOutput();
		compute();
		implicitPoly.setLabel(label);
	}

	@Override
	public void compute() {
		implicitPoly.setDefined();
		FunctionNVar f = function.getFunction();
		FunctionVariable[] fvars = function.getFunctionVariables();
		if (fvars.length != 2 || f == null) {
			implicitPoly.setUndefined();
			return;
		}
		try {
			ExpressionNode en = f.getExpression().getCopy(kernel);
			/*
			 * FunctionVariable xVar=new FunctionVariable(kernel,"x");
			 * FunctionVariable yVar=new FunctionVariable(kernel,"y");
			 * en.replace(fvars[0], xVar); en.replace(fvars[1], yVar);
			 */
			Equation equ = new Equation(kernel, en, new MyDouble(kernel));
			equ.initEquation();
			Polynomial poly = equ.getNormalForm();
			implicitPoly.fromEquation(equ, null);
			if (equ.mayBePolynomial()) {
				implicitPoly.setCoeff(poly.getCoeff());
			} else {
				implicitPoly.setCoeff((double[][]) null);
			}
		} catch (MyError e) {
			Log.debug(e.getMessage());
			implicitPoly.setUndefined();
		}
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { function };
		setOutputLength(1);
		setOutput(0, implicitPoly.toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	@Override
	public Commands getClassName() {
		return Commands.ImplicitCurve;
	}

	/**
	 * @return resulting polynomial
	 */
	public GeoImplicit getImplicitPoly() {
		return implicitPoly;
	}

}
