package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpaceImp;
import org.geogebra.common.kernel.interval.function.IntervalFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

/**
 * Class to provide samples of the given function as a
 * list of (x, y) pairs, where both x and y are intervals.
 *
 * @author Laszlo
 */
public class FunctionSampler implements IntervalFunctionSampler {

	private final IntervalFunction function;
	private EuclidianViewBounds bounds;
	private int numberOfSamples;
	private final DiscreteSpace space;

	/**
	 * @param geoFunction function to get sampled
	 * @param range (x, y) range.
	 * @param numberOfSamples the sample rate.
	 */
	public FunctionSampler(GeoFunction geoFunction, IntervalTuple range,
			int numberOfSamples) {
		this(geoFunction);
		this.numberOfSamples = numberOfSamples;
		update(range);
	}

	/**
	 * @param geoFunction function to get sampled
	 * @param range (x, y) range.
	 * @param bounds {@link EuclidianView}
	 */
	public FunctionSampler(GeoFunction geoFunction, IntervalTuple range,
			EuclidianViewBounds bounds) {
		this(geoFunction);
		this.bounds = bounds;
		update(range);
	}

	FunctionSampler(GeoFunction geoFunction) {
		this.function = new IntervalFunction(geoFunction);
		space = new DiscreteSpaceImp();
	}

	@Override
	public IntervalTupleList result() {
		return evaluate(space);
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		return evaluate(x.getLow(), x.getHigh());
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		DiscreteSpaceImp diffSpace = new DiscreteSpaceImp(low, high, space.getStep());
		return evaluate(diffSpace);
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		IntervalTupleList samples = new IntervalTupleList();
		evaluateOnEach(space, samples);
		IntervalAsymptotes asymptotes = new IntervalAsymptotes(samples);
		asymptotes.process();
		return samples;
	}

	private void evaluateOnEach(DiscreteSpace space, IntervalTupleList samples) {
		space.values().forEach(x -> {
			IntervalTuple tuple = new IntervalTuple(x, function.evaluate(x));
			samples.add(tuple);
		});
	}

	/**
	 * Updates the range on which sampler has to run.
	 *
	 * @param range the new (x, y) range
	 */
	@Override
	public void update(IntervalTuple range) {
		space.update(range.x(), calculateNumberOfSamples());
	}

	private int calculateNumberOfSamples() {
		return numberOfSamples > 0 ? numberOfSamples : bounds.getWidth();
	}

	/**
	 * Extend and evaluate on interval [min, max]
	 * @param min lower bound
	 * @param max higher bound
	 * @return tuples evaluated on [min, max].
	 */
	@Override
	public IntervalTupleList extendDomain(double min, double max) {
		setInterval(min, max);
		return evaluate(space);
	}

	/**
	 * Sets plot interval without evaluation
	 * @param low bound.
	 * @param high bound.
	 */
	@Override
	public void setInterval(double low, double high) {
		space.setInterval(low, high);
	}

	@Override
	public GeoFunction getGeoFunction() {
		return function.getFunction();
	}
}
