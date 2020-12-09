/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomial;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

/**
 * stores left and right hand side of an inequality as Expressions
 */
public class Inequality {

	/**
	 * Inequality type
	 */
	public enum IneqType {
		/** can be used e.g. by PointIn, but cannot be drawn */
		INEQUALITY_INVALID,

		/** x > f(y) */
		INEQUALITY_PARAMETRIC_X,
		/** y > f(x) */
		INEQUALITY_PARAMETRIC_Y,

		/** f(x,y) >0, f is linear */
		INEQUALITY_LINEAR,
		/** f(x,y) >0, f is quadratic */
		INEQUALITY_CONIC,
		/** f(x,y) >0, degree of f greater than 2 */
		INEQUALITY_IMPLICIT,

		/** inequality with one variable */
		INEQUALITY_1VAR_X,
		/** inequality with one variable, called y */
		INEQUALITY_1VAR_Y
	}

	private Operation op = Operation.LESS;
	private IneqType type;
	/* private GeoImplicitPoly impBorder; */
	private GeoConic conicBorder;
	private GeoLine lineBorder;
	private GeoFunction funBorder;
	private GeoElement border;
	@Weak
	private Kernel kernel;
	private boolean isAboveBorder;
	private ExpressionNode normal;
	private FunctionVariable[] fv;
	private MyDouble coef;
	private GeoPoint[] zeros;
	// if variable x or y appears with 0 coef, we want to replace the
	// variable by 0 itself to avoid errors on computation
	private MyDouble[] zeroDummy = new MyDouble[2];

	/**
	 * check whether ExpressionNodes are evaluable to instances of Polynomial or
	 * NumberValue and build an Inequality out of them
	 * 
	 * @param kernel
	 *            Kernel
	 * @param lhs
	 *            left hand side of the equation
	 * @param rhs
	 *            right hand side of the equation
	 * @param op
	 *            operation
	 * @param fv
	 *            variable
	 */
	public Inequality(Kernel kernel, ExpressionValue lhs, ExpressionValue rhs,
			Operation op, FunctionVariable[] fv) {

		this.op = op;
		this.kernel = kernel;
		this.fv = fv;

		if (op.equals(Operation.GREATER)
				|| op.equals(Operation.GREATER_EQUAL)) {
			normal = new ExpressionNode(kernel, lhs, Operation.MINUS, rhs);

		} else {
			normal = new ExpressionNode(kernel, rhs, Operation.MINUS, lhs);
		}

		if (normal.getLeftTree().getOperation() == Operation.ABS
				&& !normal.getRightTree().containsFreeFunctionVariable(null)) {
			normal.setLeft(normal.getLeftTree().getLeftTree().power(2));
			normal.setRight(normal.getRightTree()
					.multiply(normal.getRightTree().abs()));
		} else if (normal.getRightTree().getOperation() == Operation.ABS
				&& !normal.getLeftTree().containsFreeFunctionVariable(null)) {
			normal.setRight(normal.getRightTree().getLeftTree().power(2));
			normal.setLeft(
					normal.getLeftTree().multiply(normal.getLeftTree().abs()));
		} else if (normal.getRightTree().getOperation() == Operation.ABS
				&& normal.getLeftTree().getOperation() == Operation.ABS) {
			normal.setRight(normal.getRightTree().getLeftTree().power(2));
			normal.setLeft(normal.getLeftTree().getLeftTree().power(2));
		}
		update();
	}

	private void update() {
		if (fv.length == 1) {
			init1varFunction(0);
			if (!funBorder.isPolynomialFunction(false)) {
				type = IneqType.INEQUALITY_INVALID;
			} else if (fv[0].toString(StringTemplate.defaultTemplate)
					.equals("y")) {
				type = IneqType.INEQUALITY_1VAR_Y;
			} else {
				type = IneqType.INEQUALITY_1VAR_X;
			}

			return;
		}
		for (int i = 0; i < 2; i++) {
			if (zeroDummy[i] != null) {
				normal.replace(zeroDummy[i], fv[i]);
			}
		}
		Double coefY = normal.getCoefficient(fv[1]);
		Double coefX = normal.getCoefficient(fv[0]);
		Function fun = null;
		if (coefY != null && !DoubleUtil.isZero(coefY) && !Double.isNaN(coefY)
				&& coefX == null) {
			coef = new MyDouble(kernel, -coefY);
			isAboveBorder = coefY > 0;
			ExpressionNode m = new ExpressionNode(kernel,
					replaceDummy(normal, 1), Operation.DIVIDE, coef);
			m.simplifyLeafs();
			fun = new Function(m, fv[0]);
			type = IneqType.INEQUALITY_PARAMETRIC_Y;
		} else if (coefX != null && !DoubleUtil.isZero(coefX)
				&& !Double.isNaN(coefX) && coefY == null) {
			coef = new MyDouble(kernel, -coefX);
			isAboveBorder = coefX > 0;
			ExpressionNode m = new ExpressionNode(kernel,
					replaceDummy(normal, 0), Operation.DIVIDE, coef);
			m.simplifyLeafs();
			fun = new Function(m, fv[1]);
			type = IneqType.INEQUALITY_PARAMETRIC_X;
		} else if (coefX != null && DoubleUtil.isZero(coefX) && coefY == null) {
			replaceDummy(normal, 1);
			init1varFunction(1);
			type = funBorder.isPolynomialFunction(false)
					? IneqType.INEQUALITY_1VAR_Y : IneqType.INEQUALITY_INVALID;
		} else if (coefY != null && DoubleUtil.isZero(coefY) && coefX == null) {
			replaceDummy(normal, 1);
			init1varFunction(0);
			type = funBorder.isPolynomialFunction(false)
					? IneqType.INEQUALITY_1VAR_X : IneqType.INEQUALITY_INVALID;
		} else {
			FunctionVariable xVar = new FunctionVariable(kernel, "x");
			FunctionVariable yVar = new FunctionVariable(kernel, "y");
			ExpressionNode replaced = normal.deepCopy(kernel)
					.replace(fv[0], xVar).wrap().replace(fv[1], yVar).wrap();
			Equation equ = new Equation(kernel, replaced,
					new MyDouble(kernel, 0));

			equ.initEquation();

			if (!equ.isPolynomial()) {
				type = IneqType.INEQUALITY_INVALID;
				return;
			}
			Polynomial newBorder = equ.getNormalForm();
			if (newBorder.degree() < 2) {
				if (lineBorder == null) {
					lineBorder = new GeoLine(kernel.getConstruction());
				}
				// if we got here coefX and coefY are null #5315
				ExpressionValue[][] evs = equ.getNormalForm().getCoeff();
				lineBorder.setCoords(coefX = GeoConic.evalCoeff(evs, 1, 0),
						coefY = GeoConic.evalCoeff(evs, 0, 1),
						GeoConic.evalCoeff(evs, 0, 0));
				type = IneqType.INEQUALITY_LINEAR;
				border = lineBorder;
				isAboveBorder = coefY < 0 || coefY == 0.0 && coefX > 0;
			} else if (newBorder.degree() == 2) {
				if (conicBorder == null) {
					conicBorder = new GeoConic(kernel.getConstruction());
				}
				// conicBorder.setLabel("res");
				conicBorder.setCoeffs(equ.getNormalForm().getCoeff());
				type = IneqType.INEQUALITY_CONIC;
				border = conicBorder;
				setAboveBorderFromConic();
			} else {
				type = IneqType.INEQUALITY_INVALID;
				return;
			}
			// TODO implicit ineq
			/*
			 * if (newBorder.isGeoLine()) { type = IneqType.INEQUALITY_CONIC; if
			 * (conicBorder == null) conicBorder = new
			 * GeoConic(kernel.getConstruction()); border = conicBorder; }}
			 */
		}
		Log.trace(type + ":" + coefX + "," + coefY);
		if (type == IneqType.INEQUALITY_PARAMETRIC_X
				|| type == IneqType.INEQUALITY_PARAMETRIC_Y) {
			funBorder = new GeoFunction(kernel.getConstruction());
			funBorder.setFunction(fun);
			if (type == IneqType.INEQUALITY_PARAMETRIC_X) {
				funBorder.swapEval();
			}
		}
		if (funBorder != null) {
			border = funBorder;
		}
		if (isStrict()) {
			border.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		} else {
			border.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
		}
	}

	private ExpressionNode replaceDummy(ExpressionNode expression, int i) {
		zeroDummy[i] = new MyDouble(kernel, 0);
		ExpressionNode copy = expression.deepCopy(kernel);
		copy.replace(fv[i], zeroDummy[i]).wrap();
		return copy;
	}

	private void setAboveBorderFromConic() {
		if (conicBorder.getType() == GeoConicNDConstants.CONIC_EMPTY
				|| conicBorder
						.getType() == GeoConicNDConstants.CONIC_SINGLE_POINT) {
			isAboveBorder = conicBorder.evaluateInSignificantPoint() >= 0;
			return;
		}
		isAboveBorder = conicBorder.evaluateInSignificantPoint() < 0;
	}

	private void init1varFunction(int varIndex) {
		Construction cons = kernel.getConstruction();
		boolean supress = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		// funBorder for inequality f(x)>g(x) is function f(x)-g(x)
		funBorder = new GeoFunction(cons, false);
		funBorder.setFunction(new Function(normal, fv[varIndex]));
		zeros = rootMultiple(funBorder);

		// for (int i = 0; i < zeros.length; i++) {
		// Log.debug(i + ":" + zeros[i]);
		// }

		cons.setSuppressLabelCreation(supress);
		border = funBorder;
		if (isStrict()) {
			border.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		} else {
			border.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
		}

	}

	final private static GeoPoint[] rootMultiple(GeoFunction f) {
		// allow functions that can be simplified to factors of polynomials
		if (!f.isPolynomialFunction(true)) {
			return null;
		}

		AlgoRootsPolynomial algo = new AlgoRootsPolynomial(f);
		GeoPoint[] g = algo.getRootPoints();
		return g;
	}

	/**
	 * Updates the coefficient k in y &lt; k*f(x) for parametric, for implicit
	 * runs full update.
	 */
	public void updateCoef() {
		Double coefVal = null, otherVal = null;
		if (type == IneqType.INEQUALITY_PARAMETRIC_Y) {
			coefVal = normal.getCoefficient(fv[1]);
			otherVal = normal.getCoefficient(fv[0]);

		} else if (type == IneqType.INEQUALITY_PARAMETRIC_X) {
			coefVal = normal.getCoefficient(fv[0]);
			otherVal = normal.getCoefficient(fv[1]);
		}
		if (coefVal == null || coefVal == 0
				|| (otherVal != null && Math.abs(otherVal) > Math.abs(coefVal))) {
			update();
		} else {
			isAboveBorder = coefVal > 0;
			coef.set(-coefVal);
		}

	}

	// TODO remove?
	/**
	 * @return implicit border
	 */
	/*
	 * public GeoImplicitPoly getImpBorder() { return impBorder; }
	 */

	@Override
	final public String toString() {
		return "inequality";
	}

	/**
	 * @return true if strict
	 */
	public boolean isStrict() {
		return (op.equals(Operation.GREATER) || op.equals(Operation.LESS));
	}

	/**
	 * @return border for parametric equations
	 */
	public GeoFunction getFunBorder() {
		return funBorder;
	}

	/**
	 * Returns true for parametric ineqs like y &gt; border(x), false for y &lt;
	 * border(x) (for PARAMETRIC_X vars are swapped)
	 * 
	 * @return true for parametric ineqs like y &gt; border(x), false for y &lt;
	 *         border(x)
	 * 
	 */
	public boolean isAboveBorder() {
		return isAboveBorder;
	}

	/**
	 * Returns border, which can be function, conic or implicit polynomial
	 * 
	 * @return border
	 */
	public GeoElement getBorder() {
		return border;
	}

	/**
	 * Returns type of ineq
	 * 
	 * @return inequality type
	 */
	public IneqType getType() {
		return type;
	}

	/**
	 * @return the conicBorder
	 */
	public GeoConic getConicBorder() {
		return conicBorder;
	}

	/**
	 * @return the lineBorder
	 */
	public GeoLine getLineBorder() {
		return lineBorder;
	}

	/**
	 * @return zero points for 1var ineqs
	 */
	public GeoPoint[] getZeros() {
		return zeros;
	}

	/**
	 * @return operation in ineq
	 */
	public Operation getOperation() {
		return op;
	}

	/**
	 * @return simple expression of inequality
	 */
	public ExpressionNode getNormalExpression() {
		return normal;
	}

} // end of class Equation
