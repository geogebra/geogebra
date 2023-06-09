package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.euclidian.plot.interval.IntervalFunctionData;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpaceCentered;
import org.geogebra.common.kernel.interval.function.IntervalNodeFunction;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

/**
 * Class to provide samples of the given function as a
 * list of (x, y) pairs, where both x and y are intervals.
 *
 * @author Laszlo
 */
public class FunctionSampler implements IntervalFunctionSampler {
	private final EuclidianViewBounds bounds;
	private final IntervalFunctionDomainInfo domainInfo = new IntervalFunctionDomainInfo();
	private IntervalNodeFunction function;
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
		this.space = createSpaceOn(domain);
		function = data.getFunction();
		extend(domain);

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
	public void extend(Interval domain) {
		if (!domainInfo.intersects(domain) || domain.getLength() > 2 * domainInfo.getLength()) {
			resample(domain);
			return;
		}
		if (domainInfo.hasZoomedOut(domain)) {
			extendDataBothSide(domain);
		} else if (domainInfo.hasPannedLeft(domain)) {
			extendDataToLeft(domain);
		} else if (domainInfo.hasPannedRight(domain)) {
			extendDataToRight(domain);
		}
		domainInfo.update(domain);
	}

	private void extendDataBothSide(Interval domain) {
		space.extend(domain, x -> data.prepend(x, function.value(x)),
				x -> data.append(x, function.value(x)));
	}

	@Override
	public void resample(Interval domain) {
		function = data.getFunction();
		space.rescale(domain, calculateNumberOfSamples());
		evaluateAll();
		domainInfo.update(domain);
	}

	private void evaluateAll() {
		data.clear();
		space.forEach(x -> data.append(x, function.value(x)));
	}

	private void extendDataToLeft(Interval domain) {
		space.extendLeft(domain, x -> data.extendLeft(x, function.value(x)));
	}

	private void extendDataToRight(Interval domain) {
		space.extendRight(domain, x -> data.extendRight(x, function.value(x)));
	}

	int calculateNumberOfSamples() {
		return hasBounds() ? bounds.getWidth() : numberOfSamples;
	}

	private boolean hasBounds() {
		return bounds != null;
	}

}
