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

package org.geogebra.common.kernel.discrete.geom;

/**
 *
 * @author cyberpython
 */
public class Segment2D {

	private Point2D p0;
	private Point2D p1;

	/**
	 * @param p0
	 *            start point
	 * @param p1
	 *            end point
	 */
	public Segment2D(Point2D p0, Point2D p1) {
		this.p0 = p0;
		this.p1 = p1;
	}

	/**
	 * @param p0
	 *            start point
	 * @param p1
	 *            end point
	 */
	public void setPoints(Point2D p0, Point2D p1) {
		this.p0 = p0;
		this.p1 = p1;
	}

	/**
	 * @return start point
	 */
	public Point2D getStart() {
		return this.p0;
	}

	/**
	 * @return end point
	 */
	public Point2D getEnd() {
		return this.p1;
	}

	@Override
	public String toString() {
		return p0.toString() + " -> " + p1.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Segment2D) {
			if (this.hashCode() == o.hashCode()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 23 * hash + (this.p0 != null ? this.p0.hashCode() : 0);
		hash = 23 * hash + (this.p1 != null ? this.p1.hashCode() : 0);
		return hash;
	}

}
