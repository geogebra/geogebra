/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * Function limited to interval [a, b]
 */
public class AlgoFunctionFreehand extends AlgoElement {

	private GeoList inputList; // input
	private GeoFunction g; // output g

	/**
	 * Creates new AlgoDependentFunction
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            domain endpoints and function values
	 */
	public AlgoFunctionFreehand(Construction cons, String label, GeoList f) {
		this(cons, f);
		g.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            freehand data: xmin, xmax, y1, y2, ....
	 */
	public AlgoFunctionFreehand(Construction cons, GeoList f) {
		super(cons);
		this.inputList = f;

		g = new GeoFunction(cons); // output
		FunctionVariable X = new FunctionVariable(kernel);
		ExpressionNode expr = new ExpressionNode(kernel, X, Operation.SIN,
				null);
		Function fun = new Function(expr, X);
		g.setFunction(fun);
		g.setDefined(false);

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Function;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return freehand function
	 */
	public GeoFunction getFunction() {
		return g;
	}

	@Override
	public final void compute() {
		if (!(inputList.isDefined())
				|| !inputList.getElementType().equals(GeoClass.NUMERIC)
				|| inputList.size() < 4) {
			g.setUndefined();
			return;
		}

		FunctionVariable X = new FunctionVariable(kernel);
		ExpressionNode expr = new ExpressionNode(kernel, X, Operation.FREEHAND,
				inputList);
		Function fun = new Function(expr, X);
		g.setFunction(fun);
		g.setDefined(true);
		g.setInterval(((GeoNumeric) inputList.get(0)).getDouble(),
				((GeoNumeric) inputList.get(1)).getDouble());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		if (inputList.size() < 4
				|| !inputList.getElementType().equals(GeoClass.NUMERIC)) {
			return "?";
		}
		return getLoc().getPlainDefault("FreehandFunctionOnIntervalAB",
				"Freehand function on [%0, %1]",
				kernel.format(((GeoNumeric) inputList.get(0)).getDouble(), tpl),
				kernel.format(((GeoNumeric) inputList.get(1)).getDouble(),
						tpl));
	}

	@Override
	public String getDefinition(StringTemplate tpl) {
		if (tpl.isForEditorParser()) {
			return g.toValueString(tpl);
		} else {
			return super.getDefinition(tpl);
		}
	}

	/**
	 * @return input list
	 */
	public GeoList getList() {
		return inputList;
	}

	/**
	 * @return number of y values
	 */
	public int getPointLength() {
		// first two elements are the min/max so subtract 2
		return inputList.size() - 2;
	}

	/**
	 * @return points for First[] command
	 */
	public ArrayList<MyPoint> getPoints() {
		ArrayList<MyPoint> ret = new ArrayList<>();
		double step, min, max;
		int n = inputList.size() - 3;
		if (n >= 1) {
			min = inputList.get(0).evaluateDouble();
			max = inputList.get(1).evaluateDouble();

			if (min > max) {
				return ret;
			}

			step = (max - min) / n;
		} else {
			return ret;
		}

		for (int i = 2; i < inputList.size(); i++) {
			double x = min + step * (i - 2);
			ret.add(new MyPoint(x, g.value(x)));
		}

		return ret;
	}

}
