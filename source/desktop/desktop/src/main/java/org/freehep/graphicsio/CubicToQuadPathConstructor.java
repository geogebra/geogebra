// Copyright 2001-2004 FreeHEP
package org.freehep.graphicsio;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Implements the Cubic Bezier Curve PathConstructor functionality in terms of
 * Quadratic Bezier Curves
 * 
 * Uses the same algorithm published as ActionScript (SWF) by Robert Penner:
 * 
 * ========================== Cubic Bezier Drawing v1.1
 * ========================== recursive quadratic approximation with adjustable
 * tolerance
 * 
 * March 4, 2004
 * 
 * Robert Penner www.robertpenner.com/tools/bezier_cubic.zip file:
 * bezier_draw_cubic.as ==========================
 * 
 * @author Mark Donszelmann
 * @version $Id: CubicToQuadPathConstructor.java,v 1.6 2009-08-17 21:44:45
 *          murkle Exp $
 */
public abstract class CubicToQuadPathConstructor
		extends AbstractPathConstructor {

	private double resolutionSq;

	protected CubicToQuadPathConstructor(double resolution) {
		super();
		resolutionSq = resolution * resolution;
	}

	@Override
	public void move(double x, double y) throws IOException {
		currentX = x;
		currentY = y;
	}

	@Override
	public void line(double x, double y) throws IOException {
		currentX = x;
		currentY = y;
	}

	@Override
	public void cubic(double x1, double y1, double x2, double y2, double x3,
			double y3) throws IOException {
		quadratify(new Point2D.Double(currentX, currentY),
				new Point2D.Double(x1, y1), new Point2D.Double(x2, y2),
				new Point2D.Double(x3, y3));

		currentX = x3;
		currentY = y3;
	}

	@Override
	public void closePath(double x0, double y0) throws IOException {
		currentX = 0;
		currentY = 0;
	}

	public static Point2D intersect(Point2D p1, Point2D p2, Point2D p3,
			Point2D p4) {

		double dx1 = p2.getX() - p1.getX();
		double dx2 = p3.getX() - p4.getX();

		// line are vertical
		if ((dx1 == 0) && (dx2 == 0)) {
			return null;
		}

		double dy1 = p2.getY() - p1.getY();
		double dy2 = p3.getY() - p4.getY();

		// line are horizontal
		if ((dy1 == 0) && (dy2 == 0)) {
			return null;
		}

		double m1 = (p2.getY() - p1.getY()) / dx1;
		double m2 = (p3.getY() - p4.getY()) / dx2;

		if (dx1 == 0) {
			// infinity
			return new Point2D.Double(p1.getX(),
					m2 * (p1.getX() - p4.getX()) + p4.getY());
		} else if (dx2 == 0) {
			// infinity
			return new Point2D.Double(p4.getX(),
					m1 * (p4.getX() - p1.getX()) + p1.getY());
		}

		// lines are parallel
		if (m1 == m2) {
			return null;
		}

		double x = (-m2 * p4.getX() + p4.getY() + m1 * p1.getX() - p1.getY())
				/ (m1 - m2);
		double y = m1 * (x - p1.getX()) + p1.getY();
		return new Point2D.Double(x, y);
	}

	public static Point2D midPoint(Point2D a, Point2D b) {
		return new Point2D.Double((a.getX() + b.getX()) / 2.0,
				(a.getY() + b.getY()) / 2.0);
	}

	public void quadratify(Point2D a, Point2D b, Point2D c, Point2D d)
			throws IOException {
		// find intersection between bezier arms
		Point2D s = intersect(a, b, c, d);
		if (s == null) {
			return;
		}

		// find distance between the midpoints
		double dx = (a.getX() + d.getX() + s.getX() * 4
				- (b.getX() + c.getX()) * 3) * .125;
		double dy = (a.getY() + d.getY() + s.getY() * 4
				- (b.getY() + c.getY()) * 3) * .125;
		// split curve if the quadratic isn't close enough
		if (dx * dx + dy * dy > resolutionSq) {
			Point2D p01 = midPoint(a, b);
			Point2D p12 = midPoint(b, c);
			Point2D p23 = midPoint(c, d);
			Point2D p02 = midPoint(p01, p12);
			Point2D p13 = midPoint(p12, p23);
			Point2D p03 = midPoint(p02, p13);
			// recursive call to subdivide curve
			quadratify(a, p01, p02, p03);
			quadratify(p03, p13, p23, d);
		} else {
			// end recursion by drawing quadratic bezier
			quad(s.getX(), s.getY(), d.getX(), d.getY());
		}
	}

	static class Test extends CubicToQuadPathConstructor {
		public Test(double resolution) {
			super(resolution);
		}

		@Override
		public void quad(double x1, double y1, double x2, double y2) {
			System.out.println("Quad: (" + currentX + ", " + currentY + ") ("
					+ x1 + ", " + y1 + ") (" + x2 + ", " + y2 + ")");
			currentX = x2;
			currentY = y2;
		}

	}

	public static void main(String[] args) throws Exception {
		PathConstructor pc = new Test(0.5);
		// A, B, C, D
		pc.move(20, 20);
		pc.cubic(20, 40, 40, 60, 60, 60);

		// A, B, D, C
		pc.move(20, 20);
		pc.cubic(20, 40, 60, 60, 40, 60);

		// Intersecting Curve
		pc.move(183, 149);
		pc.cubic(189, 291, 256, 347, 295, 244);
		pc.cubic(334, 141, 286, 216, 214, 228);
		pc.cubic(142, 240, 142, 256, 176, 284);
	}
}
