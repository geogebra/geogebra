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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.implicit.classification.ViewportInfo;
import org.geogebra.common.kernel.MyPoint;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PlanarGraphTopologyModelTest {

	@Test
	void segmentKindShouldExposeEnumValues() {
		assertEquals(SegmentKind.CONTOUR, SegmentKind.valueOf("CONTOUR"));
		assertEquals(SegmentKind.VIEWPORT, SegmentKind.valueOf("VIEWPORT"));
	}

	@Test
	void epsilonPolicyShouldProvideDefaultsAndAccessors() {
		EpsilonPolicy defaults = EpsilonPolicy.defaults();
		assertEquals(1e-10, defaults.getSnap());
		assertEquals(1e-12, defaults.getIntersection());
		assertEquals(1e-12, defaults.getVertexMerge());
		assertEquals(1e-12, defaults.getAngle());

		EpsilonPolicy custom = new EpsilonPolicy(1e-3, 1e-4, 1e-2, 1e-5);
		assertEquals(1e-3, custom.getSnap());
		assertEquals(1e-4, custom.getIntersection());
		assertEquals(1e-2, custom.getVertexMerge());
		assertEquals(1e-5, custom.getAngle());
	}

	@Test
	void planarGraphShouldCreateVerticesEdgesAndFaces() {
		PlanarGraph graph = new PlanarGraph();

		int v0 = graph.addVertex(0, 0);
		int v1 = graph.addVertex(1, 0);
		int e0 = graph.addHalfEdge(v0, v1, SegmentKind.CONTOUR, 2);
		int e1 = graph.addHalfEdge(v1, v0, SegmentKind.VIEWPORT, -1);
		int f0 = graph.addFace();

		assertEquals(0, v0);
		assertEquals(1, v1);
		assertEquals(0, e0);
		assertEquals(1, e1);
		assertEquals(0, f0);

		Vertex vertex0 = graph.vertex(v0);
		Vertex vertex1 = graph.vertex(v1);
		assertEquals(v0, vertex0.getId());
		assertEquals(0, vertex0.getX());
		assertEquals(0, vertex0.getY());
		assertEquals(v1, vertex1.getId());
		assertEquals(1, vertex1.getX());
		assertEquals(0, vertex1.getY());
		assertEquals(1, vertex0.getOutgoingHalfEdges().size());
		assertEquals(e0, vertex0.getOutgoingHalfEdges().get(0));
		assertEquals(1, vertex1.getOutgoingHalfEdges().size());
		assertEquals(e1, vertex1.getOutgoingHalfEdges().get(0));

		HalfEdge halfEdge0 = graph.halfEdge(e0);
		assertEquals(v0, halfEdge0.getOriginVertexId());
		assertEquals(v1, halfEdge0.getTargetVertexId());
		assertEquals(SegmentKind.CONTOUR, halfEdge0.getSegmentKind());
		assertEquals(2, halfEdge0.getSourceContourId());
		assertTrue(halfEdge0.isContourEdge());
		assertFalse(halfEdge0.isViewportEdge());
		assertEquals(-1, halfEdge0.getTwinHalfEdgeId());
		assertEquals(-1, halfEdge0.getNextHalfEdgeId());
		assertEquals(-1, halfEdge0.getPrevHalfEdgeId());
		assertEquals(-1, halfEdge0.getFaceId());

		halfEdge0.setTwinHalfEdgeId(e1);
		halfEdge0.setNextHalfEdgeId(e1);
		halfEdge0.setPrevHalfEdgeId(e1);
		halfEdge0.setFaceId(f0);
		assertEquals(e1, halfEdge0.getTwinHalfEdgeId());
		assertEquals(e1, halfEdge0.getNextHalfEdgeId());
		assertEquals(e1, halfEdge0.getPrevHalfEdgeId());
		assertEquals(f0, halfEdge0.getFaceId());

		Face face = graph.face(f0);
		assertEquals(f0, face.getId());
		assertEquals(-1, face.getOuterHalfEdgeId());
		assertTrue(face.getHoleHalfEdgeIds().isEmpty());
		assertFalse(face.isExterior());
		assertEquals(null, face.getSamplePoint());

		face.setOuterHalfEdgeId(e0);
		face.addHoleHalfEdgeId(e1);
		face.setExterior(true);
		MyPoint sample = new MyPoint(0.25, 0.5);
		face.setSamplePoint(sample);

		assertEquals(e0, face.getOuterHalfEdgeId());
		assertEquals(1, face.getHoleHalfEdgeIds().size());
		assertEquals(e1, face.getHoleHalfEdgeIds().get(0));
		assertTrue(face.isExterior());
		assertSame(sample, face.getSamplePoint());

		assertEquals(2, graph.getVertices().size());
		assertEquals(2, graph.getHalfEdges().size());
		assertEquals(1, graph.getFaces().size());
	}

	@Test
	void sortVertexOutgoingHalfEdgesShouldUseIncreasingAngleOrder() {
		PlanarGraph graph = new PlanarGraph();
		int center = graph.addVertex(0, 0);
		int right = graph.addVertex(1, 0);
		int up = graph.addVertex(0, 1);
		int left = graph.addVertex(-1, 0);
		int down = graph.addVertex(0, -1);

		int edgeRight = graph.addContourEdge(center, right, 0);
		int edgeUp = graph.addContourEdge(center, up, 0);
		int edgeLeft = graph.addContourEdge(center, left, 0);
		int edgeDown = graph.addContourEdge(center, down, 0);

		graph.sortVertexOutgoingHalfEdges();

		assertEquals(List.of(edgeDown, edgeRight, edgeUp, edgeLeft),
				graph.vertex(center).getOutgoingHalfEdges());
	}

	@Test
	void linkHalfEdgesShouldFollowCounterclockwiseInteriorBoundary() {
		PlanarGraph graph = new PlanarGraph();
		int bottomLeft = graph.addVertex(0, 0);
		int bottomRight = graph.addVertex(1, 0);
		int topRight = graph.addVertex(1, 1);
		int topLeft = graph.addVertex(0, 1);

		final int bottom = graph.addContourEdge(bottomLeft, bottomRight, 0);
		final int right = graph.addContourEdge(bottomRight, topRight, 0);
		final int top = graph.addContourEdge(topRight, topLeft, 0);
		final int left = graph.addContourEdge(topLeft, bottomLeft, 0);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();

		assertEquals(right, graph.halfEdge(bottom).getNextHalfEdgeId());
		assertEquals(top, graph.halfEdge(right).getNextHalfEdgeId());
		assertEquals(left, graph.halfEdge(top).getNextHalfEdgeId());
		assertEquals(bottom, graph.halfEdge(left).getNextHalfEdgeId());

		assertEquals(left, graph.halfEdge(bottom).getPrevHalfEdgeId());
		assertEquals(bottom, graph.halfEdge(right).getPrevHalfEdgeId());
		assertEquals(right, graph.halfEdge(top).getPrevHalfEdgeId());
		assertEquals(top, graph.halfEdge(left).getPrevHalfEdgeId());
	}

	@Test
	void linkHalfEdgesShouldWrapAroundAtEndOfOutgoingOrder() {
		PlanarGraph graph = new PlanarGraph();
		int center = graph.addVertex(0, 0);
		int right = graph.addVertex(1, 0);
		int up = graph.addVertex(0, 1);
		int left = graph.addVertex(-1, 0);

		int centerToRight = graph.addContourEdge(center, right, 0);
		int upToCenter = graph.addContourEdge(up, center, 0);
		int leftToCenter = graph.addContourEdge(left, center, 0);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();

		int incomingFromUp = graph.halfEdge(upToCenter).getTwinHalfEdgeId();
		List<Integer> outgoing = graph.vertex(center).getOutgoingHalfEdges();
		assertEquals(
				List.of(centerToRight, incomingFromUp, graph.halfEdge(leftToCenter)
						.getTwinHalfEdgeId()),
				outgoing);

		int nextAfterIncomingFromLeft = graph.halfEdge(leftToCenter).getNextHalfEdgeId();
		assertEquals(centerToRight, nextAfterIncomingFromLeft);
		assertEquals(leftToCenter, graph.halfEdge(centerToRight).getPrevHalfEdgeId());
	}

	@Test
	void sortAndLinkShouldIgnoreInactiveHalfEdges() {
		PlanarGraph graph = new PlanarGraph();
		int center = graph.addVertex(0, 0);
		int right = graph.addVertex(1, 0);
		int up = graph.addVertex(0, 1);
		int left = graph.addVertex(-1, 0);

		int centerToRight = graph.addContourEdge(center, right, 0);
		int centerToUp = graph.addContourEdge(center, up, 0);
		int centerToLeft = graph.addContourEdge(center, left, 0);

		graph.halfEdge(centerToUp).setActive(false);
		graph.vertex(center).removeOutgoingHalfEdge(centerToUp);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();

		assertEquals(List.of(centerToRight, centerToLeft),
				graph.vertex(center).getOutgoingHalfEdges());
		assertEquals(-1, graph.halfEdge(centerToUp).getNextHalfEdgeId());
		assertEquals(-1, graph.halfEdge(centerToUp).getPrevHalfEdgeId());
	}

	@Test
	void removeViewportEdgeShouldRemoveMatchingHalfEdgeIdsByValue() {
		PlanarGraph graph = new PlanarGraph();
		int topLeft = graph.addVertex(0, 1);
		int topRight = graph.addVertex(1, 1);
		int bottomRight = graph.addVertex(1, 0);

		int top = graph.addViewportEdge(topLeft, topRight);
		int right = graph.addViewportEdge(topRight, bottomRight);

		graph.removeViewportEdge(topLeft, topRight);

		assertEquals(List.of(right), graph.vertex(topRight).getOutgoingHalfEdges());
		assertEquals(List.of(graph.halfEdge(right).getTwinHalfEdgeId()),
				graph.vertex(bottomRight).getOutgoingHalfEdges());
		assertFalse(graph.halfEdge(top).isActive());
		assertFalse(graph.halfEdge(graph.halfEdge(top).getTwinHalfEdgeId()).isActive());
		assertTrue(graph.halfEdge(right).isActive());
	}

	@Test
	void extractFacesShouldCreateOneFacePerBoundaryAndAssignFaceIds() {
		PlanarGraph graph = new PlanarGraph();
		int bottomLeft = graph.addVertex(0, 0);
		int bottomRight = graph.addVertex(1, 0);
		int topRight = graph.addVertex(1, 1);
		int topLeft = graph.addVertex(0, 1);

		int bottom = graph.addContourEdge(bottomLeft, bottomRight, 0);
		final int right = graph.addContourEdge(bottomRight, topRight, 0);
		final int top = graph.addContourEdge(topRight, topLeft, 0);
		final int left = graph.addContourEdge(topLeft, bottomLeft, 0);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();
		graph.extractFaces(null);

		assertEquals(2, graph.getFaces().size());

		int face0 = graph.halfEdge(bottom).getFaceId();
		int face1 = graph.halfEdge(graph.halfEdge(bottom).getTwinHalfEdgeId()).getFaceId();
		assertTrue(face0 >= 0);
		assertTrue(face1 >= 0);
		assertNotEquals(face0, face1);

		assertEquals(face0, graph.halfEdge(right).getFaceId());
		assertEquals(face0, graph.halfEdge(top).getFaceId());
		assertEquals(face0, graph.halfEdge(left).getFaceId());

		int bottomTwin = graph.halfEdge(bottom).getTwinHalfEdgeId();
		int rightTwin = graph.halfEdge(right).getTwinHalfEdgeId();
		int topTwin = graph.halfEdge(top).getTwinHalfEdgeId();
		int leftTwin = graph.halfEdge(left).getTwinHalfEdgeId();
		assertEquals(face1, graph.halfEdge(bottomTwin).getFaceId());
		assertEquals(face1, graph.halfEdge(rightTwin).getFaceId());
		assertEquals(face1, graph.halfEdge(topTwin).getFaceId());
		assertEquals(face1, graph.halfEdge(leftTwin).getFaceId());

		assertEquals(face0, graph.halfEdge(graph.face(face0).getOuterHalfEdgeId()).getFaceId());
		assertTrue(graph.face(face1).isExterior());
		assertEquals(-1, graph.face(face1).getOuterHalfEdgeId());
		assertEquals(1, graph.face(face1).getHoleHalfEdgeIds().size());
	}

	@Test
	void extractFacesShouldResetExistingFacesBeforeRebuilding() {
		PlanarGraph graph = new PlanarGraph();
		int bottomLeft = graph.addVertex(0, 0);
		int bottomRight = graph.addVertex(1, 0);
		int topRight = graph.addVertex(1, 1);
		int topLeft = graph.addVertex(0, 1);

		graph.addContourEdge(bottomLeft, bottomRight, 0);
		graph.addContourEdge(bottomRight, topRight, 0);
		graph.addContourEdge(topRight, topLeft, 0);
		graph.addContourEdge(topLeft, bottomLeft, 0);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();
		graph.extractFaces(null);
		graph.extractFaces(null);

		assertEquals(2, graph.getFaces().size());
		for (HalfEdge halfEdge : graph.getHalfEdges()) {
			assertTrue(halfEdge.getFaceId() >= 0);
		}
	}

	static ViewportInfo viewportInfo(int topLeft, int topRight, int bottomLeft,
			int bottomRight) {
		return ViewportInfo.ofBounds(topLeft, topRight, bottomLeft, bottomRight,
				-10, 10, -10, 10);
	}

	@Test
	void identifyExteriorFaceShouldMarkOneFaceForSimpleRectangle() {
		PlanarGraph graph = new PlanarGraph();
		int bottomLeft = graph.addVertex(0, 0);
		int bottomRight = graph.addVertex(2, 0);
		int topRight = graph.addVertex(2, 1);
		int topLeft = graph.addVertex(0, 1);

		final int bottom = graph.addContourEdge(bottomLeft, bottomRight, 0);
		graph.addContourEdge(bottomRight, topRight, 0);
		graph.addContourEdge(topRight, topLeft, 0);
		graph.addContourEdge(topLeft, bottomLeft, 0);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();
		graph.extractFaces(null);
		graph.identifyExteriorFace();

		assertEquals(2, graph.getFaces().size());

		int interiorFaceId = graph.halfEdge(bottom).getFaceId();
		int exteriorFaceId = graph.halfEdge(graph.halfEdge(bottom).getTwinHalfEdgeId()).getFaceId();

		assertFalse(graph.face(interiorFaceId).isExterior());
		assertTrue(graph.face(exteriorFaceId).isExterior());
	}

	@Test
	void identifyExteriorFaceShouldResetPreviousExteriorFlags() {
		PlanarGraph graph = new PlanarGraph();
		int bottomLeft = graph.addVertex(0, 0);
		int bottomRight = graph.addVertex(2, 0);
		int topRight = graph.addVertex(2, 1);
		int topLeft = graph.addVertex(0, 1);

		graph.addContourEdge(bottomLeft, bottomRight, 0);
		graph.addContourEdge(bottomRight, topRight, 0);
		graph.addContourEdge(topRight, topLeft, 0);
		graph.addContourEdge(topLeft, bottomLeft, 0);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();
		graph.extractFaces(null);
		graph.identifyExteriorFace();
		graph.identifyExteriorFace();

		long exteriorCount = graph.getFaces().stream().filter(Face::isExterior).count();
		assertEquals(1, exteriorCount);
	}

	@Test
	void identifyExteriorFaceShouldLeaveExactlyOneExteriorFace() {
		PlanarGraph graph = new PlanarGraph();
		int bottomLeft = graph.addVertex(0, 0);
		int bottomRight = graph.addVertex(2, 0);
		int topRight = graph.addVertex(2, 1);
		int topLeft = graph.addVertex(0, 1);

		graph.addContourEdge(bottomLeft, bottomRight, 0);
		graph.addContourEdge(bottomRight, topRight, 0);
		graph.addContourEdge(topRight, topLeft, 0);
		graph.addContourEdge(topLeft, bottomLeft, 0);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();
		graph.extractFaces(null);
		graph.identifyExteriorFace();

		long exteriorCount = graph.getFaces().stream().filter(Face::isExterior).count();
		assertEquals(1, exteriorCount);
	}

	@Test
	void identifyExteriorFaceShouldKeepOneExteriorFaceForDisconnectedComponents() {
		PlanarGraph graph = new PlanarGraph();
		int leftBottomLeft = graph.addVertex(0, 0);
		int leftBottomRight = graph.addVertex(1, 0);
		int leftTopRight = graph.addVertex(1, 1);
		int leftTopLeft = graph.addVertex(0, 1);

		int rightBottomLeft = graph.addVertex(3, 0);
		int rightBottomRight = graph.addVertex(4, 0);
		int rightTopRight = graph.addVertex(4, 1);
		int rightTopLeft = graph.addVertex(3, 1);

		graph.addContourEdge(leftBottomLeft, leftBottomRight, 0);
		graph.addContourEdge(leftBottomRight, leftTopRight, 0);
		graph.addContourEdge(leftTopRight, leftTopLeft, 0);
		graph.addContourEdge(leftTopLeft, leftBottomLeft, 0);

		graph.addContourEdge(rightBottomLeft, rightBottomRight, 1);
		graph.addContourEdge(rightBottomRight, rightTopRight, 1);
		graph.addContourEdge(rightTopRight, rightTopLeft, 1);
		graph.addContourEdge(rightTopLeft, rightBottomLeft, 1);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();
		graph.extractFaces(null);
		graph.identifyExteriorFace();

		long exteriorCount = graph.getFaces().stream().filter(Face::isExterior).count();
		assertEquals(1, exteriorCount);
		Face exteriorFace = graph.getFaces().stream()
				.filter(Face::isExterior)
				.findFirst()
				.orElseThrow();
		assertEquals(2, exteriorFace.getHoleHalfEdgeIds().size());
	}

	@Test
	void extractFacesShouldSupportNestedOuterHoleAndIslandBoundaries() {
		PlanarGraph graph = new PlanarGraph();

		int outerBottomLeft = graph.addVertex(0, 0);
		int outerBottomRight = graph.addVertex(6, 0);
		int outerTopRight = graph.addVertex(6, 6);
		int outerTopLeft = graph.addVertex(0, 6);

		int holeBottomLeft = graph.addVertex(1, 1);
		int holeBottomRight = graph.addVertex(5, 1);
		int holeTopRight = graph.addVertex(5, 5);
		int holeTopLeft = graph.addVertex(1, 5);

		int islandBottomLeft = graph.addVertex(2, 2);
		int islandBottomRight = graph.addVertex(4, 2);
		int islandTopRight = graph.addVertex(4, 4);
		int islandTopLeft = graph.addVertex(2, 4);

		graph.addContourEdge(outerBottomLeft, outerBottomRight, 0);
		graph.addContourEdge(outerBottomRight, outerTopRight, 0);
		graph.addContourEdge(outerTopRight, outerTopLeft, 0);
		graph.addContourEdge(outerTopLeft, outerBottomLeft, 0);

		graph.addContourEdge(holeBottomLeft, holeBottomRight, 1);
		graph.addContourEdge(holeBottomRight, holeTopRight, 1);
		graph.addContourEdge(holeTopRight, holeTopLeft, 1);
		graph.addContourEdge(holeTopLeft, holeBottomLeft, 1);

		graph.addContourEdge(islandBottomLeft, islandBottomRight, 2);
		graph.addContourEdge(islandBottomRight, islandTopRight, 2);
		graph.addContourEdge(islandTopRight, islandTopLeft, 2);
		graph.addContourEdge(islandTopLeft, islandBottomLeft, 2);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();
		graph.extractFaces(null);
		graph.identifyExteriorFace();

		long exteriorCount = graph.getFaces().stream().filter(Face::isExterior).count();
		assertEquals(1, exteriorCount);
		assertEquals(4, graph.getFaces().size());

		long holedFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior() && face.getHoleHalfEdgeIds().size() == 1)
				.count();
		assertEquals(2, holedFaceCount);

		long islandFaceCount = graph.getFaces().stream()
				.filter(face -> !face.isExterior() && face.getHoleHalfEdgeIds().isEmpty())
				.count();
		assertEquals(1, islandFaceCount);
	}

	@Disabled
	@Test
	void boundaryHierarchyShouldNotParentCycleWhenProbeIsOutsideCandidateParent() {
		PlanarGraph graph = new PlanarGraph();

		int outerBottomLeft = graph.addVertex(0, 0);
		int outerBottomRight = graph.addVertex(6, 0);
		int outerTopRight = graph.addVertex(6, 6);
		int outerTopLeft = graph.addVertex(0, 6);

		int innerBottomLeft = graph.addVertex(1, 1);
		int innerBottomRight = graph.addVertex(2, 1);
		int innerTopRight = graph.addVertex(2, 2);
		int innerTopLeft = graph.addVertex(1, 2);

		int outerBottom = graph.addContourEdge(outerBottomLeft, outerBottomRight, 0);
		int outerRight = graph.addContourEdge(outerBottomRight, outerTopRight, 0);
		int outerTop = graph.addContourEdge(outerTopRight, outerTopLeft, 0);
		int outerLeft = graph.addContourEdge(outerTopLeft, outerBottomLeft, 0);

		int innerBottom = graph.addContourEdge(innerBottomLeft, innerBottomRight, 1);
		int innerRight = graph.addContourEdge(innerBottomRight, innerTopRight, 1);
		int innerTop = graph.addContourEdge(innerTopRight, innerTopLeft, 1);
		int innerLeft = graph.addContourEdge(innerTopLeft, innerBottomLeft, 1);

		BoundaryCycle outer = new BoundaryCycle(1,
				List.of(outerBottom, outerRight, outerTop, outerLeft),
				36, new GPoint2D(3, 3));
		BoundaryCycle innerWithInvalidProbe = new BoundaryCycle(2,
				List.of(innerBottom, innerRight, innerTop, innerLeft),
				1, new GPoint2D(10, 10));

		BoundaryHierarchyBuilder builder = new BoundaryHierarchyBuilder(graph);
		builder.buildHierarchy(List.of(outer, innerWithInvalidProbe));

		assertEquals(-1, innerWithInvalidProbe.getParentId());
		assertTrue(outer.getChildIds().isEmpty());
	}

	@Test
	void extractFacesShouldSupportSingleBoundedFaceInsideViewportRoot() {
		PlanarGraph graph = new PlanarGraph();

		int viewportBottomLeft = graph.addVertex(0, 0);
		int viewportBottomRight = graph.addVertex(6, 0);
		int viewportTopRight = graph.addVertex(6, 6);
		int viewportTopLeft = graph.addVertex(0, 6);

		int innerBottomLeft = graph.addVertex(1, 1);
		int innerBottomRight = graph.addVertex(5, 1);
		int innerTopRight = graph.addVertex(5, 5);
		int innerTopLeft = graph.addVertex(1, 5);

		graph.addViewportEdge(viewportBottomLeft, viewportBottomRight);
		graph.addViewportEdge(viewportBottomRight, viewportTopRight);
		graph.addViewportEdge(viewportTopRight, viewportTopLeft);
		graph.addViewportEdge(viewportTopLeft, viewportBottomLeft);

		graph.addContourEdge(innerBottomLeft, innerBottomRight, 0);
		graph.addContourEdge(innerBottomRight, innerTopRight, 0);
		graph.addContourEdge(innerTopRight, innerTopLeft, 0);
		graph.addContourEdge(innerTopLeft, innerBottomLeft, 0);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();
		graph.extractFaces(ViewportInfo.ofBounds(viewportTopLeft, viewportTopRight,
				viewportBottomLeft, viewportBottomRight, 0, 6, 0, 6));
		graph.identifyExteriorFace();

		assertTrue(graph.getLastExtractedBoundaryCycles().size() >= 3);
		assertEquals(2, graph.getLastCanonicalBoundaryCycles().size());
		assertEquals(2, graph.getFaces().size());
		long exteriorCount = graph.getFaces().stream().filter(Face::isExterior).count();
		assertEquals(1, exteriorCount);

		Face boundedFace = graph.getFaces().stream()
				.filter(face -> !face.isExterior())
				.findFirst()
				.orElseThrow();
		assertEquals(0, boundedFace.getHoleHalfEdgeIds().size());
		assertTrue(boundedFace.getOuterHalfEdgeId() >= 0);
	}

	@Test
	void buildBoundaryHierarchyShouldNotNestNearlyEqualAreaTwinCycles() {
		PlanarGraph graph = new PlanarGraph();

		int bottomLeft = graph.addVertex(0, 0);
		int bottomRight = graph.addVertex(2, 0);
		int topRight = graph.addVertex(2, 2);
		int topLeft = graph.addVertex(0, 2);

		int bottom = graph.addContourEdge(bottomLeft, bottomRight, 0);
		int right = graph.addContourEdge(bottomRight, topRight, 0);
		int top = graph.addContourEdge(topRight, topLeft, 0);
		int left = graph.addContourEdge(topLeft, bottomLeft, 0);

		List<Integer> positive = List.of(bottom, right, top, left);
		List<Integer> negative = List.of(
				graph.halfEdge(bottom).getTwinHalfEdgeId(),
				graph.halfEdge(left).getTwinHalfEdgeId(),
				graph.halfEdge(top).getTwinHalfEdgeId(),
				graph.halfEdge(right).getTwinHalfEdgeId());

		BoundaryCycle cycleA = new BoundaryCycle(2, positive, 4.0, new GPoint2D(1, 1));
		BoundaryCycle cycleB = new BoundaryCycle(3, negative, -4.0 - 1e-15, new GPoint2D(1, 1));

		BoundaryHierarchyBuilder builder = new BoundaryHierarchyBuilder(graph);
		builder.buildHierarchy(List.of(cycleA, cycleB));

		assertEquals(-1, cycleA.getParentId());
		assertEquals(-1, cycleB.getParentId());
	}

}
