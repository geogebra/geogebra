package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.euclidian.plot.interval.IntervalFunctionData;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
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
	private EuclidianViewBounds bounds;
	private int numberOfSamples;
	private IntervalFunctionData data;
	private DiscreteSpace space;
	private Interval xRange;
	private Interval domainBefore = IntervalConstants.undefined();

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
			IntervalTuple tuple = new IntervalTuple(x, function.evaluate(x));
			data.append(tuple);
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
		if (hasZoomed(domain)) {
			zoom(domain);
		} else if (hasPannedLeft(domain)) {
			panLeft(domain);
		} else if (hasPannedRight(domain)) {
			panRight(domain);
		}

		domainBefore = domain;
	}

	private void zoom(Interval domain) {
		space.update(domain, calculateNumberOfSamples());
		evaluateAll();
		Log.debug("Zoomed - space: " + space);
	}

	private void panLeft(Interval domain) {
		double step = space.getStep();
		double toEvaluate = domain.getLow() - SPACE_MARGIN * step;
		Interval x = space.getMostLeftInterval();
		while (x.getLow() > toEvaluate) {
			space.moveLeft(1);
			x = space.getMostLeftInterval();
			IntervalTuple tuple = new IntervalTuple(x, function.evaluate(x));
			data.extendLeft(tuple);

		}

		Log.debug("Panned left - data: " + data.count() + " space: " + space);
	}

	private void panRight(Interval domain) {
		double step = space.getStep();
		double toEvaluate = domain.getHigh() + SPACE_MARGIN * step;
		Interval x = space.getMostRightInterval();
		while (x.getHigh() < toEvaluate) {
			space.moveRight(1);
			x = space.getMostRightInterval();
			IntervalTuple tuple = new IntervalTuple(x, function.evaluate(x));
			data.extendRight(tuple);
		}

		Log.debug("Panned left - data: " + data.count() + " space: " + space);
	}

	private boolean hasZoomed(Interval domain) {
		return (isMinLower(domain) && isMaxHigher(domain))
				|| (isMinHigher(domain) && isMaxLower(domain));
	}

	private boolean hasPannedLeft(Interval domain) {
		return isMinLower(domain);
	}

	private boolean hasPannedRight(Interval domain) {
		return isMaxHigher(domain);
	}

	private boolean isMinHigher(Interval domain) {
		return domain.getLow() > domainBefore.getLow() ;
	}


	private boolean isMaxLower(Interval domain) {
		return domain.getHigh() < domainBefore.getHigh();
	}

	private boolean isMaxHigher(Interval domain) {
		return domain.getHigh() > domainBefore.getHigh();
	}

	private boolean isMinLower(Interval domain) {
		return domain.getLow() < domainBefore.getLow();
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
