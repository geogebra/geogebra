/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.Operation;

/**
 * Sum of functions, may take whole list or just several first elements
 */
public class AlgoFoldExpression extends AlgoElement {

	private GeoElement expression; // input
	private GeoNumeric to; // input
	private GeoElement resultFun;
	private Operation op;
	private FoldComputer foldComputer;
	private GeoNumeric from;
	private GeoNumeric var;

	/**
	 * Creates labeled function sum algo for truncated list (or whole list if
	 * truncate == null)
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param expression
	 *            list
	 * @param var
	 *            variable
	 * @param from
	 *            min variable value
	 * @param truncate
	 *            number of elements to take
	 * @param op
	 *            operation
	 */
	public AlgoFoldExpression(Construction cons, String label,
			GeoElement expression, GeoNumeric var, GeoNumeric from,
			GeoNumeric truncate, Operation op) {
		super(cons);
		this.expression = expression;
		this.var = var;
		this.from = from;
		this.to = truncate;
		this.op = op;
		this.foldComputer = getComputer(expression);
		resultFun = foldComputer.getTemplate(cons,
				expression.getGeoClassType());
		if (op == Operation.MULTIPLY && resultFun instanceof VectorNDValue) {
			((VectorNDValue) resultFun).setMode(Kernel.COORD_COMPLEX);
		}
		setInputOutput();
		compute();
		resultFun.setLabel(label);
	}

	private static FoldComputer getComputer(GeoElement expression) {
		switch (expression.getGeoClassType()) {
		case POINT:
		case POINT3D:
		case VECTOR:
		case VECTOR3D:
			return new PointNDFold();
		case FUNCTION_NVAR:

			return new FunctionNvarFold();
		case FUNCTION:
			return new FunctionFold();
		case LIST:
			return new ListFold();
		case TEXT:
			return new TextFold();
		}
		return new NumberFold();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = expression;
		input[1] = var;
		input[2] = from;
		input[3] = to;

		setOnlyOutput(resultFun);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns result
	 * 
	 * @return sum of functions
	 */
	public GeoElement getResult() {
		return resultFun;
	}

	@Override
	public final void compute() {
		// Sum[{x^2,x^3}]
		var.setValue(from.getDouble());
		updateLocalVar();
		GeoElement fn = expression.copyInternal(cons);
		if (fn instanceof CasEvaluableFunction) {
			((CasEvaluableFunction) fn).replaceChildrenByValues(var);
		}
		if (!from.isFinite() || !to.isFinite()) {
			resultFun.setUndefined();
			return;
		}

		foldComputer.setFrom(fn, kernel);
		for (int i = (int) from.getDouble() + 1; i <= to.getDouble(); i++) {
			var.setValue(i);
			updateLocalVar();
			fn = expression.copyInternal(cons);
			if (fn instanceof CasEvaluableFunction) {
				((CasEvaluableFunction) fn).replaceChildrenByValues(var);
			}
			foldComputer.add(fn, op);
		}
		foldComputer.finish();
	}

	private void updateLocalVar() {
		// set local variable to given value
		AlgoElement expressionParentAlgo = expression.getParentAlgorithm();
		// update var's algorithms until we reach expression
		if (expressionParentAlgo != null) {
			// update all dependent algorithms of the local variable var

			var.getAlgoUpdateSet().updateAllUntil(expressionParentAlgo);

			this.setStopUpdateCascade(false);
			expressionParentAlgo.update();
		}

	}

	@Override
	public Commands getClassName() {
		return op == Operation.PLUS ? Commands.Sum : Commands.Product;
	}

}
