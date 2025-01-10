package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.plot.CurvePlotterUtils;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.LinkSegments;
import org.geogebra.common.kernel.matrix.CoordSys;

public class BernsteinPlotter extends CoordSystemAnimatedPlotter {
	private final GeneralPathClippedForCurvePlotter gp;
	private final CoordSys transformedCoordSys;

	private VisualDebug visualDebug;
	private final PlotterAlgo algo;
	private final List<MyPoint> points = new ArrayList<>();
	private final BernsteinPlotterSettings settings = new BernsteinPlotterDefaultSettings();

	/**
	 * @param geo to draw
	 * @param bounds {@link EuclidianViewBounds}
	 * @param gp {@link GeneralPathClippedForCurvePlotter}
	 * @param transformedCoordSys {@link CoordSys}
	 */
	public BernsteinPlotter(GeoElement geo, EuclidianViewBounds bounds,
			GeneralPathClippedForCurvePlotter gp, CoordSys transformedCoordSys) {
		this.gp = gp;
		this.transformedCoordSys = transformedCoordSys;
		List<BernsteinPlotCell> cells = new ArrayList<>();
		LinkSegments segments = new LinkSegments(points);
		algo = new BernsteinImplicitAlgo(bounds, geo, cells, segments, settings.getAlgoSettings());
		if (settings.visualDebug()) {
			visualDebug = new BernsteinPlotterVisualDebug(bounds, cells);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		updateOnDemand();
		CurvePlotterUtils.draw(gp, points, transformedCoordSys);
		if (settings.visualDebug()) {
			visualDebug.draw(g2);
		}
	}

	@Override
	public void update() {
		points.clear();
		algo.compute();
	}

	@Override
	protected void enableUpdate() {
		if (settings.isUpdateEnabled()) {
			super.enableUpdate();
		}
	}
}
