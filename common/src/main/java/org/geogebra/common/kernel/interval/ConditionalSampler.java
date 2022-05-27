package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

public class ConditionalSampler {
	private final ExpressionNode condition;
	private final ExpressionNode conditionBody;
	private DiscreteSpace space;
	private IntervalTupleList samples;
	private boolean negated = false;

	public ConditionalSampler(ExpressionNode condition, ExpressionNode conditionBody,
			DiscreteSpace space) {
		this.condition = condition;
		this.conditionBody = conditionBody;
		this.space = space;
		samples = new IntervalTupleList();
	}

	public static ConditionalSampler createNegated(GeoFunction function, ExpressionNode conditional,
			ExpressionNode rightTree, DiscreteSpace space) {
		ConditionalSampler sampler =
				new ConditionalSampler(conditional, rightTree, space);
		sampler.negate();
		return sampler;
	}

	public boolean isAccepted(Interval x) {
		return !negated && isConditionTrue(x);
	}

	boolean isConditionTrue(Interval x) {
		return isExpressionTrue(x, condition.getLeft(), condition.getOperation(), condition.getRight());
	}

	private boolean isExpressionTrue(Interval x, ExpressionValue left, Operation operation,
			ExpressionValue right) {
		if (operation.equals(Operation.AND_INTERVAL)) {
			ExpressionNode nodeLeft = left.wrap();
			ExpressionNode nodeRight = right.wrap();
			return isExpressionTrue(x, nodeLeft.getLeft(), nodeLeft.getOperation(), nodeLeft.getRight())
					&& isExpressionTrue(x, nodeRight.getLeft(), nodeRight.getOperation(), nodeRight.getRight());
		}

		return evaluateBoolean(IntervalFunction.evaluate(x, left), operation,
				IntervalFunction.evaluate(x, right));
	}

	private boolean evaluateBoolean(Interval y1, Operation operation, Interval y2) {
		switch (operation) {
		case EQUAL_BOOLEAN:
			return y1.contains(y2);
		case LESS:
			return y1.isLessThan(y2);
		case LESS_EQUAL:
			return y1.isLessThanOrEqual(y2);
		case GREATER:
			return y1.isGreaterThan(y2);
		case GREATER_EQUAL:
			return y2.isLessThanOrEqual(y1);
		}
		return false;
	}

	public IntervalTupleList result() {
		evaluate();
		return samples;
	}

	public void evaluate() {
		samples = evaluateOnSpace(space);
	}

	public IntervalTupleList evaluateOnSpace(DiscreteSpace space) {
		IntervalTupleList list = new IntervalTupleList();
		if (negated) {
			evaluateNegated(space, list);
		} else {
			evaluateNormal(space, list);
		}
		return list;
	}
	private void evaluateNormal(DiscreteSpace space, IntervalTupleList list) {
		space.values().filter(x -> isConditionTrue(x)).forEach(x -> {
			list.add(evaluatedTuple(x));
		});
	}

	private IntervalTuple evaluatedTuple(Interval x) {
		return new IntervalTuple(x, evaluatedValue(x));
	}

	private void evaluateNegated(DiscreteSpace space, IntervalTupleList list) {
		space.values().filter(x -> !isConditionTrue(x)).forEach(x -> {
			list.add(evaluatedTuple(x));
		});
	}

	private Interval evaluatedValue(Interval x) {
		return IntervalFunction.evaluate(x, conditionBody);
	}

	public void negate() {
		negated = true;
	}

	public void setSpace(DiscreteSpace space) {
		this.space = space;
	}

	public IntervalTupleList evaluateOn(Interval x) {
		DiscreteSpaceImp diffSpace = new DiscreteSpaceImp(x.getLow(), x.getHigh(), space.getStep());
		return evaluateOnSpace(diffSpace);
	}
}
