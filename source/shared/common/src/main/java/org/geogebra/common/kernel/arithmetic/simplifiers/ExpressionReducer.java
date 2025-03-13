package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isIntegerValue;

import java.util.ArrayList;
import java.util.Comparator;
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
		node.inspect(this::numbersFirst);
		expressions.sort(
				Comparator.comparing(n -> ((ExpressionNode) n).evaluateDouble()).reversed());
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
		return node.getLeft().inspect(this::numbersFirst)
				&& node.getRight().inspect(this::numbersFirst);
	}
}
