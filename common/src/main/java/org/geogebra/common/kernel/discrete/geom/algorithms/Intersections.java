/*
 * Copyright (c) 2010 Georgios Migdos <cyberpython@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.geogebra.common.kernel.discrete.geom.algorithms;

import java.util.Comparator;

import org.geogebra.common.kernel.discrete.geom.LineAndPointUtils;
import org.geogebra.common.kernel.discrete.geom.Point2D;
import org.geogebra.common.kernel.discrete.geom.algorithms.intersections.Segment2DEx;

/**
 *
 * @author cyberpython
 */
public class Intersections {

	public static class SegmentComparator implements Comparator<Segment2DEx> {

		private double x;

		public SegmentComparator() {
			x = 0;
		}

		public void setX(double x) {
			this.x = x;
		}

		@Override
		public int compare(Segment2DEx s1, Segment2DEx s2) {

			if ((s1 == null) && (s2 == null)) {
				return 0;
			}
			if (s1 == null) {
				return -1;
			}
			if (s2 == null) {
				return 1;
			}

			if (s1.equals(s2)) {
				return 0;
			}

			double maxY1 = Math.max(s1.getLeftEndPoint().getY(),
					s1.getRightEndPoint().getY());
			double maxY2 = Math.max(s2.getLeftEndPoint().getY(),
					s2.getRightEndPoint().getY());

			Segment2DEx sVert1 = new Segment2DEx(new Point2D(x, 0),
					new Point2D(x, maxY1 + 10));
			Segment2DEx sVert2 = new Segment2DEx(new Point2D(x, 0),
					new Point2D(x, maxY2 + 10));
			Point2D o1 = LineAndPointUtils.computeIntersectionPoint(s1, sVert1);
			Point2D o2 = LineAndPointUtils.computeIntersectionPoint(s2, sVert2);

			if (o1 == null || o2 == null) {
				return 0;
			}

			double y1 = o1.getY();
			double y2 = o2.getY();

			// findbugs CO_COMPARETO_INCORRECT_FLOATING
			return Double.compare(y1, y2);
		}
	}

}
