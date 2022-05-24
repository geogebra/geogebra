package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

public class ConditionalSampler {
	private final GeoFunction function;
	private final ExpressionNode condition;
	private final ExpressionNode conditionBody;
	private Interval x = IntervalConstants.undefined();
	private DiscreteSpace space;
	private IntervalTupleList samples;
	private boolean negated = false;

	public ConditionalSampler(GeoFunction function,
			ExpressionNode condition, ExpressionNode conditionBody, DiscreteSpace space) {
		this.function = function;
		this.condition = condition;
		this.conditionBody = conditionBody;
		this.space = space;
		samples = new IntervalTupleList();
	}

	public static ConditionalSampler createNegated(GeoFunction function, ExpressionNode conditional,
			ExpressionNode rightTree, DiscreteSpace space) {
		ConditionalSampler sampler =
				new ConditionalSampler(function, conditional, rightTree, space);
		sampler.negate();
		return sampler;
	}

	public boolean isAccepted(Interval x) {
		this.x = x;
		if (!negated == isConditionTrue(x)) {
			return true;
		}

		return false;
	}

	private boolean isConditionTrue(Interval x) {
		Interval left = IntervalFunction.evaluate(x, condition.getLeft());
		Operation operation = condition.getOperation();
		ExpressionValue value = condition.getRight();
		switch (operation) {
		case LESS:
			return left.isLessThan(IntervalFunction.evaluate(x, value));
		case GREATER:
			return left.isGreaterThan(IntervalFunction.evaluate(x, value));
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

	public void setSpace(DiscreteSpaceImp aSpace) {
		space = aSpace;
	}

	public IntervalTupleList evaluateOn(Interval x) {
		DiscreteSpaceImp diffSpace = new DiscreteSpaceImp(x.getLow(), x.getHigh(), space.getStep());
		return evaluateOnSpace(diffSpace);
	}
}
