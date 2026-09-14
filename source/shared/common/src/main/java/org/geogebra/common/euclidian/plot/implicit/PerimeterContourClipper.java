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

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial2D;
import org.geogebra.common.kernel.implicit.ContourAssembler;
import org.geogebra.common.kernel.implicit.ContourClipper;
import org.geogebra.common.util.debug.Log;

/**
 * Clips assembled contours against the rectangular viewport and exposes
 * explicit post-clip fragments.
 * <p>
 * Uses {@link PerimeterHitAlgo} to collect edge hits
 * and {@link ClippedFragmentsBuilder} to derive visible contour
 * fragments without mutating the source {@link ContourAssembler}.
 * </p>
 */
public class PerimeterContourClipper implements ContourClipper {
	private static final boolean CLIP_RECT_DEBUG = false;
	private final ContourAssembler assembler;
	private PerimeterHitAlgo algo;
	private EuclidianViewBounds bounds;
	private BernsteinPolynomial2D polynomial;
	private List<EdgeHit> hits;
	private ClippedFragmentsResult fragmentsResult =
			new ClippedFragmentsResult(List.of(), List.of());

	/**
	 * Creates a clipper that reads raw pre-clip contours from the given assembler.
	 *
	 * @param assembler receiver of stitched perimeter segments
	 */
	public PerimeterContourClipper(ContourAssembler assembler) {
		this.assembler = assembler;
	}

	/**
	 * Prepares clipping for the given bounds, computes edge hits, classifies them,
	 * and builds explicit clipped fragments for downstream consumers.
	 *
	 * @param bounds current view bounds in world coordinates
	 */
	@Override
	public void clip(EuclidianViewBounds bounds) {
		long totalStart = ImplicitPlotTimings.start();
		ContourLog.bounds(bounds);
		this.bounds = bounds;
		ClipRect clipRect = new ClipRect(bounds, BernsteinPlotterSettings.MARGIN_IN_PX);
		algo = new PerimeterHitAlgo(clipRect, ClipEpsilon.fromBounds(bounds));
		clipContours(clipRect);
		ImplicitPlotTimings.log("PerimeterContourClipper.clip", totalStart,
				"contours=" + assembler.getContours().size()
						+ " hits=" + hits.size()
						+ " fragments=" + fragmentsResult.fragments().size());
	}

	private void clipContours(ClipRect clipRect) {
		long stageStart = ImplicitPlotTimings.start();
		hits = algo.collectHits(assembler.getContours());
		ImplicitPlotTimings.log("PerimeterContourClipper.collectHits", stageStart,
				"hits=" + hits.size());
		ContourLog.hits(hits);
		stageStart = ImplicitPlotTimings.start();
		ClippedFragmentsResult result =
				ClippedFragmentsBuilder.build(assembler.getContours(), hits, clipRect);
		fragmentsResult = new ClippedFragmentsResult(result, clipRect);
		ImplicitPlotTimings.log("PerimeterContourClipper.buildFragments", stageStart,
				"fragments=" + fragmentsResult.fragments().size());
		stageStart = ImplicitPlotTimings.start();
		validateHitsAndFragments(clipRect, hits, fragmentsResult);
		ImplicitPlotTimings.log("PerimeterContourClipper.validate", stageStart);
		ContourLog.fragments(fragmentsResult.fragments());
	}

	private void validateHitsAndFragments(ClipRect clipRect, List<EdgeHit> hits,
			ClippedFragmentsResult result) {
		if (!CLIP_RECT_DEBUG) {
			return;
		}
		double eps = 1e-8;
		for (int i = 0; i < hits.size(); i++) {
			EdgeHit hit = hits.get(i);
			if (!isPointOnExpectedEdge(hit.point(), hit.edge(), clipRect, eps)) {
				Log.debug("[PerimeterContourClipper] hit/edge mismatch"
						+ " clipRect=" + formatRect(clipRect)
						+ " hitIndex=" + i
						+ " edge=" + hit.edge()
						+ " point=" + formatPoint(hit.point()));
				return;
			}
		}
		List<ClippedFragment> fragments = result.fragments();
		for (int fragmentIndex = 0; fragmentIndex < fragments.size(); fragmentIndex++) {
			ClippedFragment fragment = fragments.get(fragmentIndex);
			if (logEndpointMismatch(clipRect, fragmentIndex, "start", fragment.start(), eps)) {
				return;
			}
			if (logEndpointMismatch(clipRect, fragmentIndex, "end", fragment.end(), eps)) {
				return;
			}
		}
	}

	private boolean logEndpointMismatch(ClipRect clipRect, int fragmentIndex, String label,
			FragmentEndpoint endpoint, double eps) {
		if (endpoint == null || endpoint.getEdge() == null) {
			return false;
		}
		if (isPointOnExpectedEdge(endpoint.getPoint(), endpoint.getEdge(), clipRect, eps)) {
			return false;
		}
		Log.debug("[PerimeterContourClipper] fragment endpoint/edge mismatch"
				+ " clipRect=" + formatRect(clipRect)
				+ " fragmentIndex=" + fragmentIndex
				+ " label=" + label
				+ " endpoint=" + endpoint);
		return true;
	}

	private boolean isPointOnExpectedEdge(MyPoint point, ClipEdge edge, ClipRect clipRect,
			double eps) {
		if (point == null || edge == null) {
			return true;
		}
		switch (edge) {
		case TOP:
			return Math.abs(point.y - clipRect.getYmax()) <= eps;
		case RIGHT:
			return Math.abs(point.x - clipRect.getXmax()) <= eps;
		case BOTTOM:
			return Math.abs(point.y - clipRect.getYmin()) <= eps;
		case LEFT:
			return Math.abs(point.x - clipRect.getXmin()) <= eps;
		default:
			return true;
		}
	}

	private String formatRect(ClipRect clipRect) {
		return "[" + clipRect.getXmin() + "," + clipRect.getXmax()
				+ "]x[" + clipRect.getYmin() + "," + clipRect.getYmax() + "]";
	}

	private String formatPoint(MyPoint point) {
		return point == null ? "null" : "(" + point.x + "," + point.y + ")";
	}

	/**
	 * Inside/outside test used while stitching perimeter runs.
	 * <p>
	 * Normalizes {@code (x0,y0)} into {@code [0,1]^2} and evaluates the current
	 * Bernstein polynomial to decide membership.
	 * </p>
	 *
	 * @param x0 world x
	 * @param y0 world y
	 * @return {@code true} if the point is considered inside; {@code false} otherwise
	 */
	public boolean isInsideTest(double x0, double y0) {
		return isInsideTest(bounds, polynomial, x0, y0);
	}

	/**
	 * Evaluates whether a given real-world point lies on or inside the region defined
	 * by a 2D Bernstein polynomial inequality (i.e. {@code f(x, y) >= 0}) mapped to
	 * the unit square [0, 1] x [0, 1].
	 * <p>
	 * This method is used to test point containment in implicitly defined regions,
	 * such as those resulting from inequalities like {@code f(x, y) >= 0}, where
	 * {@code f} is represented as a Bernstein polynomial.
	 * <p>
	 *
	 * @param bounds the bounding box defining the domain in real-world coordinates
	 * @param polynomial the Bernstein polynomial defining the implicit curve or region
	 * @param x0 the x-coordinate of the point in real-world space
	 * @param y0 the y-coordinate of the point in real-world space
	 * @return {@code true} if the evaluated Bernstein polynomial at the normalized
	 *         location is non-negative (inside or on the boundary); {@code false} otherwise
	 */
	public static boolean isInsideTest(EuclidianViewBounds bounds, BernsteinPolynomial2D polynomial,
			double x0, double y0) {
		double dx = bounds.getXmax() - bounds.getXmin();
		double dy = bounds.getYmax() - bounds.getYmin();
		if (dx == 0 || dy == 0) {
			return false;
		}

		double x = (x0 - bounds.getXmin()) / dx;
		double y = (y0 - bounds.getYmin()) / dy;

		BernsteinPolynomial2D polynomialCopy = new BernsteinPolynomial2D(polynomial);

		return polynomialCopy.evaluate(x, y) >= 0;
	}

	/**
	 * Reports whether the last run produced any hits.
	 *
	 * @return {@code true} if hits were found; {@code false} otherwise
	 */
	@Override
	public boolean isClipped() {
		return hits != null && !hits.isEmpty();
	}

	/**
	 * No-op for this implementation; edge points are derived from {@link #hits}.
	 */
	@Override
	public void clearEdgePoints() {
		// remove me
	}

	/**
	 * Returns the boundary points from the most recent clipping pass.
	 *
	 * @return list of hit points in edge order
	 */
	@Override
	public List<MyPoint> getEdgePoints() {
		return hits == null ? List.of()
				: hits.stream().map(EdgeHit::point).collect(Collectors.toList());
	}

	/**
	 * Supplies the polynomial used for inside/outside tests during stitching.
	 *
	 * @param polynomial Bernstein polynomial defined over the current bounds
	 */
	@Override
	public void setPolynomial(BernsteinPolynomial2D polynomial) {
		this.polynomial = polynomial;
	}

	@Override
	public List<EdgeHit> getHits() {
		return hits;
	}

	@Override
	public List<ClippedFragment> getFragments() {
		return fragmentsResult.fragments();
	}

	@Override
	public ClippedFragmentsResult getClippedFragmentsResult() {
		return fragmentsResult;
	}
}
