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

package org.geogebra.common.euclidian.plot.implicit.classification.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.implicit.classification.ViewportInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FaceBuilderTest {
	private PlanarGraph graph;
	private FaceBuilder builder;

	@BeforeEach
	void setUp() {
		graph = new PlanarGraph();
		builder = new FaceBuilder(graph);
	}

	@Test
	void canonicalBoundaryCyclesShouldDedupSameBoundaryRecoveredTwice() {
		int bottomLeft = graph.addVertex(0, 0);
		int bottomRight = graph.addVertex(2, 0);
		int topRight = graph.addVertex(2, 2);
		int topLeft = graph.addVertex(0, 2);

		graph.addContourEdge(bottomLeft, bottomRight, 0);
		graph.addContourEdge(bottomRight, topRight, 0);
		graph.addContourEdge(topRight, topLeft, 0);
		graph.addContourEdge(topLeft, bottomLeft, 0);
		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();

		List<BoundaryCycle> extracted = builder.extractBoundaryCycles();

		List<BoundaryCycle> canonical = builder.canonicalBoundaryCyclesOf(extracted);

		assertEquals(1, canonical.size());
	}

	@Test
	void canonicalBoundaryCyclesShouldRejectDegenerateTwoEdgeCycle() {
		int bottomLeft = graph.addVertex(0, 0);
		int bottomRight = graph.addVertex(2, 0);
		int topRight = graph.addVertex(2, 2);
		int topLeft = graph.addVertex(0, 2);
		int d0 = graph.addVertex(10, 10);
		int d1 = graph.addVertex(11, 10);

		graph.addContourEdge(bottomLeft, bottomRight, 0);
		graph.addContourEdge(bottomRight, topRight, 0);
		graph.addContourEdge(topRight, topLeft, 0);
		graph.addContourEdge(topLeft, bottomLeft, 0);
		int degenerate = graph.addContourEdge(d0, d1, 1);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();

		List<BoundaryCycle> extracted = new ArrayList<>(builder.extractBoundaryCycles());
		extracted.add(new BoundaryCycle(99,
				List.of(degenerate, graph.halfEdge(degenerate).getTwinHalfEdgeId()),
				0.0, new GPoint2D(10.5, 10.0)));

		List<BoundaryCycle> canonical = builder.canonicalBoundaryCyclesOf(extracted);
		assertEquals(1, canonical.size());
	}

	@Test
	void canonicalGeometricDedupShouldCollapseEquivalentCyclesWithDifferentSegmentation() {
		int bottomLeft = graph.addVertex(0, 0);
		int bottomMid = graph.addVertex(1, 0);
		int bottomRight = graph.addVertex(2, 0);
		int topRight = graph.addVertex(2, 2);
		int topLeft = graph.addVertex(0, 2);

		int aBottom = graph.addContourEdge(bottomLeft, bottomRight, 0);
		int aRight = graph.addContourEdge(bottomRight, topRight, 0);
		int aTop = graph.addContourEdge(topRight, topLeft, 0);
		int aLeft = graph.addContourEdge(topLeft, bottomLeft, 0);

		int bBottom0 = graph.addContourEdge(bottomLeft, bottomMid, 1);
		int bBottom1 = graph.addContourEdge(bottomMid, bottomRight, 1);
		int bRight = graph.addContourEdge(bottomRight, topRight, 1);
		int bTop = graph.addContourEdge(topRight, topLeft, 1);
		int bLeft = graph.addContourEdge(topLeft, bottomLeft, 1);

		BoundaryCycle cycleA = new BoundaryCycle(2, List.of(
				graph.halfEdge(aBottom).getTwinHalfEdgeId(),
				graph.halfEdge(aLeft).getTwinHalfEdgeId(),
				graph.halfEdge(aTop).getTwinHalfEdgeId(),
				graph.halfEdge(aRight).getTwinHalfEdgeId()),
				-4.0, new GPoint2D(1.0, 1.0));

		BoundaryCycle cycleB = new BoundaryCycle(3, List.of(
				bBottom0, bBottom1, bRight, bTop, bLeft),
				4.0, new GPoint2D(1.0, 1.0));
		BoundaryCycleNormalizer normalizer = new BoundaryCycleNormalizer(graph,
				new BoundaryCycleExtractor(graph));
		List<BoundaryCycle> deduped = normalizer.dedupCanonicalCyclesGeometrically(
				List.of(cycleA, cycleB));

		assertEquals(1, deduped.size());
		assertEquals(cycleB.getId(), deduped.get(0).getId());
	}

	@Test
	void canonicalBoundaryCyclesShouldRejectNonViewportCycleOutsideViewportBounds() {

		int viewportBottomLeft = graph.addVertex(-4, -4);
		int viewportBottomRight = graph.addVertex(-1, -4);
		int viewportTopRight = graph.addVertex(-1, -1);
		int viewportTopLeft = graph.addVertex(-4, -1);

		ViewportInfo viewportInfo =
				ViewportInfo.ofBounds(viewportTopLeft, viewportTopRight, viewportBottomLeft,
						viewportBottomRight,
						-5, 5, -5, 5);
		graph.addViewportEdge(viewportBottomLeft, viewportBottomRight);
		graph.addViewportEdge(viewportBottomRight, viewportTopRight);
		graph.addViewportEdge(viewportTopRight, viewportTopLeft);
		graph.addViewportEdge(viewportTopLeft, viewportBottomLeft);

		int outsideBottomLeft = graph.addVertex(10, 10);
		int outsideBottomRight = graph.addVertex(12, 10);
		int outsideTopRight = graph.addVertex(12, 12);
		int outsideTopLeft = graph.addVertex(10, 12);

		graph.addContourEdge(outsideBottomLeft, outsideBottomRight, 0);
		graph.addContourEdge(outsideBottomRight, outsideTopRight, 0);
		graph.addContourEdge(outsideTopRight, outsideTopLeft, 0);
		graph.addContourEdge(outsideTopLeft, outsideBottomLeft, 0);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();

		List<BoundaryCycle> extracted = builder.extractBoundaryCycles();

		List<BoundaryCycle> canonical = builder.canonicalBoundaryCyclesOf(extracted, viewportInfo);

		assertEquals(1, canonical.size());
		assertTrue(canonical.get(0).getAbsArea() >= 9.0);
	}
}
