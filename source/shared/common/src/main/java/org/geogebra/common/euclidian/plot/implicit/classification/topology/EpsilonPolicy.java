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

import org.geogebra.common.kernel.Kernel;

/**
 * Numeric tolerances used while building implicit-curve classification topology.
 * <p>
 * Typical usage:
 * {@code snap} for endpoint snapping and parameter validity,
 * {@code intersection} for geometric intersection/overlap tests,
 * {@code vertexMerge} for deciding whether two geometric points should reuse one graph vertex,
 * {@code angle} for later angular ordering around vertices.
 * </p>
 */
public final class EpsilonPolicy {
	private final double snap;
	private final double intersection;
	private final double vertexMerge;
	private final double angle;

	/**
	 * @param snap tolerance for snapping values near canonical endpoints such as 0 and 1
	 * @param intersection tolerance for geometric intersection and overlap predicates
	 * @param vertexMerge tolerance for merging near-coincident graph vertices
	 * @param angle tolerance for angular comparisons during half-edge ordering
	 */
	public EpsilonPolicy(double snap, double intersection, double vertexMerge, double angle) {
		this.snap = snap;
		this.intersection = intersection;
		this.vertexMerge = vertexMerge;
		this.angle = angle;
	}

	/**
	 * @return snapping tolerance for normalized parameters and near-coincident endpoints
	 */
	public double getSnap() {
		return snap;
	}

	/**
	 * @return tolerance for geometric intersection and bounding-box overlap checks
	 */
	public double getIntersection() {
		return intersection;
	}

	/**
	 * @return tolerance for reusing an existing vertex when a new point is geometrically coincident
	 */
	public double getVertexMerge() {
		return vertexMerge;
	}

	/**
	 * @return tolerance for angular ordering/comparison around graph vertices
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @return default tolerances tuned for current implicit-curve topology construction
	 */
	public static EpsilonPolicy defaults() {
		return new EpsilonPolicy(
				1e-10, Kernel.MAX_PRECISION, Kernel.MAX_PRECISION, Kernel.MAX_PRECISION);
	}
}
