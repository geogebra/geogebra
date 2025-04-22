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
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;

/**
 * List expression, e.g. with L1 = {3, 2, 1}, L2 = {5, 1, 7} such an expression
 * could be L1 + L2
 */
public class AlgoDependentEquationList extends AlgoElement
		implements DependentAlgo {

	private GeoList list; // output
	private ExpressionNode lhs;
	private ExpressionNode rhs;
	private boolean validTypes = true;

	/**
	 * Creates new dependent list algo.
	 * 
	 * @param cons
	 *            construction
	 * @param root
	 *            expression deining the list
	 */

	public AlgoDependentEquationList(Construction cons, Equation root) {
		super(cons);

		list = new GeoList(cons);
		list.setDefinition(root.wrap());
		lhs = root.getLHS();
		rhs = root.getRHS();
		setInputOutput(); // for AlgoElement

		// compute value of dependent list
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		setInputFrom(list.getDefinition());
		setOnlyOutput(list);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the resulting list
	 * 
	 * @return resulting list
	 */
	public GeoList getList() {
		return list;
	}

	/**
	 * Returns the input expression
	 * 
	 * @return input expression
	 */
	@Override
	public ExpressionNode getExpression() {
		return list.getDefinition();
	}

	// evaluate the current value of the arithmetic tree
	@Override
	public final void compute() {
		// get resulting list of ExpressionNodes
		ExpressionValue leftList = listOrValue(lhs);

		ExpressionValue rightList = listOrValue(rhs);

		list.setDefined(true);
		int leftSize = leftList instanceof MyList ? ((MyList) leftList).size() : -1;
		int rightSize = rightList instanceof MyList ? ((MyList) rightList).size() : -1;
		if (leftSize != -1 && rightSize != -1 && leftSize != rightSize) {
			computeAndCheckTypes(leftList, rightList, 1);
			list.setUndefined();
		} else {
			computeAndCheckTypes(leftList, rightList, Math.max(leftSize, rightSize));
		}
	}

	private void computeAndCheckTypes(ExpressionValue leftList,
			ExpressionValue rightList, int max) {
		list.clear();
		boolean oldFlag = kernel.getConstruction().isSuppressLabelsActive();
		kernel.getConstruction().setSuppressLabelCreation(true);
		for (int i = 0; i < max; i++) {
			Equation eq = new Equation(kernel, get(leftList, i),
					get(rightList, i));
			eq.setLHS(AlgoDependentFunction
					.expandFunctionDerivativeNodes(eq.getLHS(), true).wrap());

			GeoElement element = kernel.getAlgebraProcessor()
					.processEquation(eq, eq.wrap(), true,
							new EvalInfo(false))[0];
			if (element != null) {
				list.add(element);
				if (element.isGeoImplicitCurve()) {
					validTypes = validTypes && ((GeoImplicitCurve) element).isValidType();
				}
			}
		}
		kernel.getConstruction().setSuppressLabelCreation(oldFlag);
	}

	private static ExpressionValue listOrValue(ExpressionNode lhs2) {
		ExpressionValue evlist = lhs2.evaluate(StringTemplate.defaultTemplate);
		if (evlist instanceof MyList) {
			return evlist;
		}
		return (evlist instanceof GeoList)
				? ((GeoList) evlist).getMyList() : lhs2.unwrap();
	}

	private ExpressionValue get(ExpressionValue leftList, int i) {
		return leftList instanceof MyList ? ((MyList) leftList).getItem(i)
				: leftList.deepCopy(kernel);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// was defined as e.g. L = 3 * {a, b, c}
		return list.getDefinition().toString(tpl);
	}

	/**
	 * @return whether all equations are of valid type
	 */
	public boolean validate() {
		return validTypes;
	}
}
