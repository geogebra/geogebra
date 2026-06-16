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

package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.geogebra.common.awt.GPoint2D;

public class Smoothing {

	private static final double SCALE = 1.0;

	/**
	 * @param toTransform points to smooth
	 * @return smooth points
	 */
	public static List<? extends GPoint2D> transform(List<? extends GPoint2D> toTransform) {
		return getStrokeOutlinePoints(getStrokePoints(toTransform));
	}

	private static class StrokePoint {
		final GPoint2D point;
		GPoint2D dirVector;
		final double runningLength;

		public StrokePoint(GPoint2D point, GPoint2D direction, double length) {
			this.point = point;
			this.dirVector = direction;
			this.runningLength = length;
		}
	}

	private static List<StrokePoint> getStrokePoints(List<? extends  GPoint2D> pts) {
		double streamline = 0;

		// If we don't have any points, return an empty array.
		if (pts.size() < 3) {
			return List.of();
		}

		// Find the interpolation level between points.
		double t = 0.15 + (1 - streamline) * 0.85;

		// The strokePoints array will hold the points for the stroke.
		// Start it out with the first point, which needs no adjustment.
		List<StrokePoint> strokePoints = new ArrayList<>();
		strokePoints.add(new StrokePoint(pts.get(0), new GPoint2D(1, 1), 0));

		// A flag to see whether we've already reached out minimum length
		boolean hasReachedMinimumLength = false;

		// We use the runningLength to keep track of the total distance
		double runningLength = 0;

		// We're set this to the latest point, so we can use it to calculate
		// the distance and vector of the next point.
		StrokePoint prev = strokePoints.get(0);

		int max = pts.size() - 1;

		// Iterate through all of the points, creating StrokePoints.
		for (int i = 1; i < pts.size(); i++) {
			GPoint2D point =
					i == max
							? // If we're at the last point, and `options.last` is true,
							// then add the actual input point.
							pts.get(i)
							: // Otherwise, using the t calculated from the streamline
							// option, interpolate a new point between the previous
							// point the current point.
							lrp(prev.point, pts.get(i), t);

			// If the new point is the same as the previous point, skip ahead.
			if (Objects.equals(prev.point, point)) {
				continue;
			}

			// How far is the new point from the previous point?
    double distance = prev.point.distance(point);

			// Add this distance to the total "running length" of the line.
			runningLength += distance;

			// At the start of the line, we wait until the new point is a
			// certain distance away from the original point, to avoid noise
			if (i < max && !hasReachedMinimumLength) {
				if (runningLength < 3 / SCALE) {
					continue;
				}
				hasReachedMinimumLength = true;
			}
			// Create a new strokepoint (it will be the new "previous" one).
			prev = new StrokePoint(
					// The adjusted point
					point,

					// The vector from the current point to the previous point
					uni(sub(prev.point, point)),

					// The total distance so far
					runningLength
			);

			// Push it to the strokePoints array.
			strokePoints.add(prev);
		}

		// Set the vector of the first point to be the same as the second point.
		strokePoints.get(0).dirVector = strokePoints.get(1).dirVector;

		return strokePoints;
	}

	private static List<GPoint2D> getStrokeOutlinePoints(
			List<StrokePoint> points) {

		// We can't do anything with an empty array or a stroke with negative size.
		if (points.isEmpty()) {
			return List.of();
		}

		// The total length of the line
		double totalLength = points.get(points.size() - 1).runningLength;

		ArrayList<GPoint2D> leftPts = new ArrayList<>();

		// Previous vector
		GPoint2D prevVector = points.get(0).dirVector;

		// Previous left and right points
		GPoint2D pl = points.get(0).point;

		// Temporary left and right points
		GPoint2D tl = pl;

		// Keep track of whether the previous point is a sharp corner
		// ... so that we don't detect the same corner twice
		boolean isPrevPointSharpCorner = false;

		for (int i = 0; i < points.size(); i++) {
			GPoint2D point = points.get(i).point;
			GPoint2D vector = points.get(i).dirVector;
			double runningLength = points.get(i).runningLength;

			// Removes noise from the end of the line
			if (i < points.size() - 1 && totalLength - runningLength < 3 / SCALE) {
				continue;
			}

			GPoint2D nextVector = points.get(i < points.size() - 1 ? i + 1 : i).dirVector;
			double nextDpr = i < points.size() - 1 ? dpr(vector, nextVector) : 1.0;
			double prevDpr = dpr(vector, prevVector);
			boolean isPointSharpCorner = prevDpr < 0 && !isPrevPointSharpCorner;
			boolean isNextPointSharpCorner = Double.isFinite(nextDpr) && nextDpr < 0;

			if (isPointSharpCorner || isNextPointSharpCorner) {
				// Either tl or points.get(i) are sharp corners.
				// The sharp corner needs to be added *twice* to avoid Bezier curve effects
				leftPts.add(tl);
				pl = tl;
				if (isNextPointSharpCorner) {
						leftPts.add(points.get(i).point);
						leftPts.add(points.get(i).point);
					isPrevPointSharpCorner = true;
				} else {
					leftPts.add(tl);
				}
				continue;
			}

			isPrevPointSharpCorner = false;

			// Handle the last point
			if (i == points.size() - 1) {
				leftPts.add(point);
				continue;
			}

			tl = point;
			double minDistance = Math.pow(2 / SCALE, 2);
			if (i <= 1 || pl.distanceSq(tl) > minDistance) {
				leftPts.add(tl);
				pl = tl;
			}

			// Set variables for next iteration
			prevVector = vector;
		}

		return leftPts;
	}

	private static double dpr(GPoint2D a, GPoint2D b) {
		return a.x * b.x + a.y * b.y;
	}

	static GPoint2D lrp(GPoint2D A, GPoint2D B, double t) {
		return add(A, mul(sub(B, A), t));
	}

	static GPoint2D mul(GPoint2D pt, double t) {
		return new GPoint2D(pt.x * t, pt.y * t);
	}

	static GPoint2D sub(GPoint2D pt, GPoint2D other) {
		return new GPoint2D(pt.x - other.x, pt.y - other.y);
	}

	static GPoint2D add(GPoint2D pt, GPoint2D other) {
		return new GPoint2D(pt.x + other.x, pt.y + other.y);
	}

	static GPoint2D uni(GPoint2D pt) {
		double norm = pt.distance(0, 0);
		return new GPoint2D(pt.x / norm, pt.y / norm);
	}
}
