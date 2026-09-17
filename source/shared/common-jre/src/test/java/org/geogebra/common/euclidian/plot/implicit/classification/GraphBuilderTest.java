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

package org.geogebra.common.euclidian.plot.implicit.classification;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.plot.implicit.ClipEdge;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragment;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragmentsResult;
import org.geogebra.common.euclidian.plot.implicit.EdgeHit;
import org.geogebra.common.euclidian.plot.implicit.FragmentEndpoint;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryCycle;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.EpsilonPolicy;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle2D;
import org.junit.jupiter.api.Test;

class GraphBuilderTest {

	@Test
	void addContourSubEdgeShouldSkipDegenerateMergedEndpoints() {
		GraphBuilder builder = new GraphBuilder(EpsilonPolicy.defaults());

		builder.addContourSubEdge(new GPoint2D(0, 0), new GPoint2D(1e-12, 0), 7);

		PlanarGraph graph = builder.getGraph();
		assertEquals(1, graph.getVertices().size());
		assertEquals(0, graph.getHalfEdges().size());
	}

	@Test
	void findOrAddVertexShouldReuseExactPoint() {
		GraphBuilder builder = new GraphBuilder(EpsilonPolicy.defaults());

		int first = builder.findOrAddVertex(new GPoint2D(1, 2));
		int second = builder.findOrAddVertex(new GPoint2D(1, 2));

		assertAll(
				() -> assertEquals(first, second),
				() -> assertEquals(1, builder.getGraph().getVertices().size()));
	}

	@Test
	void findOrAddVertexShouldReusePointWithinMergeTolerance() {
		EpsilonPolicy epsilonPolicy = new EpsilonPolicy(1e-10, 1e-12, 1e-3, 1e-12);
		GraphBuilder builder = new GraphBuilder(epsilonPolicy);

		int first = builder.findOrAddVertex(new GPoint2D(1, 2));
		int second = builder.findOrAddVertex(new GPoint2D(1.0005, 2.0004));

		assertAll(
				() -> assertEquals(first, second),
				() -> assertEquals(1, builder.getGraph().getVertices().size()));
	}

	@Test
	void findOrAddVertexShouldNotReusePointOutsideMergeTolerance() {
		EpsilonPolicy epsilonPolicy = new EpsilonPolicy(1e-10, 1e-12, 1e-3, 1e-12);
		GraphBuilder builder = new GraphBuilder(epsilonPolicy);

		int first = builder.findOrAddVertex(new GPoint2D(1, 2));
		int second = builder.findOrAddVertex(new GPoint2D(1.002, 2));

		assertAll(
				() -> assertNotEquals(first, second),
				() -> assertEquals(2, builder.getGraph().getVertices().size()));
	}

	@Test
	void addContourSubEdgesShouldReuseMergedSharedEndpoint() {
		GraphBuilder builder = new GraphBuilder(EpsilonPolicy.defaults());

		builder.addContourSubEdge(new GPoint2D(0, 0), new GPoint2D(1, 0), 7);
		builder.addContourSubEdge(new GPoint2D(1 + 5e-13, 5e-13), new GPoint2D(2, 0), 7);

		PlanarGraph graph = builder.getGraph();
		assertAll(
				() -> assertEquals(3, graph.getVertices().size()),
				() -> assertEquals(4, graph.getHalfEdges().size()));
	}

	@Test
	void openTopRightFragmentShouldNotCollapseToZeroAreaCycle() {
		GraphBuilder builder = new GraphBuilder(EpsilonPolicy.defaults());
		builder.build(
				bounds(-12.198461432801118, -15.489628634034508, 11.589930649819534, 14.767628485014281),
				new ClippedFragmentsResult(
						List.of(topRightOpenFragment()),
						List.of(
								edgeHit(
										ClipEdge.TOP,
										0.6767430927688882,
										0,
										0.0,
										0.6767430927688882,
										-4.150824595037196,
										-0.14423690625754026,
										0),
								edgeHit(
										ClipEdge.RIGHT,
										0.03404135588352595,
										226,
										0.03404135588352595,
										1.034041355883526,
										-0.030767540218894274,
										-0.6862826913982158,
										0))));

		PlanarGraph graph = builder.getGraph();

		assertTrue(
				graph.getLastExtractedBoundaryCycles().stream()
						.anyMatch(cycle -> Math.abs(cycle.getSignedArea()) > 1e-6),
				"Open TOP->RIGHT fragment should not collapse into a near-zero extracted cycle: "
						+ describeCycles(graph.getLastExtractedBoundaryCycles()));
		assertTrue(
				!graph.getLastCanonicalBoundaryCycles().isEmpty(),
				"Open TOP->RIGHT fragment should keep bounded canonical cycles: "
						+ describeCycles(graph.getLastCanonicalBoundaryCycles()));
		assertTrue(
				hasBoundedCanonicalCycle(graph, 11.589930649819534 * 14.767628485014281),
				"Open TOP->RIGHT fragment should keep a bounded non-viewport canonical cycle: "
						+ describeCycles(graph.getLastCanonicalBoundaryCycles()));
	}

	@Test
	void openSameEdgeFragmentShouldNotCloseAsViewportOrRibbon() {
		GraphBuilder builder = new GraphBuilder(EpsilonPolicy.defaults());
		builder.build(
				bounds(0, 0, 10, 10),
				new ClippedFragmentsResult(
						List.of(rightRightOpenFragment()),
						List.of(
								edgeHit(ClipEdge.RIGHT, 0.2, 0, 0.0, 1.2, 10, 8, 0),
								edgeHit(ClipEdge.RIGHT, 0.8, 2, 1.0, 1.8, 10, 2, 0))));

		PlanarGraph graph = builder.getGraph();

		assertTrue(
				graph.getLastExtractedBoundaryCycles().stream()
						.anyMatch(cycle ->
								Math.abs(cycle.getSignedArea()) > 1e-6 && Math.abs(cycle.getSignedArea()) < 99),
				"Same-edge open fragment should produce a bounded non-ribbon extracted cycle: "
						+ describeCycles(graph.getLastExtractedBoundaryCycles()));
		assertTrue(
				hasBoundedCanonicalCycle(graph, 100),
				"Same-edge open fragment should keep a bounded non-viewport canonical cycle: "
						+ describeCycles(graph.getLastCanonicalBoundaryCycles()));
	}

	@Test
	void repeatedOpenFragmentBuildsShouldNotAccumulateClosureState() {
		GraphBuilder builder = new GraphBuilder(EpsilonPolicy.defaults());
		ClippedFragmentsResult topRight = new ClippedFragmentsResult(
				List.of(topRightOpenFragment()),
				List.of(
						edgeHit(
								ClipEdge.TOP,
								0.6767430927688882,
								0,
								0.0,
								0.6767430927688882,
								-4.150824595037196,
								-0.14423690625754026,
								0),
						edgeHit(
								ClipEdge.RIGHT,
								0.03404135588352595,
								226,
								0.03404135588352595,
								1.034041355883526,
								-0.030767540218894274,
								-0.6862826913982158,
								0)));
		ClippedFragmentsResult rightRight = new ClippedFragmentsResult(
				List.of(rightRightOpenFragment()),
				List.of(
						edgeHit(ClipEdge.RIGHT, 0.2, 0, 0.0, 1.2, 10, 8, 0),
						edgeHit(ClipEdge.RIGHT, 0.8, 2, 1.0, 1.8, 10, 2, 0)));

		for (int i = 0; i < 10; i++) {
			builder.build(
					bounds(-12.198461432801118, -15.489628634034508, 11.589930649819534, 14.767628485014281),
					topRight);
			assertTrue(builder.debugSummary().contains("openClosure={status=SUCCESS open=1"));
			builder.build(bounds(0, 0, 10, 10), rightRight);
			assertTrue(builder.debugSummary().contains("openClosure={status=SUCCESS open=1"));
		}
	}

	private static GRectangle2D bounds(double x, double y, double width, double height) {
		GRectangle2D rect = new Rectangle2D.Double();
		rect.setRect(x, y, width, height);
		return rect;
	}

	private static ClippedFragment topRightOpenFragment() {
		MyPoint start = point(-4.150824595037196, -0.14423690625754026, false);
		MyPoint p1 = point(-4.036486640402118, -0.5796356749832339, true);
		MyPoint p2 = point(-3.9294008722338134, -0.7804986773499493, true);
		MyPoint p3 = point(-3.8159121268244496, -0.9359982376091255, true);
		MyPoint p4 = point(-3.625295508427286, -1.1278788270610152, true);
		MyPoint p5 = point(-2.9122037313611115, -1.4804662035168348, true);
		MyPoint p6 = point(-2.4392277829791715, -1.5309569664907767, true);
		MyPoint p7 = point(-1.9164648926622903, -1.4643309912253966, true);
		MyPoint p8 = point(-1.2941281184755267, -1.2419923382830287, true);
		MyPoint p9 = point(-0.7210875919675381, -0.9412793547500036, true);
		MyPoint p10 = point(-0.22370886687429367, -0.7172650829963223, true);
		MyPoint endPoint = point(-0.030767540218894274, -0.6862826913982158, true);

		FragmentEndpoint startEndpoint =
				new FragmentEndpoint(start, ClipEdge.TOP, 0.6767430927688882, 0, 0, 0.0);
		FragmentEndpoint endEndpoint =
				new FragmentEndpoint(endPoint, ClipEdge.RIGHT, 1.034041355883526, 0, 226, Double.NaN);
		return new ClippedFragment(
				0,
				List.of(start, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, endPoint),
				false,
				startEndpoint,
				endEndpoint);
	}

	private static ClippedFragment rightRightOpenFragment() {
		MyPoint start = point(10, 8, false);
		MyPoint p1 = point(7.5, 7, true);
		MyPoint p2 = point(7, 5, true);
		MyPoint p3 = point(7.5, 3, true);
		MyPoint endPoint = point(10, 2, true);

		FragmentEndpoint startEndpoint = new FragmentEndpoint(start, ClipEdge.RIGHT, 1.2, 0, 0, 0.0);
		FragmentEndpoint endEndpoint = new FragmentEndpoint(endPoint, ClipEdge.RIGHT, 1.8, 0, 2, 1.0);
		return new ClippedFragment(
				0, List.of(start, p1, p2, p3, endPoint), false, startEndpoint, endEndpoint);
	}

	private static EdgeHit edgeHit(
			ClipEdge edge,
			double tOnEdge,
			int segIndex,
			double tSegment,
			double sPerimeter,
			double x,
			double y,
			int contourId) {
		EdgeHit hit = new EdgeHit(edge, tOnEdge, segIndex, tSegment, sPerimeter, x, y);
		hit.setContourId(contourId);
		return hit;
	}

	private static MyPoint point(double x, double y, boolean lineTo) {
		MyPoint point = new MyPoint(x, y, SegmentType.LINE_TO);
		point.setLineTo(lineTo);
		return point;
	}

	private static String describeCycles(List<BoundaryCycle> cycles) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < cycles.size(); i++) {
			BoundaryCycle cycle = cycles.get(i);
			if (i > 0) {
				sb.append(", ");
			}
			sb.append("{id=")
					.append(cycle.getId())
					.append(", area=")
					.append(cycle.getSignedArea())
					.append(", edges=")
					.append(cycle.getHalfEdgeIds().size())
					.append('}');
		}
		sb.append(']');
		return sb.toString();
	}

	private static boolean hasBoundedCanonicalCycle(PlanarGraph graph, double viewportArea) {
		return graph.getLastCanonicalBoundaryCycles().stream()
				.anyMatch(cycle -> cycle.getAbsArea() > 1e-6 && cycle.getAbsArea() < viewportArea * 0.99);
	}
}
