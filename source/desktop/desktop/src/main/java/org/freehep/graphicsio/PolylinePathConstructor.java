// Copyright 2001 FreeHEP.
package org.freehep.graphicsio;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Vector;

/**
 * @author Mark Donszelmann
 * @version $Id: PolylinePathConstructor.java,v 1.5 2009-08-17 21:44:45 murkle
 *          Exp $
 */
public abstract class PolylinePathConstructor
		extends CubicToLinePathConstructor {
	private Vector polyline;

	protected boolean closed;

	protected boolean fill;

	public PolylinePathConstructor(boolean fill) {
		this(fill, 0.025);
	}

	public PolylinePathConstructor(boolean fill, double resolution) {
		super(resolution);
		closed = false;
		this.fill = fill;
	}

	@Override
	public void move(double x, double y) throws IOException {
		writePolyline();
		polyline = new Vector();
		polyline.add(new Point2D.Double(x, y));
		super.move(x, y);
	}

	@Override
	public void line(double x, double y) throws IOException {
		// System.out.println("Line "+x+" "+y);
		polyline.add(new Point2D.Double(x, y));
		super.line(x, y);
	}

	@Override
	public void closePath(double x0, double y0) throws IOException {
		closed = true;
		writePolyline();
		super.closePath(x0, y0);
	}

	public void writePolyline() throws IOException {
		if (polyline != null) {
			writePolyline(polyline);
		}
		closed = false;
		polyline = null;
	}

	protected abstract void writePolyline(Vector polyline) throws IOException;
}
