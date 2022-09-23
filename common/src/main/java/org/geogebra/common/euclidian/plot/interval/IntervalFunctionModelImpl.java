package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.interval.samplers.IntervalFunctionSampler;

/**
 * Model for Interval plotter.
 *
 * @author laszlo
 */
public class IntervalFunctionModelImpl implements IntervalFunctionModel {
	private final IntervalFunctionData data;
	private final IntervalFunctionSampler sampler;
	private final IntervalPath path;
	private final EuclidianViewBounds bounds;
	private boolean resampleNeeded = true;

	/**
	 * Constructor
	 * @param data of the function sampled.
	 * @param sampler to retrieve function data from.
	 * @param bounds {@link EuclidianView}
	 */
	public IntervalFunctionModelImpl(IntervalFunctionData data, IntervalFunctionSampler sampler,
			EuclidianViewBounds bounds, IntervalPath path) {
		this.data = data;
		this.sampler = sampler;
		this.bounds = bounds;
		this.path = path;
	}

	/**
	 * Updates what's necessary.
	 */
	@Override
	public void update() {
		if (resampleNeeded) {
			resample();
		}

		path.update();
	}

	/**
	 * Updates the entire model.
	 */
	@Override
	public void resample() {
		sampler.resample(bounds.domain());
		updatePath();
		resampleNeeded = false;
	}

	/**
	 *
	 * update function domain to plot due to the visible x range.
	 */
	@Override
	public void updateDomain() {
		sampler.extend(bounds.domain());
		updatePath();
	}

	private void updatePath() {
		if (data.isValid()) {
			path.update();
		}
	}

	/**
	 * Clears the entire model.
	 */
	@Override
	public void clear() {
		path.reset();
		data.clear();
	}

	@Override
	public void needsResampling() {
		resampleNeeded = true;
	}
}