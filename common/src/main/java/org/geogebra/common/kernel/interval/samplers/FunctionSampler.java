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

/**
 * Class to provide samples of the given function as a
 * list of (x, y) pairs, where both x and y are intervals.
 *
 * @author Laszlo
 */
public class FunctionSampler implements IntervalFunctionSampler {
	final IntervalFunction function;

	private EuclidianViewBounds bounds;
	private int numberOfSamples;
	private IntervalFunctionData data;
	private UpdateFunctionData updateFunctionData;

	/**
	 * @param geoFunction function to get sampled
	 * @param numberOfSamples the sample rate.
	 * @param domain
	 */
	public FunctionSampler(IntervalFunctionData data, Interval domain, int numberOfSamples) {
		this(data.getGeoFunction());
		this.numberOfSamples = numberOfSamples;
		this.data = data;
		updateFunctionData = new UpdateFunctionData(this, data, createSpaceOn(domain));
		update(domain);
	}

	private DiscreteSpace createSpaceOn(Interval domain) {
		return new DiscreteSpaceCentered(domain.getLength() / calculateNumberOfSamples());
	}

	/**
	 * @param geoFunction function to get sampled
	 * @param bounds {@link EuclidianView}
	 * @param data
	 */
	public FunctionSampler(IntervalFunctionData data, EuclidianViewBounds bounds) {
		this(data.getGeoFunction());
		this.bounds = bounds;
		numberOfSamples = -1;
		this.data = data;
		updateFunctionData = new UpdateFunctionData(this, data, createSpaceOn(bounds.domain()));
		update(bounds.domain());
	}

	FunctionSampler(GeoFunction geoFunction) {
		this.function = new IntervalFunction(geoFunction);
	}

	@Override
	public IntervalTupleList tuples() {
		return data.tuples();
	}

	@Override
	public void update(Interval domain) {
		updateFunctionData.update(domain);
	}


	int calculateNumberOfSamples() {
		return numberOfSamples > 0 ? numberOfSamples : bounds.getWidth();
	}

	@Override
	public GeoFunction getGeoFunction() {
		return function.getFunction();
	}

	@Override
	public IntervalTuple at(int index) {
		return data.at(index);
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}
}
