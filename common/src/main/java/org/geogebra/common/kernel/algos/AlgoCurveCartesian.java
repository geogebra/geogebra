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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.main.App;

/**
 * Cartesian curve: Curve[ x-expression in var, y-expression in var, var, from,
 * to]
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class AlgoCurveCartesian extends AlgoElement {

	private NumberValue[] coords; // input
	private NumberValue from, to; // input
	private GeoNumeric localVar; // input
	private GeoCurveCartesianND curve; // output
	private boolean[] containsFunctions;
	private ExpressionNode[] exp;

	/** Creates new AlgoJoinPoints */
	public AlgoCurveCartesian(Construction cons, ExpressionNode point,
			NumberValue[] coords, GeoNumeric localVar, NumberValue from,
			NumberValue to) {
		super(cons);

		this.coords = coords;
		this.from = from;
		this.to = to;
		this.localVar = localVar;

		// we need to create Function objects for the coord NumberValues,
		// so let's get the expressions of xcoord and ycoord and replace
		// the localVar by a functionVar
		FunctionVariable funVar = new FunctionVariable(kernel);
		funVar.setVarString(localVar.getLabelSimple());

		exp = new ExpressionNode[coords.length];
		Function[] fun = new Function[coords.length];
		containsFunctions = new boolean[coords.length];

		for (int i = 0; i < coords.length; i++) {
			exp[i] = kernel.convertNumberValueToExpressionNode(coords[i]
					.toGeoElement());
			exp[i] = exp[i].replace(localVar, funVar).wrap();
			fun[i] = new Function(exp[i], funVar);
			containsFunctions[i] = AlgoDependentFunction.containsFunctions(exp[i]);
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
	 * @param cons
	 * @param fun
	 * @return a curve
	 */
	protected GeoCurveCartesianND createCurve(Construction cons,
			Function[] fun, ExpressionNode point) {
		return new GeoCurveCartesian(cons, fun[0], fun[1], point);
	}

	@Override
	public Commands getClassName() {
		return Commands.CurveCartesian;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[coords.length + 3];

		for (int i = 0; i < coords.length; i++)
			input[i] = coords[i].toGeoElement();
		input[coords.length] = localVar;
		input[coords.length + 1] = from.toGeoElement();
		input[coords.length + 2] = to.toGeoElement();

		super.setOutputLength(1);
		super.setOutput(0, curve);
		setDependencies(); // done by AlgoElement
	}

	public GeoCurveCartesianND getCurve() {
		return curve;
	}

	@Override
	public final void compute() {

		// take care to set the curve undefined
		// if its predecessors are undefined
		for (int i = 0; i <= 1; i++) {
			AlgoElement algo = null;
			if (coords[i].toGeoElement() != null)
				algo = (coords[i].toGeoElement()).getParentAlgorithm();
			if (algo != null && algo.isUndefined()) {
				curve.setUndefined();
				return;
			}
			if(containsFunctions[i]){
				ExpressionValue ev = null;
				try { // needed for eg f(x)=floor(x) f'(x)

					// boolean internationalizeDigits =
					// Kernel.internationalizeDigits;
					// Kernel.internationalizeDigits = false;
					// TODO: seems that we never read internationalize digits flag
					// here ...
					ev = AlgoDependentFunction.expandFunctionDerivativeNodes(exp[i].deepCopy(kernel));

					// Kernel.internationalizeDigits = internationalizeDigits;

				} catch (Exception e) {
					e.printStackTrace();
					App.debug("derivative failed");
				}
				if (ev == null) {
					curve.setUndefined();
					return;
				}
				ExpressionNode node;
				if (ev.isExpressionNode())
					node = (ExpressionNode) ev;
				else
					node = new ExpressionNode(kernel, ev);

				//expandedFun.setExpression(node);
				
				curve.getFun(i).setExpression(node);
			}
		}
		curve.setDefined(true);

		// the coord-functions don't have to be updated,
		// so we only set the interval
		curve.setInterval(from.getDouble(), to.getDouble());
	}

	// TODO Consider locusequability
}
