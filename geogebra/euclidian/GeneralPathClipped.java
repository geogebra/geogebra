package geogebra.euclidian;

import geogebra.euclidian.clipping.ClipLine;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * A GeneralPath implementation that does clipping of 
 * line segments at the screen in double coordinates.
 * This is important to avoid rendering problems 
 * that occur with GeneralPath when coordinates are larger than Float.MAX_VALUE.
 * 
 * @author Markus Hohenwarter
 * @version October 2009
 */
public class GeneralPathClipped implements Shape {
	
	private static final float MAX_COORD_VALUE = 10000;
	private static final double TOLERANCE = 0.01; // pixel distance for equal points 
	
	private ArrayList pathPoints;
	private GeneralPath gp;
	private EuclidianView view;
	private double largestCoord;
	private boolean needClosePath;
	private Rectangle bounds;
	
	public GeneralPathClipped(EuclidianView view) {
		this.view = view;
		pathPoints = new ArrayList();
		gp = new GeneralPath();
		//bounds = new Rectangle();
		reset();
	}

	final public void reset() {
		pathPoints.clear();
		gp.reset();
		//bounds.setBounds(0,0,0,0);
		bounds = null;
		largestCoord = 0;
		needClosePath = false;
	}
	
	final public void closePath() {
		needClosePath = true;
	}
	
	private GeneralPath getGeneralPath() {
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
		for (int i=0; i < size; i++) {
			PathPoint curP = (PathPoint) pathPoints.get(i);
			addToGeneralPath(curP, curP.lineTo);
		}
		if (needClosePath)
			gp.closePath();
	}
	
	/**
	 * Clip all segments at screen to make sure we don't have to render huge coordinates.
	 * This is especially important for fill the GeneralPath.
	 */
	private void addClippedSegments() {	
		Rectangle viewRect = new Rectangle(0,0, view.width, view.height);
		PathPoint curP = null, prevP;

		int size = pathPoints.size();
		for (int i=0; i < size; i++) {
			prevP = curP;
			curP = (PathPoint) pathPoints.get(i);
			if (!curP.lineTo || prevP == null) {
				// moveTo point, make sure it is only slightly outside screen
				Point2D p = getPointCloseToScreen(curP.x, curP.y);
				addToGeneralPath(p, false);
			}
			else {
				// clip line at screen
				addClippedLine(prevP, curP, viewRect);
			}
		}
		
		if (needClosePath) {
			// line from last point to first point
			addClippedLine(curP, (PathPoint) pathPoints.get(0), viewRect);
			gp.closePath();
		}
	}
	
	private void addClippedLine(PathPoint prevP, PathPoint curP, Rectangle viewRect) {
		// check if both points on screen
		if (viewRect.contains(prevP) && viewRect.contains(curP)) {
			// draw line to point
			addToGeneralPath(curP, true);
			return;
		}
		
		// at least one point is not on screen: clip line at screen
		Point2D.Double [] clippedPoints = 
			ClipLine.getClipped(prevP.x, prevP.y, curP.x, curP.y, -10, view.width+10, -10, view.height+10);

		if (clippedPoints != null) {
			// we have two intersection points with the screen
			// get closest clip point to prevP
			int first = 0;
			int second = 1;
			if (clippedPoints[first].distance(prevP.x, prevP.y) > 
				clippedPoints[second].distance(prevP.x, prevP.y)) {
				first = 1;
				second = 0;
			}
			
			// draw line to first clip point
			addToGeneralPath(clippedPoints[first], true);
			// draw line between clip points: this ensures high quality rendering
			// which Java2D doesn't deliver with the regular float GeneralPath and huge coords
			addToGeneralPath(clippedPoints[second], true);
			
			// draw line to end point if not already there
			addToGeneralPath(getPointCloseToScreen(curP.x, curP.y), true);
		}
		else {
			// line is off screen
			// draw line to off screen end point
			addToGeneralPath(getPointCloseToScreen(curP.x, curP.y), true);
		}
	}
	
	private Point2D.Double getPointCloseToScreen(double x, double y) {
		double border = 10;
		double right = view.width + border;
		double bottom = view.height + border;
		if (x > right) {
			x = right;
		} 
		else if (x < -border) {
			x = -border;
		}
		if (y > bottom) {
			y = bottom;
		} 
		else if (y < -border) {
			y = -border;
		}
		return new Point2D.Double(x, y);
	}
	
	private void addToGeneralPath(Point2D q, boolean lineTo) {
		Point2D p = gp.getCurrentPoint();
		if (p != null && p.distance(q) < TOLERANCE) {
			return;
		}
		
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
	 */
	final public void moveTo(double x, double y) {
		addPoint(x, y, false);
	}
	
	/**
	 * Line to (x,y).
	 */
	final public void lineTo(double x, double y) {
		addPoint(x, y, true);
	}
	
	/**
	 * Adds point to point list and keeps track of largest coordinate.
	 */
	final public void addPoint(int pos, double x, double y) {
		PathPoint p = new PathPoint(x, y, true);
		updateBounds(p);
		pathPoints.ensureCapacity(pos+1);
		while (pathPoints.size() <= pos) {
			pathPoints.add(null);
		}
		pathPoints.set(pos, p);
	}
	
	/**
	 * Adds point to point list and keeps track of largest coordinate.
	 */
	private void addPoint(double x, double y, boolean lineTo) {
		PathPoint p = new PathPoint(x, y, lineTo);
		updateBounds(p);
		pathPoints.add(p);
	}
	
	private void updateBounds(PathPoint p) {
		
		if (bounds == null) {
			bounds = new Rectangle();
			bounds.setBounds((int)p.x, (int)p.y, 0, 0);
		}
		
		if (Math.abs(p.x) > largestCoord)
			largestCoord = Math.abs(p.x);
		if (Math.abs(p.y) > largestCoord)
			largestCoord = Math.abs(p.y);
		
		bounds.add(p.x, p.y);
	}
	
	public Point2D getCurrentPoint() {
		if (pathPoints.size() == 0)
			return null;
		else
			return (Point2D) pathPoints.get(pathPoints.size() - 1);
	}
	
	public void transform(AffineTransform af) {
		int size = pathPoints.size();
		for (int i=0; i < size; i++) {
			PathPoint p = (PathPoint) pathPoints.get(i);
			af.transform(p, p);
		}
	}

	public boolean contains(Point2D p) {
		return getGeneralPath().contains(p);
	}

	public boolean contains(Rectangle2D p) {
		return getGeneralPath().contains(p);
	}

	public boolean contains(double arg0, double arg1) {
		return getGeneralPath().contains(arg0, arg1);
	}

	public boolean contains(double arg0, double arg1, double arg2, double arg3) {
		return getGeneralPath().contains(arg0, arg1, arg2, arg3);
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public Rectangle2D getBounds2D() {
		return bounds;
	}

	public PathIterator getPathIterator(AffineTransform arg0) {
		return getGeneralPath().getPathIterator(arg0);
	}

	public PathIterator getPathIterator(AffineTransform arg0, double arg1) {
		return getGeneralPath().getPathIterator(arg0, arg1);
	}

	public boolean intersects(Rectangle2D arg0) {
		return getGeneralPath().intersects(arg0);
	}

	public boolean intersects(double arg0, double arg1, double arg2, double arg3) {
		return getGeneralPath().intersects(arg0, arg1, arg2, arg3);
	}
	
	private class PathPoint extends Point2D.Double {
		boolean lineTo;
		
		PathPoint(double x, double y, boolean lineTo) {
			this.x = x;
			this.y = y;
			this.lineTo = lineTo;
		}
	}

}
