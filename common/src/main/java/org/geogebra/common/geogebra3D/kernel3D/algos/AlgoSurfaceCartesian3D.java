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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;

/**
 * Cartesian curve: Curve[ x-expression in var, y-expression in var, var, from,
 * to]
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class AlgoSurfaceCartesian3D extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private NumberValue[] coords; // input
	private NumberValue[] from, to; // input
	private GeoNumeric[] localVar; // input
	private GeoSurfaceCartesianND surface; // output

	/** Creates new AlgoJoinPoints */
	public AlgoSurfaceCartesian3D(Construction cons, String label,
			ExpressionNode point,
			NumberValue[] coords, GeoNumeric[] localVar, NumberValue[] from,
			NumberValue[] to) {
		super(cons);

		this.coords = coords;
		this.from = from;
		this.to = to;
		this.localVar = localVar;

		// we need to create Function objects for the coord NumberValues,
		// so let's get the expressions of xcoord and ycoord and replace
		// the localVar by a functionVar
		FunctionVariable[] funVar = new FunctionVariable[localVar.length];
		for (int i = 0; i < localVar.length; i++) {
			funVar[i] = new FunctionVariable(kernel);
			funVar[i].setVarString(localVar[i]
					.getLabel(StringTemplate.defaultTemplate));
		}

		ExpressionNode[] exp = new ExpressionNode[coords.length];
		FunctionNVar[] fun = new FunctionNVar[coords.length];

		for (int i = 0; i < coords.length; i++) {
			exp[i] = kernel.convertNumberValueToExpressionNode(coords[i]
					.toGeoElement());
			for (int j = 0; j < localVar.length; j++) {
				exp[i] = exp[i].replace(localVar[j], funVar[j]).wrap();
			}
			fun[i] = new FunctionNVar(exp[i], funVar);
		}


		// create the curve
		surface = createCurve(cons, point, fun);

		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
		surface.setLabel(label);
	}

	/**
	 * creates a curve
	 * 
	 * @param cons
	 * @param fun
	 * @return a curve
	 */
	protected GeoSurfaceCartesianND createCurve(Construction cons,
			ExpressionNode point, FunctionNVar[] fun) {
		return new GeoSurfaceCartesian3D(cons, point, fun);
	}

	@Override
	public Commands getClassName() {
		return Commands.Surface;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		int offset = coords.length;
		if (surface.getPointExpression() != null) {
			input = new GeoElement[1 + 3 * localVar.length];
			offset = 1;
			input[0] = new AlgoDependentFunction(cons, new Function(
					surface.getPointExpression(), new FunctionVariable(kernel)))
					.getFunction();
			cons.removeFromConstructionList(input[0].getParentAlgorithm());
			for (int i = 0; i < offset; i++) {
				coords[i].toGeoElement().addAlgorithm(this);
			}
		} else {
			input = new GeoElement[coords.length + 3 * localVar.length];
			for (int i = 0; i < coords.length; i++)
				input[i] = coords[i].toGeoElement();
		}


		for (int i = 0; i < localVar.length; i++) {
			input[offset + 3 * i] = localVar[i];
			input[offset + 3 * i + 1] = from[i].toGeoElement();
			input[offset + 3 * i + 2] = to[i].toGeoElement();
		}

		setOnlyOutput(surface);
		setDependencies(); // done by AlgoElement
	}

	public GeoSurfaceCartesianND getSurface() {
		return surface;
	}

	@Override
	public final void compute() {

		double[] min = new double[from.length];
		double[] max = new double[to.length];

		// set min, max, and localVar (to avoid false undefined)
		for (int i = 0; i < 2; i++) {
			min[i] = from[i].getDouble();
			max[i] = to[i].getDouble();
			localVar[i].setValue(min[i]);
		}

		// take care to set the surface undefined
		// if its predecessors are undefined
		for (int i = 0; i < 3; i++) {
			AlgoElement algo = null;
			if (coords[i].toGeoElement() != null)
				algo = (coords[i].toGeoElement()).getParentAlgorithm();
			if (algo != null) {
				for (GeoElement geo : algo.getInput()) {
					if (!geo.isDefined()) {
						surface.setUndefined();
						return;
					}
				}
			}
			// if (containsFunctions[i]) {
			// ExpressionValue ev = null;
			// try { // needed for eg f(x)=floor(x) f'(x)
			//
			// // boolean internationalizeDigits =
			// // Kernel.internationalizeDigits;
			// // Kernel.internationalizeDigits = false;
			// // TODO: seems that we never read internationalize digits
			// // flag
			// // here ...
			// ev = AlgoDependentFunction
			// .expandFunctionDerivativeNodes(exp[i]
			// .deepCopy(kernel));
			//
			// // Kernel.internationalizeDigits = internationalizeDigits;
			//
			// } catch (Exception e) {
			// e.printStackTrace();
			// App.debug("derivative failed");
			// }
			// if (ev == null) {
			// curve.setUndefined();
			// return;
			// }
			// ExpressionNode node;
			// if (ev.isExpressionNode())
			// node = (ExpressionNode) ev;
			// else
			// node = new ExpressionNode(kernel, ev);
			//
			// // expandedFun.setExpression(node);
			//
			// curve.getFun(i).setExpression(node);
			// }
		}

		// the coord-functions don't have to be updated,
		// so we only set the interval
		surface.setIntervals(min, max);


	}


	// TODO Consider locusequability
}
