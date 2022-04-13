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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.VectorArithmetic;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.util.debug.Log;

/**
 * Cartesian curve: Curve[ x-expression in var, y-expression in var, var, from,
 * to]
 * 
 * @author Markus Hohenwarter
 */
public class AlgoCurveCartesian extends AlgoElement {

	private GeoNumberValue[] coords; // input
	private GeoNumberValue from; // input
	private GeoNumberValue to; // input
	private GeoNumeric localVar; // input
	private GeoCurveCartesianND curve; // output
	private boolean[] containsFunctions;
	private ExpressionNode[] exp;
	private boolean vectorFunctions;

	/**
	 * Creates new AlgoJoinPoints
	 * 
	 * @param cons
	 *            construction
	 * @param point
	 *            point expression (null when defined per coord)
	 * @param coords
	 *            coordinate expressions
	 * @param localVar
	 *            variable
	 * @param from
	 *            min parameter
	 * @param to
	 *            max parameter
	 */
	public AlgoCurveCartesian(Construction cons, ExpressionNode point,
			GeoNumberValue[] coords, GeoNumeric localVar, GeoNumberValue from,
			GeoNumberValue to) {
		super(cons);

		this.coords = coords;
		this.from = from;
		this.to = to;
		this.localVar = localVar;

		// we need to create Function objects for the coord NumberValues,
		// so let's get the expressions of xcoord and ycoord and replace
		// the localVar by a functionVar
		FunctionVariable funVar = new FunctionVariable(kernel, localVar.getLabelSimple());

		exp = new ExpressionNode[coords.length];
		Function[] fun = new Function[coords.length];
		containsFunctions = new boolean[coords.length];
		vectorFunctions = point != null
				&& AlgoDependentFunction.containsVectorFunctions(point);
		for (int i = 0; i < coords.length; i++) {
			exp[i] = kernel.convertNumberValueToExpressionNode(
					coords[i].toGeoElement());
			exp[i] = exp[i].replace(localVar, funVar).wrap();
			fun[i] = new Function(exp[i], funVar);
			containsFunctions[i] = vectorFunctions
					|| AlgoDependentFunction
					.containsFunctions(exp[i]);
		}

		// create the curve
		curve = createCurve(cons, fun, point);

		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
	}

	/**
	 * creates a curve
	 * 
	 * @param cons1
	 *            construction
	 * @param fun
	 *            functions
	 * @param point
	 *            point expression
	 * @return a curve
	 */
	protected GeoCurveCartesianND createCurve(Construction cons1,
			Function[] fun, ExpressionNode point) {
		return new GeoCurveCartesian(cons1, fun[0], fun[1], point);
	}

	@Override
	public Commands getClassName() {
		return Commands.CurveCartesian;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		int offset = coords.length;
		if (curve.getPointExpression() != null) {
			input = new GeoElement[4];
			offset = 1;
			input[0] = new AlgoDependentFunction(cons,
					new Function(curve.getPointExpression(),
							new FunctionVariable(kernel)),
					false).getFunction();
			for (int i = 0; i < offset; i++) {
				coords[i].toGeoElement().addAlgorithm(this);
			}
		} else {
			input = new GeoElement[offset + 3];
			for (int i = 0; i < offset; i++) {
				input[i] = coords[i].toGeoElement();
			}
		}
		input[offset] = localVar;
		input[offset + 1] = from.toGeoElement();
		input[offset + 2] = to.toGeoElement();

		super.setOutputLength(1);
		super.setOutput(0, curve);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting curve
	 */
	public GeoCurveCartesianND getCurve() {
		return curve;
	}

	@Override
	public final void compute() {

		// take care to set the curve undefined
		// if its predecessors are undefined
		for (int i = 0; i < coords.length; i++) {
			AlgoElement algo = null;
			if (coords[i].toGeoElement() != null) {
				algo = (coords[i].toGeoElement()).getParentAlgorithm();
			}
			if (algo != null && algo.isUndefined()) {
				curve.setUndefined();
				return;
			}
			if (containsFunctions[i]) {
				ExpressionValue ev = null;
				try { // needed for eg f(x)=floor(x) f'(x)

					// boolean internationalizeDigits =
					// Kernel.internationalizeDigits;
					// Kernel.internationalizeDigits = false;
					// TODO: seems that we never read internationalize digits
					// flag
					// here ...
					if (vectorFunctions) {
						exp[i] = VectorArithmetic
								.computeCoord(curve.getPointExpression(), i)
								.replace(localVar,
										curve.getFun(i).getFunctionVariable())
								.wrap();
					}
					ev = AlgoDependentFunction.expandFunctionDerivativeNodes(
							exp[i].deepCopy(kernel), false);

					// Kernel.internationalizeDigits = internationalizeDigits;

				} catch (Exception e) {
					Log.debug(e);
					Log.debug("derivative failed");
				}
				if (ev == null) {
					curve.setUndefined();
					return;
				}
				ExpressionNode node = ev.wrap();

				// expandedFun.setExpression(node);

				curve.getFun(i).setExpression(node);
			}
		}
		curve.setDefined(true);

		// the coord-functions don't have to be updated,
		// so we only set the interval
		curve.setInterval(from.getDouble(), to.getDouble());
	}

	@Override
	public boolean mayShowDescriptionInsteadOfDefinition() {
		return false;
	}

}
