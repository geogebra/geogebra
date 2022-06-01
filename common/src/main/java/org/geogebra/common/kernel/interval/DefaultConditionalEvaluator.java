package org.geogebra.common.kernel.interval;

import java.util.List;

public class DefaultConditionalEvaluator implements IntervalEvaluatable {

	private List<ConditionalSampler> samplers;

	public DefaultConditionalEvaluator(List<ConditionalSampler> samplers) {
		this.samplers = samplers;
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		return evaluate(new Interval(low, high));
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		for (int i = 0; i < samplers.size(); i++) {
			ConditionalSampler sampler = samplers.get(i);
			IntervalTupleList newPoints = sampler.evaluate(x);
			if (!newPoints.isEmpty()) {
				newPoints.setPiece(i);
				return newPoints;
			}
		}
		return IntervalTupleList.emptyList();
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		for (int i = 0; i < samplers.size(); i++) {
			ConditionalSampler sampler = samplers.get(i);
			IntervalTupleList newPoints = sampler.evaluate(space);
			if (!newPoints.isEmpty()) {
				newPoints.setPiece(i);
				return newPoints;
			}
		}

		return IntervalTupleList.emptyList();
	}
}
