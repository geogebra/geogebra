// Copyright 2001 FreeHEP.
package org.freehep.graphicsio.emf;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.QuadToCubicPathConstructor;
import org.freehep.graphicsio.emf.gdi.CloseFigure;
import org.freehep.graphicsio.emf.gdi.LineTo;
import org.freehep.graphicsio.emf.gdi.MoveToEx;
import org.freehep.graphicsio.emf.gdi.PolyBezierTo;
import org.freehep.graphicsio.emf.gdi.PolyBezierTo16;
import org.freehep.graphicsio.emf.gdi.PolylineTo;
import org.freehep.graphicsio.emf.gdi.PolylineTo16;

/**
 * @author Mark Donszelmann
 * @version $Id: EMFPathConstructor.java,v 1.4 2009-08-17 21:44:45 murkle Exp $
 */
public class EMFPathConstructor extends QuadToCubicPathConstructor
		implements EMFConstants {
	private EMFOutputStream os;

	private Rectangle imageBounds;

	private boolean curved;

	private int pointIndex = 0;

	private boolean wide = false;

	private Point[] points = new Point[4];

	public EMFPathConstructor(EMFOutputStream os, Rectangle imageBounds) {
		super();
		this.os = os;
		this.imageBounds = imageBounds;
		this.curved = false;
	}

	@Override
	public void move(double x, double y) throws IOException {
		flush();
		os.writeTag(new MoveToEx(new Point(toUnit(x), toUnit(y))));
		super.move(x, y);
	}

	private void addPoint(int n, double x, double y) {
		if (n >= points.length) {
			Point[] buf = new Point[n << 1];
			System.arraycopy(points, 0, buf, 0, points.length);
			points = buf;
		}

		int ix = toUnit(x);
		int iy = toUnit(y);

		if (wide || (ix < Short.MIN_VALUE) || (ix > Short.MAX_VALUE)
				|| (iy < Short.MIN_VALUE) || (iy > Short.MAX_VALUE)) {
			wide = true;
		}

		if (points[n] == null) {
			points[n] = new Point(ix, iy);
		} else {
			points[n].x = ix;
			points[n].y = iy;
		}
	}

	@Override
	public void line(double x, double y) throws IOException {
		if (curved && (pointIndex > 0)) {
			flush();
		}
		curved = false;
		addPoint(pointIndex++, x, y);
		super.line(x, y);
	}

	@Override
	public void cubic(double x1, double y1, double x2, double y2, double x3,
			double y3) throws IOException {
		if (!curved && (pointIndex > 0)) {
			flush();
		}
		curved = true;
		addPoint(pointIndex++, x1, y1);
		addPoint(pointIndex++, x2, y2);
		addPoint(pointIndex++, x3, y3);
		super.cubic(x1, y1, x2, y2, x3, y3);
	}

	@Override
	public void closePath(double x0, double y0) throws IOException {
		flush();
		os.writeTag(new CloseFigure());
		super.closePath(x0, y0);
	}

	@Override
	public void flush() throws IOException {
		if (curved) {
			if (wide) {
				os.writeTag(new PolyBezierTo(imageBounds, pointIndex, points));
			} else {
				os.writeTag(
						new PolyBezierTo16(imageBounds, pointIndex, points));
			}
		} else if (pointIndex == 1) {
			os.writeTag(new LineTo(points[0]));
		} else if (pointIndex > 1) {
			if (wide) {
				os.writeTag(new PolylineTo(imageBounds, pointIndex, points));
			} else {
				os.writeTag(new PolylineTo16(imageBounds, pointIndex, points));
			}
		}
		pointIndex = 0;
		wide = false;
		super.flush();
	}

	protected int toUnit(double d) {
		return (int) (d * UNITS_PER_PIXEL * TWIPS);
	}
}
