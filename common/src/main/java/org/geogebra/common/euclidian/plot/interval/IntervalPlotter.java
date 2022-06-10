package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.samplers.ConditionalFunctionSampler;
import org.geogebra.common.kernel.interval.samplers.FunctionSampler;
import org.geogebra.common.kernel.interval.samplers.IntervalFunctionSampler;

/**
 * Function plotter based on interval arithmetic
 *
 * @author laszlo
 */
public class IntervalPlotter {
	private final EuclidianViewBounds evBounds;
	private final IntervalPathPlotter gp;
	private boolean enabled;
	private IntervalPlotModel model = null;
	private boolean updateAll = true;
	private IntervalPlotController controller;

	/**
	 * Creates a disabled plotter
	 */
	public IntervalPlotter(EuclidianViewBounds bounds, IntervalPathPlotter pathPlotter) {
		this.evBounds = bounds;
		this.gp = pathPlotter;
		this.enabled = false;
	}

	/**
	 * Enables plotter without controller
	 */
	public void enableFor(GeoFunction function) {
		enabled = true;
		createModel(function);
		createController();
		needsUpdateAll();
		update();
	}

	/**
	 * Enables plotter
	 */
	public void enableFor(GeoFunction function, EuclidianController euclidianController) {
		enabled = true;
		createModel(function);
		createController();
		this.controller.attachEuclidianController(euclidianController);
		needsUpdateAll();
		update();
	}

	private void createController() {
		this.controller = new IntervalPlotController(model);
	}

	private void createModel(GeoFunction function) {
		IntervalTuple range = new IntervalTuple(evBounds.domain(), evBounds.range());
		IntervalFunctionSampler sampler = createSampler(function, range);
		model = new IntervalPlotModel(range, sampler, evBounds);
		IntervalPath path = new IntervalPath(gp, evBounds, model);
		model.setPath(path);
	}

	private IntervalFunctionSampler createSampler(GeoFunction function, IntervalTuple range) {
		return function.isGeoFunctionConditional()
				? new ConditionalFunctionSampler(function, range, evBounds)
				: new FunctionSampler(function, range, evBounds);
	}

	/**
	 * Update path to draw.
	 */
	public void update() {
		if (updateAll) {
			model.updateAll();
			updateAll = false;
		} else {
			model.update();
		}
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
		return model.getLabelPoint();
	}

	/**
	 * Call it when plotter needs a full update
	 */
	public void needsUpdateAll() {
		updateAll = true;
	}
}
