package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

public class ConditionalSampler implements IntervalEvaluatable {
	private final ExpressionNode condition;
	private final ExpressionNode conditionBody;
	private DiscreteSpace space;
	private IntervalTupleList samples;
	private boolean negated = false;

	/**
	 *
	 * @param condition of the sampler.
	 * @param conditionBody to evaluate with, when condition is true
	 * @param space to evaluate on.
	 */
	public ConditionalSampler(ExpressionNode condition, ExpressionNode conditionBody,
			DiscreteSpace space) {
		this.condition = condition;
		this.conditionBody = conditionBody;
		this.space = space;
		samples = new IntervalTupleList();
	}

	/**
	 * @param condition of the sampler
	 * @param conditionBody to evaluate with, when condition is false
	 */
	public ConditionalSampler(ExpressionNode condition, ExpressionNode conditionBody) {
		this(condition, conditionBody, null);
	}

	/**
	 *
	 * @param function of the conditional sampler
	 * @param condition of the sampler
	 * @param conditionBody to evaluate with, when condition is false
	 * @return a negated conditional sampler with no space specified.
	 */
	public static ConditionalSampler createNegated(GeoFunction function, ExpressionNode condition,
			ExpressionNode conditionBody) {
		return createNegated(function, condition, conditionBody, null);
	}

	/**
	 *
	 * @param function of the conditional sampler
	 * @param condition of the sampler
	 * @param conditionBody to evaluate with, when condition is false
	 * @param space to evaluate on
	 * @return a negated conditional sampler.
	 */
	public static ConditionalSampler createNegated(GeoFunction function, ExpressionNode condition,
			ExpressionNode conditionBody, DiscreteSpace space) {
		ConditionalSampler sampler =
				new ConditionalSampler(condition, conditionBody, space);
		sampler.negate();
		return sampler;
	}

	public boolean isAccepted(Interval x) {
		return !negated && isConditionTrue(x);
	}

	boolean isConditionTrue(Interval x) {
		return isExpressionTrue(x, condition.getLeft(),
				condition.getOperation(), condition.getRight());
	}

	private boolean isExpressionTrue(Interval x, ExpressionValue left, Operation operation,
			ExpressionValue right) {
		if (operation.equals(Operation.AND_INTERVAL)) {
			ExpressionNode nodeLeft = left.wrap();
			ExpressionNode nodeRight = right.wrap();
			return isExpressionTrue(x, nodeLeft.getLeft(),
						nodeLeft.getOperation(), nodeLeft.getRight())
					&& isExpressionTrue(x, nodeRight.getLeft(),
						nodeRight.getOperation(), nodeRight.getRight());
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

	/**
	 *
	 * @return the evaluated result.
	 */
	public IntervalTupleList result() {
		evaluate();
		return samples;
	}

	/**
	 * Evaluate on space that is currently set.
	 */
	public void evaluate() {
		samples = evaluate(space);
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		return evaluate(x.getLow(), x.getHigh());
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		DiscreteSpaceImp diffSpace = new DiscreteSpaceImp(low, high, space.getStep());
		return evaluate(diffSpace);
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
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

	/**
	 * negate the condition (for else part)
	 */
	public void negate() {
		negated = true;
	}

	/**
	 *
	 * @param space to set for all samplers to evaluate
	 */
	public void setSpace(DiscreteSpace space) {
		this.space = space;
	}
}
