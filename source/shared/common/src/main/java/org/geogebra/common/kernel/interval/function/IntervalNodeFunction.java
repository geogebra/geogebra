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

package org.geogebra.common.kernel.interval.function;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;
import org.geogebra.common.kernel.interval.node.IntervalFunctionVariable;
import org.geogebra.common.kernel.interval.node.IntervalNode;

/**
 * Represents a function that is built from IntervalNodes
 * thus for interval x, y, y = f(x) can easily be evaluated.
 */
public class IntervalNodeFunction {
	IntervalExpressionNode root;
	private final IntervalFunctionVariable functionVariable;

	/**
	 *
	 * @param root node of the function tree.
	 * @param functionVariable the variable (usually called "x").
	 *
	 * Note that each IntervalFunction has exactly one function variable "x",
	 * because of the "Dependency Problem".
	 */
	public IntervalNodeFunction(IntervalExpressionNode root,
			IntervalFunctionVariable functionVariable) {
		assert root != null;
		this.root = root;
		this.functionVariable = functionVariable;
	}

	/**
	 * Evaluates the function for interval x.
	 *
	 * @param x the interval to get the function value.
	 * @return the interval representing the function value at x.
	 */
	public Interval value(Interval x) {
		functionVariable.set(x);
		IntervalNode expression = root.evaluate();
		return expression == null ? IntervalConstants.undefined() : expression.value();
	}

	/**
	 *
	 * @return root node of the function tree.
	 */
	public IntervalExpressionNode getRoot() {
		return root;
	}

	public void setRoot(IntervalExpressionNode root) {
		this.root = root;
	}
}
