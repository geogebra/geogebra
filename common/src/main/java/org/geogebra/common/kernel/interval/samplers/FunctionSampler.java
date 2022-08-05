package org.geogebra.common.kernel.interval.samplers;

import java.util.stream.Stream;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpaceCentered;
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
	private DiscreteSpace space;
	private Interval xRange;

	/**
	 * @param geoFunction function to get sampled
	 * @param numberOfSamples the sample rate.
	 * @param domain
	 */
	public FunctionSampler(GeoFunction geoFunction,
			Interval domain, int numberOfSamples) {
		this(geoFunction);
		xRange = domain;
		this.numberOfSamples = numberOfSamples;
		createSpace();
		update(bounds.domain());
	}

	private void createSpace() {
		space = new DiscreteSpaceCentered(calculateStep());
	}

	private double calculateStep() {
		return (bounds != null ? bounds.domain().getLength()
			: xRange.getLength() ) / numberOfSamples;
	}

	/**
	 * @param geoFunction function to get sampled
	 * @param bounds {@link EuclidianView}
	 */
	public FunctionSampler(GeoFunction geoFunction,
			EuclidianViewBounds bounds) {
		this(geoFunction);
		this.bounds = bounds;
		numberOfSamples = bounds.getWidth();
		createSpace();
		update(bounds.domain());
	}

	FunctionSampler(GeoFunction geoFunction) {
		this.function = new IntervalFunction(geoFunction);
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
	public IntervalTupleList evaluate(DiscreteSpace space) {
		return evaluateOnStream(space.values());
	}

	private IntervalTupleList evaluateOnStream(Stream<Interval> values) {
		IntervalTupleList tuples = new IntervalTupleList();
		values.forEach(x -> {
			IntervalTuple tuple = new IntervalTuple(x, function.evaluate(x));
			tuples.add(tuple);
		});
		return processAsymptotes(tuples);
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		return evaluateOnStream(space.values(low, high));
	}

	private static IntervalTupleList processAsymptotes(IntervalTupleList samples) {
		IntervalAsymptotes asymptotes = new IntervalAsymptotes(samples);
		asymptotes.process();
		return samples;
	}

	@Override
	public void update(Interval domain) {
		space.update(domain, calculateNumberOfSamples());
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
		space.update(min, max, calculateStep());
		return evaluate(space);
	}

	@Override
	public GeoFunction getGeoFunction() {
		return function.getFunction();
	}

	@Override
	public Interval getDomain() {
		return space.getDomain();
	}
}
