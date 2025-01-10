// Copyright 2004, FreeHEP.
package org.freehep.graphics2d;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This class can be used in a transient way to deal with the drawing or filling
 * of an array of double points as a polyline/polygon. The class implements a
 * shape and comes with an associated iterator.
 * 
 * @author Mark Donszelmann
 * @version $Id: ArrayPath.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class ArrayPath implements Shape {

	private class ArrayPathIterator implements PathIterator {

		private double[] xPoints, yPoints;

		private double lastX, lastY;

		private int nPoints;

		private boolean closed;

		private int resolution;

		private int currentPoint;

		private boolean isDone;

		private ArrayPathIterator(double[] xPoints, double[] yPoints,
				int nPoints, boolean closed, int resolution) {
			this.xPoints = xPoints;
			this.yPoints = yPoints;
			this.nPoints = nPoints;
			this.closed = closed;
			this.resolution = resolution;
			currentPoint = 0;
			isDone = nPoints == 0;
		}

		@Override
		public boolean isDone() {
			return isDone;
		}

		@Override
		public void next() {
			currentPoint++;
			while ((currentPoint < nPoints - 1)
					&& (Math.abs(xPoints[currentPoint] - lastX) < resolution)
					&& (Math.abs(yPoints[currentPoint] - lastY) < resolution)) {
				currentPoint++;
			}

			if (closed && (currentPoint == nPoints - 1)
					&& (Math.abs(
							xPoints[currentPoint] - xPoints[0]) < resolution)
					&& (Math.abs(
							yPoints[currentPoint] - yPoints[0]) < resolution)) {
				currentPoint++; // skip last point since it is same as first
			}

			isDone = (closed) ? currentPoint > nPoints
					: currentPoint >= nPoints;
		}

		@Override
		public int currentSegment(double[] coords) {
			if (closed && (currentPoint == nPoints)) {
				return PathIterator.SEG_CLOSE;
			}

			coords[0] = lastX = xPoints[currentPoint];
			coords[1] = lastY = yPoints[currentPoint];
			return (currentPoint == 0) ? PathIterator.SEG_MOVETO
					: PathIterator.SEG_LINETO;
		}

		@Override
		public int currentSegment(float[] coords) {
			if (closed && (currentPoint == nPoints)) {
				return PathIterator.SEG_CLOSE;
			}

			lastX = xPoints[currentPoint];
			lastY = yPoints[currentPoint];
			coords[0] = (float) lastX;
			coords[1] = (float) lastY;
			return (currentPoint == 0) ? PathIterator.SEG_MOVETO
					: PathIterator.SEG_LINETO;
		}

		@Override
		public int getWindingRule() {
			return PathIterator.WIND_NON_ZERO;
		}
	}

	private double[] xPoints, yPoints;

	private int nPoints;

	private boolean closed;

	private int resolution;

	public ArrayPath(double[] xPoints, double[] yPoints, int nPoints,
			boolean closed, int resolution) {
		this.xPoints = xPoints;
		this.yPoints = yPoints;
		this.nPoints = nPoints;
		this.closed = closed;
		this.resolution = resolution;
	}

	@Override
	public boolean contains(double x, double y) {
		// conservative guess
		return false;
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		// conservative guess
		return false;
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		// conservative guess
		return true;
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getPathIterator(at);
	}

	@Override
	public Rectangle2D getBounds2D() {
		double x1, y1, x2, y2;
		int i = nPoints;
		if (i > 0) {
			i--;
			y1 = y2 = yPoints[i];
			x1 = x2 = xPoints[i];
			while (i > 0) {
				i--;
				double y = yPoints[i];
				double x = xPoints[i];
				if (x < x1) {
					x1 = x;
				}
				if (y < y1) {
					y1 = y;
				}
				if (x > x2) {
					x2 = x;
				}
				if (y > y2) {
					y2 = y;
				}
			}
		} else {
			x1 = y1 = x2 = y2 = 0.0f;
		}
		return new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
	}

	@Override
	public Rectangle getBounds() {
		return getBounds2D().getBounds();
	}

	@Override
	public PathIterator getPathIterator(AffineTransform t) {
		double[] transformedXPoints = xPoints;
		double[] transformedYPoints = yPoints;
		if (t != null) {
			// FIXME, this seems a silly slow way to deal with this.
			transformedXPoints = new double[nPoints];
			transformedYPoints = new double[nPoints];
			Point2D s = new Point2D.Double();
			Point2D d = new Point2D.Double();
			for (int i = 0; i < nPoints; i++) {
				s.setLocation(xPoints[i], yPoints[i]);
				t.transform(s, d);
				transformedXPoints[i] = d.getX();
				transformedYPoints[i] = d.getY();
			}
		}
		return new ArrayPathIterator(transformedXPoints, transformedYPoints,
				nPoints, closed, resolution);
	}
}
