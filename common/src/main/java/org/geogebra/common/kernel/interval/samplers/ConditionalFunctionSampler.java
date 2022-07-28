package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpaceImp;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class ConditionalFunctionSampler implements IntervalFunctionSampler {

	private ConditionalSamplerList samplers;
	private GeoFunction function;
	private IntervalTuple range;
	private EuclidianViewBounds evBounds;
	private final DiscreteSpace space;

	/**
	 *
	 * @param function to sample.
	 * @param range (x, y) range to sample on.
	 * @param evBounds {@link EuclidianViewBounds}
	 */
	public ConditionalFunctionSampler(GeoFunction function, IntervalTuple range,
			EuclidianViewBounds evBounds) {
		this.function = function;
		samplers = new ConditionalSamplerList(function);
		this.range = range;
		this.evBounds = evBounds;
		space = new DiscreteSpaceImp(range.x(), evBounds.getWidth());
	}

	@Override
	public IntervalTupleList result() {
		update(range);
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
	public void update(IntervalTuple range) {
		space.update(range.x(), evBounds.getWidth());
		samplers.update(range.x(), evBounds.getWidth());
	}

	@Override
	public IntervalTupleList extendDomain(double min, double max) {
		setInterval(min, max);
		return evaluate(space);
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		return samplers.evaluate(space);
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
