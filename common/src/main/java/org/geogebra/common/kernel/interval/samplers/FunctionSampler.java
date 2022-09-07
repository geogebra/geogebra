package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.euclidian.plot.interval.IntervalFunctionData;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpaceCentered;
import org.geogebra.common.kernel.interval.function.IntervalFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.util.debug.Log;

/**
 * Class to provide samples of the given function as a
 * list of (x, y) pairs, where both x and y are intervals.
 *
 * @author Laszlo
 */
public class FunctionSampler implements IntervalFunctionSampler {
	public static final int SPACE_MARGIN = 0;
	private final IntervalFunction function;

	private final IntervalFunctionDomainInfo domainInfo = new IntervalFunctionDomainInfo();
	private EuclidianViewBounds bounds;
	private int numberOfSamples;
	private IntervalFunctionData data;
	private DiscreteSpace space;
	private Interval xRange;
	/**
	 * @param geoFunction function to get sampled
	 * @param numberOfSamples the sample rate.
	 * @param domain
	 */
	public FunctionSampler(GeoFunction geoFunction,
			Interval domain, int numberOfSamples, IntervalFunctionData data) {
		this(geoFunction);
		xRange = domain;
		this.numberOfSamples = numberOfSamples;
		this.data = data;
		createSpace();
		update(xRange);
	}

	private void createSpace() {
		space = new DiscreteSpaceCentered(calculateStep());
	}

	private double calculateStep() {
		return (bounds != null ? bounds.domain().getLength()
			: xRange.getLength() ) / calculateNumberOfSamples();
	}

	/**
	 * @param geoFunction function to get sampled
	 * @param bounds {@link EuclidianView}
	 * @param data
	 */
	public FunctionSampler(GeoFunction geoFunction,
			EuclidianViewBounds bounds, IntervalFunctionData data) {
		this(geoFunction);
		this.bounds = bounds;
		numberOfSamples = -1;
		this.data = data;
		createSpace();
		update(bounds.domain());
	}

	FunctionSampler(GeoFunction geoFunction) {
		this.function = new IntervalFunction(geoFunction);
	}

	@Override
	public IntervalTupleList result() {
		return data.tuples();
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		this.space = space;
		return evaluateAll();
	}

	private IntervalTupleList evaluateAll() {
		data.clear();

		space.forEach(x -> {
			data.append(x, function.evaluate(x));
		});

		processAsymptotes(data.tuples());
		return data.tuples();
	}

	private static void processAsymptotes(IntervalTupleList samples) {
		IntervalAsymptotes asymptotes = new IntervalAsymptotes(samples);
		asymptotes.process();
	}

	@Override
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
		space.update(domain, calculateNumberOfSamples());
		evaluateAll();
	}

	private void panLeft(Interval domain) {
		double step = space.getStep();
		double toEvaluate = domain.getLow() - SPACE_MARGIN * step;
		Interval x = space.getMostLeftInterval();
		while (x.getLow() > toEvaluate) {
			space.moveLeft(1);
			x = space.getMostLeftInterval();
			data.extendLeft(x, function.evaluate(x));

		}
		Log.debug("Panned left - count: " + data.count());
	}

	private void panRight(Interval domain) {
		double step = space.getStep();
		double toEvaluate = domain.getHigh() + SPACE_MARGIN * step;
		Interval x = space.getMostRightInterval();
		while (x.getHigh() < toEvaluate) {
			space.moveRight(1);
			x = space.getMostRightInterval();
			data.extendRight(x, function.evaluate(x));
		}

		Log.debug("Panned right - count: " + data.count());
	}

	private int calculateNumberOfSamples() {
		return numberOfSamples > 0 ? numberOfSamples : bounds.getWidth();
	}

	@Override
	public GeoFunction getGeoFunction() {
		return function.getFunction();
	}

	@Override
	public IntervalTuple at(int index) {
		return data.tuples().get(index);
	}
}
