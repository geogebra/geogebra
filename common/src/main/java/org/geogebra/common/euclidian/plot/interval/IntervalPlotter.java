package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.GeoFunctionConverter;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.FunctionSampler;

/**
 * Function plotter based on interval arithmetic
 *
 * @author laszlo
 */
public class IntervalPlotter {
	private final EuclidianViewBounds evBounds;
	private final IntervalPathPlotter gp;
	private boolean enabled;
	private IntervalFunctionModel model = null;

	private IntervalPlotController controller;
	private final GeoFunctionConverter converter;
	private IntervalPath path;

	/**
	 * Creates a disabled plotter
	 */
	public IntervalPlotter(GeoFunctionConverter converter, EuclidianViewBounds bounds,
			IntervalPathPlotter pathPlotter) {
		this.converter = converter;
		this.evBounds = bounds;
		this.gp = pathPlotter;
		this.enabled = false;
	}

	/**
	 * Enables plotter without controller
	 */
	public void enableFor(GeoFunction function) {
		build(function);
		enable();
	}

	private void enable() {
		enabled = true;
		model.update();
	}

	/**
	 * Enables plotter
	 */
	public void enableFor(GeoFunction function, EuclidianView view) {
		build(function);
		this.controller.attachEuclidianView(view);
		enable();
	}

	private void build(GeoFunction function) {
		IntervalTupleList tuples = new IntervalTupleList();
		IntervalFunctionData data = new IntervalFunctionData(function, converter, evBounds, tuples);
		FunctionSampler sampler = new FunctionSampler(data, evBounds);
		QueryFunctionData query = new QueryFunctionDataImpl(tuples);
		path = new IntervalPath(gp, evBounds, query);
		model = new IntervalFunctionModelImpl(data, sampler, evBounds, path);
		this.controller = new IntervalPlotController(model, function);
	}

	/**
	 * Update path to draw.
	 */
	public void update() {
		model.update();
	}

	/**
	 * Draws result to Graphics
	 *
	 * @param g2 {@link GGraphics2D}
	 */
	public void draw(GGraphics2D g2) {
		gp.draw(g2);
	}

	/**
	 *
	 * @return if plotter is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Disable interval plotter.
	 */
	public void disable() {
		enabled = false;
		if (model != null) {
			model.clear();
		}

		if (controller != null) {
			controller.detach();
		}
	}

	/**
	 * @return point of label
	 */
	public GPoint getLabelPoint() {
		return path.getLabelPoint();
	}

	/**
	 * Call it when plotter needs a full update
	 */
	public void needsUpdateAll() {
		model.needsResampling();
	}
}
