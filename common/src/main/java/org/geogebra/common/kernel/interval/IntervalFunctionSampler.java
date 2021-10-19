package org.geogebra.common.kernel.interval;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Class to provide samples of the given function as a
 * list of (x, y) pairs, where both x and y are intervals.
 *
 * @author Laszlo
 */
public class IntervalFunctionSampler {

	private final IntervalFunction function;
	private EuclidianView view;
	private int numberOfSamples;
	private final DiscreteSpace space;
	private boolean addEmpty;

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
	 * @param view {@link EuclidianView}
	 */
	public IntervalFunctionSampler(GeoFunction geoFunction, IntervalTuple range,
			EuclidianView view) {
		this(geoFunction);
		this.view = view;
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
			e.printStackTrace();
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
		addEmpty = true;
		space.values().forEach(x -> {
					try {
						Interval y = function.evaluate(x);
						if (!y.isEmpty() || addEmpty) {
							IntervalTuple tuple = new IntervalTuple(x, y);
							samples.add(tuple);
						}

						addEmpty = !y.isEmpty();

					} catch (Exception e) {
						e.printStackTrace();
					}
				});

		IntervalAsymptotes asymptotes = new IntervalAsymptotes(samples);
		asymptotes.process();
		return samples;
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
		return numberOfSamples > 0 ? numberOfSamples : view.getWidth();
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