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

/**
 * Directed edge in DCEL-style topology.
 */
public final class HalfEdge {
	private final int id;
	private final int originVertexId;
	private final int targetVertexId;
	private final SegmentKind segmentKind;
	private final int sourceContourId;
	private int twinHalfEdgeId = -1;
	private int nextHalfEdgeId = -1;
	private int prevHalfEdgeId = -1;
	private int faceId = -1;
	private boolean active = true;

	/**
	 * @param id graph-local half-edge id
	 * @param originVertexId origin vertex id
	 * @param targetVertexId target vertex id
	 * @param segmentKind source kind of the edge
	 * @param sourceContourId contour id for contour edges, {@code -1} for viewport edges
	 */
	public HalfEdge(
			int id,
			int originVertexId,
			int targetVertexId,
			SegmentKind segmentKind,
			int sourceContourId) {
		this.id = id;
		this.originVertexId = originVertexId;
		this.targetVertexId = targetVertexId;
		this.segmentKind = segmentKind;
		this.sourceContourId = sourceContourId;
	}

	public int getId() {
		return id;
	}

	public int getOriginVertexId() {
		return originVertexId;
	}

	public int getTargetVertexId() {
		return targetVertexId;
	}

	public SegmentKind getSegmentKind() {
		return segmentKind;
	}

	public int getSourceContourId() {
		return sourceContourId;
	}

	public int getTwinHalfEdgeId() {
		return twinHalfEdgeId;
	}

	public void setTwinHalfEdgeId(int twinHalfEdgeId) {
		this.twinHalfEdgeId = twinHalfEdgeId;
	}

	public int getNextHalfEdgeId() {
		return nextHalfEdgeId;
	}

	public void setNextHalfEdgeId(int nextHalfEdgeId) {
		this.nextHalfEdgeId = nextHalfEdgeId;
	}

	public int getPrevHalfEdgeId() {
		return prevHalfEdgeId;
	}

	public void setPrevHalfEdgeId(int prevHalfEdgeId) {
		this.prevHalfEdgeId = prevHalfEdgeId;
	}

	public int getFaceId() {
		return faceId;
	}

	public void setFaceId(int faceId) {
		this.faceId = faceId;
	}

	public boolean isContourEdge() {
		return segmentKind == SegmentKind.CONTOUR;
	}

	public boolean isViewportEdge() {
		return segmentKind == SegmentKind.VIEWPORT;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return whether this active edge is missing face-walk links
	 */
	public boolean inInvalid() {
		return prevHalfEdgeId == -1 || nextHalfEdgeId == -1;
	}
}
