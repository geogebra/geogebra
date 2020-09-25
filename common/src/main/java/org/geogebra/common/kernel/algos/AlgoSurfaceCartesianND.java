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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.VectorArithmetic;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;

/**
 * Cartesian curve: Curve[ x-expression in var, y-expression in var, var, from,
 * to]
 * 
 * @author Markus Hohenwarter
 */
public class AlgoSurfaceCartesianND extends AlgoElement {

	private GeoNumberValue[] coords; // input
	private GeoNumberValue[] from;
	private GeoNumberValue[] to; // input
	private GeoNumeric[] localVar; // input
	private GeoSurfaceCartesianND surface; // output
	private boolean vectorFunctions;

	/**
	 * Creates new algo for Surface
	 * 
	 * @param cons
	 *            construction
	 * @param point
	 *            point expression
	 * @param coords
	 *            coordinate functions
	 * @param localVar
	 *            variables
	 * @param from
	 *            range min
	 * @param to
	 *            range max
	 */
	public AlgoSurfaceCartesianND(Construction cons, ExpressionNode point, GeoNumberValue[] coords,
			GeoNumeric[] localVar, GeoNumberValue[] from, GeoNumberValue[] to) {
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
			String varStr = localVar[i].getLabel(StringTemplate.defaultTemplate);
			funVar[i] = new FunctionVariable(kernel, varStr);
		}

		ExpressionNode[] exp = new ExpressionNode[coords.length];
		FunctionNVar[] fun = new FunctionNVar[coords.length];

		for (int i = 0; i < coords.length; i++) {
			exp[i] = kernel.convertNumberValueToExpressionNode(
					coords[i].toGeoElement());
			for (int j = 0; j < localVar.length; j++) {
				exp[i] = exp[i].replace(localVar[j], funVar[j]).wrap();
			}
			fun[i] = new FunctionNVar(exp[i], funVar);
		}

		// create the curve
		surface = createCurve(cons, point, fun);
		vectorFunctions = AlgoDependentFunction.containsVectorFunctions(point);

		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
	}

	/**
	 * creates the surface
	 * 
	 * @param cons1
	 *            construction
	 * @param fun
	 *            functions
	 * @return a surface
	 */
	protected GeoSurfaceCartesianND createCurve(Construction cons1,
			ExpressionNode point, FunctionNVar[] fun) {
		return kernel.getGeoFactory().newSurface(cons1, point, fun);
	}

	@Override
	public GetCommand getClassName() {
		return surface.getComplexVariable() == null ? Commands.Surface : Algos.Expression;
	}

	@Override
	public String getDefinition(StringTemplate tpl) {
		if (surface.getComplexVariable() == null) {
			return super.getDefinition(tpl);
		}
		return getRHS(tpl);
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (surface.getDefinition() == null) {
			return super.toString(tpl);
		}
		if (surface.isLabelSet()) {
			return surface.getLabel(tpl) + "(" + surface.getVarString(tpl) + ") = " + getRHS(tpl);
		}
		return getRHS(tpl);
	}

	private String getRHS(StringTemplate tpl) {
		return surface.getDefinition() == null ? "?" : surface.getDefinition().toString(tpl);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		int offset = coords.length;
		if (surface.getPointExpression() != null) {
			input = new GeoElement[1 + 3 * localVar.length];
			offset = 1;
			input[0] = new AlgoDependentFunction(cons,
					new Function(surface.getPointExpression(),
							new FunctionVariable(kernel)),
					false).getFunction();
			for (int i = 0; i < offset; i++) {
				coords[i].toGeoElement().addAlgorithm(this);
			}
		} else {
			input = new GeoElement[coords.length + 3 * localVar.length];
			for (int i = 0; i < coords.length; i++) {
				input[i] = coords[i].toGeoElement();
			}
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
		for (int i = 0; i < coords.length; i++) {
			AlgoElement algo = null;
			if (coords[i].toGeoElement() != null) {
				algo = (coords[i].toGeoElement()).getParentAlgorithm();
			}
			if (algo != null) {
				for (GeoElement geo : algo.getInput()) {
					if (!geo.isDefined()) {
						surface.setUndefined();
						return;
					}
				}
			}
			if (vectorFunctions) {
				ExpressionNode exp = VectorArithmetic
						.computeCoord(surface.getPointExpression(), i);
				if (exp != null) {
					for (int var = 0; var < 2; var++) {
						exp = exp.replace(localVar[var],
								surface.getFunctions()[i]
										.getFunctionVariables()[var])
							.wrap();
					}
					ExpressionValue ev = AlgoDependentFunction
							.expandFunctionDerivativeNodes(exp.deepCopy(kernel),
									false);
					surface.getFunctions()[i].setExpression(ev.wrap());
				} else {
					surface.setUndefined();
				}
			}
		}

		// the coord-functions don't have to be updated,
		// so we only set the interval
		surface.setIntervals(min, max);

	}

	public GeoNumeric getLocalVar(int i) {
		return this.localVar[i];
	}

	@Override
	protected String toExpString(StringTemplate tpl) {
		if (!surface.isDefined() && surface.getComplexVariable() != null) {
			return surface.getAssignmentLHS(tpl) + " = ?";
		}
		return toString(tpl);
	}
}
