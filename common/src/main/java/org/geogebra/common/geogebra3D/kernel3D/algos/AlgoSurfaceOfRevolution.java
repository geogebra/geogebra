/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSurfaceCartesian3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.ChangeableParent;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.Parametrizable;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Cartesian curve: Curve[ x-expression in var, y-expression in var, var, from,
 * to]
 * 
 * @author Markus Hohenwarter
 */
public class AlgoSurfaceOfRevolution extends AlgoElement {

	private ParametricCurve function; // input
	private GeoNumberValue angle; // input

	private GeoSurfaceCartesian3D surface; // output
	private GeoLineND line;
	private FunctionVariable[] funVar;
	private Path path;
	private double[] min;
	private double[] max;

	/**
	 * @param cons
	 *            construction
	 * @param function
	 *            path to be rotated
	 * @param angle
	 *            max angle
	 */
	public AlgoSurfaceOfRevolution(Construction cons,
			Parametrizable function, GeoNumberValue angle) {
		this(cons, function, angle, null);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param path
	 *            path to be rotated
	 * @param angle
	 *            max angle
	 * @param line
	 *            rotation axis
	 */
	public AlgoSurfaceOfRevolution(Construction cons, Path path,
			GeoNumberValue angle, GeoLineND line) {

		super(cons);
		if (path instanceof ParametricCurve) {
			this.function = (ParametricCurve) path;
		} else {
			GeoCurveCartesianND gc = kernel.getGeoFactory()
					.newCurve(path.isGeoElement3D() ? 3 : 2, cons);
			this.function = gc;
		}

		this.angle = angle;
		this.line = line == null ? kernel.getXAxis() : line;
		this.path = path;
		min = new double[2];
		max = new double[2];
		min[1] = 0;
		this.funVar = new FunctionVariable[2];
		funVar[0] = new FunctionVariable(kernel, "u");
		funVar[1] = new FunctionVariable(kernel, "v");

		FunctionNVar[] fun = new FunctionNVar[3];
		fun[0] = new FunctionNVar(funVar[0].wrap(), funVar);
		fun[1] = new FunctionNVar(funVar[0].wrap(), funVar);
		fun[2] = new FunctionNVar(funVar[0].wrap(), funVar);
		surface = createSurface(cons, fun);

		if (path instanceof ParametricCurve
				&& ((ParametricCurve) path).isFunctionInX()) {
			surface.setIsSurfaceOfRevolutionAroundOx(true);
		}
		GeoNumeric changeableAngle = ChangeableParent.getGeoNumeric(angle);
		if (changeableAngle != null) {
			ChangeableParent changeableParent = new ChangeableParent(
					changeableAngle, this.line,
					new RotationConverter(this.line));
			surface.setChangeableParent(changeableParent);
		}

		setInputOutput(); // for AlgoElement

		// compute value
		compute();

		cons.registerEuclidianViewCE(this);
	}

	/**
	 * creates a surface
	 * 
	 * @param cons1
	 *            construction
	 * @param fun
	 *            functions
	 * @return a curve
	 */
	protected GeoSurfaceCartesian3D createSurface(Construction cons1,
			FunctionNVar[] fun) {
		return new GeoSurfaceCartesian3D(cons1, null, fun);
	}

	@Override
	public Commands getClassName() {
		return Commands.Surface;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = path.toGeoElement();
		input[1] = (GeoElement) angle;
		input[2] = line.toGeoElement();

		setOnlyOutput(surface);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting surface
	 */
	public GeoSurfaceCartesianND getSurface() {
		return surface;
	}

	@Override
	public final void compute() {
		if (path instanceof Parametrizable) {
			((Parametrizable) path)
					.toGeoCurveCartesian((GeoCurveCartesianND) function);
		}
		if (function.isDefined() && angle.isDefined()) {
			surface.setDefined(true);
		} else {
			surface.setUndefined();
			return;
		}
		boolean isXAxis = line == kernel.getXAxis();
		ExpressionValue[][] coeffs = new ExpressionValue[4][4];
		FunctionNVar[] fun = surface.getFunctions();
		if (isXAxis) {
			rotation4x4(Coords.VX, funVar[1], coeffs, kernel);
			transform(function, coeffs, fun, Coords.O);
		} else {
			rotation4x4(line.getDirectionInD3().normalized(), funVar[1], coeffs, kernel);
			transform(function, coeffs, fun, line.getStartInhomCoords());
		}

		String var = function.getFunctionVariables()[0] + "";
		for (int i = 0; i < 3; i++) {
			fun[i].getExpression().replaceVariables(var, funVar[0]);
			fun[i].getExpression().replaceVariables(
					funVar[1].toString(StringTemplate.defaultTemplate), funVar[1]);
		}
		min[0] = function.getMinParameter();
		max[0] = function.getMaxParameter();
		// set min, max, and localVar (to avoid false undefined)
		max[1] = angle.getDouble();

		// the coord-functions don't have to be updated,
		// so we only set the interval
		surface.setIntervals(min, max);
	}

	private void transform(ParametricCurve curve, ExpressionValue[][] m,
			FunctionNVar[] fun1, Coords startPoint) {
		// current expressions
		ExpressionNode[] expr = new ExpressionNode[3];
		for (int i = 0; i < 3; i++) {
			expr[i] = curve.getFun(i).deepCopy(kernel).getExpression()
					.subtract(startPoint.get(i + 1));
		}

		for (int row = 0; row < 3; row++) {
			ExpressionValue[] coeff = m[row];

			ExpressionNode trans = new ExpressionNode(kernel, coeff[3]);
			for (int i = 0; i < 3; i++) {
				trans = trans.plus(expr[i].multiplyR(coeff[i]));
			}

			fun1[row].setExpression(trans.plus(startPoint.get(row + 1)));
		}
	}

	private static final void rotation4x4(Coords u, ExpressionValue angle,
			ExpressionValue[][] m, Kernel kernel) {

		double ux = u.getX();
		double uy = u.getY();
		double uz = u.getZ();

		ExpressionNode c = angle.wrap().cos();
		ExpressionNode s = angle.wrap().sin();
		ExpressionNode oneMinusC = new ExpressionNode(kernel, 1).subtract(c);
		// Coords[] vec = m.vectors;
		m[0][0] = diagonalCoeff(ux, c, kernel);
		m[0][1] = oneMinusC.multiply(ux * uy).subtract(s.multiply(uz));
		m[0][2] = oneMinusC.multiply(ux * uz).plus(s.multiply(uy));
		// vals[3] = 0;

		m[1][0] = oneMinusC.multiply(ux * uy).plus(s.multiply(uz));
		m[1][1] = diagonalCoeff(uy, c, kernel);
		m[1][2] = oneMinusC.multiply(uy * uz).subtract(s.multiply(ux));
		// vals[7] = 0;

		m[2][0] = oneMinusC.multiply(ux * uz).subtract(s.multiply(uy));
		m[2][1] = oneMinusC.multiply(uy * uz).plus(s.multiply(ux));
		m[2][2] = diagonalCoeff(uz, c, kernel);

		for (int i = 0; i < 3; i++) {
			m[3][i] = new ExpressionNode(kernel, 0);
			m[i][3] = new ExpressionNode(kernel, 0);
		}

		m[3][3] = new ExpressionNode(kernel, 1);
	}

	private static ExpressionValue diagonalCoeff(double ux, ExpressionNode c,
			Kernel kernel) {
		// use plus(ExpressionValue) rather than plus(double) to make sure
		// zeros are canceled
		return c.multiply(1 - ux * ux).plus(new MyDouble(kernel, ux * ux));
	}

}
