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
		samples = new IntervalTupleList();
		if (negated) {
			evaluateNegated();
		} else {
			evaluateNormal();
		}
	}
	private void evaluateNormal() {
		space.values().filter(x -> isConditionTrue(x)).forEach(x -> {
			addEvaluatedToSamples(x);
		});
	}

	private void addEvaluatedToSamples(Interval x) {
		IntervalTuple tuple = new IntervalTuple(x, evaluatedValue(x));
		samples.add(tuple);
	}

	private void evaluateNegated() {
		space.values().filter(x -> !isConditionTrue(x)).forEach(x -> {
			addEvaluatedToSamples(x);
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
}
