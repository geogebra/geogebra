package org.geogebra.common.kernel.interval.samplers;

import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.evaluators.IntervalEvaluatable;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class MultiSampler implements IntervalEvaluatable {
	IntervalEvaluatable evaluator;
	List<ConditionalSampler> samplers;

	/**
	 *
	 * @param evaluator for this compound, multi-conditional sampler (Ifs)
	 * @param samplers the samplers for each condition
	 */
	public MultiSampler(IntervalEvaluatable evaluator, List<ConditionalSampler> samplers) {
		this.evaluator = evaluator;
		this.samplers = samplers;
		setSamplerIndices();
	}

	private void setSamplerIndices() {
		for (int i = 0; i < samplers.size(); i++) {
			samplers.get(i).setIndex(i);
		}
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
