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
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Planar-graph vertex with outgoing half-edge references.
 */
public final class Vertex {
	private final int id;
	private final double x;
	private final double y;
	private final List<Integer> outgoingHalfEdges = new ArrayList<>();

	/**
	 * @param id graph-local vertex id
	 * @param x x coordinate in world space
	 * @param y y coordinate in world space
	 */
	public Vertex(int id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public int getId() {
		return id;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	/**
	 * Adds an outgoing half-edge reference for topology linking.
	 * @param halfEdgeId outgoing half-edge id
	 */
	public void addOutgoingHalfEdge(int halfEdgeId) {
		outgoingHalfEdges.add(halfEdgeId);
	}

	void removeOutgoingHalfEdge(int halfEdgeId) {
		outgoingHalfEdges.removeIf(id -> id == halfEdgeId);
	}

	public List<Integer> getOutgoingHalfEdges() {
		return Collections.unmodifiableList(outgoingHalfEdges);
	}

	void sortOutgoingHalfEdges(Comparator<Integer> comparator) {
		outgoingHalfEdges.sort(comparator);
	}

	void removeInactiveOutgoingEdges(Predicate<? super Integer> filter) {
		outgoingHalfEdges.removeIf(filter);
	}

	/**
	 * @param id half-edge id to locate
	 * @return index of the outgoing half-edge, or {@code -1} if absent
	 */
	public int findOutgoingHalfEdge(Integer id) {
		return outgoingHalfEdges.indexOf(id);
	}
}
