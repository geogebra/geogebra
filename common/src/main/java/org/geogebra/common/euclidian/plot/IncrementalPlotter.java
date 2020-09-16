package org.geogebra.common.euclidian.plot;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawParametricCurve;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.util.debug.Log;

public class IncrementalPlotter {

	private CurvePlotter plotter;
	private DrawParametricCurve d;
	private final EuclidianView view;
	private final PathPlotter gp;
	private final boolean labelVisible;
	private final boolean fillCurve;
	private boolean started;

	public IncrementalPlotter(DrawParametricCurve d, EuclidianView view, PathPlotter gp, boolean labelVisible,
			boolean fillCurve) {
		this.d = d;
		this.view = view;
		this.gp = gp;
		this.labelVisible = labelVisible;
		this.fillCurve = fillCurve;
	}

	public void start(double min, double max, CurveEvaluable toPlot) {
		Log.debug("START PLOT CURVE");

		started = true;
		plotter = new CurvePlotter(toPlot, min, max, view, gp,
				labelVisible, fillCurve ? Gap.CORNER
				: Gap.MOVE_TO);
		view.setPlottersEnabled(true);
		nextPlot();
	}

	public void nextPlot() {
		if (!started) {
			return;
		}

		plotter.plot();
		drawOutput();

		if (plotter.isReady()) {
			onReady();
		}
		updateCurve();
	}

	public void run() {
		while (!plotter.isReady()) {
			nextPlot();
		}
	}

	protected void updateCurve() {
		view.updateCurve(this);
	}

	protected void onReady() {
		started = false;
		if( d != null) {
			d.onPlotterFinished(plotter.getLabelPoint());
		}
	}

	protected void drawOutput() {
		if (d != null) {
			d.draw(view.getGraphicsForPen());
		}
	}

	public GPoint getLabelPoint() {
		return plotter.getLabelPoint();
	}

	public boolean isReady() {
		return plotter.isReady();
	}

	public boolean isStarted() {
		return started;
	}

	public void reset() {
		started = false;
	}
}
