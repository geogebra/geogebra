/* 
 * Convex hull algorithm - Library (Java)
 * 
 * Copyright (c) 2017 Project Nayuki
 * https://www.nayuki.io/page/convex-hull-algorithm
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (see COPYING.txt and COPYING.LESSER.txt).
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.geogebra.common.kernel.discrete.geom.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.discrete.geom.Point2D;

/**
 * Convex hull algorithm
 */
public final class ConvexHull {

	/**
	 * This algorithm runs in O(n log n) time.
	 * 
	 * @param points
	 *            points
	 * @return Returns a new list of points representing the convex hull of the
	 *         given set of points. The convex hull excludes collinear points.
	 * 
	 */
	public static List<Point2D> makeHull(List<Point2D> points) {
		List<Point2D> newPoints = new ArrayList<>(points);
		Collections.sort(newPoints);
		return makeHullPresorted(newPoints);
	}

	/**
	 * Runs in O(n) time.
	 * 
	 * @param points
	 *            points
	 * @return Returns the convex hull, assuming that each points[i] &lt;= points[i
	 *         + 1].
	 */
	public static List<Point2D> makeHullPresorted(List<Point2D> points) {
		if (points.size() <= 1) {
			return new ArrayList<>(points);
		}

		// Andrew's monotone chain algorithm. Positive y coordinates correspond
		// to "up"
		// as per the mathematical convention, instead of "down" as per the
		// computer
		// graphics convention. This doesn't affect the correctness of the
		// result.

		List<Point2D> upperHull = new ArrayList<>();
		for (Point2D p : points) {
			while (upperHull.size() >= 2) {
				Point2D q = upperHull.get(upperHull.size() - 1);
				Point2D r = upperHull.get(upperHull.size() - 2);
				if ((q.x - r.x) * (p.y - r.y) >= (q.y - r.y) * (p.x - r.x)) {
					upperHull.remove(upperHull.size() - 1);
				} else {
					break;
				}
			}
			upperHull.add(p);
		}
		upperHull.remove(upperHull.size() - 1);

		List<Point2D> lowerHull = new ArrayList<>();
		for (int i = points.size() - 1; i >= 0; i--) {
			Point2D p = points.get(i);
			while (lowerHull.size() >= 2) {
				Point2D q = lowerHull.get(lowerHull.size() - 1);
				Point2D r = lowerHull.get(lowerHull.size() - 2);
				if ((q.x - r.x) * (p.y - r.y) >= (q.y - r.y) * (p.x - r.x)) {
					lowerHull.remove(lowerHull.size() - 1);
				} else {
					break;
				}
			}
			lowerHull.add(p);
		}
		lowerHull.remove(lowerHull.size() - 1);

		if (!(upperHull.size() == 1 && upperHull.equals(lowerHull))) {
			upperHull.addAll(lowerHull);
		}
		return upperHull;
	}

}
