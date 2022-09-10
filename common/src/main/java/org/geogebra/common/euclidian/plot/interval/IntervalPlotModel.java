package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.samplers.IntervalFunctionSampler;

/**
 * Model for Interval plotter.
 *
 * @author laszlo
 */
public class IntervalPlotModel {
	private final IntervalFunctionData data;
	private final IntervalFunctionSampler sampler;
	private IntervalPath path;
	private final EuclidianViewBounds bounds;
	private boolean resampleNeeded = true;
	/**
	 * Constructor
	 * @param data of the function sampled.
	 * @param sampler to retrieve function data from.
	 * @param bounds {@link EuclidianView}
	 */
	public IntervalPlotModel(IntervalFunctionData data, IntervalFunctionSampler sampler,
			EuclidianViewBounds bounds) {
		this.data = data;
		this.sampler = sampler;
		this.bounds = bounds;
	}

	public void setPath(IntervalPath path) {
		this.path = path;
	}

	/**
	 * Updates what's necessary.
	 */
	public void update() {
		if (resampleNeeded) {
			resample();
		}

		path.update();
	}

	/**
	 * Updates the entire model.
	 */
	public void resample() {
		sampler.resample(bounds.domain());
		resampleNeeded = false;
	}

	private void updatePath() {
		if (data.isValid()) {
			path.update();
		}
	}

	/**
	 *
	 * update function domain to plot due to the visible x range.
	 */
	public void updateDomain() {
		sampler.extend(bounds.domain());
		updatePath();
	}

	/**
	 * Clears the entire model.
	 */
	public void clear() {
		path.reset();
		data.clear();
	}

	GPoint getLabelPoint() {
		return path.getLabelPoint();
	}

	public GeoFunction getGeoFunction() {
		return data.getGeoFunction();
	}

	public void needsResampling() {
		resampleNeeded = true;
	}
}