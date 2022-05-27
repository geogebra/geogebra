package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoFunction;

public class IfFunctionSampler implements IntervalFunctionSampler {

	private ConditionalSamplerList samplers;
	private GeoFunction function;
	private IntervalTuple range;
	private EuclidianViewBounds evBounds;
	private final DiscreteSpace space;
	private List<IntervalTupleList> results = new ArrayList<>();

	public IfFunctionSampler(GeoFunction function, IntervalTuple range,
			EuclidianViewBounds evBounds) {
		this.function = function;
		samplers = new ConditionalSamplerList(function);
		this.range = range;
		this.evBounds = evBounds;
		space = new DiscreteSpaceImp(range.x(), evBounds.getWidth());
	}

	private void extractConditions(GeoFunction function) {
		samplers.process(function);
	}

	@Override
	public IntervalTupleList result() {
		results = results();
		return results.isEmpty() ? IntervalTupleList.emptyList(): results().get(0);
	}

	@Override
	public List<IntervalTupleList> results() {
		results.clear();
		update(range);
		samplers.forEach(sampler -> results.add(sampler.result()));
		return results;
	}

	@Override
	public IntervalTupleList evaluateOn(Interval x) {
		return evaluateOn(x.getLow(), x.getHigh());
	}

	@Override
	public IntervalTupleList evaluateOn(double low, double high) {
		return samplers.evaluateBetween(low, high);
	}

	@Override
	public void update(IntervalTuple range) {
		samplers.update(range.x(), evBounds.getWidth());
	}

	@Override
	public IntervalTupleList extendDomain(double min, double max) {
		setInterval(min, max);
		return evaluateOnSpace(space);
	}

	private IntervalTupleList evaluateOnSpace(DiscreteSpace space) {
		return samplers.evaluateOnSpace(space);
	}

	@Override
	public void setInterval(double low, double high) {
		space.setInterval(low, high);
	}

	@Override
	public GeoFunction getGeoFunction() {
		return function;
	}
}
