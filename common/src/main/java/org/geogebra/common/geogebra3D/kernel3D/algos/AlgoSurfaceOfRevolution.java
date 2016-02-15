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
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.plugin.Operation;

/**
 * Cartesian curve: Curve[ x-expression in var, y-expression in var, var, from,
 * to]
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class AlgoSurfaceOfRevolution extends AlgoElement {

	private GeoFunction function; // input
	private NumberValue angle; // input

	private GeoSurfaceCartesian3D surface; // output


	/**
	 * 
	 * @param cons
	 * @param label
	 * @param function
	 * @param angle
	 */
	public AlgoSurfaceOfRevolution(Construction cons, String label,
			GeoFunction function, NumberValue angle) {

		super(cons);

		this.function = function;
		this.angle = angle;

		// functions describing the surface
		FunctionVariable[] funVar = new FunctionVariable[2];
		funVar[0] = new FunctionVariable(kernel);
		funVar[0].setVarString("u");
		funVar[1] = new FunctionVariable(kernel);
		funVar[1].setVarString("v");

		ExpressionNode expU = new ExpressionNode(kernel, funVar[0]);
		ExpressionNode expF = new ExpressionNode(kernel, function,
				Operation.FUNCTION, funVar[0]);
		ExpressionNode expV = new ExpressionNode(kernel, funVar[1]);
		ExpressionNode expCos = expV.cos();
		ExpressionNode expSin = expV.sin();

		FunctionNVar[] fun = new FunctionNVar[3];
		fun[0] = new FunctionNVar(expU, funVar);
		fun[1] = new FunctionNVar(expCos.multiply(expF), funVar);
		fun[2] = new FunctionNVar(expSin.multiply(expF), funVar);


		min = new double[2];
		max = new double[2];
		min[0] = Double.NEGATIVE_INFINITY;
		max[0] = Double.POSITIVE_INFINITY;
		min[1] = 0;

		// create the curve
		surface = createSurface(cons, fun);
		surface.setIsSurfaceOfRevolutionAroundOx(true);

		setInputOutput(); // for AlgoElement

		// compute value
		compute();
		surface.setLabel(label);

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
		input = new GeoElement[2];
		input[0] = function;
		input[1] = (GeoElement) angle;
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

		// set min, max, and localVar (to avoid false undefined)
		max[1] = angle.getDouble();

		// the coord-functions don't have to be updated,
		// so we only set the interval
		surface.setIntervals(min, max);


	}


	// TODO Consider locusequability
}
