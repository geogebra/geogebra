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

package org.geogebra.common.kernel.discrete.geom.algorithms.intersections;

import org.geogebra.common.kernel.discrete.geom.Point2D;

/**
 *
 * @author cyberpython
 */
public class SegmentsIntersection extends Intersection {
	private Segment2DEx s1;
	private Segment2DEx s2;

	/**
	 * @param p0
	 *            intersection point
	 * @param s1
	 *            segment
	 * @param s2
	 *            segment
	 */
	public SegmentsIntersection(Point2D p0, Segment2DEx s1, Segment2DEx s2) {
		super(p0);
		this.s1 = s1;
		this.s2 = s2;
	}

	public Segment2DEx getSegment1() {
		return this.s1;
	}

	public Segment2DEx getSegment2() {
		return this.s2;
	}

	@Override
	public String toString() {
		return s1.toString() + " CROSSES " + s2.toString() + " AT: "
				+ getPoint().toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SegmentsIntersection) {
			SegmentsIntersection s = (SegmentsIntersection) o;
			if (this.getPoint().equals(s.getPoint())) {
				return (s1.equals(s.s1) && s2.equals(s.s2))
						|| (s1.equals(s.s2) && s2.equals(s.s1));
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + (this.s1 != null ? this.s1.hashCode() : 0);
		hash = 71 * hash + (this.s2 != null ? this.s2.hashCode() : 0);
		hash = 71 * hash
				+ (this.getPoint() != null ? this.getPoint().hashCode() : 0);
		return hash;
	}

}
