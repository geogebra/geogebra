package org.geogebra.common.euclidian.clipping;

//============================================================================
//File:               Clipping.java
//
//Project:            DXF Viewer and general purpose
//
//Purpose:            Workaround for Java 1.2/1.3 problem with line drawing
//
//Author:             Rammi
//
//Copyright Notice:   (c) 2000  Rammi (rammi@caff.de)
//                  This source code is in the public domain. 
//                  USE AT YOUR OWN RISK!
//
//Version History:   
//                  Oct 27, 2000: First release
//
//                  May 17, 2010: Bug fix repairing incorrect results if 
//                                lower corner is hit
//=============================================================================

//package de.caff.gimmicks;

//import java.awt.geom.Point2D;
//import geogebra.common.awt.Point2D;
import org.geogebra.common.factories.AwtFactory;

/**
 * Clipping of lines to the inside of a rectangle. This is useful as a
 * workaround for Java bug id 4252578 (i.e. the JVM hangs when you try to draw a
 * line with starting and/or end point a long distance outside the image area)
 * which came in with JDK 1.2 and is still there in JDK 1.3. It's also useful
 * because all Java versions have problems with lines outside the image area
 * which are sometimes drawn completely wrong.
 * 
 * @author Rammi (rammi@caff.de)
 */
public class ClipLine {
	// some constants
	/** Flag for point lying left of clipping area. */
	public final static int LEFT = 0x01;
	/** Flag for point lying between horizontal bounds of area. */
	public final static int H_CENTER = 0x02;
	/** Flag for point lying right of clipping area. */
	public final static int RIGHT = 0x04;

	/** Flag for point lying &quot;below&quot; clipping area. */
	public final static int BELOW = 0x10;
	/** Flag for point lying between vertical bounds of clipping area. */
	public final static int V_CENTER = 0x20;
	/** Flag for point lying &quot;above&quot; clipping area. */
	public final static int ABOVE = 0x40;

	/** Mask for points which are inside. */
	public final static int INSIDE = H_CENTER | V_CENTER;
	/** Mask for points which are outside. */
	public final static int OUTSIDE = LEFT | RIGHT | BELOW | ABOVE;

	/**
	 * Calculate the clipping points of a line with a rectangle.
	 * 
	 * @param x1
	 *            starting x of line
	 * @param y1
	 *            starting y of line
	 * @param x2
	 *            ending x of line
	 * @param y2
	 *            ending y of line
	 * @param xmin
	 *            lower left x of rectangle
	 * @param xmax
	 *            upper right x of rectangle
	 * @param ymin
	 *            lower left y of rectangle
	 * @param ymax
	 *            upper right y of rectangle
	 * @return <code>null</code> (does not clip) or array of two points
	 */
	public static org.geogebra.common.awt.GPoint2D[] getClipped(double x1,
			double y1, double x2, double y2, int xmin, int xmax, int ymin,
			int ymax) {
		int mask1 = 0; // position mask for first point
		int mask2 = 0; // position mask for second point

		if (x1 < xmin) {
			mask1 |= LEFT;
		} else if (x1 >= xmax) {
			mask1 |= RIGHT;
		} else {
			mask1 |= H_CENTER;
		}
		if (y1 < ymin) {
			// btw: I know that in AWT y runs from down but I more used to
			// y pointing up and it makes no difference for the algorithms
			mask1 |= BELOW;
		} else if (y1 >= ymax) {
			mask1 |= ABOVE;
		} else {
			mask1 |= V_CENTER;
		}
		if (x2 < xmin) {
			mask2 |= LEFT;
		} else if (x2 >= xmax) {
			mask2 |= RIGHT;
		} else {
			mask2 |= H_CENTER;
		}
		if (y2 < ymin) {
			mask2 |= BELOW;
		} else if (y2 >= ymax) {
			mask2 |= ABOVE;
		} else {
			mask2 |= V_CENTER;
		}

		int mask = mask1 | mask2;

		if ((mask & OUTSIDE) == 0) {
			// fine. everything's internal
			org.geogebra.common.awt.GPoint2D[] ret = new org.geogebra.common.awt.GPoint2D[2];
			ret[0] = AwtFactory.prototype.newPoint2D(x1, y1);
			ret[1] = AwtFactory.prototype.newPoint2D(x2, y2);
			return ret;
		} else if ((mask & (H_CENTER | LEFT)) == 0 || // everything's right
				(mask & (H_CENTER | RIGHT)) == 0 || // everything's left
				(mask & (V_CENTER | BELOW)) == 0 || // everything's above
				(mask & (V_CENTER | ABOVE)) == 0) { // everything's below
			// nothing to do
			return null;
		} else {
			// need clipping
			return getClipped(x1, y1, mask1, x2, y2, mask2, xmin, xmax, ymin,
					ymax);
		}
	}

	/**
	 * Calculate the clipping points of a line with a rectangle.
	 * 
	 * @param x1
	 *            starting x of line
	 * @param y1
	 *            starting y of line
	 * @param mask1
	 *            clipping info mask for starting point
	 * @param x2
	 *            ending x of line
	 * @param y2
	 *            ending y of line
	 * @param mask2
	 *            clipping info mask for ending point
	 * @param xmin
	 *            lower left x of rectangle
	 * @param ymin
	 *            lower left y of rectangle
	 * @param xmax
	 *            upper right x of rectangle
	 * @param ymax
	 *            upper right y of rectangle
	 * @return <code>null</code> (does not clip) or array of two points
	 */
	protected static org.geogebra.common.awt.GPoint2D[] getClipped(double x1,
			double y1, int mask1, double x2, double y2, int mask2, double xmin,
			double xmax, double ymin, double ymax) {
		int mask = mask1 ^ mask2;
		org.geogebra.common.awt.GPoint2D p1 = null;

		/*
		 * System.out.println("mask1 = "+mask1);
		 * System.out.println("mask2 = "+mask2);
		 * System.out.println("mask = "+mask);
		 */

		if (mask1 == INSIDE) {
			// point 1 is internal
			p1 = AwtFactory.prototype.newPoint2D((x1 + 0.5), (y1 + 0.5));
			if (mask == 0) {
				// both masks are the same, so the second point is inside, too
				org.geogebra.common.awt.GPoint2D[] ret = new org.geogebra.common.awt.GPoint2D[2];
				ret[0] = p1;
				ret[1] = AwtFactory.prototype
						.newPoint2D((x2 + 0.5), (y2 + 0.5));
				return ret;
			}
		} else if (mask2 == INSIDE) {
			// point 2 is internal
			p1 = AwtFactory.prototype.newPoint2D((x2 + 0.5), (y2 + 0.5));
		} else if (mask == 0) {
			// shortcut: no point is inside, but both are in the same sector, so
			// no intersection is possible
			return null;
		}

		if ((mask & LEFT) != 0) {
			// System.out.println("Trying left");
			// try to calculate intersection with left line
			org.geogebra.common.awt.GPoint2D p = intersect(x1, y1, x2, y2, xmin,
					ymin, xmin, ymax);
			if (p != null) {
				if (p1 == null) {
					p1 = p;
				} else {
					org.geogebra.common.awt.GPoint2D[] ret = new org.geogebra.common.awt.GPoint2D[2];
					ret[0] = p1;
					ret[1] = p;
					return ret;
				}
			}
		}
		if ((mask & RIGHT) != 0) {
			// System.out.println("Trying right");
			// try to calculate intersection with right line
			org.geogebra.common.awt.GPoint2D p = intersect(x1, y1, x2, y2, xmax,
					ymin, xmax, ymax);
			if (p != null) {
				if (p1 == null) {
					p1 = p;
				} else {
					org.geogebra.common.awt.GPoint2D[] ret = new org.geogebra.common.awt.GPoint2D[2];
					ret[0] = p1;
					ret[1] = p;
					return ret;
				}
			}
		}
		if (p1 != null && p1.getY() == (ymin + 0.5)) {
			// use different sequence if a lower corner of clipping rectangle is
			// hit

			if ((mask & ABOVE) != 0) {
				// System.out.println("Trying top");
				// try to calculate intersection with upper line
				org.geogebra.common.awt.GPoint2D p = intersect(x1, y1, x2, y2,
						xmin, ymax, xmax, ymax);
				if (p != null) {
					org.geogebra.common.awt.GPoint2D[] ret = new org.geogebra.common.awt.GPoint2D[2];
					ret[0] = p1;
					ret[1] = p;
					return ret;
				}
			}
			if ((mask & BELOW) != 0) {
				// System.out.println("Trying bottom");
				// try to calculate intersection with lower line
				org.geogebra.common.awt.GPoint2D p = intersect(x1, y1, x2, y2,
						xmin, ymin, xmax, ymin);
				if (p != null) {
					org.geogebra.common.awt.GPoint2D[] ret = new org.geogebra.common.awt.GPoint2D[2];
					ret[0] = p1;
					ret[1] = p;
					return ret;
				}
			}
		} else {
			if ((mask & BELOW) != 0) {
				// System.out.println("Trying bottom");
				// try to calculate intersection with lower line
				org.geogebra.common.awt.GPoint2D p = intersect(x1, y1, x2, y2,
						xmin, ymin, xmax, ymin);
				if (p != null) {
					if (p1 == null) {
						p1 = p;
					} else {
						org.geogebra.common.awt.GPoint2D[] ret = new org.geogebra.common.awt.GPoint2D[2];
						ret[0] = p1;
						ret[1] = p;
						return ret;
					}
				}
			}
			if ((mask & ABOVE) != 0) {
				// System.out.println("Trying top");
				// try to calculate intersection with upper line
				org.geogebra.common.awt.GPoint2D p = intersect(x1, y1, x2, y2,
						xmin, ymax, xmax, ymax);
				if (p != null) {
					if (p1 == null) {
						p1 = p;
					} else {
						org.geogebra.common.awt.GPoint2D[] ret = new org.geogebra.common.awt.GPoint2D[2];
						ret[0] = p1;
						ret[1] = p;
						return ret;
					}
				}
			}
		}

		// no (or not enough) intersections found
		return null;
	}

	/**
	 * Intersect two lines.
	 * 
	 * @param x11
	 *            starting x of 1st line
	 * @param y11
	 *            starting y of 1st line
	 * @param x12
	 *            ending x of 1st line
	 * @param y12
	 *            ending y of 1st line
	 * @param x21
	 *            starting x of 2nd line
	 * @param y21
	 *            starting y of 2nd line
	 * @param x22
	 *            ending x of 2nd line
	 * @param y22
	 *            ending y of 2nd line
	 * @return intersection point or <code>null</code>
	 */
	private static org.geogebra.common.awt.GPoint2D intersect(double x11,
			double y11, double x12, double y12, double x21, double y21,
			double x22, double y22) {
		double dx1 = x12 - x11;
		double dy1 = y12 - y11;
		double dx2 = x22 - x21;
		double dy2 = y22 - y21;
		double det = (dx2 * dy1 - dy2 * dx1);

		/*
		 * System.out.println("intersect"); System.out.println("x1  = "+x11);
		 * System.out.println("y1  = "+y11); System.out.println("x2  = "+x21);
		 * System.out.println("y2  = "+y21); System.out.println("dx1 = "+dx1);
		 * System.out.println("dy1 = "+dy1); System.out.println("dx2 = "+dx2);
		 * System.out.println("dy2 = "+dy2);
		 */

		if (det != 0.0) {
			double mu = ((x11 - x21) * dy1 - (y11 - y21) * dx1) / det;
			// System.out.println("mu = "+mu);
			if (mu >= 0.0 && mu <= 1.0) {
				org.geogebra.common.awt.GPoint2D p = AwtFactory.prototype
						.newPoint2D((x21 + mu * dx2 + 0.5),
								(y21 + mu * dy2 + 0.5));
				// System.out.println("p = "+p);
				return p;
			}
		}

		return null;
	}

}
