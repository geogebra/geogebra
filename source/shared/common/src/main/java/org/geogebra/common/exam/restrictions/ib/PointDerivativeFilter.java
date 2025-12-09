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

package org.geogebra.common.exam.restrictions.ib;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.plugin.Operation;

/**
 * Restricts derivative expressions over a variable e.g. f'(x), but allows
 * derivatives at point e.g. f'(5).
 */
public final class PointDerivativeFilter implements ExpressionFilter, Inspecting {

	@Override
	public boolean isAllowed(@Nonnull ExpressionValue expression) {
		// Inspecting searches for derivatives over a variable
		return !expression.any(this);
	}

	@Override
	public boolean check(ExpressionValue v) {
		if (v.isOperation(Operation.FUNCTION) && v instanceof ExpressionNode) {
			return checkFunction((ExpressionNode) v);
		} else if (v instanceof Variable) {
			return checkVariable((Variable) v);
		}
		return false;
	}

	private boolean checkFunction(ExpressionNode node) {
		ExpressionValue left = node.getLeft();
		ExpressionValue right = node.getRight();
		return left != null && left.isOperation(Operation.DERIVATIVE) && right != null
				&& right.any(e -> e instanceof FunctionVariable);
	}

	private boolean checkVariable(Variable variable) {
		EvalInfo info = new EvalInfo().withAutocreate(false).withSymbolic(false)
				.withSymbolicMode(SymbolicMode.NONE);
		try {
			ExpressionValue value = variable.resolveAsExpressionValue(info);
			return value.any(this);
		} catch (Exception e) {
			return false;
		}
	}
}