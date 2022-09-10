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
	private final EuclidianViewBounds bounds;
	private final IntervalFunctionDomainInfo domainInfo = new IntervalFunctionDomainInfo();
	private final IntervalFunction function;
	private final DiscreteSpace space;
	private final int numberOfSamples;
	private final IntervalFunctionData data;
	
	

	/**
	 * @param data where the sampled data of the function will be stored.
	 * @param domain an interval of x to sample.
	 * @param numberOfSamples to take on the domain.
	 */
	public FunctionSampler(IntervalFunctionData data, Interval domain, int numberOfSamples) {
		this(data, null, domain, numberOfSamples);
	}
	private FunctionSampler(IntervalFunctionData data, EuclidianViewBounds bounds, Interval domain,
			int numberOfSamples) {
		this.bounds = bounds;
		this.numberOfSamples = numberOfSamples;
		this.data = data;
		this.function = new IntervalFunction(data.getGeoFunction());
		this.space = createSpaceOn(domain);
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
		this(data, bounds, bounds.domain(), -1);
	}

	@Override
	public IntervalTupleList tuples() {
		return data.tuples();
	}

	@Override
	public void pan(Interval domain) {
		completeDataOn(domain);
	}

	public void completeDataOn(Interval domain) {
		if (domainInfo.hasZoomedOut(domain)) {
			extendDataBothSide(domain);
		} else if (domainInfo.hasPannedLeft(domain)) {
			extendDataToLeft(domain);
		} else if (domainInfo.hasPannedRight(domain)) {
			extendDataToRight(domain);
		}
		Log.debug("count: " + data.count());
		domainInfo.update(domain);
	}

	private void extendDataBothSide(Interval domain) {
		space.extend(domain, x -> data.prepend(x, function.evaluate(x)),
				x -> data.append(x, function.evaluate(x)));
	}

	public void resample(Interval domain) {
		space.rescale(domain, calculateNumberOfSamples());
		evaluateAll();
		domainInfo.update(domain);
	}

	private void evaluateAll() {
		data.clear();
		space.forEach(x -> data.append(x, function.evaluate(x)));
		processAsymptotes(data.tuples());
	}

	private static void processAsymptotes(IntervalTupleList samples) {
		IntervalAsymptotes asymptotes = new IntervalAsymptotes(samples);
		asymptotes.process();
	}

	private void extendDataToLeft(Interval domain) {
		space.extendLeft(domain, x -> data.extendLeft(x, function.evaluate(x)));
	}

	private void extendDataToRight(Interval domain) {
		space.extendRight(domain, x -> data.extendRight(x, function.evaluate(x)));
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
