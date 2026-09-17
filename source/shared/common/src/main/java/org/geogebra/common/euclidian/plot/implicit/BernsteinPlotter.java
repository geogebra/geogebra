/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.plot.CurvePlotterUtils;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial2D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.CompleteContourLinker;
import org.geogebra.common.kernel.implicit.ContourAssembler;
import org.geogebra.common.kernel.implicit.ContourClipper;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.jspecify.annotations.NonNull;

/**
 * Renders an implicit curve by subdivision into Bernstein cells
 * and linking their contours in a coordinate system.
 *
 * <p>This plotter runs the marching-cell algorithm to build
 * a contour using {@link ContourAssembler}, then draws the resulting polyline
 * on the provided {@link GeneralPathClippedForCurvePlotter}
 * and, optionally, renders debug overlays.</p>
 */
public class BernsteinPlotter extends CoordSystemAnimatedPlotter {
	final GeoElement geo;
	private final GeneralPathClippedForCurvePlotter gp;
	private final CoordSys transformedCoordSys;
	private final ContourAssembler assembler;
	private final EuclidianViewBounds bounds;
	protected final ContourClipper clipper;

	VisualDebug visualDebug;
	private final PlotterAlgo algo;
	final BernsteinPlotterSettings settings = new BernsteinPlotterSettings();
	private final double[] labelPosition = new double[2];

	private final CurveSignature curveSignature;

	/**
	 * Constructs a plotter for the given GeoElement and coordinate system.
	 *
	 * <p>The plotter initializes its marching-cell algorithm with
	 * {@link CompleteContourLinker} and, if enabled in
	 * {@link BernsteinPlotterSettings}, sets up a visual debug overlay.</p>
	 * @param geo the {@link GeoElement} defining the implicit curve
	 * @param bounds the {@link EuclidianViewBounds} for screen mapping
	 * @param gp the {@link GeneralPathClippedForCurvePlotter} path to draw onto
	 * @param transformedCoordSys the {@link CoordSys} used for coordinate transforms
	 * @param curveSignature {@link CurveSignature} to detect if the curve has changed
	 */
	public BernsteinPlotter(
			@NonNull GeoElement geo,
			@NonNull EuclidianViewBounds bounds,
			GeneralPathClippedForCurvePlotter gp,
			@NonNull CoordSys transformedCoordSys,
			CurveSignature curveSignature) {
		this.geo = geo;
		this.gp = gp;
		this.transformedCoordSys = transformedCoordSys;
		assembler = new ContourAssembler(new CompleteContourLinker());
		List<BernsteinPlotCell> cells = new ArrayList<>();
		this.bounds = bounds;
		algo = new BernsteinImplicitAlgo(
				this.bounds, geo, cells, assembler, settings.minCellSizeInPixels());

		if (settings.hasVisualDebug()) {
			visualDebug = new BernsteinPlotterVisualDebug(bounds, cells);
		}
		clipper = new PerimeterContourClipper(getAssembler());
		this.curveSignature = curveSignature;
	}

	@Override
	public void draw(GGraphics2D g2) {
		update();
		drawContour(gp);
		drawVisualDebugIfEnabled(g2);
	}

	protected void drawVisualDebugIfEnabled(GGraphics2D g2) {
		if (settings.hasVisualDebug()) {
			visualDebug.setEdgePoints(clipper.getEdgePoints());
			visualDebug.draw(g2);
		}
	}

	protected List<EdgeHit> getEdgeHits() {
		return clipper.getHits();
	}

	protected void drawContour(GeneralPathClippedForCurvePlotter gp1) {
		gp1.reset();
		double[] coords = gp1.newDoubleArray();
		for (ClippedFragment fragment : getClippedFragments()) {
			List<MyPoint> points = buildDrawablePoints(fragment);
			if (points.isEmpty()) {
				continue;
			}
			CurvePlotterUtils.draw(gp1, points, transformedCoordSys);
			MyPoint lastPoint = fragment.points().get(fragment.points().size() - 1);
			if (gp1.copyCoords(lastPoint, coords, transformedCoordSys)) {
				labelPosition[0] = bounds.toScreenCoordXd(coords[0]);
				labelPosition[1] = bounds.toScreenCoordYd(coords[1]);
			}
		}
	}

	@Override
	public void doUpdate() {
		runAlgo();
		clip();
		buildContour();
		updateSignature();
		reset();
	}

	@Override
	public boolean isUpdateEnabled() {
		return super.isUpdateEnabled() || hasCurveChanged();
	}

	protected void updateSignature() {
		curveSignature.update();
	}

	protected void clip() {
		clipper.clip(getBounds());
	}

	protected void clip(EuclidianViewBounds bounds) {
		clipper.clip(bounds);
	}

	protected void reset() {
		clipper.clearEdgePoints();
	}

	/**
	 * Runs the {@link BernsteinImplicitAlgo} marching algorithm
	 */
	protected void runAlgo() {
		algo.compute();
		clipper.setPolynomial(getPolynomial());
	}

	protected BernsteinPolynomial2D getPolynomial() {
		return ((BernsteinImplicitAlgo) algo).polynomial;
	}

	/**
	 * Appends all contour segments to the path.
	 */
	protected void buildContour() {
		drawContour(gp);
		assembler.flush();
	}

	static List<MyPoint> buildDrawablePoints(ClippedFragment fragment) {
		List<MyPoint> points = new ArrayList<>(fragment.points());
		if (fragment.closed() && !points.isEmpty()) {
			MyPoint first = points.get(0);
			MyPoint closingPoint = new MyPoint(first.x, first.y, SegmentType.LINE_TO);
			closingPoint.setLineTo(true);
			points.add(closingPoint);
		}
		return points;
	}

	@Override
	protected void enableUpdate() {
		if (settings.isUpdateEnabled()) {
			super.enableUpdate();
		}
	}

	/**
	 *
	 * @return the assembler.
	 */
	protected ContourAssembler getAssembler() {
		return assembler;
	}

	/**
	 *
	 * @return the bounds that plotter draws within.
	 */
	public EuclidianViewBounds getBounds() {
		return bounds;
	}

	public double[] getLabelPosition() {
		return labelPosition;
	}

	protected boolean isClipped() {
		return clipper.isClipped();
	}

	protected List<ClippedFragment> getClippedFragments() {
		return clipper.getFragments();
	}

	protected ClippedFragmentsResult getClippedFragmentsResult() {
		return clipper.getClippedFragmentsResult();
	}

	/**
	 * @return whether the implicit border coefficients changed since the last successful update
	 */
	public boolean hasCurveChanged() {
		return curveSignature.isOutdated();
	}

	@Override
	public void onAxisZoomStop() {
		// Not needed.
	}
}
