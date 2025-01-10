/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.plugin.Operation;

/**
 * Integral of a function
 *
 * @author Markus Hohenwarter
 */
public class AlgoIntegralNumericInterval extends AlgoElement {

	private final GeoNumberValue startX;
	private final GeoNumberValue startY;
	private final GeoNumberValue endX;
	private final GeoFunctionable f;
	private final GeoFunction g;

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            function
	 */
	public AlgoIntegralNumericInterval(Construction cons, GeoFunctionable f,
			GeoNumberValue startX, GeoNumberValue startY, GeoNumberValue endX) {
		super(cons);
		this.startX = startX;
		this.endX = endX;
		this.startY = startY;
		this.f = f;
		this.g = new GeoFunction(cons);
		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] {f.toGeoElement(), startX.toGeoElement(),
				startY.toGeoElement(), endX.toGeoElement()};
		setOnlyOutput(g);
		setDependencies();
	}

	@Override
	public void compute() {
		if (!f.isDefined()) {
			g.setUndefined();
			return;
		}
		g.setDefined(true);
		Function inFun = f.getFunction();
		FunctionVariable[] fVars = inFun.getFunctionVariables();
		// for conditional expressions the symbolic integral may not be continuous
		boolean skipIntegration = inFun.getExpression().includesFreehandOrData()
				|| inFun.getExpression().isConditional();
		ExpressionNode integral = skipIntegration
				? null : inFun.integral(fVars[0], kernel);
		if (isInvalid(integral)) {
			integral = numericIntegral(inFun);
		} else {
			fVars[0].set(startX.getDouble());
			ExpressionNode condition = new ExpressionNode(kernel, fVars[0],
					Operation.GREATER_EQUAL, startX).and(new ExpressionNode(kernel, fVars[0],
					Operation.LESS_EQUAL, endX));
			integral = condition.apply(Operation.IF,
					integral.plus(startY.evaluateDouble() - integral.evaluateDouble()));
		}
		inFun = new Function(integral, fVars[0]);
		inFun.setSecret(this);
		g.setFunction(inFun);
		g.setDefined(true);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.NIntegral;
	}

	private boolean isInvalid(ExpressionNode integral) {
		return integral == null || integral.inspect(v -> v instanceof MyDouble
				&& Double.isNaN(((MyDouble) v).getDouble()));
	}

	private ExpressionNode numericIntegral(Function inFun) {
		MyList xVal = new MyList(kernel);
		MyList yVal = new MyList(kernel);
		MyNumberPair xyVal = new MyNumberPair(kernel, xVal, yVal);
		ExpressionNode node = new ExpressionNode(kernel, inFun.getFunctionVariable(),
				Operation.DATA, xyVal);
		double value = startY.evaluateDouble();
		double step = 0.1;
		double xMax = endX.evaluateDouble();
		xVal.addListElement(new MyDouble(kernel, startX.evaluateDouble()));
		yVal.addListElement(new MyDouble(kernel, startY.evaluateDouble()));
		double x = startX.evaluateDouble();
		while (x < xMax) {
			double delta = step;
			for (int bisections = 0; bisections < 5; bisections++) {
				double increment = AlgoIntegralDefinite.doGaussQuadSimple(inFun, x,
						x + delta, Kernel.MIN_PRECISION);
				if (!Double.isNaN(increment) || Double.isNaN(inFun.value(x))) {
					value += increment;
					xVal.addListElement(new MyDouble(kernel, x + delta));
					yVal.addListElement(new MyDouble(kernel, value));
					break;
				}
				delta *= 0.5;
				if (Double.isNaN(value)) { // all subsequent values will be undefined too
					break;
				}
			}
			x += delta;
		}
		return node;
	}

}
