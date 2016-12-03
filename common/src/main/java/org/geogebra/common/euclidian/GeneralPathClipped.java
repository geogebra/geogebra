package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.clipping.ClipLine;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.util.debug.Log;

/**
 * A GeneralPath implementation that does clipping of line segments at the
 * screen in double coordinates. This is important to avoid rendering problems
 * that occur with GeneralPath when coordinates are larger than Float.MAX_VALUE.
 * 
 * @author Markus Hohenwarter
 * @version October 2009
 */
public class GeneralPathClipped implements GShape {

	private static final float MAX_COORD_VALUE = 10000;

	private ArrayList<MyPoint> pathPoints;
	private GGeneralPath gp;
	/** view */
	protected EuclidianViewInterfaceSlim view;
	private double largestCoord;
	private boolean needClosePath;
	private GRectangle bounds;

	/**
	 * Creates new clipped general path
	 * 
	 * @param view
	 *            view
	 */
	public GeneralPathClipped(EuclidianViewInterfaceSlim view) {
		// this.view = (EuclidianView)view;
		this.view = view;
		pathPoints = new ArrayList<MyPoint>();
		gp = AwtFactory.getPrototype().newGeneralPath();
		// bounds = new Rectangle();
		reset();
	}

	/**
	 * @return first point of the path
	 */
	public MyPoint firstPoint() {
		if (pathPoints.size() == 0) {
			return null;
		}
		return pathPoints.get(0);
	}

	/**
	 * Clears all points and resets internal variables
	 */
	final public void reset() {
		pathPoints.clear();
		gp.reset();
		// bounds.setBounds(0,0,0,0);
		bounds = null;
		largestCoord = 0;
		needClosePath = false;
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
		if (pathPoints.size() == 0)
			return gp;

		gp.reset();
		if (largestCoord < MAX_COORD_VALUE) {
			addSimpleSegments();
		} else {
			addClippedSegments();
		}

		// clear pathPoints to free up memory
		pathPoints.clear();

		return gp;
	}

	private void addSimpleSegments() {
		int size = pathPoints.size();
		// double comparison for GGB-975
		for (int i = 0; i < size && i < pathPoints.size(); i++) {
			MyPoint curP = pathPoints.get(i);
			/// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android&et=CRASH&lr=LAST_7_DAYS&ecn=java.lang.NullPointerException&tf=SourceFile&tc=org.geogebra.common.euclidian.GeneralPathClipped&tm=addSimpleSegments&nid&an&c&s=new_status_desc
			if (curP != null) {
				addToGeneralPath(curP, curP.getLineTo());
			} else {
				Log.error("curP shouldn't be null here");
			}
		}
		if (needClosePath) {
			gp.closePath();
		}
	}

	/**
	 * Clip all segments at screen to make sure we don't have to render huge
	 * coordinates. This is especially important for fill the GeneralPath.
	 */
	private void addClippedSegments() {
		GRectangle viewRect = AwtFactory.getPrototype().newRectangle(0, 0,
				view.getWidth(), view.getHeight());
		MyPoint curP = null, prevP;

		int size = pathPoints.size();
		// GGB-975: under unknown conditions pathPoints may shrink so we need
		// double comparison
		for (int i = 0; i < size && i < pathPoints.size(); i++) {
			prevP = curP;
			curP = pathPoints.get(i);
			if (!curP.getLineTo() || prevP == null) {
				// moveTo point, make sure it is only slightly outside screen
				GPoint2D p = getPointCloseToScreen(curP.getX(), curP.getY());
				addToGeneralPath(p, false);
			} else {
				// clip line at screen
				addClippedLine(prevP, curP, viewRect);
			}
		}

		if (needClosePath) {
			// line from last point to first point
			addClippedLine(curP, pathPoints.get(0), viewRect);
			gp.closePath();
		}
	}

	private void addClippedLine(MyPoint prevP, MyPoint curP,
			GRectangle viewRect) {
		// check if both points on screen
		if (viewRect.contains(prevP) && viewRect.contains(curP)) {
			// draw line to point
			addToGeneralPath(curP, true);
			return;
		}

		// at least one point is not on screen: clip line at screen
		GPoint2D[] clippedPoints = ClipLine.getClipped(prevP.getX(),
				prevP.getY(), curP.getX(), curP.getY(), -10,
				view.getWidth() + 10, -10, view.getHeight() + 10);

		if (clippedPoints != null) {
			// we have two intersection points with the screen
			// get closest clip point to prevP
			int first = 0;
			int second = 1;
			if (clippedPoints[first].distance(prevP.getX(),
					prevP.getY()) > clippedPoints[second].distance(prevP.getX(),
							prevP.getY())) {
				first = 1;
				second = 0;
			}

			// draw line to first clip point
			addToGeneralPath(clippedPoints[first], true);
			// draw line between clip points: this ensures high quality
			// rendering
			// which Java2D doesn't deliver with the regular float GeneralPath
			// and huge coords
			addToGeneralPath(clippedPoints[second], true);

			// draw line to end point if not already there
			addToGeneralPath(getPointCloseToScreen(curP.getX(), curP.getY()),
					true);
		} else {
			// line is off screen
			// draw line to off screen end point
			addToGeneralPath(getPointCloseToScreen(curP.getX(), curP.getY()),
					true);
		}
	}

	private GPoint2D getPointCloseToScreen(double ptx, double pty) {
		double x = ptx;
		double y = pty;
		double border = 10;
		double right = view.getWidth() + border;
		double bottom = view.getHeight() + border;
		if (x > right) {
			x = right;
		} else if (x < -border) {
			x = -border;
		}
		if (y > bottom) {
			y = bottom;
		} else if (y < -border) {
			y = -border;
		}
		return AwtFactory.getPrototype().newPoint2D(x, y);
	}

	private void addToGeneralPath(GPoint2D q, boolean lineTo) {
		GPoint2D p = gp.getCurrentPoint();

		/*
		 * We don't need to check the distance, since it has been already
		 * checked when gp was constructed. Anyway, the distance check is not
		 * enough here: we also would need to check if this is really a new
		 * point or just a single point in the same position when a
		 * moveTo-lineTo-moveTo construct was done.
		 */
		// boolean distant = true;
		// if (p != null) {
		// distant = p.distance(q) >= TOLERANCE;
		// }
		// if (!distant) {
		// return;
		// }

		if (lineTo && p != null) {
			try {
				gp.lineTo((float) q.getX(), (float) q.getY());
			} catch (Exception e) {
				gp.moveTo((float) q.getX(), (float) q.getY());
			}
		} else {
			gp.moveTo((float) q.getX(), (float) q.getY());
		}
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
		addPoint(x, y, false);
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
		addPoint(x, y, true);
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

		MyPoint p = new MyPoint(x, y, true);
		updateBounds(p);
		pathPoints.ensureCapacity(pos + 1);
		while (pathPoints.size() <= pos) {
			pathPoints.add(null);
		}
		pathPoints.set(pos, p);
	}

	/**
	 * Adds point to point list and keeps track of largest coordinate.
	 */
	private void addPoint(double x, double y, boolean lineTo) {
		if (Double.isNaN(y)) {
			return;
		}

		MyPoint p = new MyPoint(x, y, lineTo);
		updateBounds(p);
		pathPoints.add(p);
	}

	private void updateBounds(MyPoint p) {

		if (bounds == null) {
			bounds = AwtFactory.getPrototype().newRectangle();
			bounds.setBounds((int) p.getX(), (int) p.getY(), 0, 0);
		}

		if (Math.abs(p.getX()) > largestCoord)
			largestCoord = Math.abs(p.getX());
		if (Math.abs(p.getY()) > largestCoord)
			largestCoord = Math.abs(p.getY());

		bounds.add(p.getX(), p.getY());
	}

	/**
	 * @return current point
	 */
	public GPoint2D getCurrentPoint() {
		if (pathPoints.size() == 0) {
			return null;
		}
		return pathPoints.get(pathPoints.size() - 1);
	}

	/**
	 * Transforms this path
	 * 
	 * @param af
	 *            transformation
	 */
	public void transform(GAffineTransform af) {
		int size = pathPoints.size();
		for (int i = 0; i < size; i++) {
			MyPoint p = pathPoints.get(i);
			af.transform(p, p);
		}
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

	public boolean contains(double arg0, double arg1) {
		return getGeneralPath().contains(arg0, arg1);
	}

	/**
	 * @param arg0
	 *            x min
	 * @param arg1
	 *            y min
	 * @param arg2
	 *            width
	 * @param arg3
	 *            height
	 * @return true if contains rectangle given by args
	 */
	public boolean contains(double arg0, double arg1, double arg2,
			double arg3) {
		return getGeneralPath().contains(arg0, arg1, arg2, arg3);
	}

	public boolean contains(int x, int y) {
		// TODO Auto-generated method stub
		return getGeneralPath().contains(x, y);
	}

	/**
	 * @param rectangle
	 *            rectangle to be checked
	 * @return whether rectangle is contained in this path
	 */
	public boolean contains(GRectangle rectangle) {
		// TODO Auto-generated method stub
		return getGeneralPath().contains(rectangle);
	}

	public GRectangle getBounds() {
		return bounds == null ? AwtFactory.getPrototype().newRectangle() : bounds;
	}

	public GRectangle2D getBounds2D() {
		return bounds == null ? AwtFactory.getPrototype().newRectangle() : bounds;
	}

	/*
	 * public PathIterator getPathIterator(AffineTransform arg0) { return
	 * geogebra
	 * .awt.GeneralPath.getAwtGeneralPath(getGeneralPath()).getPathIterator
	 * (arg0); }
	 */

	public GPathIterator getPathIterator(GAffineTransform arg0) {
		return getGeneralPath().getPathIterator(arg0);
	}

	public boolean intersects(GRectangle2D arg0) {
		return getGeneralPath().intersects(arg0);
	}

	public boolean intersects(double arg0, double arg1, double arg2,
			double arg3) {
		return getGeneralPath().intersects(arg0, arg1, arg2, arg3);
	}

	public boolean intersects(int i, int j, int k, int l) {
		return getGeneralPath().intersects(i, j, k, l);
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

	/*
	 * public Shape getAwtShape() { return
	 * geogebra.awt.GeneralPath.getAwtGeneralPath(getGeneralPath()); }
	 */

}
