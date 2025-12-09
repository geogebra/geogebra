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

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyList;

/**
 * Class to help intersecting curve that is a polynomial spline
 */
public class Spline {
	private final MyList conditions;
	private final MyList funcXs;
	private final MyList funcYs;
	private final FunctionVariable functionVariable;

	/**
	 *
	 * @param xExpression the x expression of the curve.
	 * @param yExpression the y expression of the curve.
	 * @param functionVariable of the curve.
	 */
	public Spline(ExpressionNode xExpression, ExpressionNode yExpression,
			FunctionVariable functionVariable) {
		conditions = (MyList) xExpression.getLeft();
		funcXs = (MyList) xExpression.getRight();
		funcYs = (MyList) yExpression.getRight();
		this.functionVariable = functionVariable;
	}

	/**
	 *
	 * @return the total polynomial curves in the spline.
	 */
	public int size() {
		return funcYs.size();
	}

	/**
	 *
	 * @param idx index of the polynomial
	 *
	 * @return the polynomial x curve of the spline at idx.
	 */
	public ExpressionNode getFuncX(int idx) {
		return funcXs.getItem(idx).wrap();
	}

	/**
	 *
	 * @param idx index of the polynomial
	 *
	 * @return the polynomial y curve of the spline at idx.
	 */
	public ExpressionNode getFuncY(int idx) {
		return funcYs.getItem(idx).wrap();
	}

	/**
	 *
	 * @return the function variable of the spline.
	 */
	public FunctionVariable getFunctionVariable() {
		return functionVariable;
	}

	/**
	 *
	 * @param root of the spline
	 * @param i index of the polynomial curve in the spline.
	 * @return if root belongs to ith but not (i - 1)th condition.
	 */
	public boolean isInInterval(double root, int i) {
		return satisfiesCondition(root, i) && !satisfiesCondition(root, i - 1);
	}

	private boolean satisfiesCondition(double root, int i) {
		if (i < 0) {
			return root < 0;
		}
		if (i >= conditions.size()) {
			return root <= 1;
		}
		functionVariable.set(root);
		ExpressionValue condition = conditions.getItem(i);
		return condition.wrap().evaluateBoolean();
	}
}
