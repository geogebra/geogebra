package org.geogebra.common.kernel.interval;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Class to provide samples of the given function as a
 * list of (x, y) pairs, where both x and y are intervals.
 *
 * @author Laszlo
 */
public class IntervalFunctionSampler {

	private final IntervalFunction function;
	private final int numberOfSamples;
	private final LinearSpace space;
	private final IntervalTuple range;

	/**
	 *
	 * @param geo function to get sampled
	 * @param range (x, y) range.
	 * @param numberOfSamples the sample rate.
	 */
	public IntervalFunctionSampler(GeoFunction geo, IntervalTuple range,
			int numberOfSamples) {
		this.function = new IntervalFunction(geo);
		this.numberOfSamples = numberOfSamples;
		this.range = range;
		space = new LinearSpace();
		update(range);
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
	 * Gets the samples with the predefined range and sample rate
	 *
	 * @return the sample list
	 */
	public IntervalTupleList result(LinearSpace space) {
		try {
			return evaluateOnSpace(space);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new IntervalTupleList();
	}

	private IntervalTupleList evaluateOnSpace(LinearSpace space) throws Exception {
		List<Double> xCoords = space.values();
		IntervalTupleList samples = new IntervalTupleList();
		if (xCoords.size() == 1) {
			Interval x = new Interval(xCoords.get(0));
			Interval y = function.evaluate(x);
			samples.add(new IntervalTuple(x, y));
			return samples;
		}

		for (int i = 0; i < xCoords.size() - 1; i += 1) {
			Interval x = new Interval(xCoords.get(i), xCoords.get(i + 1));
			Interval y = function.evaluate(x);

			if (!y.isEmpty() && !y.isWhole()) {
				samples.add(new IntervalTuple(x, y));
			}
			if (y.isWhole()) {
				// means that the next and prev intervals need to be fixed
				samples.add(null);
			}
		}
		fixAsymtotes(samples);
		return samples;
	}

	private void fixAsymtotes(IntervalTupleList samples) {
		for (int i = 1; i < samples.count(); i++) {
			IntervalTuple point = samples.get(i);
			if (point == null) {
				IntervalTuple prev = samples.get(i - 1);
				IntervalTuple next = samples.get(i + 1);
				if (prev != null && next != null && !prev.y().isOverlap(next.y())) {
					if (prev.y().getLow() > next.y().getHigh()) {
						prev.y().setHigh(Math.max(prev.y().getHigh(), range.y().getHigh()));
						next.y().setLow(Math.min(range.y().getLow(), next.y().getLow()));
					}

					if (prev.y().getHigh() < next.y().getLow()) {
						prev.y().setLow(Math.min(prev.y().getLow(), range.y().getLow()));
						next.y().setHigh(Math.max(range.y().getHigh(), next.y().getHigh()));
					}
				}
			}
		}
	}

	/**
	 * Updates the range on which sampler has to run.
	 *
	 * @param range the new (x, y) range
	 */
	public void update(IntervalTuple range) {
		space.update(range.x(), numberOfSamples);
	}

	public IntervalTupleList extendMax(double max) {
		return evaluateAtDomain(space.extendMax(max));
	}

	private IntervalTupleList evaluateAtDomain(LinearSpace domain) {
		try {
			return evaluateOnSpace(domain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public IntervalTupleList extendMin(double min) {
		return evaluateAtDomain(space.extendMin(min));
	}

	public int shrinkMax(double max) {
		return space.shrinkMax(max);
	}

	public int shrinkMin(double min) {
		return space.shrinkMin(min);
	}
}
