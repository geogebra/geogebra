package org.geogebra.common.kernel.interval;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.util.debug.Log;

/**
 * Class to provide samples of the given function as a
 * list of (x, y) pairs, where both x and y are intervals.
 *
 * @author Laszlo
 */
public class IntervalFunctionSampler {

	private final IntervalFunction function;
	private EuclidianViewBounds bounds;
	private int numberOfSamples;
	private final DiscreteSpace space;

	/**
	 * @param geoFunction function to get sampled
	 * @param range (x, y) range.
	 * @param numberOfSamples the sample rate.
	 */
	public IntervalFunctionSampler(GeoFunction geoFunction, IntervalTuple range,
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
	public IntervalFunctionSampler(GeoFunction geoFunction, IntervalTuple range,
			EuclidianViewBounds bounds) {
		this(geoFunction);
		this.bounds = bounds;
		update(range);
	}

	private IntervalFunctionSampler(GeoFunction geoFunction) {
		this.function = new IntervalFunction(geoFunction);
		space = new DiscreteSpaceImp();
	}

	/**
	 * Gets the samples with the predefined range and sample rate
	 *
	 * @return the sample list
	 */
	public IntervalTupleList result() {
		try {
			return evaluateOnSpace(space);
		} catch (Exception e) {
			Log.debug(e);
		}
		return new IntervalTupleList();
	}

	/**
	 * Evaluate on interval [high, low] with the same step that used before
	 * @param low  lower bound
	 * @param high higher bound
	 * @return tuples evaluated on [low, high].
	 */
	public IntervalTupleList evaluateOn(double low, double high) {
		DiscreteSpaceImp diffSpace = new DiscreteSpaceImp(low, high, space.getStep());
		return evaluateOnSpace(diffSpace);
	}

	private IntervalTupleList evaluateOnSpace(DiscreteSpace space) {
		IntervalTupleList samples = new IntervalTupleList();
		evaluateOnEach(space, samples);
		IntervalAsymptotes asymptotes = new IntervalAsymptotes(samples);
		asymptotes.process();
		return samples;
	}

	private void evaluateOnEach(DiscreteSpace space, IntervalTupleList samples) {
		space.values().forEach(x -> {
					try {
						Interval y = function.evaluate(x);
						IntervalTuple tuple = new IntervalTuple(x, y);
						if (isNonDegeneratedValue(tuple.y())) {
							samples.add(tuple);
						}
					} catch (Exception e) {
						Log.debug(e);
					}
				});
	}

	private boolean isNonDegeneratedValue(Interval y) {
		return !(y.isPositiveInfinity() && y.isInverted());
	}

	/**
	 * Updates the range on which sampler has to run.
	 *
	 * @param range the new (x, y) range
	 */
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
	public IntervalTupleList extendDomain(double min, double max) {
		setInterval(min, max);
		return evaluateOnSpace(space);
	}

	/**
	 * Sets plot interval without evaluation
	 * @param low bound.
	 * @param high bound.
	 */
	public void setInterval(double low, double high) {
		space.setInterval(low, high);
	}

	public GeoFunction getGeoFunction() {
		return function.getFunction();
	}
}
