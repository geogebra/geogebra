package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.euclidian.plot.interval.IntervalFunctionData;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.function.IntervalFunction;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class UpdateFunctionData {
	private final IntervalFunctionDomainInfo domainInfo = new IntervalFunctionDomainInfo();
	private final IntervalFunction function;
	private final FunctionSampler sampler;
	private final IntervalFunctionData data;
	private final DiscreteSpace space;

	public UpdateFunctionData(FunctionSampler sampler, IntervalFunctionData data,
			DiscreteSpace space) {
		this.function = new IntervalFunction(data.getGeoFunction());
		this.sampler = sampler;
		this.data = data;
		this.space = space;
	}

	public void completeDataOn(Interval domain) {
		if (domainInfo.hasZoomedOut(domain)) {
			extendDataToLeft(domain);
			extendDataToRight(domain);
		} else if (domainInfo.hasPannedLeft(domain)) {
			extendDataToLeft(domain);
		} else if (domainInfo.hasPannedRight(domain)) {
			extendDataToRight(domain);
		}
		domainInfo.update(domain);
	}

	public void zoom(Interval domain) {
		space.update(domain, sampler.calculateNumberOfSamples());
		evaluateAll();
		domainInfo.update(domain);
	}

	private void evaluateAll() {
		data.clear();
		space.forEach(x -> data.append(x, function.evaluate(x)));
		processAsymptotes(data.tuples());
	}

	private static void processAsymptotes(IntervalTupleList samples) {
		IntervalAsymptotes asymptotes = new IntervalAsymptotes(samples);
		asymptotes.process();
	}

	private void extendDataToLeft(Interval domain) {
		space.extendLeft(domain, x -> data.extendLeft(x, function.evaluate(x)));
	}

	private void extendDataToRight(Interval domain) {
		space.extendRight(domain, x -> data.extendRight(x, function.evaluate(x)));
	}
}
