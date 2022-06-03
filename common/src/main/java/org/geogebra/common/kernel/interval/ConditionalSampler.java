package org.geogebra.common.kernel.interval;

public class ConditionalSampler implements IntervalEvaluatable {
	private IntervalConditionalExpression conditionalExpression;
	private DiscreteSpace space;
	private IntervalTupleList samples;
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
		samples = new IntervalTupleList();
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
		space.values().filter(x -> conditionalExpression.isTrue(x)).forEach(x2 -> {
			list.add(evaluatedTuple(x2));
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

	public void setIndex(int index) {
		this.index = index;
	}
}
