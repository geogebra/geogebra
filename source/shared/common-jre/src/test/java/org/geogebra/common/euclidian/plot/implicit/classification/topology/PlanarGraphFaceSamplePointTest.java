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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.MyPoint;
import org.junit.jupiter.api.Test;

class PlanarGraphFaceSamplePointTest {

	@Test
	void computeFaceSamplePointsShouldPopulateBoundedRectangleFace() {
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
		graph.computeFaceSamplePoints();

		Face boundedFace =
				graph.getFaces().stream().filter(face -> !face.isExterior()).findFirst().orElseThrow();

		MyPoint sample = boundedFace.getSamplePoint();
		assertNotNull(sample);
		assertTrue(
				sample.x > 0 && sample.x < 2, "Sample point should lie strictly inside rectangle in x");
		assertTrue(
				sample.y > 0 && sample.y < 1, "Sample point should lie strictly inside rectangle in y");
		assertTrue(
				clearance(graph, boundedFace, sample) >= 0.49,
				"Rectangle sample should be near the maximum-clearance center");
	}

	@Test
	void computeFaceSamplePointsShouldRespectHoleAndIslandBoundaries() {
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
		graph.computeFaceSamplePoints();

		Face holedFace = graph.getFaces().stream()
				.filter(face -> !face.isExterior() && face.getHoleHalfEdgeIds().size() == 1)
				.findFirst()
				.orElseThrow();
		Face islandFace = graph.getFaces().stream()
				.filter(face -> !face.isExterior() && face.getHoleHalfEdgeIds().isEmpty())
				.max(Comparator.comparingInt(Face::getId))
				.orElseThrow();

		MyPoint holedSample = holedFace.getSamplePoint();
		assertNotNull(holedSample);
		assertTrue(
				holedSample.x > 0 && holedSample.x < 6 && holedSample.y > 0 && holedSample.y < 6,
				"Holed-face sample should stay inside the outer boundary");
		assertTrue(
				holedSample.x < 1 || holedSample.x > 5 || holedSample.y < 1 || holedSample.y > 5,
				"Holed-face sample should stay outside the rectangular hole");
		assertTrue(
				clearance(graph, holedFace, holedSample) > 0.45,
				"Holed-face sample should maximize clearance in the ring");

		MyPoint islandSample = islandFace.getSamplePoint();
		assertNotNull(islandSample);
		assertTrue(
				islandSample.x > 2 && islandSample.x < 4 && islandSample.y > 2 && islandSample.y < 4,
				"Island-face sample should lie strictly inside the island rectangle");
	}

	@Test
	void computeFaceSamplePointsShouldPopulateThinRingFace() {
		PlanarGraph graph = new PlanarGraph();

		int outerBottomLeft = graph.addVertex(0, 0);
		int outerBottomRight = graph.addVertex(10, 0);
		int outerTopRight = graph.addVertex(10, 10);
		int outerTopLeft = graph.addVertex(0, 10);

		int holeBottomLeft = graph.addVertex(0.01, 0.01);
		int holeBottomRight = graph.addVertex(9.99, 0.01);
		int holeTopRight = graph.addVertex(9.99, 9.99);
		int holeTopLeft = graph.addVertex(0.01, 9.99);

		graph.addContourEdge(outerBottomLeft, outerBottomRight, 0);
		graph.addContourEdge(outerBottomRight, outerTopRight, 0);
		graph.addContourEdge(outerTopRight, outerTopLeft, 0);
		graph.addContourEdge(outerTopLeft, outerBottomLeft, 0);

		graph.addContourEdge(holeBottomLeft, holeBottomRight, 1);
		graph.addContourEdge(holeBottomRight, holeTopRight, 1);
		graph.addContourEdge(holeTopRight, holeTopLeft, 1);
		graph.addContourEdge(holeTopLeft, holeBottomLeft, 1);

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();
		graph.extractFaces(null);
		graph.identifyExteriorFace();
		graph.computeFaceSamplePoints();

		Face holedFace =
				graph.getFaces().stream().filter(face -> !face.isExterior()).findFirst().orElseThrow();

		MyPoint sample = holedFace.getSamplePoint();
		assertNotNull(sample);
		assertPointInsideFace(graph, holedFace, sample);
	}

	@Test
	void computeFaceSamplePointsShouldAvoidDumbbellNeck() {
		PlanarGraph graph = new PlanarGraph();
		int[] vertices = {
			graph.addVertex(-4, -1),
			graph.addVertex(-1, -1),
			graph.addVertex(-0.2, -0.2),
			graph.addVertex(0.2, -0.2),
			graph.addVertex(1, -1),
			graph.addVertex(4, -1),
			graph.addVertex(4, 1),
			graph.addVertex(1, 1),
			graph.addVertex(0.2, 0.2),
			graph.addVertex(-0.2, 0.2),
			graph.addVertex(-1, 1),
			graph.addVertex(-4, 1)
		};
		for (int i = 0; i < vertices.length; i++) {
			graph.addContourEdge(vertices[i], vertices[(i + 1) % vertices.length], 0);
		}

		graph.sortVertexOutgoingHalfEdges();
		graph.linkHalfEdges();
		graph.extractFaces(null);
		graph.identifyExteriorFace();
		graph.computeFaceSamplePoints();

		Face boundedFace =
				graph.getFaces().stream().filter(face -> !face.isExterior()).findFirst().orElseThrow();
		MyPoint sample = boundedFace.getSamplePoint();

		assertNotNull(sample);
		assertPointInsideFace(graph, boundedFace, sample);
		assertTrue(Math.abs(sample.x) > 1, "Dumbbell sample should prefer a lobe over the narrow neck");
		assertTrue(
				clearance(graph, boundedFace, sample) > 0.8,
				"Dumbbell sample should have lobe-level boundary clearance");
	}

	private static void assertPointInsideFace(PlanarGraph graph, Face face, MyPoint point) {
		GPoint2D sample = new GPoint2D(point.x, point.y);
		assertTrue(
				classifyPointInPolygon(graph, sample, graph.outerBoundaryOf(face)),
				"Sample point should be inside the face outer boundary");
		for (List<Integer> holeBoundary : graph.holeBoundariesOf(face)) {
			assertTrue(
					!classifyPointInPolygon(graph, sample, holeBoundary),
					"Sample point should stay outside every hole boundary");
		}
	}

	private static boolean classifyPointInPolygon(
			PlanarGraph graph, GPoint2D point, List<Integer> cycle) {
		boolean inside = false;
		int size = cycle.size();
		for (int i = 0, j = size - 1; i < size; j = i++) {
			double xi = graph.vertex(graph.halfEdge(cycle.get(i)).getOriginVertexId()).getX();
			double yi = graph.vertex(graph.halfEdge(cycle.get(i)).getOriginVertexId()).getY();
			double xj = graph.vertex(graph.halfEdge(cycle.get(j)).getOriginVertexId()).getX();
			double yj = graph.vertex(graph.halfEdge(cycle.get(j)).getOriginVertexId()).getY();
			boolean intersects = ((yi > point.y) != (yj > point.y))
					&& (point.x < (xj - xi) * (point.y - yi) / (yj - yi) + xi);
			if (intersects) {
				inside = !inside;
			}
		}
		return inside;
	}

	private static double clearance(PlanarGraph graph, Face face, MyPoint point) {
		GPoint2D sample = new GPoint2D(point.x, point.y);
		double minDistanceSquared =
				minDistanceSquared(graph, sample, graph.outerBoundaryOf(face), Double.POSITIVE_INFINITY);
		for (List<Integer> holeBoundary : graph.holeBoundariesOf(face)) {
			minDistanceSquared = minDistanceSquared(graph, sample, holeBoundary, minDistanceSquared);
		}
		return Math.sqrt(minDistanceSquared);
	}

	private static double minDistanceSquared(
			PlanarGraph graph, GPoint2D point, List<Integer> boundary, double currentMinimum) {
		double minDistanceSquared = currentMinimum;
		for (int halfEdgeId : boundary) {
			HalfEdge edge = graph.halfEdge(halfEdgeId);
			Vertex origin = graph.vertex(edge.getOriginVertexId());
			Vertex target = graph.vertex(edge.getTargetVertexId());
			minDistanceSquared =
					Math.min(minDistanceSquared, distanceSquaredToSegment(point, origin, target));
		}
		return minDistanceSquared;
	}

	private static double distanceSquaredToSegment(GPoint2D point, Vertex origin, Vertex target) {
		double dx = target.getX() - origin.getX();
		double dy = target.getY() - origin.getY();
		double lengthSquared = dx * dx + dy * dy;
		if (lengthSquared == 0) {
			return distanceSquared(point.x, point.y, origin.getX(), origin.getY());
		}
		double t = ((point.x - origin.getX()) * dx + (point.y - origin.getY()) * dy) / lengthSquared;
		double clampedT = Math.max(0, Math.min(1, t));
		double closestX = origin.getX() + clampedT * dx;
		double closestY = origin.getY() + clampedT * dy;
		return distanceSquared(point.x, point.y, closestX, closestY);
	}

	private static double distanceSquared(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return dx * dx + dy * dy;
	}
}
