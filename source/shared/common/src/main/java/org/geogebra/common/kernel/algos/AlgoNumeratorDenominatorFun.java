/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.Evaluate2Var;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Find Numerator
 * 
 * @author Michael Borcherds
 */
public class AlgoNumeratorDenominatorFun extends AlgoElement {

	private Evaluate2Var f; // input
	private GeoElement g; // output
	private Commands type;

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            function
	 * @param type
	 *            denominator / numerator
	 */
	public AlgoNumeratorDenominatorFun(Construction cons, Evaluate2Var f,
			Commands type) {
		super(cons);
		this.f = f;
		this.type = type;

		if (f instanceof GeoFunction || f instanceof GeoNumeric) {
			g = new GeoFunction(cons);
		} else {
			g = new GeoFunctionNVar(cons);
		}
		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return type;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = (GeoElement) f;

		super.setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return denominator or numerator
	 */
	public GeoElement getResult() {
		return g;
	}

	@Override
	public final void compute() {
		if (!f.isDefined()) {
			g.setUndefined();
			return;
		}
		ExpressionValue[] numDen = new ExpressionValue[2];
		f.getFunctionExpression().deepCopy(kernel).wrap().getFraction(numDen,
				false);
		ExpressionValue ev;
		if (type == Commands.Numerator) {
			ev = numDen[0];
		} else if (numDen[1] == null) {
			ev = new ExpressionNode(kernel, 1);
		} else {
			ev = numDen[1];
		}

		if (ev.isExpressionNode()) {

			if (g instanceof GeoFunction) {
				Function fun = new Function((ExpressionNode) ev,
						f.getFunction().getFunctionVariables()[0]);
				((GeoFunction) g).setFunction(fun);
			} else {
				FunctionNVar fun = new FunctionNVar((ExpressionNode) ev,
						f.getFunction().getFunctionVariables());
				((GeoFunctionNVar) g).setFunction(fun);
			}
		} else if (ev instanceof FunctionVariable) {
			if (f instanceof GeoFunction) {

				// construct function f(x) = x
				FunctionVariable fv = ((GeoFunction) f)
						.getFunctionVariables()[0].deepCopy(kernel);
				ExpressionNode en = new ExpressionNode(kernel, fv);
				Function tempFun = new Function(en, fv);
				tempFun.initFunction();
				((GeoFunction) g).setFunction(tempFun);

			} else {
				// construct eg f(a,b)=b
				GeoFunctionNVar ff = (GeoFunctionNVar) f;
				FunctionVariable[] vars = ff.getFunctionVariables();
				ExpressionNode en = new ExpressionNode(kernel, ev);
				FunctionNVar newFun = new FunctionNVar(en, vars);
				((GeoFunctionNVar) g).setFunction(newFun);
			}
		} else if (ev instanceof NumberValue) {
			if (f instanceof GeoFunction) {
				// construct function f(x) = 1
				FunctionVariable fv = new FunctionVariable(kernel);
				ExpressionNode en = new ExpressionNode(kernel,
						new MyDouble(kernel, ev.evaluateDouble()));
				Function tempFun = new Function(en, fv);
				tempFun.initFunction();
				((GeoFunction) g).setFunction(tempFun);
			} else {
				// GeoFunctionNVar

				// construct eg f(a,b)=3
				GeoFunctionNVar ff = (GeoFunctionNVar) f;
				FunctionVariable[] vars = ff.getFunctionVariables();
				ExpressionNode en = new ExpressionNode(kernel, ev);
				FunctionNVar newFun = new FunctionNVar(en, vars);
				((GeoFunctionNVar) g).setFunction(newFun);
			}
		} else {
			g.setUndefined();
			return;
		}

		((FunctionalNVar) g).setDefined(true);
	}

	/**
	 * over-ridden in AlgoDenominator
	 */
	protected ExpressionValue getPart(ExpressionValue[] node) {
		return node[0];
	}

}
