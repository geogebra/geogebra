package org.geogebra.common.kernel.interval;

import java.util.List;
import java.util.function.Consumer;

public class MultiSampler implements IntervalEvaluatable {
	IntervalEvaluatable evaluator;
	List<ConditionalSampler> samplers;

	public MultiSampler(IntervalEvaluatable evaluator, List<ConditionalSampler> samplers) {
		this.evaluator = evaluator;
		this.samplers = samplers;
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		return evaluator.evaluate(x);
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		return evaluator.evaluate(low, high);
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		return evaluator.evaluate(space);
	}

	public void updateSpace(DiscreteSpace space) {
		samplers.forEach(sampler -> sampler.setSpace(space));
	}

	public void forEach(Consumer<? super ConditionalSampler> action) {
		samplers.forEach(action);
	}
}
