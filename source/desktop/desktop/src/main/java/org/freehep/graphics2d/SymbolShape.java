// Copyright 2001-2004, FreeHEP.
package org.freehep.graphics2d;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This class can be used to create and render simple shapes quickly and without
 * memory allocation. A common point array is used for all created shapes. The
 * factory methods don't return a new shape, but set the object to the selected
 * shape. Hence, the class is not thread-safe and only one PathIterator can be
 * used at the same time.<br>
 * 
 * @author Simon Fischer
 * @version $Id: SymbolShape.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SymbolShape implements Shape {

	private static final double SQRT_2 = Math.sqrt(2.);

	private static final double SQRT_3 = Math.sqrt(3.);

	private class ArrayPathIterator implements PathIterator {

		private int currentPoint = 0;

		private double[] points;

		private int[] type;

		private int numberOfPoints;

		private ArrayPathIterator(double[] points, int[] type) {
			this.points = points;
			this.type = type;
		}

		@Override
		public boolean isDone() {
			return currentPoint >= numberOfPoints;
		}

		@Override
		public void next() {
			currentPoint++;
		}

		@Override
		public int currentSegment(double[] coords) {
			coords[0] = points[2 * currentPoint];
			coords[1] = points[2 * currentPoint + 1];
			return type[currentPoint];
		}

		@Override
		public int currentSegment(float[] coords) {
			coords[0] = (float) points[2 * currentPoint];
			coords[1] = (float) points[2 * currentPoint + 1];
			return type[currentPoint];
		}

		@Override
		public int getWindingRule() {
			return PathIterator.WIND_NON_ZERO;
		}

		private void reset() {
			currentPoint = 0;
		}

		private void done() {
			currentPoint = numberOfPoints;
		}
	}

	private double points[];

	private int type[];

	private ArrayPathIterator pathIterator;

	private double x, y;

	private double size;

	private int symbol;

	public SymbolShape() {
		ensureNumberOfPoints(10);
		type[0] = PathIterator.SEG_MOVETO;
		for (int i = 1; i < type.length; i++) {
			type[i] = PathIterator.SEG_LINETO;
		}
		this.pathIterator = new ArrayPathIterator(points, type);
	}

	@Override
	public boolean contains(double x, double y) {
		return getBounds2D().contains(x, y);
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return contains(x, y) && contains(x + w, y) && contains(x, y + h)
				&& contains(x + w, y + h);
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Returns true, if at least one of the points is contained by the shape.
	 */
	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return contains(x, y) || contains(x + w, y) || contains(x, y + h)
				|| contains(x + w, y + h);
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
		return new Rectangle2D.Double(x - size / 2, y - size / 2, size, size);
	}

	@Override
	public Rectangle getBounds() {
		return getBounds2D().getBounds();
	}

	@Override
	public PathIterator getPathIterator(AffineTransform t) {
		if (t != null) {
			t.transform(points, 0, pathIterator.points, 0, points.length / 2);
		}
		// if (!pathIterator.isDone()) {
		// System.err.println("SymbolShape: concurrent PathIterator
		// requested!");
		// }
		pathIterator.reset();
		return pathIterator;
	}

	// -------------------- factory methods --------------------

	private void createNew(int n) {
		// if (!pathIterator.isDone()) {
		// System.err.println("SymbolShape: concurrent modification!");
		// }
		ensureNumberOfPoints(n);
		pathIterator.numberOfPoints = n;
		pathIterator.done();
	}

	/**
	 * Type must be one of the symbols defined in VectorGraphicsConstants except
	 * TYPE_CIRCLE.
	 * 
	 * @see org.freehep.graphics2d.VectorGraphicsConstants
	 */
	public void create(int symbol, double x, double y, double size) {
		this.symbol = symbol;
		this.x = x;
		this.y = y;
		this.size = size;
		switch (symbol) {
		case VectorGraphicsConstants.SYMBOL_VLINE:
			createVLine(x, y, size);
			break;
		case VectorGraphicsConstants.SYMBOL_HLINE:
			createHLine(x, y, size);
			break;
		case VectorGraphicsConstants.SYMBOL_PLUS:
			createPlus(x, y, size);
			break;
		case VectorGraphicsConstants.SYMBOL_CROSS:
			createCross(x, y, size);
			break;
		case VectorGraphicsConstants.SYMBOL_STAR:
			createStar(x, y, size);
			break;
		case VectorGraphicsConstants.SYMBOL_BOX:
			createBox(x, y, size);
			break;
		case VectorGraphicsConstants.SYMBOL_UP_TRIANGLE:
			createUpTriangle(x, y, size);
			break;
		case VectorGraphicsConstants.SYMBOL_DN_TRIANGLE:
			createDownTriangle(x, y, size);
			break;
		case VectorGraphicsConstants.SYMBOL_DIAMOND:
			createDiamond(x, y, size);
			break;
		}
	}

	@Override
	public String toString() {
		return getClass() + ": " + symbol + " (" + x + ", " + y + ") size: "
				+ size;
	}

	private void createHLine(double x, double y, double size) {
		createNew(2);

		type[0] = PathIterator.SEG_MOVETO;
		points[0] = x - size / 2;
		points[1] = y;

		type[1] = PathIterator.SEG_LINETO;
		points[2] = x + size / 2;
		points[3] = y;
	}

	private void createVLine(double x, double y, double size) {
		createNew(2);

		type[0] = PathIterator.SEG_MOVETO;
		points[0] = x;
		points[1] = y - size / 2;

		type[1] = PathIterator.SEG_LINETO;
		points[2] = x;
		points[3] = y + size / 2;
	}

	private void createPlus(double x, double y, double size) {
		createNew(4);
		double length = size / 2.;

		type[0] = PathIterator.SEG_MOVETO;
		points[0] = x + length;
		points[1] = y;

		type[1] = PathIterator.SEG_LINETO;
		points[2] = x - length;
		points[3] = y;

		type[2] = PathIterator.SEG_MOVETO;
		points[4] = x;
		points[5] = y + length;

		type[3] = PathIterator.SEG_LINETO;
		points[6] = x;
		points[7] = y - length;
	}

	private void createCross(double x, double y, double size) {
		createNew(4);
		double side = size / 2. / SQRT_2;

		type[0] = PathIterator.SEG_MOVETO;
		points[0] = x - side;
		points[1] = y - side;

		type[1] = PathIterator.SEG_LINETO;
		points[2] = x + side;
		points[3] = y + side;

		type[2] = PathIterator.SEG_MOVETO;
		points[4] = x + side;
		points[5] = y - side;

		type[3] = PathIterator.SEG_LINETO;
		points[6] = x - side;
		points[7] = y + side;
	}

	private void createStar(double x, double y, double size) {
		createNew(8);

		double delta = size / 2.;

		type[0] = PathIterator.SEG_MOVETO;
		points[0] = x;
		points[1] = y - delta;

		type[1] = PathIterator.SEG_LINETO;
		points[2] = x;
		points[3] = y + delta;

		type[2] = PathIterator.SEG_MOVETO;
		points[4] = x - delta;
		points[5] = y;

		type[3] = PathIterator.SEG_LINETO;
		points[6] = x + delta;
		points[7] = y;

		delta = size / 2. / SQRT_2;

		type[4] = PathIterator.SEG_MOVETO;
		points[8] = x - delta;
		points[9] = y - delta;

		type[5] = PathIterator.SEG_LINETO;
		points[10] = x + delta;
		points[11] = y + delta;

		type[6] = PathIterator.SEG_MOVETO;
		points[12] = x + delta;
		points[13] = y - delta;

		type[7] = PathIterator.SEG_LINETO;
		points[14] = x - delta;
		points[15] = y + delta;
	}

	private void createUpTriangle(double x, double y, double size) {
		createNew(4);

		type[0] = PathIterator.SEG_MOVETO;
		points[0] = x;
		points[1] = y - size / SQRT_3;

		type[1] = PathIterator.SEG_LINETO;
		points[2] = x - size / 2.;
		points[3] = y + (-size / SQRT_3 + size * SQRT_3 / 2.);

		type[2] = PathIterator.SEG_LINETO;
		points[4] = x + size / 2.;
		points[5] = y + (-size / SQRT_3 + size * SQRT_3 / 2.);

		type[3] = PathIterator.SEG_CLOSE;
	}

	private void createDownTriangle(double x, double y, double size) {
		createNew(4);

		type[0] = PathIterator.SEG_MOVETO;
		points[0] = x;
		points[1] = y + size / SQRT_3;

		type[1] = PathIterator.SEG_LINETO;
		points[2] = x - size / 2.;
		points[3] = y - (-size / SQRT_3 + size * SQRT_3 / 2.);

		type[2] = PathIterator.SEG_LINETO;
		points[4] = x + size / 2.;
		points[5] = y - (-size / SQRT_3 + size * SQRT_3 / 2.);

		type[3] = PathIterator.SEG_CLOSE;
	}

	private void createDiamond(double x, double y, double size) {
		createNew(5);
		double length = size / 2.;

		type[0] = PathIterator.SEG_MOVETO;
		points[0] = x + length;
		points[1] = y;

		type[1] = PathIterator.SEG_LINETO;
		points[2] = x;
		points[3] = y + length;

		type[2] = PathIterator.SEG_LINETO;
		points[4] = x - length;
		points[5] = y;

		type[3] = PathIterator.SEG_LINETO;
		points[6] = x;
		points[7] = y - length;

		type[4] = PathIterator.SEG_CLOSE;
	}

	private void createBox(double x, double y, double size) {
		createNew(5);
		double side = size / SQRT_2 / 2;

		type[0] = PathIterator.SEG_MOVETO;
		points[0] = x - side;
		points[1] = y - side;

		type[1] = PathIterator.SEG_LINETO;
		points[2] = x + side + 1;
		points[3] = y - side;

		type[2] = PathIterator.SEG_LINETO;
		points[4] = x + side + 1;
		points[5] = y + side + 1;

		type[3] = PathIterator.SEG_LINETO;
		points[6] = x - side;
		points[7] = y + side + 1;

		type[4] = PathIterator.SEG_CLOSE;
	}

	private void ensureNumberOfPoints(int n) {
		if ((type == null) || (type.length < n)) {
			this.points = new double[n * 2];
			this.type = new int[n];
		}
	}
}
