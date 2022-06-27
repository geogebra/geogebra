package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpaceImp;
import org.geogebra.common.kernel.interval.evaluators.IntervalEvaluatable;
import org.geogebra.common.kernel.interval.function.IntervalFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class ConditionalSampler implements IntervalEvaluatable {
	private IntervalConditionalExpression conditionalExpression;
	private DiscreteSpace space;
	private int index = 0;

	/**
	 *
	 * @param conditionalExpression to evaluate.
	 * @param space to evaluate on.
	 */
	public ConditionalSampler(IntervalConditionalExpression conditionalExpression,
			DiscreteSpace space) {
		this(conditionalExpression);
		this.space = space;
	}

	/**
	 * @param conditionalExpression to evaluate.
	 */
	public ConditionalSampler(IntervalConditionalExpression conditionalExpression) {
		this.conditionalExpression = conditionalExpression;
	}

	/**
	 *
	 * @return the evaluated result.
	 */
	public IntervalTupleList result() {
		return evaluate(space);
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
		space.values().filter(x -> conditionalExpression.isTrue(x)
				|| conditionalExpression.isTrueBetween(x)
		).forEach(x2 -> {
			if (conditionalExpression.isTrueBetween(x2))  {
				double split = conditionalExpression.getSplitValue();
				Interval splitX1 = new Interval(x2.getLow(), split);
				if (!splitX1.isUndefined() && conditionalExpression.isTrue(splitX1)) {
					list.add(evaluatedTuple(splitX1));
				}
				Interval splitX2 = new Interval(split, x2.getHigh());
				if (!splitX2.isUndefined() && conditionalExpression.isTrue(splitX2)) {
					list.add(evaluatedTuple(splitX2));
				}
			} else {
				list.add(evaluatedTuple(x2));
			}
		});
		return list;
	}

	private IntervalTuple evaluatedTuple(Interval x) {
		return new IntervalTuple(x, evaluatedValue(x), index);
	}

	private Interval evaluatedValue(Interval x) {
		return IntervalFunction.evaluate(x,
				conditionalExpression.getBody());
	}

	/**
	 *
	 * @param space to set for all samplers to evaluate
	 */
	public void setSpace(DiscreteSpace space) {
		this.space = space;
	}

	public boolean isAccepted(Interval x) {
		return conditionalExpression.isTrue(x);
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
