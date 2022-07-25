package org.geogebra.common.kernel.interval.evaluators;

import java.util.List;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.ConditionalSampler;

public class IfListEvaluator implements IntervalEvaluatable {
	private List<ConditionalSampler> samplers;

	public IfListEvaluator(List<ConditionalSampler> samplers) {
		this.samplers = samplers;
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		return evaluateWithFistSampler(x);
	}

	private IntervalTupleList evaluateWithFistSampler(Interval x) {
		for (int i = 0; i < samplers.size(); i++) {
			ConditionalSampler sampler = samplers.get(i);
			if (sampler.isAccepted(x)) {
				return sampler.evaluate(x);
			}
		}
		return IntervalTupleList.emptyList();
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		return evaluate(new Interval(low, high));
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		IntervalTupleList result = new IntervalTupleList();
		space.values().forEach(x -> result.append(evaluateWithFistSampler(x)));
		return result;
	}
}
