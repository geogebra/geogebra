package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.euclidian.plot.interval.IntervalFunctionData;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.function.IntervalFunction;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.util.debug.Log;

public class UpdateFunctionData {
	private final IntervalFunctionDomainInfo domainInfo = new IntervalFunctionDomainInfo();
	private final IntervalFunction function;
	private final FunctionSampler sampler;
	private final IntervalFunctionData data;
	private final DiscreteSpace space;

	public UpdateFunctionData(FunctionSampler sampler, IntervalFunctionData data,
			DiscreteSpace space) {
		this.function = sampler.function;
		this.sampler = sampler;
		this.data = data;
		this.space = space;
	}

	public void update(Interval domain) {
		if (domainInfo.hasZoomed(domain)) {
			zoom(domain);
		} else if (domainInfo.hasPannedLeft(domain)) {
			panLeft(domain);
		} else if (domainInfo.hasPannedRight(domain)) {
			panRight(domain);
		}
		domainInfo.update(domain);
	}

	private void zoom(Interval domain) {
		space.update(domain, sampler.calculateNumberOfSamples());
		evaluateAll();
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

	private void panLeft(Interval domain) {
		double evaluateTo = domain.getLow();
		Interval x = space.head();
		while (x.getLow() > evaluateTo) {
			space.moveLeft();
			x = space.head();
			data.extendLeft(x, function.evaluate(x));

		}
		Log.debug("Panned left - count: " + data.count());
	}

	private void panRight(Interval domain) {
		double evaluateTo = domain.getHigh();
		Interval x = space.tail();
		while (x.getHigh() < evaluateTo) {
			space.moveRight();
			x = space.tail();
			data.extendRight(x, function.evaluate(x));
		}

		Log.debug("Panned right - count: " + data.count());
	}
}
