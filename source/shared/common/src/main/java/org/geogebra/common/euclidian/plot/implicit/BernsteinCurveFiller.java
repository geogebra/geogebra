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

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.CoordSystemInfo;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.euclidian.plot.implicit.classification.ClassifiedRegion;
import org.geogebra.common.euclidian.plot.implicit.classification.RegionClassifier;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryCycle;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Face;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.HalfEdge;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Vertex;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.util.debug.Log;

/**
 * Renders and fills an implicit curve defined by an inequality using Bernstein subdivision.
 *
 * <p>The plotter computes the curve contour, applies clipping to the visible area,
 * then constructs a {@link GArea} representing the filled region (or its complement),
 * suspending update during pan/zoom to make it faster</p>
 */
public class BernsteinCurveFiller extends BernsteinPlotter {
	public static final int INSIDE_SAMPLE_SIZE = 9;
	private static final ClippedFragmentsResult EMPTY_FRAGMENTS_RESULT =
			new ClippedFragmentsResult(List.of(), List.of());
	private static final boolean REGION_CLASSIFICATION_DEBUG = false;
	private final Inequality ineq;

	private final GeneralPathClippedForCurvePlotter gp;
	private final InequalityArea ineqArea;
	private final ViewportPanZoomDelta borderPanZoom;
	private final RegionClassifier regionClassifier;
	private EuclidianViewBounds classificationBoundsSnapshot;
	private ClippedFragmentsResult classificationFragmentsResult = EMPTY_FRAGMENTS_RESULT;
	private GShape clippedBorderShape;

	private static GeoImplicitCurve requireImplicitCurveBorder(Inequality ineq) {
		GeoImplicitCurve border = ineq.getImplicitCurveBorder();
		if (border == null) {
			throw new IllegalArgumentException("Implicit inequality has no implicit curve border");
		}
		return border;
	}

	/**
	 * Constructs a fill-capable plotter for the given implicit-curve inequality.
	 *
	 * @param ineq the {@link Inequality} defining the implicit curve boundary
	 * @param view the {@link EuclidianView} in which to render and fill
	 * @param gp the {@link GeneralPathClippedForCurvePlotter} used for contour drawing
	 */
	public BernsteinCurveFiller(Inequality ineq, EuclidianView view,
			GeneralPathClippedForCurvePlotter gp) {
		this(ineq, requireImplicitCurveBorder(ineq), view, gp);
	}

	private BernsteinCurveFiller(Inequality ineq, GeoImplicitCurve border, EuclidianView view,
			GeneralPathClippedForCurvePlotter gp) {
		super(border, new EuclidianViewBoundsImp(view), gp, border
						.getTransformedCoordSys(), createCurveSignature(ineq));
		this.ineq = ineq;
		this.gp = gp;
		EuclidianViewBounds bounds = getBounds();
		classificationBoundsSnapshot = snapshot(bounds);
		regionClassifier = new RegionClassifier(() -> classificationFragmentsResult,
				() -> getViewBounds(classificationBoundsSnapshot));
		if (settings.hasVisualDebug()) {
			visualDebug = new CompositeVisualDebug(visualDebug,
					new PlanarGraphVisualDebug(() -> classificationBoundsSnapshot,
							regionClassifier.getGraph()));
		}

		ineqArea = new InequalityArea(bounds);
		borderPanZoom = new ViewportPanZoomDelta(bounds);
		enableUpdate();
	}

	private static CurveSignature createCurveSignature(Inequality ineq) {
		return new CurveSignature(ineq);
	}

	private static GRectangle2D getViewBounds(EuclidianViewBounds bounds) {
		GRectangle2D rect = AwtFactory.getPrototype().newRectangle2D();
		rect.setRect(bounds.getXmin(), bounds.getYmin(),
				bounds.getXmax() - bounds.getXmin(),
				bounds.getYmax() - bounds.getYmin());
		return rect;
	}

	private static EuclidianViewBounds snapshot(EuclidianViewBounds bounds) {
		return new SnapshotViewBounds(bounds);
	}

	@Override
	public void draw(GGraphics2D g2) {
		GArea area = getArea();
		if (area != null) {
			g2.draw(area);
		}
		drawVisualDebugIfEnabled(g2);

	}

	/**
	 * Draws the border (contour) of the implicit inequality.
	 *
	 * @param g2            graphics context to draw on
	 * @param color         main color of the contour
	 * @param isHighlighted whether the border is currently highlighted
	 * @param selStroke     stroke to use when highlighted
	 */
	public void drawBorder(GGraphics2D g2, GColor color, boolean isHighlighted,
			GBasicStroke selStroke) {
		if (clippedBorderShape == null) {
			return;
		}
		if (isHighlighted) {
			drawHighlightedBorder(g2, selStroke);
		}

		g2.setPaint(color);
		drawStyledBorder(g2);
	}

	private void drawHighlightedBorder(GGraphics2D g2, GBasicStroke selStroke) {
		g2.setPaint(geo.getSelColor());
		g2.setStroke(selStroke);
		g2.draw(clippedBorderShape);
	}

	private void drawStyledBorder(GGraphics2D g2) {
		GeoImplicitCurve border = ineq.getImplicitCurveBorder();
		if (border == null) {
			return;
		}

		int thickness = border.getLineThickness();
		if (thickness > 0) {
			g2.setStroke(EuclidianStatic.getStroke(thickness / 2.0f,
					border.lineType));
			g2.draw(clippedBorderShape);
		}
	}

	@Override
	public void doUpdate() {
		final long totalStart = ImplicitPlotTimings.start();
		EuclidianViewBounds boundsSnapshot = snapshot(getBounds());
		long stageStart = ImplicitPlotTimings.start();
		runAlgo();
		ImplicitPlotTimings.log("BernsteinCurveFiller.runAlgo", stageStart);
		stageStart = ImplicitPlotTimings.start();
		clip(boundsSnapshot);
		ImplicitPlotTimings.log("BernsteinCurveFiller.clip", stageStart);
		classificationBoundsSnapshot = boundsSnapshot;
		classificationFragmentsResult = getClippedFragmentsResult();
		stageStart = ImplicitPlotTimings.start();
		buildContour();
		clippedBorderShape = gp.getGeneralPath();
		borderPanZoom.snapshot();
		ImplicitPlotTimings.log("BernsteinCurveFiller.buildContour", stageStart);
		stageStart = ImplicitPlotTimings.start();
		boolean classified = classifyRegions(boundsSnapshot);
		Log.debug("[BernsteinCurveFiller] classifyRegions success: " + classified);
		updateSignature();
		ImplicitPlotTimings.log("BernsteinCurveFiller.classifyRegions", stageStart);
		reset();
		ImplicitPlotTimings.log("BernsteinCurveFiller.doUpdate", totalStart);
	}

	private boolean classifyRegions(EuclidianViewBounds boundsSnapshot) {
		regionClassifier.reset();
		long processStart = ImplicitPlotTimings.start();
		if (regionClassifier.process()) {
			ImplicitPlotTimings.log("RegionClassifier.process", processStart);
			try {
				long stageStart = ImplicitPlotTimings.start();
				List<ClassifiedRegion> results = regionClassifier.getResults(boundsSnapshot);
				ImplicitPlotTimings.log("RegionClassifier.getResults", stageStart,
						"regions=" + results.size());
				stageStart = ImplicitPlotTimings.start();
				evaluateFilled(results);
				ImplicitPlotTimings.log("BernsteinCurveFiller.evaluateFilled", stageStart,
						"regions=" + results.size());
				logRegionClassification(results, boundsSnapshot);
				logFalseRegionContainment(results, boundsSnapshot);
				stageStart = ImplicitPlotTimings.start();
				ineqArea.update(results, boundsSnapshot);
				ImplicitPlotTimings.log("InequalityArea.update", stageStart,
						"regions=" + results.size());
				return true;
			} catch (Exception e) {
				Log.debug("[BernsteinCurveFiller]" + e.getMessage());
				ineqArea.update(List.of());
				return false;
			}
		} else {
			ImplicitPlotTimings.log("RegionClassifier.process", processStart, "failed");
			ineqArea.update(List.of());
			return false;
		}
	}

	private void evaluateFilled(List<ClassifiedRegion> regions) {
		for (ClassifiedRegion region: regions) {
			GPoint2D p = region.getSamplePoint();
			region.setFilled(ineq.valueAround(p.x, p.y).boolVal());
		}
		repairSuspiciousExteriorClassification(regions);
	}

	private void logRegionClassification(List<ClassifiedRegion> regions,
			EuclidianViewBounds boundsSnapshot) {
		if (!REGION_CLASSIFICATION_DEBUG) {
			return;
		}
		StringBuilder sb = new StringBuilder("[BernsteinCurveFiller] regions=");
		sb.append(regions.size())
				.append(" view=[")
				.append(boundsSnapshot.getXmin()).append(",")
				.append(boundsSnapshot.getXmax()).append("]x[")
				.append(boundsSnapshot.getYmin()).append(",")
				.append(boundsSnapshot.getYmax()).append("] ");
		for (int i = 0; i < regions.size(); i++) {
			ClassifiedRegion region = regions.get(i);
			GPoint2D sample = region.getSamplePoint();
			Boolean predicate = null;
			if (sample != null) {
				predicate = ineq.valueAround(sample.x, sample.y).boolVal();
			}
			sb.append(i)
					.append(":{viewport=")
					.append(isViewportRegion(region))
					.append(", filled=")
					.append(region.isFilled())
					.append(", predicate=")
					.append(predicate)
					.append(", holes=")
					.append(region.getHoles().size())
					.append(", face=")
					.append(region.getSourceFaceId())
					.append(", outer=")
					.append(region.getOuterBoundary() != null)
					.append(", sample=")
					.append(sample)
					.append("}");
			if (i + 1 < regions.size()) {
				sb.append("; ");
			}
		}
		Log.debug(sb.toString());
		logMissingBoundedRegion(regions, boundsSnapshot);
	}

	private void logFalseRegionContainment(List<ClassifiedRegion> regions,
			EuclidianViewBounds boundsSnapshot) {
		if (!REGION_CLASSIFICATION_DEBUG) {
			return;
		}
		GArea filledUnion = AwtFactory.getPrototype().newArea();
		List<GArea> regionAreas = new java.util.ArrayList<>();
		for (ClassifiedRegion region : regions) {
			GArea regionArea = areaOf(region);
			regionAreas.add(regionArea);
			if (region.isFilled() && regionArea != null) {
				filledUnion.add(regionArea);
			}
		}
		for (int i = 0; i < regions.size(); i++) {
			ClassifiedRegion region = regions.get(i);
			GPoint2D sample = region.getSamplePoint();
			if (region.isFilled() || sample == null) {
				continue;
			}
			double screenX = boundsSnapshot.toScreenCoordXd(sample.x);
			double screenY = boundsSnapshot.toScreenCoordYd(sample.y);
			boolean finalContains = filledUnion.contains(screenX, screenY);
			String containingFilledRegions = containingFilledRegions(regions, regionAreas,
					screenX, screenY, region);
			if (finalContains || !containingFilledRegions.isEmpty()) {
				Log.debug("[BernsteinCurveFiller] false region overlap"
						+ " region=" + i
						+ " face=" + region.getSourceFaceId()
						+ " sample=" + sample
						+ " screen=(" + screenX + "," + screenY + ")"
						+ " finalContains=" + finalContains
						+ " filledOwners=[" + containingFilledRegions + "]"
						+ " topology=" + topologySummary(region)
						+ " cycles=" + detailedCycleSummary(region));
			}
		}
	}

	private GArea areaOf(ClassifiedRegion region) {
		if (region.getOuterBoundary() == null) {
			return null;
		}
		GArea area = AwtFactory.getPrototype().newArea(region.getOuterBoundary());
		for (GShape hole : region.getHoles()) {
			area.subtract(AwtFactory.getPrototype().newArea(hole));
		}
		return area;
	}

	private String containingFilledRegions(List<ClassifiedRegion> regions, List<GArea> regionAreas,
			double screenX, double screenY, ClassifiedRegion falseRegion) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < regions.size(); i++) {
			ClassifiedRegion region = regions.get(i);
			GArea area = regionAreas.get(i);
			if (!region.isFilled() || area == null || !area.contains(screenX, screenY)) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(';');
			}
			sb.append(i)
					.append(":face=").append(region.getSourceFaceId())
					.append(":").append(topologySummary(region))
					.append(":rel=").append(cycleRelationship(falseRegion, region));
		}
		return sb.toString();
	}

	private String topologySummary(ClassifiedRegion region) {
		PlanarGraph graph = regionClassifier.getGraph();
		Face face = findFace(graph, region.getSourceFaceId());
		if (face == null) {
			return "face=n/a";
		}
		return "outerEdge=" + face.getOuterHalfEdgeId()
				+ ":outerCycle=" + cycleIdForBoundaryStart(face.getOuterHalfEdgeId())
				+ ":holeEdges=" + face.getHoleHalfEdgeIds()
				+ ":holeCycles=" + cycleIdsForBoundaryStarts(face.getHoleHalfEdgeIds());
	}

	private String detailedCycleSummary(ClassifiedRegion region) {
		Face face = findFace(regionClassifier.getGraph(), region.getSourceFaceId());
		if (face == null) {
			return "face=n/a";
		}
		BoundaryCycle outerCycle = cycleForBoundaryStart(face.getOuterHalfEdgeId());
		StringBuilder sb = new StringBuilder();
		sb.append("outer=").append(cycleDetails(outerCycle));
		sb.append(":holes=[");
		List<Integer> holeHalfEdgeIds = face.getHoleHalfEdgeIds();
		for (int i = 0; i < holeHalfEdgeIds.size(); i++) {
			sb.append(cycleDetails(cycleForBoundaryStart(holeHalfEdgeIds.get(i))));
			if (i + 1 < holeHalfEdgeIds.size()) {
				sb.append(';');
			}
		}
		return sb.append(']').toString();
	}

	private String cycleRelationship(ClassifiedRegion falseRegion, ClassifiedRegion filledRegion) {
		BoundaryCycle falseCycle = outerCycle(falseRegion);
		BoundaryCycle filledCycle = outerCycle(filledRegion);
		if (falseCycle == null || filledCycle == null) {
			return "cycle=n/a";
		}
		return "falseCycle=" + falseCycle.getId()
				+ ":filledCycle=" + filledCycle.getId()
				+ ":falseParent=" + falseCycle.getParentId()
				+ ":filledParent=" + filledCycle.getParentId()
				+ ":filledChildren=" + filledCycle.getChildIds()
				+ ":falseProbeInFilled="
				+ containmentOf(falseCycle.getContainmentProbePoint(), filledCycle)
				+ ":filledProbeInFalse="
				+ containmentOf(filledCycle.getContainmentProbePoint(), falseCycle);
	}

	private BoundaryCycle outerCycle(ClassifiedRegion region) {
		Face face = findFace(regionClassifier.getGraph(), region.getSourceFaceId());
		return face == null ? null : cycleForBoundaryStart(face.getOuterHalfEdgeId());
	}

	private String cycleDetails(BoundaryCycle cycle) {
		if (cycle == null) {
			return "n/a";
		}
		return cycle.getId()
				+ "{area=" + cycle.getSignedArea()
				+ ",abs=" + cycle.getAbsArea()
				+ ",edges=" + cycle.getHalfEdgeIds().size()
				+ ",parent=" + cycle.getParentId()
				+ ",depth=" + cycle.getDepth()
				+ ",children=" + cycle.getChildIds()
				+ ",start=" + cycle.getStartHalfEdgeId()
				+ ",bbox=" + boundaryBox(cycle)
				+ ",probe=" + cycle.getContainmentProbePoint()
				+ "}";
	}

	private Face findFace(PlanarGraph graph, int faceId) {
		for (Face face : graph.getFaces()) {
			if (face.getId() == faceId) {
				return face;
			}
		}
		return null;
	}

	private int cycleIdForBoundaryStart(int halfEdgeId) {
		BoundaryCycle cycle = cycleForBoundaryStart(halfEdgeId);
		return cycle == null ? -1 : cycle.getId();
	}

	private BoundaryCycle cycleForBoundaryStart(int halfEdgeId) {
		if (halfEdgeId == -1) {
			return null;
		}
		for (BoundaryCycle cycle : regionClassifier.getGraph().getLastCanonicalBoundaryCycles()) {
			if (cycle.getStartHalfEdgeId() == halfEdgeId
					|| cycle.getHalfEdgeIds().contains(halfEdgeId)) {
				return cycle;
			}
		}
		return null;
	}

	private String cycleIdsForBoundaryStarts(List<Integer> halfEdgeIds) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < halfEdgeIds.size(); i++) {
			sb.append(cycleIdForBoundaryStart(halfEdgeIds.get(i)));
			if (i + 1 < halfEdgeIds.size()) {
				sb.append(',');
			}
		}
		return sb.append(']').toString();
	}

	private String containmentOf(GPoint2D point, BoundaryCycle cycle) {
		if (point == null || cycle == null) {
			return "n/a";
		}
		boolean inside = false;
		List<Integer> halfEdgeIds = cycle.getHalfEdgeIds();
		PlanarGraph graph = regionClassifier.getGraph();
		for (int i = 0, j = halfEdgeIds.size() - 1; i < halfEdgeIds.size(); j = i++) {
			Vertex current = graph.vertex(graph.halfEdge(halfEdgeIds.get(i)).getOriginVertexId());
			Vertex previous = graph.vertex(graph.halfEdge(halfEdgeIds.get(j)).getOriginVertexId());
			if (isPointOnSegment(point, previous, current)) {
				return "BOUNDARY";
			}
			boolean intersects = ((current.getY() > point.y) != (previous.getY() > point.y))
					&& (point.x < (previous.getX() - current.getX())
							* (point.y - current.getY())
							/ (previous.getY() - current.getY()) + current.getX());
			if (intersects) {
				inside = !inside;
			}
		}
		return inside ? "INSIDE" : "OUTSIDE";
	}

	private boolean isPointOnSegment(GPoint2D point, Vertex first, Vertex second) {
		double dx = second.getX() - first.getX();
		double dy = second.getY() - first.getY();
		double cross = (point.x - first.getX()) * dy - (point.y - first.getY()) * dx;
		if (Math.abs(cross) > Kernel.MAX_PRECISION) {
			return false;
		}
		double dot = (point.x - first.getX()) * dx + (point.y - first.getY()) * dy;
		if (dot < -Kernel.MAX_PRECISION) {
			return false;
		}
		double lengthSquared = dx * dx + dy * dy;
		return dot <= lengthSquared + Kernel.MAX_PRECISION;
	}

	private String boundaryBox(BoundaryCycle cycle) {
		PlanarGraph graph = regionClassifier.getGraph();
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (int halfEdgeId : cycle.getHalfEdgeIds()) {
			HalfEdge halfEdge = graph.halfEdge(halfEdgeId);
			Vertex vertex = graph.vertex(halfEdge.getOriginVertexId());
			minX = Math.min(minX, vertex.getX());
			minY = Math.min(minY, vertex.getY());
			maxX = Math.max(maxX, vertex.getX());
			maxY = Math.max(maxY, vertex.getY());
		}
		return "[" + minX + "," + maxX + "]x[" + minY + "," + maxY + "]";
	}

	private void logMissingBoundedRegion(List<ClassifiedRegion> regions,
			EuclidianViewBounds boundsSnapshot) {
		if (!REGION_CLASSIFICATION_DEBUG
				|| regions.size() != 1
				|| !isViewportRegion(regions.get(0))) {
			return;
		}
		PlanarGraph graph = regionClassifier.getGraph();
		StringBuilder sb = new StringBuilder("[BernsteinCurveFiller] missing bounded region");
		sb.append(" view=[")
				.append(boundsSnapshot.getXmin()).append(",")
				.append(boundsSnapshot.getXmax()).append("]x[")
				.append(boundsSnapshot.getYmin()).append(",")
				.append(boundsSnapshot.getYmax()).append("] extracted=");
		appendCycleSummary(sb, graph.getLastExtractedBoundaryCycles());
		sb.append(" canonical=");
		appendCycleSummary(sb, graph.getLastCanonicalBoundaryCycles());
		Log.debug(sb.toString());
	}

	private static void appendCycleSummary(StringBuilder sb, List<BoundaryCycle> cycles) {
		sb.append('[');
		for (int i = 0; i < cycles.size(); i++) {
			BoundaryCycle cycle = cycles.get(i);
			sb.append(cycle.getId())
					.append(":area=")
					.append(cycle.getSignedArea())
					.append(":edges=")
					.append(cycle.getHalfEdgeIds().size())
					.append(":probe=")
					.append(cycle.getContainmentProbePoint());
			if (i + 1 < cycles.size()) {
				sb.append("; ");
			}
		}
		sb.append(']');
	}

	private void repairSuspiciousExteriorClassification(List<ClassifiedRegion> regions) {
		if (regions.size() != 2) {
			return;
		}
		ClassifiedRegion viewportRegion = null;
		ClassifiedRegion boundedRegion = null;
		for (ClassifiedRegion region : regions) {
			if (isViewportRegion(region)) {
				viewportRegion = region;
			} else {
				boundedRegion = region;
			}
		}
		if (viewportRegion == null || boundedRegion == null) {
			return;
		}
		GPoint2D boundedSample = boundedRegion.getSamplePoint();
		if (!viewportRegion.isFilled() || boundedRegion.isFilled()
				|| boundedSample == null
				|| getBounds().isOnView(boundedSample.x, boundedSample.y)) {
			return;
		}
		viewportRegion.setFilled(false);
		boundedRegion.setFilled(true);
	}

	private boolean isViewportRegion(ClassifiedRegion region) {
		if (region.getOuterBoundary() == null) {
			return false;
		}
		GRectangle regionBounds = region.getOuterBoundary().getBounds();
		if (regionBounds == null) {
			return false;
		}
		int width = getBounds().getWidth();
		int height = getBounds().getHeight();
		return Math.abs(regionBounds.getWidth() - width) <= 1
				&& Math.abs(regionBounds.getHeight() - height) <= 1
				&& Math.abs(regionBounds.getX()) <= 1
				&& Math.abs(regionBounds.getY()) <= 1;
	}

	/**
	 * Returns the current filled shape for rendering.
	 * @return a {@link GShape} representing the filled (or inverse-filled) region
	 */
	public GArea getArea() {
		return ineqArea.getFilledArea();
	}

	@Override
	public void onMove(CoordSystemInfo info) {
		super.onMove(info);
		ineqArea.applyTransformations();
		if (clippedBorderShape != null) {
			clippedBorderShape = borderPanZoom.applyTo(clippedBorderShape);
		}
	}

	@Override
	public void onMoveStop() {
		ineqArea.onMoveStop();
		borderPanZoom.snapshot();
		super.onMoveStop();
	}

	@Override
	public void onZoomStop(CoordSystemInfo info) {
		super.onZoomStop(info);
		ineqArea.onZoomStop();
		borderPanZoom.snapshot();
	}

	/**
	 * Determines if the plotter's fill needs recomputing.
	 *
	 * <p>Returns {@code true} when the clipping has changed, updates are enabled,
	 * or the fill has moved offscreen since the last render.</p>
	 * @return {@code true} if the filled shape must be updated; {@code false} otherwise
	 */
	public boolean needsUpdate() {
		if (hasCurveChanged()) {
			return true;
		}

		if (ineqArea.needsUpdate()) {
			return true;
		}
		GArea area = getArea();
		if (area == null) {
			return true;
		}
		GRectangle boundsOfArea = area.getBounds();
		boolean onScreen = boundsOfArea != null && getBounds().getBoundingPath()
				.intersects(boundsOfArea);
		return isClipped() || isUpdateEnabled() || !onScreen;
	}

	/**
	 * @param border candidate implicit border
	 * @return whether this filler can still be used for the candidate border
	 */
	public boolean matchesBorder(GeoImplicitCurve border) {
		return border == ineq.getImplicitCurveBorder() && !hasCurveChanged();
	}

	private static final class SnapshotViewBounds implements EuclidianViewBounds {
		private final int width;
		private final int height;
		private final double xmin;
		private final double xmax;
		private final double ymin;
		private final double ymax;
		private final double invXscale;
		private final double invYscale;
		private final double xZero;
		private final double yZero;
		private final double xScale;
		private final double yScale;

		private SnapshotViewBounds(EuclidianViewBounds bounds) {
			width = bounds.getWidth();
			height = bounds.getHeight();
			xmin = bounds.getXmin();
			xmax = bounds.getXmax();
			ymin = bounds.getYmin();
			ymax = bounds.getYmax();
			invXscale = bounds.getInvXscale();
			invYscale = bounds.getInvYscale();
			xZero = bounds.getXZero();
			yZero = bounds.getYZero();
			xScale = bounds.getXScale();
			yScale = bounds.getYScale();
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public Interval domain() {
			return new Interval(xmin, xmax);
		}

		@Override
		public Interval range() {
			return new Interval(ymin, ymax);
		}

		@Override
		public double getXmin() {
			return xmin;
		}

		@Override
		public double getXmax() {
			return xmax;
		}

		@Override
		public double getYmin() {
			return ymin;
		}

		@Override
		public double getYmax() {
			return ymax;
		}

		@Override
		public Interval toScreenIntervalX(Interval x) {
			return new Interval(toScreenCoordXd(x.getLow()), toScreenCoordXd(x.getHigh()));
		}

		@Override
		public Interval toScreenIntervalY(Interval y) {
			if (y.isWhole()) {
				return y;
			}
			if (y.isNegativeInfinity()) {
				return new Interval(toScreenCoordYd(ymin));
			}
			if (y.isPositiveInfinity()) {
				return IntervalConstants.zero();
			}
			double screenYLow = y.getHigh() == Double.POSITIVE_INFINITY ? 0
					: toScreenCoordYd(y.getHigh());
			double screenYHigh = y.getLow() == Double.NEGATIVE_INFINITY ? height
					: toScreenCoordYd(y.getLow());
			return new Interval(screenYLow, screenYHigh);
		}

		@Override
		public boolean isOnView(double x, double y) {
			return x >= xmin && x <= xmax && y >= ymin && y <= ymax;
		}

		@Override
		public double toScreenCoordXd(double x) {
			return (x - xmin) * xScale;
		}

		@Override
		public double toScreenCoordYd(double y) {
			return (ymax - y) * yScale;
		}

		@Override
		public double toRealWorldCoordX(double x) {
			return xmin + x * invXscale;
		}

		@Override
		public double toRealWorldCoordY(double y) {
			return ymax - y * invYscale;
		}

		@Override
		public boolean isOnView(Interval y) {
			return (y.getLow() >= ymin && y.getLow() <= ymax)
					|| (y.getHigh() >= ymin && y.getHigh() <= ymax);
		}

		@Override
		public double getInvXscale() {
			return invXscale;
		}

		@Override
		public double getInvYscale() {
			return invYscale;
		}

		@Override
		public double getXZero() {
			return xZero;
		}

		@Override
		public double getYZero() {
			return yZero;
		}

		@Override
		public GShape getBoundingPath() {
			return null;
		}

		@Override
		public double getXScale() {
			return xScale;
		}

		@Override
		public double getYScale() {
			return yScale;
		}
	}
}
