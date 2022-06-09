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
		IntervalTupleList result = new IntervalTupleList();
		samplers.forEach(sampler -> result.append(sampler.evaluate(x)));
		return result;
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		IntervalTupleList result = new IntervalTupleList();
		samplers.forEach(sampler -> result.append(sampler.evaluate(space)));
		return result;
	}
}
