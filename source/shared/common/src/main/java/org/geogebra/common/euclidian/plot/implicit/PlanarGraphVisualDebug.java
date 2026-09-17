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
import java.util.function.Supplier;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryCycle;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Face;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.HalfEdge;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Vertex;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;

/**
 * Development-only overlay for drawing the current planar graph on screen.
 * <p>
 * Renders active contour and viewport half-edges, vertex markers, and vertex ids.
 * </p>
 */
final class PlanarGraphVisualDebug implements VisualDebug {
	private static final boolean SHOW_VERTEX_IDS = false;
	private static final boolean SHOW_FACE_IDS = true;
	private static final boolean SHOW_DIRECTION_TICKS = false;
	private static final GColor CONTOUR_COLOR = GColor.RED;
	private static final GColor VIEWPORT_COLOR = GColor.GREEN;
	private static final GColor VERTEX_COLOR = GColor.BLUE;
	private static final GColor EXTRACTED_CYCLE_COLOR = GColor.newColor(90, 90, 90);

	// GOLD
	private static final GColor CANONICAL_CYCLE_COLOR = GColor.newColor(255, 215, 0);

	// opaque yellow
	private static final GColor FACE_FILL_COLOR = GColor.newColor(255, 165, 0, 48);

	// intense orange
	private static final GColor FACE_BOUNDARY_COLOR = GColor.newColor(255, 140, 0);

	// medium shade of purple
	private static final GColor HOLE_BOUNDARY_COLOR = GColor.newColor(128, 0, 128);

	// deep, dark teal or aqua color
	private static final GColor EXTERIOR_HOLE_COLOR = GColor.newColor(0, 180, 180);

	// teal
	private static final GColor SAMPLE_POINT_COLOR = GColor.newColor(0, 160, 120);
	private static final GColor LABEL_COLOR = GColor.BLACK;
	private static final int VERTEX_SIZE = 4;
	private static final int SAMPLE_POINT_SIZE = 12;
	private static final int ARROW_SIZE = 6;

	private final Supplier<EuclidianViewBounds> boundsSupplier;
	private final PlanarGraph graph;

	PlanarGraphVisualDebug(EuclidianViewBounds bounds, PlanarGraph graph) {
		this(() -> bounds, graph);
	}

	PlanarGraphVisualDebug(Supplier<EuclidianViewBounds> boundsSupplier, PlanarGraph graph) {
		this.boundsSupplier = boundsSupplier;
		this.graph = graph;
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (graph == null || bounds() == null) {
			return;
		}

		fillFaces(g2);
		drawBoundaryCycles(g2, graph.getLastExtractedBoundaryCycles(), EXTRACTED_CYCLE_COLOR);
		drawBoundaryCycles(g2, graph.getLastCanonicalBoundaryCycles(), CANONICAL_CYCLE_COLOR);
		drawHalfEdges(g2);
		drawFaces(g2);
		drawVertices(g2);
		drawDebugSummary(g2);
	}

	private void drawHalfEdges(GGraphics2D g2) {
		for (HalfEdge halfEdge : graph.getHalfEdges()) {
			if (!halfEdge.isActive() || halfEdge.getId() > halfEdge.getTwinHalfEdgeId()) {
				continue;
			}

			Vertex origin = graph.vertex(halfEdge.getOriginVertexId());
			Vertex target = graph.vertex(halfEdge.getTargetVertexId());
			int x1 = (int) Math.round(bounds().toScreenCoordXd(origin.getX()));
			int y1 = (int) Math.round(bounds().toScreenCoordYd(origin.getY()));
			int x2 = (int) Math.round(bounds().toScreenCoordXd(target.getX()));
			int y2 = (int) Math.round(bounds().toScreenCoordYd(target.getY()));

			g2.setColor(halfEdge.isContourEdge() ? CONTOUR_COLOR : VIEWPORT_COLOR);
			g2.drawLine(x1, y1, x2, y2);
			if (SHOW_DIRECTION_TICKS) {
				drawDirectionTick(g2, x1, y1, x2, y2);
			}
		}
	}

	private void drawDirectionTick(GGraphics2D g2, int x1, int y1, int x2, int y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double length = Math.hypot(dx, dy);
		if (length < 1) {
			return;
		}

		double ux = dx / length;
		double uy = dy / length;
		double px = -uy;
		double py = ux;
		double mx = x1 + 0.65 * dx;
		double my = y1 + 0.65 * dy;
		int tx1 = (int) Math.round(mx - ARROW_SIZE * (ux + 0.5 * px));
		int ty1 = (int) Math.round(my - ARROW_SIZE * (uy + 0.5 * py));
		int tx2 = (int) Math.round(mx - ARROW_SIZE * (ux - 0.5 * px));
		int ty2 = (int) Math.round(my - ARROW_SIZE * (uy - 0.5 * py));
		int mxInt = (int) Math.round(mx);
		int myInt = (int) Math.round(my);
		g2.drawLine(mxInt, myInt, tx1, ty1);
		g2.drawLine(mxInt, myInt, tx2, ty2);
	}

	private void drawVertices(GGraphics2D g2) {
		for (Vertex vertex : graph.getVertices()) {
			int sx = (int) Math.round(bounds().toScreenCoordXd(vertex.getX()));
			int sy = (int) Math.round(bounds().toScreenCoordYd(vertex.getY()));
			g2.setColor(VERTEX_COLOR);
			g2.fillRect(sx - VERTEX_SIZE / 2, sy - VERTEX_SIZE / 2, VERTEX_SIZE, VERTEX_SIZE);
			if (SHOW_VERTEX_IDS) {
				g2.setColor(LABEL_COLOR);
				g2.drawString(String.valueOf(vertex.getId()), sx + 4, sy - 4);
			}
		}
	}

	private void drawFaces(GGraphics2D g2) {
		for (Face face : graph.getFaces()) {
			if (face.isExterior()) {
				for (List<Integer> holeBoundary : graph.holeBoundariesOf(face)) {
					drawBoundary(g2, holeBoundary, EXTERIOR_HOLE_COLOR);
				}
				continue;
			}
			if (face.getOuterHalfEdgeId() == -1) {
				continue;
			}
			drawBoundary(g2, graph.outerBoundaryOf(face), FACE_BOUNDARY_COLOR);
			for (List<Integer> holeBoundary : graph.holeBoundariesOf(face)) {
				drawBoundary(g2, holeBoundary, HOLE_BOUNDARY_COLOR);
			}
			drawSamplePoint(g2, face);
		}
	}

	private void drawBoundaryCycles(GGraphics2D g2, List<BoundaryCycle> cycles, GColor color) {
		for (BoundaryCycle cycle : cycles) {
			drawBoundary(g2, cycle.getHalfEdgeIds(), color);
		}
	}

	private void drawDebugSummary(GGraphics2D g2) {
		int boundedFaceCount = 0;
		int sampledFaceCount = 0;
		for (Face face : graph.getFaces()) {
			if (!face.isExterior()) {
				boundedFaceCount++;
				if (face.getSamplePoint() != null) {
					sampledFaceCount++;
				}
			}
		}
		g2.setColor(LABEL_COLOR);
		g2.drawString(
				"faces=" + graph.getFaces().size()
						+ " bounded=" + boundedFaceCount
						+ " sampled=" + sampledFaceCount
						+ " cycles=" + graph.getLastExtractedBoundaryCycles().size()
						+ " canonical=" + graph.getLastCanonicalBoundaryCycles().size(),
				12,
				18);
		g2.drawString(
				"view=[" + format(bounds().getXmin()) + "," + format(bounds().getXmax())
						+ "] x [" + format(bounds().getYmin()) + "," + format(bounds().getYmax()) + "]"
						+ " px=" + bounds().getWidth() + "x" + bounds().getHeight()
						+ " scale=" + formatScale(1.0 / bounds().getInvXscale())
						+ "x" + formatScale(1.0 / bounds().getInvYscale())
						+ " inv=" + format(bounds().getInvXscale())
						+ "x" + format(bounds().getInvYscale()),
				12,
				34);
	}

	private EuclidianViewBounds bounds() {
		return boundsSupplier.get();
	}

	private String format(double value) {
		double rounded = Math.round(value * 1000d) / 1000d;
		String text = Double.toString(rounded);
		if (text.contains("E") || text.contains("e")) {
			return text;
		}
		int dot = text.indexOf('.');
		if (dot < 0) {
			return text + ".000";
		}
		int decimals = text.length() - dot - 1;
		if (decimals >= 3) {
			return text;
		}
		StringBuilder builder = new StringBuilder(text);
		while (decimals < 3) {
			builder.append('0');
			decimals++;
		}
		return builder.toString();
	}

	private String formatScale(double value) {
		if (!Double.isFinite(value)) {
			return Double.toString(value);
		}
		return format(value);
	}

	private void fillFaces(GGraphics2D g2) {
		for (Face face : graph.getFaces()) {
			if (face.isExterior() || face.getOuterHalfEdgeId() == -1) {
				continue;
			}
			g2.setColor(FACE_FILL_COLOR);
			g2.fill(buildBoundaryPath(graph.outerBoundaryOf(face)));
		}
	}

	private void drawBoundary(GGraphics2D g2, List<Integer> boundary, GColor color) {
		g2.setColor(color);
		g2.draw(buildBoundaryPath(boundary));
	}

	private GGeneralPath buildBoundaryPath(List<Integer> boundary) {
		if (boundary.isEmpty()) {
			return AwtFactory.getPrototype().newGeneralPath();
		}
		GGeneralPath path = AwtFactory.getPrototype().newGeneralPath();
		Vertex start = graph.vertex(graph.halfEdge(boundary.get(0)).getOriginVertexId());
		path.moveTo(bounds().toScreenCoordXd(start.getX()), bounds().toScreenCoordYd(start.getY()));
		for (int halfEdgeId : boundary) {
			Vertex target = graph.vertex(graph.halfEdge(halfEdgeId).getTargetVertexId());
			path.lineTo(bounds().toScreenCoordXd(target.getX()), bounds().toScreenCoordYd(target.getY()));
		}
		path.closePath();
		return path;
	}

	private void drawSamplePoint(GGraphics2D g2, Face face) {
		MyPoint samplePoint = face.getSamplePoint();
		if (samplePoint == null) {
			return;
		}
		int sx = (int) Math.round(bounds().toScreenCoordXd(samplePoint.x));
		int sy = (int) Math.round(bounds().toScreenCoordYd(samplePoint.y));
		g2.setColor(SAMPLE_POINT_COLOR);
		g2.fillRect(
				sx - SAMPLE_POINT_SIZE / 2,
				sy - SAMPLE_POINT_SIZE / 2,
				SAMPLE_POINT_SIZE,
				SAMPLE_POINT_SIZE);
		if (SHOW_FACE_IDS) {
			g2.setColor(LABEL_COLOR);
			g2.drawString("F" + face.getId(), sx + 6, sy - 6);
		}
	}

	@Override
	public void fill(GGraphics2D g2) {
		draw(g2);
	}

	@Override
	public void setEdgePoints(List<MyPoint> edgePoints) {
		// Unused: graph debug draws directly from the planar graph.
	}
}
