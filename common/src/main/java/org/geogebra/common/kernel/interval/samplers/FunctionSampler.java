package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.euclidian.plot.interval.IntervalFunctionData;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpaceCentered;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

/**
 * Class to provide samples of the given function as a
 * list of (x, y) pairs, where both x and y are intervals.
 *
 * @author Laszlo
 */
public class FunctionSampler implements IntervalFunctionSampler {
	private final EuclidianViewBounds bounds;
	private int numberOfSamples;
	private final IntervalFunctionData data;
	private final UpdateFunctionData updateFunctionData;

	/**
	 * @param data where the sampled data of the function will be stored.
	 * @param domain an interval of x to sample.
	 * @param numberOfSamples to take on the domain.
	 */
	public FunctionSampler(IntervalFunctionData data, Interval domain, int numberOfSamples) {
		bounds = null;
		this.numberOfSamples = numberOfSamples;
		this.data = data;
		updateFunctionData = new UpdateFunctionData(this, data, createSpaceOn(domain));
		pan(domain);
	}

	private DiscreteSpace createSpaceOn(Interval domain) {
		return new DiscreteSpaceCentered(domain.getLength() / calculateNumberOfSamples());
	}

	/**
	 * @param data where the sampled function data will be stored.
	 * @param bounds {@link EuclidianView} to calculate domain and number of samples.
	 */
	public FunctionSampler(IntervalFunctionData data, EuclidianViewBounds bounds) {
		this.bounds = bounds;
		this.data = data;
		updateFunctionData = new UpdateFunctionData(this, data, createSpaceOn(bounds.domain()));
		pan(bounds.domain());
	}

	@Override
	public IntervalTupleList tuples() {
		return data.tuples();
	}

	@Override
	public void pan(Interval domain) {
		updateFunctionData.completeDataOn(domain);
	}

	@Override
	public void zoom(Interval domain) {
		updateFunctionData.zoom(domain);
	}

	int calculateNumberOfSamples() {
		return hasBounds() ? bounds.getWidth() : numberOfSamples;
	}

	private boolean hasBounds() {
		return bounds != null;
	}

	@Override
	public GeoFunction getGeoFunction() {
		return data.getGeoFunction();
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
