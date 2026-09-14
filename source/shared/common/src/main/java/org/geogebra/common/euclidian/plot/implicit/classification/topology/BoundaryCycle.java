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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;

/**
 * One closed boundary cycle extracted from the linked planar graph.
 * <p>
 * Stores the ordered half-edge ids of the cycle together with geometric and
 * hierarchy metadata used later for boundary nesting, hole assignment, and
 * face construction.
 * </p>
 */
public final class BoundaryCycle {
	private final int id;
	private final List<Integer> halfEdgeIds;
	private final int startHalfEdgeId;
	private final double signedArea;
	private final double absArea;
	private final GPoint2D containmentProbePoint;
	private int parentId = -1;
	private int depth = -1;
	private final List<Integer> childIds = new ArrayList<>();
	private double[] xCoordinates;
	private double[] yCoordinates;
	private PlanarGeometry.BoundingBox boundingBox;

	/**
	 * @param id cycle id
	 * @param halfEdgeIds ordered boundary half-edge ids
	 * @param signedArea signed area of the boundary walk
	 * @param containmentProbePoint lightweight probe used for containment checks
	 */
	public BoundaryCycle(int id, List<Integer> halfEdgeIds, double signedArea,
			GPoint2D containmentProbePoint) {
		this.id = id;
		this.halfEdgeIds = new ArrayList<>(halfEdgeIds);
		this.startHalfEdgeId = halfEdgeIds.get(0);
		this.signedArea = signedArea;
		this.absArea = Math.abs(signedArea);
		this.containmentProbePoint = containmentProbePoint;
	}

	public int getId() {
		return id;
	}

	/**
	 * @return ordered half-edge ids forming this closed boundary cycle
	 */
	public List<Integer> getHalfEdgeIds() {
		return Collections.unmodifiableList(halfEdgeIds);
	}

	/**
	 * @return representative half-edge id of the cycle
	 */
	public int getStartHalfEdgeId() {
		return startHalfEdgeId;
	}

	/**
	 * @return signed area of this cycle in traversal order
	 */
	public double getSignedArea() {
		return signedArea;
	}

	/**
	 * @return absolute area of this cycle
	 */
	public double getAbsArea() {
		return absArea;
	}

	/**
	 * @return lightweight probe point associated with this boundary cycle
	 */
	public GPoint2D getContainmentProbePoint() {
		return containmentProbePoint;
	}

	/**
	 * @return parent cycle id, or {@code -1} if this cycle is a root
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @param parentId parent cycle id, or {@code -1} for a root cycle
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return nesting depth in the boundary forest, or {@code -1} if not assigned yet
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth nesting depth in the boundary forest
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * @return ids of immediate child cycles in the nesting forest
	 */
	public List<Integer> getChildIds() {
		return Collections.unmodifiableList(childIds);
	}

	/**
	 * @param childId id of a nested child cycle
	 */
	public void addChildId(int childId) {
		childIds.add(childId);
	}

	void ensureGeometry(PlanarGraph graph) {
		if (xCoordinates != null) {
			return;
		}
		xCoordinates = new double[halfEdgeIds.size()];
		yCoordinates = new double[halfEdgeIds.size()];
		boundingBox = new PlanarGeometry.BoundingBox();
		for (int i = 0; i < halfEdgeIds.size(); i++) {
			Vertex vertex = graph.vertex(graph.halfEdge(halfEdgeIds.get(i)).getOriginVertexId());
			xCoordinates[i] = vertex.getX();
			yCoordinates[i] = vertex.getY();
			boundingBox.include(vertex.getX(), vertex.getY());
		}
	}

	PlanarGeometry.BoundingBox getBoundingBox(PlanarGraph graph) {
		ensureGeometry(graph);
		return boundingBox;
	}

	double[] getXCoordinates(PlanarGraph graph) {
		ensureGeometry(graph);
		return xCoordinates;
	}

	double[] getYCoordinates(PlanarGraph graph) {
		ensureGeometry(graph);
		return yCoordinates;
	}

	int size() {
		return halfEdgeIds.size();
	}

}
