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

import java.util.Arrays;

import org.geogebra.common.kernel.MyPoint;

final class BoundarySegment {
	final int fragmentId;
	final int contourId;
	final int segmentIndex;
	final int segmentCount;
	final boolean closed;
	final double startX;
	final double startY;
	final double endX;
	final double endY;
	final double dx;
	final double dy;
	final double minX;
	final double maxX;
	final double minY;
	final double maxY;
	private double[] params = new double[8];
	private int paramCount;

	BoundarySegment(
			int fragmentId,
			int contourId,
			int segmentIndex,
			int segmentCount,
			boolean closed,
			MyPoint start,
			MyPoint end) {
		this.fragmentId = fragmentId;
		this.contourId = contourId;
		this.segmentIndex = segmentIndex;
		this.segmentCount = segmentCount;
		this.closed = closed;
		this.startX = start.x;
		this.startY = start.y;
		this.endX = end.x;
		this.endY = end.y;
		this.dx = endX - startX;
		this.dy = endY - startY;
		this.minX = Math.min(startX, endX);
		this.maxX = Math.max(startX, endX);
		this.minY = Math.min(startY, endY);
		this.maxY = Math.max(startY, endY);
		params[paramCount++] = 0.0;
		params[paramCount++] = 1.0;
	}

	void addParam(double t) {
		if (paramCount == params.length) {
			params = Arrays.copyOf(params, params.length * 2);
		}
		params[paramCount++] = t;
	}

	void normalize(double eps) {
		for (int i = 0; i < paramCount; i++) {
			params[i] = clamp(params[i], eps);
		}
		Arrays.sort(params, 0, paramCount);
		int write = 0;
		for (int i = 0; i < paramCount; i++) {
			double t = params[i];
			if (write == 0 || Math.abs(t - params[write - 1]) > eps) {
				params[write++] = t;
			}
		}
		paramCount = write;
	}

	boolean isAdjacent(BoundarySegment other) {
		if (fragmentId != other.fragmentId) {
			return false;
		}
		int diff = Math.abs(segmentIndex - other.segmentIndex);
		return diff == 1 || (closed && segmentCount >= 2 && diff == segmentCount - 1);
	}

	private static double clamp(double t, double eps) {
		if (Math.abs(t) <= eps) {
			return 0.0;
		}
		if (Math.abs(1.0 - t) <= eps) {
			return 1.0;
		}
		return Math.max(0.0, Math.min(1.0, t));
	}

	int paramCount() {
		return paramCount;
	}

	double paramAt(int index) {
		return params[index];
	}

	double xAt(double t) {
		return startX + t * dx;
	}

	double yAt(double t) {
		return startY + t * dy;
	}
}
