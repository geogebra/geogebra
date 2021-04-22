package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.IntervalFunctionSampler;
import org.geogebra.common.kernel.interval.IntervalTuple;

/**
 * Function plotter based on interval arithmetic
 *
 * @author laszlo
 */
public class IntervalPlotter {
	private final EuclidianView view;
	private final IntervalPathPlotter gp;
	private boolean enabled;
	private IntervalPlotModel model = null;
	private boolean updateAll = true;
	private IntervalPlotController controller;

	/**
	 * Creates a disabled plotter
	 */
	public IntervalPlotter(EuclidianView view, GeneralPathClipped gp) {
		this.view = view;
		this.gp = new IntervalPathPlotterImpl(gp);
		this.enabled = false;
	}

	/**
	 * Creates a disabled plotter
	 */
	public IntervalPlotter(EuclidianView view, IntervalPathPlotter pathPlotter) {
		this.view = view;
		this.gp = pathPlotter;
		this.enabled = false;
	}

	/**
	 * Enables plotter
	 */
	public void enableFor(GeoFunction function) {
		enabled = true;
		createModel(function);
		createController();
		needsUpdateAll();
		update();
	}

	private void createController() {
		controller = new IntervalPlotController(model);
		controller.attachEuclidianController(view.getEuclidianController());
	}

	private void createModel(GeoFunction function) {
		IntervalTuple range = new IntervalTuple();
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, view);
		model = new IntervalPlotModel(range, sampler, view);
		IntervalPath path = new IntervalPath(gp, view, model);
		model.setPath(path);
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
