// Copyright 2001 FreeHEP.
package org.freehep.graphicsio;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Stack;

/**
 * Implements cubics by approximating them using a polyline. Useful class for
 * output formats that do NOT implement bezier curves at all, or if you need
 * only straight lines.
 * 
 * @author Mark Donszelmann
 * @version $Id: CubicToLinePathConstructor.java,v 1.5 2009-08-17 21:44:45
 *          murkle Exp $
 */
public abstract class CubicToLinePathConstructor
		extends QuadToCubicPathConstructor {

	private double resolution;

	protected CubicToLinePathConstructor() {
		this(0.025);
	}

	protected CubicToLinePathConstructor(double resolution) {
		this.resolution = Math.abs(resolution);
	}

	@Override
	public void cubic(double x1, double y1, double x2, double y2, double x3,
			double y3) throws IOException {

		// ControlSets are written at the end
		Stack/* <ControlSet> */ controls = new Stack/* <ControlSet> */();

		// System.out.println("Cubic "+x1+" "+y1+" "+x2+" "+y2+" "+x3+" "+y3);
		Point2D p0 = new Point2D.Double(currentX, currentY);
		Point2D p1 = new Point2D.Double(x1, y1);
		Point2D p2 = new Point2D.Double(x2, y2);
		Point2D p3 = new Point2D.Double(x3, y3);

		// ControlSets to create the controls
		Stack/* <ControlSet> */ temps = new Stack/* <ControlSet> */();
		temps.push(new ControlSet(p0, p1, p2, p3));

		while (!temps.empty()) {
			ControlSet control = (ControlSet) temps.pop();
			if (control.breadth() > resolution) {
				temps.push(control);
				temps.push(control.bisect());
			} else {
				controls.push(control);
			}
		}

		/*
		 * tempSet[l++] = new ControlSet(p0, p1, p2, p3); while (l > 0) {
		 * ControlSet control1 = tempSet[--l]; double b = control1.breadth(); if
		 * (b > resolution) { ControlSet control3 = control1.bisect();
		 * tempSet[l++] = control1; tempSet[l++] = control3; } else {
		 * controls.push(control1); } }
		 */

		// write out control sets
		// System.out.println(k);
		while (!controls.empty()) {
			Point2D p = ((ControlSet) controls.pop()).getPoint();
			line(p.getX(), p.getY());
			// System.out.println(control2.getPoint());
		}

		// store currentX and currentY
		super.cubic(x1, y1, x2, y2, x3, y3);
	}

	class ControlSet {
		private Point2D point0;

		private Point2D point1;

		private Point2D point2;

		private Point2D point3;

		public ControlSet(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
			point0 = p0;
			point1 = p1;
			point2 = p2;
			point3 = p3;
		}

		public double breadth() {
			double f0 = point0.getX();
			double f4 = point0.getY();
			double f1 = point1.getX();
			double f5 = point1.getY();
			double f2 = point2.getX();
			double f6 = point2.getY();
			double f3 = point3.getX();
			double f7 = point3.getY();
			if ((Math.abs(f0 - f3) < resolution)
					&& (Math.abs(f4 - f7) < resolution)) {

				double f8 = Math.abs(f1 - f0) + Math.abs(f5 - f4);
				double f10 = Math.abs(f2 - f0) + Math.abs(f6 - f4);
				return Math.max(f10, f8);

			}
			double d0 = f4 - f7;
			double d1 = f3 - f0;
			double f12 = Math.sqrt(d0 * d0 + d1 * d1);
			double d2 = f3 * f4 - f0 * f7;
			double f9 = Math.abs((d0 * f2 + d1 * f6) - d2) / f12;
			double f11 = Math.abs((d0 * f1 + d1 * f5) - d2) / f12;
			return Math.max(f9, f11);
		}

		public ControlSet bisect() {
			Point2D p0 = average(point0, point1);
			Point2D p1 = average(point1, point2);
			Point2D p2 = average(point2, point3);
			Point2D p3 = average(p0, p1);
			Point2D p4 = average(p1, p2);
			Point2D p5 = average(p3, p4);
			ControlSet controlset = new ControlSet(p5, p4, p2, point3);
			point1 = p0;
			point2 = p3;
			point3 = p5;
			return controlset;
		}

		public Point2D average(Point2D p1, Point2D p2) {
			return new Point2D.Double((p1.getX() + p2.getX()) / 2.0,
					(p1.getY() + p2.getY()) / 2.0);
		}

		public Point2D getPoint() {
			return point3;
		}
	}

}
