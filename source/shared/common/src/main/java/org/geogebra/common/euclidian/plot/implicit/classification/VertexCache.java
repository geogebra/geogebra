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

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;

final class VertexCache {
	private final PlanarGraph graph;
	private final double mergeTolerance;

	private final Map<Long, IntBucket> buckets = new HashMap<>();

	VertexCache(PlanarGraph graph, double mergeTolerance) {
		this.graph = graph;
		this.mergeTolerance = mergeTolerance;
	}

	int find(double x, double y) {
		if (mergeTolerance <= 0) {
			return findInBucket(vertexBucketKey(x, y), x, y, this.mergeTolerance);
		}
		long qx = quantizeVertexCoordinate(x, mergeTolerance);
		long qy = quantizeVertexCoordinate(y, mergeTolerance);
		for (long dx = -1; dx <= 1; dx++) {
			for (long dy = -1; dy <= 1; dy++) {
				int vertexId = findInBucket(vertexBucketKey(qx + dx, qy + dy), x, y, mergeTolerance);
				if (vertexId > -1) {
					return vertexId;
				}
			}
		}
		return -1;
	}

	private int findInBucket(long key, double x, double y, double mergeTolerance) {
		IntBucket bucket = buckets.get(key);
		if (bucket != null) {
			double mergeToleranceSq = mergeTolerance * mergeTolerance;
			for (int i = 0; i < bucket.size(); i++) {
				int vertexId = bucket.get(i);
				double dx = graph.vertex(vertexId).getX() - x;
				double dy = graph.vertex(vertexId).getY() - y;
				if (dx * dx + dy * dy <= mergeToleranceSq) {
					return vertexId;
				}
			}
		}
		return -1;
	}

	void put(int vertexId, double x, double y) {
		long key = vertexBucketKey(x, y, mergeTolerance);
		IntBucket bucket = buckets.get(key);
		if (bucket == null) {
			bucket = new IntBucket();
			buckets.put(key, bucket);
		}
		bucket.add(vertexId);
	}

	void clearBuckets() {
		buckets.clear();
	}

	private long vertexBucketKey(double x, double y, double tolerance) {
		if (tolerance <= 0) {
			return vertexBucketKey(x, y);
		}
		return vertexBucketKey(
				quantizeVertexCoordinate(x, tolerance), quantizeVertexCoordinate(y, tolerance));
	}

	private long vertexBucketKey(double x, double y) {
		long xBits = Double.doubleToLongBits(x);
		long yBits = Double.doubleToLongBits(y);
		return xBits * 31 + yBits;
	}

	private long quantizeVertexCoordinate(double value, double tolerance) {
		return Math.round(value / tolerance);
	}

	private long vertexBucketKey(long qx, long qy) {
		long hash = qx;
		hash ^= qy + 0x9E3779B97F4A7C15L + (hash << 6) + (hash >>> 2);
		return hash;
	}

	int findOrAddToGraph(double x, double y) {
		int cachedVertex = find(x, y);
		if (cachedVertex > -1) {
			return cachedVertex;
		}
		int vertexId = graph.addVertex(x, y);
		put(vertexId, x, y);
		return vertexId;
	}
}
