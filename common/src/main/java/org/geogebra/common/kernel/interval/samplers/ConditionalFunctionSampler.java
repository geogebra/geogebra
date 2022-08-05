package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpaceImp;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class ConditionalFunctionSampler implements IntervalFunctionSampler {

	private final ConditionalSamplerList samplers;
	private final GeoFunction function;
	private final IntervalTuple range;
	private final EuclidianViewBounds evBounds;
	private final DiscreteSpace space;

	/**
	 *
	 * @param function to sample.
	 * @param domain x range to sample on.
	 * @param evBounds {@link EuclidianViewBounds}
	 */
	public ConditionalFunctionSampler(GeoFunction function, Interval domain,
			EuclidianViewBounds evBounds) {
		this.function = function;
		samplers = new ConditionalSamplerList(function);
		this.range = null;
		this.evBounds = evBounds;
		space = new DiscreteSpaceImp(domain, evBounds.getWidth());
	}

	@Override
	public IntervalTupleList result() {
		update(range.x());
		return evaluate(space);
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		return evaluate(x.getLow(), x.getHigh());
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		return samplers.evaluate(low, high);
	}

	@Override
	public void update(Interval domain) {
		space.update(domain, evBounds.getWidth());
		samplers.update(domain, evBounds.getWidth());
	}

	@Override
	public IntervalTupleList extendDomain(double min, double max) {
		space.update(min, max, calculateStep());
		return evaluate(space);
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		return samplers.evaluate(space);
	}

	private double calculateStep() {
		return range.x().getLength() / evBounds.getWidth();
	}

	@Override
	public GeoFunction getGeoFunction() {
		return function;
	}

	@Override
	public Interval getDomain() {
		return null;
	}

}
