/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

/**
 * A GeneralPath implementation that does clipping of line segments at the
 * screen in double coordinates. This is important to avoid rendering problems
 * that occur with GeneralPath when coordinates are larger than Float.MAX_VALUE.
 * 
 * @author Markus Hohenwarter
 * @version October 2009
 */
public class GeneralPathClipped {
	private static final double EPSILON = 0.01;
	private final ArrayList<MyPoint> unprocessedPathPoints;
	/** Cached clipped path, considered invalid if {@code unprocessedPathPoints} are not empty */
	private final GGeneralPath gp;
	private static final double MAX_COORD_VALUE = 10000;

	/** view */
	protected EuclidianViewInterfaceSlim view;
	private int lineThickness;

	private boolean smallPolygon = true;
	private boolean polygon = true;

	private boolean needClosePath;
	private GRectangle2D bounds;
	private double auxX;
	private double auxY;
	// first control point
	private double cont1X = Double.NaN;
	private double cont1Y = Double.NaN;
	// second control point
	private double cont2X = Double.NaN;
	private double cont2Y = Double.NaN;

	private GRectangle2D oldBounds;
	private final ClipAlgoSutherlandHodogman clipAlgoSutherlandHodogman;

	/**
	 * Creates new clipped general path
	 *
	 * @param view
	 *            view
	 */
	public GeneralPathClipped(EuclidianViewInterfaceSlim view) {
		this.view = view;
		unprocessedPathPoints = new ArrayList<>();
		clipAlgoSutherlandHodogman = new ClipAlgoSutherlandHodogman();
		gp = AwtFactory.getPrototype().newGeneralPath();
	}

	/**
	 * @return first point of the path
	 */
	public MyPoint firstPoint() {
		if (unprocessedPathPoints.isEmpty()) {
			return null;
		}
		return unprocessedPathPoints.get(0);
	}

	/**
	 * Clears all points and resets internal variables
	 */
	final public void reset() {
		unprocessedPathPoints.clear();
		gp.reset();
		oldBounds = bounds;
		bounds = null;
		smallPolygon = true;
		polygon = true;
		needClosePath = false;
	}

	/**
	 * Clears all points and resets internal variables
	 * and the line thickness too.
	 * @param lineThickness line thickness
	 */
	final public void resetWithThickness(int lineThickness) {
		reset();
		this.lineThickness = lineThickness;
	}

	/**
	 * Closes path
	 */
	final public void closePath() {
		needClosePath = true;
	}

	/**
	 * @return this as GeneralPath
	 */
	public GGeneralPath getGeneralPath() {
		if (unprocessedPathPoints.isEmpty()) {
			return gp;
		}

		gp.reset();
		if (smallPolygon || !polygon) {
			addSimpleSegments();
		} else {
			addClippedSegmentsWithSutherlandHodogman();
		}

		// clear pathPoints to free up memory
		unprocessedPathPoints.clear();

		return gp;
	}

	private void addSimpleSegments() {
		for (int i = 0; i < unprocessedPathPoints.size(); i++) {
			MyPoint curP = unprocessedPathPoints.get(i);
			// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android&et=CRASH&lr=LAST_7_DAYS&ecn=java.lang.NullPointerException&tf=SourceFile&tc=org.geogebra.common.euclidian.GeneralPathClipped&tm=addSimpleSegments&nid&an&c&s=new_status_desc
			if (curP != null) {
				addToGeneralPath(curP, curP.getSegmentType());
			} else {
				Log.error("curP shouldn't be null here");
			}
		}
		if (needClosePath) {
			gp.closePath();
		}
	}

	private void addClippedSegmentsWithSutherlandHodogman() {
		int padding = lineThickness + 5;
		double[][] clipPoints = {
				{ -padding, -padding},
				{ -padding, view.getHeight() + padding},
				{ view.getWidth() + padding, view.getHeight() + padding},
				{ view.getWidth() + padding, -padding},
		};

		if (needClosePath) {
			unprocessedPathPoints.get(0).setLineTo(true);
		}

		List<MyPoint> result = clipAlgoSutherlandHodogman
				.process(unprocessedPathPoints, clipPoints);

		for (MyPoint curP : result) {
			addToGeneralPath(curP, curP.getSegmentType());
		}

		if (!result.isEmpty() && needClosePath) {
			gp.closePath();
		}
	}

	private void addToGeneralPath(GPoint2D q, SegmentType lineTo) {
		GPoint2D p = gp.getCurrentPoint();

		if (lineTo == SegmentType.CONTROL) {
			if (Double.isNaN(cont1X) && Double.isNaN(cont1Y)) {
				cont1X = q.getX();
				cont1Y = q.getY();
			} else {
				cont2X = q.getX();
				cont2Y = q.getY();
			}
		} else if (lineTo == SegmentType.CURVE_TO) {
			if (!Double.isNaN(cont1X) && !Double.isNaN(cont1Y)
					&& !Double.isNaN(cont2X) && !Double.isNaN(cont2Y)) {
				gp.curveTo(cont1X, cont1Y, cont2X, cont2Y, q.getX(), q.getY());
				cont1X = Double.NaN;
				cont1Y = Double.NaN;
				cont2X = Double.NaN;
				cont2Y = Double.NaN;
			}
		}
		else if (lineTo == SegmentType.AUXILIARY) {
			auxX = q.getX();
			auxY = q.getY();
		} else if (lineTo == SegmentType.ARC_TO && p != null) {
			try {

				double dx1 = auxX - p.getX();
				double dy1 = auxY - p.getY();
				double dx2 = auxX - q.getX();
				double dy2 = auxY - q.getY();
				double angle = MyMath.angle(dx1, dy1, dx2, dy2);
				double cv = btan(Math.PI - angle) * Math.tan(angle / 2);
				gp.curveTo(p.getX() + dx1 * cv, p.getY() + dy1 * cv,
						q.getX() + dx2 * cv, q.getY() + dy2 * cv, q.getX(),
						q.getY());

			} catch (Exception e) {
				gp.moveTo(q.getX(), q.getY());
			}
		}
		else if (lineTo == SegmentType.LINE_TO && p != null) {
			try {
				// Safari: 0 length segments not drawn (MOW-1818 / MOW-878)
				if (p.distance(q) < EPSILON) {
					gp.lineTo(q.getX() + EPSILON, q.getY());
				} else {
					gp.lineTo(q.getX(), q.getY());
				}
			} catch (Exception e) {
				gp.moveTo(q.getX(), q.getY());
			}
		} else {
			gp.moveTo(q.getX(), q.getY());
		}
	}

	private static double btan(double angle) {
		double increment = angle / 2.0;
		return 4.0 / 3.0 * Math.sin(increment) / (1.0 + Math.cos(increment));
	}

	/**
	 * Move to (x,y).
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	final public void moveTo(double x, double y) {
		addPoint(x, y, SegmentType.MOVE_TO);
	}

	/**
	 * Line to (x,y).
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	final public void lineTo(double x, double y) {
		addPoint(x, y, SegmentType.LINE_TO);
	}

	/**
	 * Adds point to point list and keeps track of largest coordinate.
	 * 
	 * @param pos
	 *            insert position
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	final public void addPoint(int pos, double x, double y) {
		if (Double.isNaN(y)) {
			return;
		}

		MyPoint p = new MyPoint(x, y, SegmentType.LINE_TO);
		unprocessedPathPoints.ensureCapacity(pos + 1);
		while (unprocessedPathPoints.size() <= pos) {
			unprocessedPathPoints.add(null);
		}
		unprocessedPathPoints.set(pos, p);
	}

	/**
	 * Adds point to point list and keeps track of largest coordinate.
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param segmentType
	 *            path segment type
	 */
	protected void addPoint(double x, double y, SegmentType segmentType) {
		if (Double.isNaN(y)) {
			return;
		}

		if (segmentType != SegmentType.LINE_TO && segmentType != SegmentType.MOVE_TO) {
			polygon = false;
		}

		MyPoint p = new MyPoint(x, y, segmentType);
		updateBounds(p);
		unprocessedPathPoints.add(p);
	}

	private void updateBounds(GPoint2D point) {
		double x = point.getX();
		double y = point.getY();
		if (bounds == null) {
			bounds = oldBounds != null ? oldBounds
					: AwtFactory.getPrototype().newRectangle2D();
			bounds.setRect(x, y, 0, 0);
		}
		smallPolygon = smallPolygon && polygon
				&& Math.abs(x) < MAX_COORD_VALUE && Math.abs(y) < MAX_COORD_VALUE;

		bounds.add(x, y);
	}

	/**
	 * @apiNote This should not be called after {@code getGeneralPath}.
	 * @return current point
	 */
	public GPoint2D getCurrentPoint() {
		if (unprocessedPathPoints.isEmpty()) {
			return null;
		}
		return unprocessedPathPoints.get(unprocessedPathPoints.size() - 1);
	}

	/**
	 * @param p
	 *            point
	 * @return true if contains given point
	 */
	public boolean contains(GPoint2D p) {
		return getGeneralPath().contains(p);
	}

	/**
	 * @param rect
	 *            rectangle
	 * @return true if contains given rectangle
	 */
	public boolean contains(GRectangle2D rect) {
		return getGeneralPath().contains(rect);
	}

	/**
	 * @param x
	 *            x min
	 * @param y
	 *            y min
	 * @param w
	 *            width
	 * @param h
	 *            height
	 * @return true if contains rectangle given by args
	 */
	public boolean contains(double x, double y, double w, double h) {
		return getGeneralPath().contains(x, y, w, h);
	}

	/**
	 * @param x x-coord
	 * @param y y-coord
	 * @return whether area enclosed by this path contains given point
	 */
	public boolean contains(int x, int y) {
		return getGeneralPath().contains(x, y);
	}

	/**
	 * @param rectangle
	 *            rectangle to be checked
	 * @return whether rectangle is contained in this path
	 */
	public boolean contains(GRectangle rectangle) {
		return getGeneralPath().contains(rectangle);
	}

	/**
	 * @return path bounds
	 */
	public GRectangle getBounds() {
		return bounds == null ? AwtFactory.getPrototype().newRectangle()
				: bounds.getBounds();
	}

	/**
	 * @return path bounds
	 */
	public GRectangle2D getBounds2D() {
		return bounds == null ? AwtFactory.getPrototype().newRectangle2D()
				: bounds;
	}

	/**
	 * @param arg0 transform
	 * @return path iterator
	 */
	public GPathIterator getPathIterator(GAffineTransform arg0) {
		return getGeneralPath().getPathIterator(arg0);
	}

	/**
	 * @param arg0 rectangle
	 * @return whether this intersects given rectangle
	 */
	public boolean intersects(GRectangle2D arg0) {
		return getGeneralPath().intersects(arg0);
	}

	/**
	 * Checks for intersection with a rectangle.
	 * @return whether this intersects given rectangle.
	 */
	public boolean intersects(double x, double y, double w, double h) {
		return getGeneralPath().intersects(x, y, w, h);
	}

	/**
	 * @param x
	 *            center x-coord
	 * @param y
	 *            center y-coord
	 * @param radius
	 *            inradius of the square
	 * @return whether this intersects square with center (x,y) and inradius
	 *         radius
	 */
	public boolean intersects(int x, int y, int radius) {
		return getGeneralPath().intersects(x - radius, y - radius, 2 * radius,
				2 * radius);
	}

	/**
	 * @return number of points
	 */
	public int size() {
		return unprocessedPathPoints.size();
	}

	/**
	 * Append given path to this to create one continuous path (ignoring CLOSE and MOVE_TOs).
	 * NOTE: QUAD_TO is not supported.
	 * @param path path to append
	 */
	public void append(GShape path) {
		GPathIterator iterator = path.getPathIterator(null);
		double[] current = new double[6];
		while (!iterator.isDone()) {
			int type = iterator.currentSegment(current);
			iterator.next();
			switch (type) {
			case GPathIterator.SEG_LINETO:
				lineTo(current[0], current[1]);
				break;
			case GPathIterator.SEG_CUBICTO:
				addPoint(current[0], current[1], SegmentType.CONTROL);
				addPoint(current[2], current[3], SegmentType.CONTROL);
				addPoint(current[4], current[5], SegmentType.CURVE_TO);
				break;
			default: // skip
				break;
			}
		}
	}

	/**
	 * Draw this onto a graphics.
	 * @param g2 graphics
	 */
	public void draw(GGraphics2D g2) {
		g2.draw(getGeneralPath());
	}
}
