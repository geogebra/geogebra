package org.geogebra.common.euclidian.plot;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawParametricCurve;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.PathPlotter;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.util.debug.Log;

public class IncrementalPlotter {

	private CurvePlotter plotter;
	private DrawParametricCurve d;
	private final EuclidianView view;
	private final PathPlotter gp;
	private final boolean labelVisible;
	private final boolean fillCurve;

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
		if (plotter != null) {
			return;
		}

		plotter = new CurvePlotter(toPlot, min, max, view, gp,
				labelVisible, fillCurve ? Gap.CORNER
				: Gap.MOVE_TO);

	}

	public boolean plotCurve() {
		if (plotter == null) {
			return false;
		}

		if (plotter.isReady()) {
			d.onPlotterFinished(plotter.getLabelPoint());
			return false;
		}

		plotter.plot();
		view.updateCurve(d);
		d.draw(view.getGraphicsForPen());

		return true;
	}
}
