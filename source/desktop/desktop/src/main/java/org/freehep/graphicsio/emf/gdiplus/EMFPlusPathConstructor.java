// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.freehep.graphicsio.QuadToCubicPathConstructor;

/**
 * Converts Java Paths into a List of PathPoints for usage by EMFPlus.
 * 
 * @author Mark Donszelmann
 * @version $Id: EMFPlusPathConstructor.java,v 1.1 2009-08-17 21:44:44 murkle
 *          Exp $
 */
public class EMFPlusPathConstructor extends QuadToCubicPathConstructor {

	private List path;

	public EMFPlusPathConstructor() {
		super();
		path = new ArrayList();
	}

	@Override
	public void move(double x, double y) throws IOException {
		super.move(x, y);
		path.add(new PathPoint(PathPoint.TYPE_START, x, y));
	}

	@Override
	public void line(double x, double y) throws IOException {
		super.line(x, y);
		path.add(new PathPoint(PathPoint.TYPE_LINE, x, y));
	}

	@Override
	public void cubic(double x1, double y1, double x2, double y2, double x3,
			double y3) throws IOException {
		super.cubic(x1, y1, x2, y2, x3, y3);
		// NOTE: we may need to add the move point here...
		path.add(new PathPoint(PathPoint.TYPE_BEZIER, x1, y1));
		path.add(new PathPoint(PathPoint.TYPE_BEZIER, x2, y2));
		path.add(new PathPoint(PathPoint.TYPE_BEZIER, x3, y3));
	}

	@Override
	public void closePath(double x, double y) throws IOException {
		super.closePath(x, y);
		PathPoint last = (PathPoint) path.get(path.size() - 1);
		last.setType(last.getType() | PathPoint.TYPE_CLOSE_SUBPATH);
	}

	public void reset() {
		path.clear();
	}

	public PathPoint[] getPath() {
		return (PathPoint[]) path.toArray(new PathPoint[path.size()]);
	}

}
