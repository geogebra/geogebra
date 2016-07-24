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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.plugin.Operation;

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

	public AlgoSurfaceOfRevolution(Construction cons, String label,
			ParametricCurve function, GeoNumberValue angle) {
		this(cons, label, function, angle, null);
	}

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param function
	 * @param angle
	 */
	public AlgoSurfaceOfRevolution(Construction cons, String label,
			ParametricCurve function, GeoNumberValue angle, GeoLineND line) {

		super(cons);

		this.function = function;
		this.angle = angle;
		this.line = line;
		min = new double[2];
		max = new double[2];
		min[1] = 0;
		this.funVar = new FunctionVariable[2];
		funVar[0] = new FunctionVariable(kernel);
		funVar[0].setVarString("u");
		funVar[1] = new FunctionVariable(kernel);
		funVar[1].setVarString("v");
		if (line == null) {
			rotateAroundX();
		} else {


			FunctionNVar[] fun = new FunctionNVar[3];
			fun[0] = new FunctionNVar(funVar[0].wrap(), funVar);
			fun[1] = new FunctionNVar(funVar[0].wrap(), funVar);
			fun[2] = new FunctionNVar(funVar[0].wrap(), funVar);
			surface = createSurface(cons, fun);




		}
		if (function.isFunctionInX()) {
			surface.setIsSurfaceOfRevolutionAroundOx(true);
		}

		setInputOutput(); // for AlgoElement

		// compute value
		compute();
		surface.setLabel(label);

	}

	private void rotateAroundX() {

		// functions describing the surface
		ExpressionNode expU, expF, expV;
		if (function.isFunctionInX()) {
			expU = new ExpressionNode(kernel, funVar[0]);
			expF = new ExpressionNode(kernel, function,
				Operation.FUNCTION, funVar[0]);
		} else {
			expF = function.getFun(1).getExpression().deepCopy(kernel).replace(
					function.getFun(1).getFunctionVariable(), funVar[0]).wrap();
			expU = function.getFun(0).getExpression().deepCopy(kernel).replace(
					function.getFun(0).getFunctionVariable(), funVar[0]).wrap();

		}
		expV = new ExpressionNode(kernel, funVar[1]);
		ExpressionNode expCos = expV.cos();
		ExpressionNode expSin = expV.sin();

		FunctionNVar[] fun = new FunctionNVar[3];
		fun[0] = new FunctionNVar(expU, funVar);
		fun[1] = new FunctionNVar(expCos.multiply(expF), funVar);
		fun[2] = new FunctionNVar(expSin.multiply(expF), funVar);


		// create the curve
		surface = createSurface(cons, fun);
		

	}

	/**
	 * creates a surface
	 * 
	 * @param cons
	 * @param fun
	 * @return a curve
	 */
	protected GeoSurfaceCartesian3D createSurface(Construction cons,
			FunctionNVar[] fun) {
		return new GeoSurfaceCartesian3D(cons, null, fun);
	}

	@Override
	public Commands getClassName() {
		return Commands.Surface;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[line == null ? 2 : 3];
		input[0] = function.toGeoElement();
		input[1] = (GeoElement) angle;
		if (line != null) {
			input[2] = line.toGeoElement();
		}
		setOnlyOutput(surface);
		setDependencies(); // done by AlgoElement
	}

	public GeoSurfaceCartesianND getSurface() {
		return surface;
	}

	private double[] min, max;

	@Override
	public final void compute() {

		if (function.isDefined() && angle.isDefined()) {
			surface.setDefined(true);
		} else {
			surface.setUndefined();
		}
		if (line != null) {
			ExpressionValue[][] coeffs = new ExpressionValue[4][4];
			Rotation4x4(line.getDirectionInD3().normalized(),
					funVar[1],
					line.getStartInhomCoords(), coeffs);
			FunctionNVar[] fun = surface.getFunctions();
			transform(function, coeffs, fun);
			String var = function.getFunctionVariables()[0] + "";
			for (int i = 0; i < 3; i++) {
				fun[i].getExpression().replaceVariables(var, funVar[0]);
				fun[i].getExpression().replaceVariables(
						funVar[1].toString(StringTemplate.defaultTemplate),
						funVar[1]);
			}
		}
		min[0] = function.getMinParameter();
		max[0] = function.getMaxParameter();
		// set min, max, and localVar (to avoid false undefined)
		max[1] = angle.getDouble();

		// the coord-functions don't have to be updated,
		// so we only set the interval
		surface.setIntervals(min, max);


	}

	public void transform(ParametricCurve curve, ExpressionValue[][] m,
			FunctionNVar[] fun1) {

		// current expressions
		ExpressionNode[] expr = new ExpressionNode[3];
		for (int i = 0; i < 3; i++) {
			expr[i] = curve.getFun(i).deepCopy(kernel).getExpression()
					.subtract(line.getStartInhomCoords().get(i + 1));
		}

		for (int row = 0; row < 3; row++) {
			ExpressionValue[] coeff = m[row];

			ExpressionNode trans = new ExpressionNode(kernel, coeff[3]);
			for (int i = 0; i < 3; i++) {
				trans = trans.plus(expr[i].multiply(coeff[i]));
			}

			fun1[row].setExpression(trans.plus(line.getStartInhomCoords().get(
					row + 1)));
		}

	}

	public static final void Rotation4x4(Coords u, ExpressionValue angle,
			Coords center, ExpressionValue[][] m) {

		double ux = u.getX();
		double uy = u.getY();
		double uz = u.getZ();

		ExpressionNode c = angle.wrap().cos();
		ExpressionNode s = angle.wrap().sin();
		Kernel kernel = c.getKernel();
		ExpressionNode oneMinusC = new ExpressionNode(kernel, 1).subtract(c);
		// Coords[] vec = m.vectors;
		m[0][0] = oneMinusC.multiply(ux * ux).plus(c);
		m[0][1] = oneMinusC.multiply(ux * uy).plus(s.multiply(uz));
		m[0][2] = oneMinusC.multiply(ux * uz).subtract(s.multiply(uy));
		// vals[3] = 0;

		m[1][0] = oneMinusC.multiply(ux * uy).subtract(s.multiply(uz));
		m[1][1] = oneMinusC.multiply(uy * uy).plus(c);
		m[1][2] = oneMinusC.multiply(uy * uz).plus(s.multiply(ux));
		// vals[7] = 0;

		m[2][0] = oneMinusC.multiply(ux * uz).plus(s.multiply(uy));
		m[2][1] = oneMinusC.multiply(uy * uz).subtract(s.multiply(ux));
		m[2][2] = oneMinusC.multiply(uz * uz).plus(c);

		m[3][0] = new ExpressionNode(kernel, 0);
		m[3][1] = new ExpressionNode(kernel, 0);
		m[3][2] = new ExpressionNode(kernel, 0);
		// vals[11] = 0;
		m[0][3] = new ExpressionNode(kernel, 0);
		m[1][3] = new ExpressionNode(kernel, 0);
		m[2][3] = new ExpressionNode(kernel, 0);

		m[3][3] = new ExpressionNode(kernel, 1);
		// use (Id-M)center for translation
		// vec[3].set(0.0);
		// m.setOrigin(center.sub(m.mul(center)));

	}

	// TODO Consider locusequability
}
