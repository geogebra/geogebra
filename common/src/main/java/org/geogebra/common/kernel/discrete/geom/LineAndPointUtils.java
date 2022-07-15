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
public class LineAndPointUtils {

	private static double errorTolerance = 0.001;

	public static double getErrorTolerance() {
		return LineAndPointUtils.errorTolerance;
	}

	public static void setErrorTolerance(double errorTolerance) {
		LineAndPointUtils.errorTolerance = errorTolerance;
	}

	/**
	 * @param s1
	 *            segment
	 * @param s2
	 *            segment
	 * @return intersection
	 */
	public static Point2D computeIntersectionPoint(Segment2D s1, Segment2D s2) {

		if ((s1 == null) || (s2 == null)) {
			return null;
		}

		Point2D line1Point1 = s1.getStart();
		Point2D line1Point2 = s1.getEnd();
		Point2D line2Point1 = s2.getStart();
		Point2D line2Point2 = s2.getEnd();

		Point2D intersectionPoint = findIntersectionPointOfLines(line1Point1,
				line1Point2, line2Point1, line2Point2);

		if (isOnSegment(intersectionPoint, line1Point1, line1Point2)
				&& isOnSegment(intersectionPoint, line2Point1, line2Point2)) {
			return intersectionPoint;
		}

		return null;

	}

	/**
	 * @param p
	 *            point
	 * @param segmentPoint1
	 *            segment start point
	 * @param segmentPoint2
	 *            segment end point
	 * @return whether point is on segment
	 */
	public static boolean isOnSegment(Point2D p, Point2D segmentPoint1,
			Point2D segmentPoint2) {

		if (p == null) {
			return false;
		}

		Double x0 = p.getX();
		Double y0 = p.getY();

		Double x1 = segmentPoint1.getX();
		Double y1 = segmentPoint1.getY();
		Double x2 = segmentPoint2.getX();
		Double y2 = segmentPoint2.getY();

		Double maxX = Math.max(x1, x2);
		Double maxY = Math.max(y1, y2);
		Double minX = Math.min(x1, x2);
		Double minY = Math.min(y1, y2);

		return (x0 - minX >= -errorTolerance) && (x0 - maxX <= errorTolerance)
				&& (y0 - minY >= -errorTolerance)
				&& (y0 - maxY <= errorTolerance);
	}

	/**
	 * @param line1Point1
	 *            first line point
	 * @param line1Point2
	 *            first line point
	 * @param line2Point1
	 *            second line point
	 * @param line2Point2
	 *            second line point
	 * @return intersection of lines
	 */
	public static Point2D findIntersectionPointOfLines(Point2D line1Point1,
			Point2D line1Point2, Point2D line2Point1, Point2D line2Point2) {

		Double line1X1 = line1Point1.getX();
		Double line1Y1 = line1Point1.getY();
		Double line1X2 = line1Point2.getX();
		Double line1Y2 = line1Point2.getY();

		Double line2X1 = line2Point1.getX();
		Double line2Y1 = line2Point1.getY();
		Double line2X2 = line2Point2.getX();
		Double line2Y2 = line2Point2.getY();

		Double line1Lambda;
		Double line2Lambda;

		if (line1X2.compareTo(line1X1) == 0) {
			line1Lambda = null;
		} else {
			line1Lambda = (line1Y2 - line1Y1) / (line1X2 - line1X1);
			// System.out.println("line1 -> " + line1Lambda);
		}

		if (line2X2.compareTo(line2X1) == 0) {
			line2Lambda = null;
		} else {
			line2Lambda = (line2Y2 - line2Y1) / (line2X2 - line2X1);
			// System.out.println("line2 -> " + line2Lambda);
		}

		// Both lines are vertical:
		if ((line1Lambda == null) && (line2Lambda == null)) {
			return null;
		}

		// Lines are parallel to each other:
		if ((line1Lambda != null) && (line2Lambda != null)) {
			if (line1Lambda.compareTo(line2Lambda) == 0) {
				return null;
			}
		}

		Double x0;
		Double y0;

		if (line1Lambda == null) { // Line 1 is vertical

			x0 = line1X1;
			y0 = line2Lambda * (x0 - line2X1) + line2Y1;

		} else if (line2Lambda == null) { // Line 2 is vertical

			x0 = line2X1;
			y0 = line1Lambda * (x0 - line1X1) + line1Y1;

		} else { // Okay, let's find the intersection:

			x0 = ((line1Lambda * line1X1) - line1Y1 - (line2Lambda * line2X1)
					+ line2Y1) / (line1Lambda - line2Lambda);

			y0 = line1Lambda * (x0 - line1X1) + line1Y1;
		}

		return new Point2D(x0, y0);

	}

	/**
	 * @param p1
	 *            point
	 * @param p2
	 *            point
	 * @return whether points are equal
	 */
	public static boolean pointsAreEqual(Point2D p1, Point2D p2) {
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p2.getX();
		double y2 = p2.getY();

		return (Math.abs(y2 - y1) <= errorTolerance)
				&& (Math.abs(x2 - x1) <= errorTolerance);
	}

}
