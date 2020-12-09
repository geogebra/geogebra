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

	/**
	 * Creates a disabled plotter
	 */
	public IntervalPlotter(EuclidianView view, GeneralPathClipped gp) {
		this.view = view;
		this.gp = new IntervalPathPlotterImpl(gp);
		this.enabled = false;
	}

	/**
	 * Enables plotter
	 */
	public void enableFor(GeoFunction function) {
		enabled = true;
		createModel(function);
		createController();
		model.updateAll();
	}

	private void createController() {
		IntervalPlotController controller = new IntervalPlotController(model);
		controller.attachEuclidianController(view.getEuclidianController());
	}

	private void createModel(GeoFunction function) {
		IntervalTuple range = new IntervalTuple();
		int numberOfSamples = view.getWidth();
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, numberOfSamples);
		model = new IntervalPlotModel(range, sampler, view);
		IntervalPath path = new IntervalPath(gp, view, model);
		model.setPath(path);
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
	}

	/**
	 * @return point of label
	 */
	public GPoint getLabelPoint() {
		return model.getLabelPoint();
	}
}
