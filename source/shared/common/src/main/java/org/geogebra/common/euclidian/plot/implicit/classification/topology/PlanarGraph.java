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

import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.extractFaceBoundary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.implicit.classification.ViewportInfo;
import org.geogebra.common.kernel.Kernel;

/**
 * DCEL-like planar topology container used during implicit-region classification.
 * <p>
 * It stores graph vertices, directed half-edges, and faces. Both viewport and
 * contour boundaries are represented by pairs of twin half-edges.
 * </p>
 */
public final class PlanarGraph {
	static final double GEOMETRY_EPSILON = Kernel.MAX_PRECISION;
	static final double AREA_RELATIVE_TOLERANCE = 1e-9;

	private final List<Vertex> vertices = new ArrayList<>();
	private final List<HalfEdge> halfEdges = new ArrayList<>();

	private final List<Face> faces = new ArrayList<>();

	private final FaceBuilder faceBuilder;

	enum Containment {
		INSIDE,
		OUTSIDE,
		BOUNDARY;
	}

	// ---------------------------------------------------------------------
	// Graph storage and linkage
	// ---------------------------------------------------------------------
	public PlanarGraph() {
		faceBuilder = new FaceBuilder(this);
	}

	/**
	 * Adds a vertex from an existing point.
	 * @param point vertex coordinates
	 * @return id of the created vertex
	 */
	public int addVertex(GPoint2D point) {
		return addVertex(point.x, point.y);
	}

	/**
	 * Adds a vertex at the given coordinates.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return id of the created vertex
	 */
	public int addVertex(double x, double y) {
		int id = vertices.size();
		vertices.add(new Vertex(id, x, y));
		return id;
	}

	/**
	 * Adds one directed half-edge. Undirected edges are built as twin pairs on top
	 * of this primitive.
	 * @param originVertexId origin vertex id
	 * @param targetVertexId target vertex id
	 * @param segmentKind source kind of the edge
	 * @param sourceContourId contour id for contour edges, {@code -1} for viewport edges
	 * @return id of the created half-edge
	 */
	int addHalfEdge(int originVertexId, int targetVertexId,
			SegmentKind segmentKind, int sourceContourId) {
		int id = halfEdges.size();
		halfEdges.add(new HalfEdge(id, originVertexId, targetVertexId, segmentKind,
				sourceContourId));
		vertices.get(originVertexId).addOutgoingHalfEdge(id);
		return id;
	}

	/**
	 * Adds a face placeholder.
	 * @return id of the created face
	 */
	public int addFace() {
		int id = faces.size();
		faces.add(new Face(id));
		return id;
	}

	/**
	 * @param id vertex id
	 * @return vertex with the given id
	 */
	public Vertex vertex(int id) {
		return vertices.get(id);
	}

	/**
	 * @param id half-edge id
	 * @return half-edge with the given id
	 */
	public HalfEdge halfEdge(int id) {
		return halfEdges.get(id);
	}

	/**
	 * @param id face id
	 * @return face with the given id
	 */
	public Face face(int id) {
		return faces.get(id);
	}

	/**
	 * @return immutable view of all vertices
	 */
	public List<Vertex> getVertices() {
		return Collections.unmodifiableList(vertices);
	}

	/**
	 * @return immutable view of all half-edges
	 */
	public List<HalfEdge> getHalfEdges() {
		return Collections.unmodifiableList(halfEdges);
	}

	/**
	 * @return the number of all halfEdges in graph
	 */
	int halfEdgeCount() {
		return halfEdges.size();
	}

	/**
	 * @return immutable view of all faces
	 */
	public List<Face> getFaces() {
		return Collections.unmodifiableList(faces);
	}

	public List<BoundaryCycle> getLastExtractedBoundaryCycles() {
		return faceBuilder.getLastExtractedBoundaryCycles();
	}

	public List<BoundaryCycle> getLastCanonicalBoundaryCycles() {
		return faceBuilder.getLastCanonicalBoundaryCycles();
	}

	/**
	 * Adds one undirected viewport edge as a pair of twin half-edges.
	 * @param id1 first endpoint vertex id
	 * @param id2 second endpoint vertex id
	 * @return id of one of the created half-edges
	 */
	public int addViewportEdge(int id1, int id2) {
		validateId(id1);
		validateId(id2);

		int halfEdge1 = addHalfEdge(id1, id2, SegmentKind.VIEWPORT, -1);
		int halfEdge2 = addHalfEdge(id2, id1, SegmentKind.VIEWPORT, -1);
		halfEdge(halfEdge1).setTwinHalfEdgeId(halfEdge2);
		halfEdge(halfEdge2).setTwinHalfEdgeId(halfEdge1);
		return halfEdge1;
	}

	/**
	 * Adds one undirected contour edge as a pair of twin half-edges.
	 * @param id1 first endpoint vertex id
	 * @param id2 second endpoint vertex id
	 * @param contourId source contour id
	 * @return id of one of the created half-edges
	 */
	public int addContourEdge(int id1, int id2, int contourId) {
		validateId(id1);
		validateId(id2);

		int halfEdge1 = addHalfEdge(id1, id2, SegmentKind.CONTOUR, contourId);
		int halfEdge2 = addHalfEdge(id2, id1, SegmentKind.CONTOUR, contourId);
		halfEdge(halfEdge1).setTwinHalfEdgeId(halfEdge2);
		halfEdge(halfEdge2).setTwinHalfEdgeId(halfEdge1);
		return halfEdge1;
	}

	private void validateId(int id) {
		if (id < 0 || id >= vertices.size()) {
			throw new IllegalArgumentException("vertex id " + id + " is invalid");
		}
	}

	/**
	 * Removes all vertices, edges, and faces.
	 */
	public void clear() {
		vertices.clear();
		halfEdges.clear();
		faces.clear();
		faceBuilder.clear();
	}

	/**
	 * Creates filled-region faces plus one exterior face from canonical boundary cycles.
	 * @throws IllegalStateException if boundary extraction fails because the current
	 * half-edge linkage does not form valid closed cycles
	 */
	public void extractFaces(ViewportInfo viewportInfo) {
		faceBuilder.extract(viewportInfo);
	}

	/**
	 * Finds the first vertex whose coordinates are within {@code delta} from the
	 * given point.
	 * @param point point to search for
	 * @param delta coordinate tolerance for vertex reuse
	 * @return matching vertex id, or {@code -1} if none was found
	 */
	public int findVertex(GPoint2D point, double delta) {
		double deltaSquared = delta * delta;
		for (Vertex vertex : vertices) {
			double dx = vertex.getX() - point.x;
			double dy = vertex.getY() - point.y;
			if (dx * dx + dy * dy <= deltaSquared) {
				return vertex.getId();
			}
		}
		return -1;
	}

	/**
	 * Sorts each vertex's active outgoing half-edges by direction.
	 */
	public void sortVertexOutgoingHalfEdges() {
		for (Vertex vertex : vertices) {
			vertex.removeInactiveOutgoingEdges(i -> !halfEdge(i).isActive());
			vertex.sortOutgoingHalfEdges(this::compareHalfEdges);
		}
	}

	private int compareHalfEdges(Integer idx1, Integer idx2) {
		double angle1 = getHalfEdgeAngle(idx1);
		double angle2 = getHalfEdgeAngle(idx2);
		return Double.compare(angle1, angle2);
	}

	private double getHalfEdgeAngle(Integer idx) {
		HalfEdge halfEdge = halfEdge(idx);
		Vertex origin = vertex(halfEdge.getOriginVertexId());
		Vertex target = vertex(halfEdge.getTargetVertexId());
		double dx = target.getX() - origin.getX();
		double dy = target.getY() - origin.getY();
		return Math.atan2(dy, dx);
	}

	/**
	 * Links half-edges into face-walk successor chains.
	 * <p>
	 * Outgoing half-edges are assumed to be sorted by increasing {@code atan2(dy, dx)}.
	 * For a half-edge {@code e = u -> v}, the successor is chosen at {@code v} by locating
	 * {@code twin(e) = v -> u} in that cyclic order and taking the next outgoing edge
	 * ({@code +1} with wrap-around). This yields the counterclockwise boundary walk for
	 * the face lying to the left of {@code e}.
	 * </p>
	 */
	public void linkHalfEdges() {
		for (HalfEdge halfEdge : halfEdges) {
			if (!halfEdge.isActive()) {
				continue;
			}

			Vertex target = vertex(halfEdge.getTargetVertexId());
			int twinIdInTarget = target.findOutgoingHalfEdge(halfEdge.getTwinHalfEdgeId());
			if (twinIdInTarget < 0) {
				continue;
			}
			List<Integer> outgoing = target.getOutgoingHalfEdges();
			int size = outgoing.size();
			int nextPos = (twinIdInTarget + 1) % size;
			int nextId = outgoing.get(nextPos);
			halfEdge.setNextHalfEdgeId(nextId);
			halfEdge(nextId).setPrevHalfEdgeId(halfEdge.getId());
		}
	}

	void clearFaces() {
		faces.clear();
		for (HalfEdge halfEdge : halfEdges) {
			halfEdge.setFaceId(-1);
		}
	}

	/**
	 * @return whether the graph has non-empty storage, valid face markers, and valid links
	 */
	public boolean isValid() {
		return !isEmpty() && hasValidFaces() && hasValidLinks();
	}

	/**
	 * @return whether the graph has no vertices, half-edges, or faces
	 */
	public boolean isEmpty() {
		return vertices.isEmpty() && halfEdges.isEmpty() && faces.isEmpty();
	}

	/**
	 * @return whether face extraction produced at least one face and one exterior face
	 */
	public boolean hasValidFaces() {
		boolean hasExactlyOneExteriorFace =
				faces.stream().filter(Face::isExterior).count() == 1;
		return !faces.isEmpty() && hasExactlyOneExteriorFace;
	}

	/**
	 * @return whether all active half-edges have valid face-walk links
	 */
	public boolean hasValidLinks() {
		for (HalfEdge halfEdge : halfEdges) {
			if (halfEdge.isActive() && halfEdge.inInvalid()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Soft-removes the viewport edge between the given vertices by marking matching
	 * viewport half-edges inactive and removing them from outgoing lists.
	 * @param start one endpoint vertex id
	 * @param end the other endpoint vertex id
	 */
	public void removeViewportEdge(int start, int end) {
		for (HalfEdge edge : halfEdges) {
			if (!edge.isActive() || !edge.isViewportEdge()) {
				continue;
			}
			boolean matchForward =
					edge.getOriginVertexId() == start && edge.getTargetVertexId() == end;
			boolean matchBackward =
					edge.getOriginVertexId() == end && edge.getTargetVertexId() == start;
			if (matchForward || matchBackward) {
				edge.setActive(false);
				vertices.get(edge.getOriginVertexId()).removeOutgoingHalfEdge(edge.getId());
			}
		}
	}

	/**
	 * @param face face whose outer boundary should be returned
	 * @return half-edge ids forming the outer boundary of the face
	 */
	public List<Integer> outerBoundaryOf(Face face) {
		if (face.getOuterHalfEdgeId() == -1) {
			throw new IllegalStateException("Face " + face.getId() + " has no outer boundary");
		}
		return extractFaceBoundary(this, face.getOuterHalfEdgeId(), new HashSet<>());
	}

	/**
	 * @param face face whose hole boundaries should be returned
	 * @return half-edge id cycles for all holes of the face
	 */
	public List<List<Integer>> holeBoundariesOf(Face face) {
		List<List<Integer>> boundaries = new ArrayList<>(face.getHoleHalfEdgeIds().size());
		for (int holeStartId : face.getHoleHalfEdgeIds()) {
			boundaries.add(extractFaceBoundary(this, holeStartId, new HashSet<>()));
		}
		return boundaries;
	}

	/**
	 * @return whether the point belongs to a face bounded by the given outer and hole cycles
	 */
	public boolean isPointInsideFace(GPoint2D candidate, List<Integer> outerBoundary,
			List<List<Integer>> holeBoundaries) {
		return faceBuilder.isPointInsideFace(candidate, outerBoundary, holeBoundaries);
	}

	/**
	 * Checks that the graph contains exactly one exterior face.
	 */
	public void identifyExteriorFace() {
		faceBuilder.identifyExteriorFace();
	}

	/**
	 * Assigns predicate sample points to all non-exterior faces.
	 */
	public void computeFaceSamplePoints() {
		faceBuilder.computeFaceSamplePoints();
	}
}
