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

package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isIntegerValue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

public class ExpressionReducer implements SimplifyNode {
	private final SimplifyUtils utils;
	private final Operation operation;
	private ExpressionValue num;
	private List<ExpressionValue> expressions = new ArrayList<>();

	/**
	 *
	 * @param utils {@link SimplifyUtils}
	 * @param operation to reduce along with.
	 */
	public ExpressionReducer(SimplifyUtils utils, Operation operation) {
		this.utils = utils;
		this.operation = operation;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		if (isIntegerValue(node)) {
			return utils.newDouble(node.evaluateDouble()).wrap();
		}
		num = null;
		expressions.clear();
		node.any(this::numbersFirst);
		double constValue = num != null ? num.evaluateDouble() : 1;
		ExpressionValue result = null;
		if (Math.abs(constValue) != 1) {
			result = utils.newDouble(constValue);
		}
		for (ExpressionValue ev : expressions) {
			if (ev == expressions.get(0) && constValue == -1) {
				result = ev.wrap().multiply(utils.minusOne());
			} else {
				result = utils.applyOrLet(result, operation, ev);
			}
		}
		return result.wrap();
	}

	private boolean numbersFirst(ExpressionValue ev) {
		if (ExpressionValueUtils.isSqrtNode(ev)) {
			expressions.add(ev);
			return true;
		}

		ExpressionNode node = ev.wrap();
		if (isIntegerValue(node)) {
			num = utils.applyOrLet(num, operation, node);
			return true;
		}
		return node.getLeft().any(this::numbersFirst)
				&& node.getRight().any(this::numbersFirst);
	}
}
