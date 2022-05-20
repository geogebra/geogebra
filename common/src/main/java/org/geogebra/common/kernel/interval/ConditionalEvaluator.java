package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.plugin.Operation;

public class ConditionalEvaluator implements IntervalEvaluator {
	private ExpressionNode node;
	private Operation operation;

	private List<Operation> acceptedOperations = Arrays.asList(Operation.IF, Operation.IF_ELSE,
			Operation.IF_LIST);

	/**
	 * Default constructor.
	 */
	public ConditionalEvaluator() {
		// default
	}

	/**
	 * Constructor to test isAccepted()
	 *
	 * @param operation to test.
	 */
	ConditionalEvaluator(Operation operation) {
		this.operation = operation;
	}

	public void setNode(ExpressionNode node) {
		this.node = node;
		operation = node.getOperation();
	}

	@Override
	public boolean isAccepted() {
		return acceptedOperations.contains(operation);
	}

	private boolean isIfElse() {
		return Operation.IF_ELSE.equals(operation)  ;
	}

	@Override
	public Interval evaluate(Interval x) {
		switch (operation) {
		case IF:
			return evaluateIf(x, node);
		case IF_ELSE:
			return evaluateIfElse(x);
		}
		return null;
	}

	private Interval evaluateIf(Interval x, ExpressionNode node) {
		if (evaluateBoolean(x, node.getLeftTree())) {
			return IntervalFunction.evaluate(x, node.getRight());
		}
		return undefined();
	}

	private Interval evaluateIfElse(Interval x) {
		ExpressionValue left = node.getLeft();
		ExpressionValue right = node.getRight();
		if (left instanceof MyNumberPair) {
			MyNumberPair pair = (MyNumberPair) left;
			ExpressionValue condition = pair.getX();
			if (evaluateBoolean(x, condition.wrap())) {
				ExpressionNode ifExpr = pair.getY().wrap();
				return IntervalFunction.evaluate(x, ifExpr);
			} else {
				return IntervalFunction.evaluate(x, right);
			}
		}

		return null;
	}

	private boolean evaluateBoolean(Interval x, ExpressionNode condition) {
		Interval left = IntervalFunction.evaluate(x, condition.getLeft());
		Operation operation = condition.getOperation();
		ExpressionValue value = condition.getRight();
		switch (operation) {
		case LESS:
			return left.isLessThan(IntervalFunction.evaluate(x, value));
		case GREATER:
			return left.isGreaterThan(IntervalFunction.evaluate(x, value));
		}
		return true;
	}
}
