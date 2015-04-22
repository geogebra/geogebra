package org.geogebra.common.kernel.discrete;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.main.App;

/**
 * Class to convert a GeoPolygon to a set of triangles
 * 
 * based on monotone pieces and sweep line, as described here:
 * https://www.cs.ucsb.edu/~suri/cs235/Triangulation.pdf
 * (Subhash Suri, UC Santa Barbara)
 * 
 * @author mathieu
 *
 */
public class PolygonTriangulation {

	private class MyTreeSet<E> extends TreeSet<E> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyTreeSet() {
			super();
		}

		/**
		 * we have to implement that for gwt
		 */
		public E higher(E e) {
			SortedSet<E> set = tailSet(e);
			Iterator<E> it = set.iterator();

			if (it.hasNext()) {
				E first = it.next();
				if (first != e) {
					return first;
				}

				if (it.hasNext()) {
					return it.next();
				}
			}

			return null;
		}

		/**
		 * we have to implement that for gwt
		 */
		public E lower(E e) {

			SortedSet<E> set = headSet(e);

			if (set == null || set.isEmpty()) {
				return null;
			}

			return set.last();
		}

	}

	static private class TriangulationException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public enum Type {
			LEFT_POINT_INTERSECTION, ZERO_SEGMENT, DEAD_END
		};

		private Type type;

		public TriangulationException(Type type) {
			this.type = type;
		}

		@Override
		public String getMessage() {
			return "Triangulation exception : " + type;
		}
	}

	final static private boolean DEBUG = false;

	public static final int CORNERS_NUMBER = 8;

	/**
	 * message debug
	 * 
	 * @param s
	 *            message
	 */
	final static protected void debug(String s) {
		if (DEBUG) {
			App.debug(s);
		}
	}

	/**
	 * message error
	 * 
	 * @param s
	 *            message
	 */
	final static protected void error(String s) {
		if (DEBUG) {
			App.error(s);
		}
	}

	protected Point nextNewPointForNonSelfIntersectingPolygon = null;

	final private Comparator<Point> nonSelfIntersectingPolygonPointComparator = new Comparator<Point>() {

		public int compare(Point p1, Point p2) {

			if (p1 == p2) {
				return 0;
			}

			if (p1.id == p2.id) {
				/*
				 * error("same ids"); debug(p1.debugSegments());
				 * debug(p2.debugSegments());
				 */

				// copy segments
				if (p1.toRight != null) {
					if (p2.toRight == null) {
						p2.toRight = new MyTreeSet<Segment>();
					}
					for (Segment seg : p1.toRight) {
						seg.leftPoint = p2;
						p2.toRight.add(seg);
					}
				}

				if (p1.toLeft != null) {
					if (p2.toLeft == null) {
						p2.toLeft = new MyTreeSet<Segment>();
					}
					for (Segment seg : p1.toLeft) {
						seg.rightPoint = p2;
						p2.toLeft.add(seg);
					}
				}

				// add diagonal need
				if (p1.needsDiagonal) {
					p2.needsDiagonal = true;
				}

				nextNewPointForNonSelfIntersectingPolygon = p2;

				return 0;
			}

			return p1.compareToOnly(p2);
		}

	};

	private class Point implements Comparable<Point> {
		public double x, y;
		public int id;
		public String name;
		public double orientationToNext;
		Point prev, next; // previous and next point

		MyTreeSet<Segment> toRight, toLeft;

		boolean needsDiagonal = false;

		public Point(double x, double y, int id) {
			this.x = x;
			this.y = y;
			this.id = id;
		}

		public Point clone() {
			Point ret = new Point(x, y, id);
			ret.name = name;
			return ret;
		}

		public String debugSegments() {
			String s = name + " ";
			if (toLeft != null) {
				s += "/ to left : ";
				for (Segment segment : toLeft) {
					s += ((int) (segment.orientation * 180 / Math.PI)) + "°:"
							+ segment.leftPoint.name + "(" + segment.usable
							+ "), ";
				}
			}
			if (toRight != null) {

				s += "/ to right : ";
				for (Segment segment : toRight) {
					s += ((int) (segment.orientation * 180 / Math.PI)) + "°:"
							+ segment.rightPoint.name + "(" + segment.usable
							+ "), ";
				}
			}

			return s;
		}

		public void removeSegmentToRight(Segment segment) {
			toRight.remove(segment);
		}

		public boolean addSegmentToRight(Segment segment) {
			if (toRight == null) {
				toRight = new MyTreeSet<Segment>();
			}
			return toRight.add(segment);
		}

		public void removeSegmentToLeft(Segment segment) {
			toLeft.remove(segment);
		}

		public boolean addSegmentToLeft(Segment segment) {
			if (toLeft == null) {
				toLeft = new MyTreeSet<Segment>();
			}
			return toLeft.add(segment);
		}

		public boolean hasNoSegment() {
			return (toLeft == null || toLeft.isEmpty())
					&& (toRight == null || toRight.isEmpty());
		}

		final public int compareTo(Point p2) {

			if (id == p2.id) {
				return 0;
			}

			// smallest x
			if (Kernel.isGreater(p2.x, x)) {
				return -1;
			}
			if (Kernel.isGreater(x, p2.x)) {
				return 1;
			}

			// then smallest y
			if (Kernel.isGreater(p2.y, y)) {
				return -1;
			}
			if (Kernel.isGreater(y, p2.y)) {
				return 1;
			}

			// same point : add all point-to-point set to existing point
			error(this.name + "==" + p2.name);

			if (toRight != null) {
				if (p2.toRight == null) {
					p2.toRight = new MyTreeSet<Segment>();
				}
				for (Segment seg : toRight) {
					seg.leftPoint = p2;
					p2.toRight.add(seg);
					try {
						cutAfterComparisonToRight(seg);
					} catch (TriangulationException e) {
						debug(e.getMessage());
					}
				}
			}

			if (toLeft != null) {
				if (p2.toLeft == null) {
					p2.toLeft = new MyTreeSet<Segment>();
				}
				for (Segment seg : toLeft) {
					seg.rightPoint = p2;
					p2.toLeft.add(seg);
					try {
						cutAfterComparisonToLeft(seg);
					} catch (TriangulationException e) {
						debug(e.getMessage());
					}

				}
			}

			return 0;
		}

		final public int compareToOnly(Point p2) {

			// smallest x
			if (Kernel.isGreater(p2.x, x)) {
				return -1;
			}
			if (Kernel.isGreater(x, p2.x)) {
				return 1;
			}

			// then smallest y
			if (Kernel.isGreater(p2.y, y)) {
				return -1;
			}
			if (Kernel.isGreater(y, p2.y)) {
				return 1;
			}

			return 0;

		}

		/**
		 * this method is only used for intersection
		 * 
		 * @param x1
		 *            x
		 * @param y1
		 *            y
		 * @return -1 if this is before (x1,y1); 1 if this is after (x1,y1); 0
		 *         otherwise
		 */
		final public int compareTo(double x1, double y1) {

			// smallest x
			if (Kernel.isGreater(x1, x)) {
				return -1;
			}
			if (Kernel.isGreater(x, x1)) {
				return 1;
			}

			// then smallest y
			if (Kernel.isGreater(y1, y)) {
				return -1;
			}
			if (Kernel.isGreater(y, y1)) {
				return 1;
			}

			return 0;
		}

		/**
		 * convert it to GPoint2D.Double
		 * 
		 * @return (x,y) GPoint2D.Double
		 */
		public GPoint2D.Double toDouble() {
			return new GPoint2D.Double(x, y);
		}
	}

	protected Segment comparedSameOrientationSegment;
	protected int comparedSameOrientationValue;

	protected Segment comparedSameSegment;

	private class Segment implements Comparable<Segment> {
		double orientation;
		Point leftPoint, rightPoint;
		Segment above, below;
		Segment next;
		int usable = 1;

		Running running = Running.STOP;

		// equation vector
		double x, y, z;
		private boolean equationNeedsUpdate = true;

		public Segment() {
			// dummy constructor
		}

		public boolean isDummy() {
			return leftPoint == null;
		}

		public Segment(double orientation, Point leftPoint, Point rightPoint) {
			this(leftPoint, rightPoint);
			this.orientation = orientation;
		}

		public Segment clone() {
			return new Segment(orientation, leftPoint, rightPoint);
		}

		public Segment(Point leftPoint, Point rightPoint) {
			this.leftPoint = leftPoint;
			this.rightPoint = rightPoint;
		}

		public void setEquation() {
			if (equationNeedsUpdate) {
				y = rightPoint.x - leftPoint.x;
				x = -rightPoint.y + leftPoint.y;
				z = -x * rightPoint.x - y * rightPoint.y;
				equationNeedsUpdate = false;
			}
		}

		public void equationNeedsUpdate() {
			equationNeedsUpdate = true;
		}

		@Override
		public String toString() {
			if (leftPoint != null) {
				/*
				 * if (running == Running.LEFT){ return
				 * rightPoint.name+leftPoint.name; }
				 */
				return leftPoint.name + rightPoint.name;
			}
			return "dummy";
		}

		public Point getFirstPoint() {
			if (running == Running.LEFT) {
				return rightPoint;
			}

			// running == Running.RIGHT
			return leftPoint;
		}

		/**
		 * remove this segment from left and right points
		 */
		public void removeFromPoints() {
			leftPoint.removeSegmentToRight(this);
			rightPoint.removeSegmentToLeft(this);
		}

		/**
		 * add this segment to left and right points
		 * 
		 * @return true if new segment
		 */
		public boolean addToPoints() {
			boolean newRight = leftPoint.addSegmentToRight(this);
			boolean newLeft = rightPoint.addSegmentToLeft(this);
			return newRight && newLeft;
		}

		public int compareTo(Segment seg) {

			if (this == seg) {
				return 0;
			}

			if (Kernel.isGreater(seg.orientation, orientation)) {
				return -1;
			}

			if (Kernel.isGreater(orientation, seg.orientation)) {
				return 1;
			}

			comparedSameOrientationSegment = seg;
			if (rightPoint.id != seg.rightPoint.id) {
				comparedSameOrientationValue = rightPoint
						.compareToOnly(seg.rightPoint);
			} else {
				comparedSameOrientationValue = leftPoint
						.compareToOnly(seg.leftPoint);
			}
			// error(this+","+seg+" : "+c);
			/*
			 * if (c > 0){ seg.rightPoint.removeSegmentToLeft(seg);
			 * rightPoint.addSegmentToLeft(seg); seg.rightPoint = rightPoint;
			 * }else{ rightPoint.removeSegmentToLeft(this); }
			 */

			// same orientation : check next point id
			if (rightPoint.id < seg.rightPoint.id) {
				return -1;
			}

			if (rightPoint.id > seg.rightPoint.id) {
				return 1;
			}

			/*
			 * // same right point : augment usability if (rightPoint.id ==
			 * seg.rightPoint.id){ seg.usable += usable/2; // usable is always
			 * multiple of 2, and will be add twice (from left and from right)
			 * debug(seg+": "+seg.usable); }
			 */

			// error("same points : "+this+","+seg);
			comparedSameSegment = seg;

			// same ptp
			return 0;
		}
	}

	/**
	 * TriangleFan is composed of apex point index, and list of fan points
	 * indices, knowing the clockwise/anti-clockwise orientation
	 * 
	 * @author mathieu
	 *
	 */
	public class TriangleFan extends ArrayList<Integer> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private boolean isClockWise;
		private int apex;

		private Iterator<Integer> iterator;

		/**
		 * 
		 * @param apex
		 *            of the fan
		 * @param isClockWise
		 *            orientation
		 */
		public TriangleFan(int apex, boolean isClockWise) {
			this.apex = apex;
			this.isClockWise = isClockWise;
		}

		/**
		 * 
		 * @return apex point
		 */
		public int getApexPoint() {
			return apex;
		}

		/**
		 * 
		 * @param i
		 *            i-th index
		 * @return vertex index regarding clockwise/anti clockwise orientation
		 */
		public int getVertexIndex(int i) {
			if (isClockWise) {
				return get(size() - i - 1);
			}

			return get(i);
		}

	}

	private class PolygonPoints extends TreeSet<Point> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * says that at least one diagonal is needed
		 */
		public boolean needsDiagonals = false;

		public PolygonPoints(Comparator<Point> comparator) {
			super(comparator);
		}

	}

	private GeoPolygon polygon;

	private int maxPointIndex;

	private Point firstPoint;

	private ArrayList<PolygonPoints> polygonPointsList;

	private ArrayList<TriangleFan> fansList;

	private GPoint2D.Double[] pointsArray;

	/**
	 * Constructor
	 */
	public PolygonTriangulation() {

		polygonPointsList = new ArrayList<PolygonPoints>();
		fansList = new ArrayList<TriangleFan>();
		pointsArray = new GPoint2D.Double[0];
	}

	/**
	 * set the polygon
	 * 
	 * @param p
	 *            polygon
	 */
	public void setPolygon(GeoPolygon p) {
		this.polygon = p;
	}

	/**
	 * clear lists
	 */
	public void clear() {
		polygonPointsList.clear();
		fansList.clear();
		maxPointIndex = 0;
		firstPoint = null;
	}

	/**
	 * set point id
	 * 
	 * @param point
	 * @param i
	 */
	private void setName(Point point, int i) {
		if (DEBUG) {
			point.name = ((GeoElement) polygon.getPointsND()[i])
					.getLabelSimple();
		}
	}

	/**
	 * update points list: creates a chain from firstPoint to next points ; two
	 * consecutive points can't be equal ; three consecutive points can't be
	 * aligned. For each point orientation to the next (angle about Ox) is
	 * stored.
	 * 
	 * @return points left
	 */
	public int updatePoints() {

		maxPointIndex = polygon.getPointsLength() + CORNERS_NUMBER;

		// feed the list with no successively equal points
		Point point = new Point(polygon.getPointX(0), polygon.getPointY(0), 0);
		setName(point, 0);
		firstPoint = point;
		int n = 1;
		int length = polygon.getPointsLength();
		for (int i = 1; i < length; i++) {
			double x1 = polygon.getPointX(i);
			double y1 = polygon.getPointY(i);
			if (!Kernel.isEqual(point.x, x1) || !Kernel.isEqual(point.y, y1)) {
				point.next = new Point(x1, y1, i);
				setName(point.next, i);
				point.next.prev = point;
				point = point.next;
				n++;
			}
		}

		// corners

		for (int i = 0; i < CORNERS_NUMBER; i++) {
			double x1 = corners[i].getX();
			double y1 = corners[i].getY();
			if (!Kernel.isEqual(point.x, x1) || !Kernel.isEqual(point.y, y1)) {
				point.next = new Point(x1, y1, length + i);
				setName(point.next, i);
				point.next.prev = point;
				point = point.next;
				n++;
			}
		}

		// check first point <> last point
		if (Kernel.isEqual(point.x, firstPoint.x)
				&& Kernel.isEqual(point.y, firstPoint.y)) {
			firstPoint = firstPoint.next;
			n--;
		}
		if (n < 3) {
			return n;
		}
		point.next = firstPoint;
		firstPoint.prev = point;

		if (DEBUG) {
			String s1 = "\n";
			for (point = firstPoint; point.next != firstPoint; point = point.next) {
				s1 += "\n" + point.name + " = (" + point.x + "," + point.y
						+ ")";
			}
			s1 += "\n" + point.name + " = (" + point.x + "," + point.y + ")";
			debug(s1);
		}

		// set orientations and remove flat points
		Point prevPoint = firstPoint;
		point = prevPoint.next;
		prevPoint.orientationToNext = Math.atan2(point.y - prevPoint.y, point.x
				- prevPoint.x);

		int removedPoints = 0;
		for (int i = 0; i < n && removedPoints < n - 1; i++) {
			// make it n times since at each step :
			// * we remove 1 point and go on
			// * we remove 2 points and go back
			// * we go on
			// so each point is visited at least once
			Point nextPoint = point.next;
			point.orientationToNext = Math.atan2(nextPoint.y - point.y,
					nextPoint.x - point.x);
			// delta orientation between 0 and 2pi
			double delta = point.orientationToNext
					- prevPoint.orientationToNext;
			if (delta < 0) {
				delta += 2 * Math.PI;
			}
			debug(prevPoint.name + " : "
					+ (prevPoint.orientationToNext * 180 / Math.PI));
			debug(prevPoint.name + "/" + point.name + "/" + nextPoint.name
					+ " : " + (delta * 180 / Math.PI));
			if (Kernel.isZero(delta)) { // point aligned
				// remove point
				prevPoint.next = nextPoint;
				nextPoint.prev = prevPoint;
				removedPoints++;
				point = nextPoint;
			} else if (Kernel.isEqual(delta, Math.PI)) { // U-turn
				debug("U-turn");
				if (Kernel.isEqual(nextPoint.x, prevPoint.x)
						&& Kernel.isEqual(nextPoint.y, prevPoint.y)) {
					// same point
					debug(prevPoint.name + "==" + nextPoint.name);

					// index is going back
					i--;
					// set correct orientation
					// error(prevPoint.orientationToNext*180/Math.PI+"/"+nextPoint.orientationToNext*180/Math.PI);
					prevPoint.orientationToNext = nextPoint.orientationToNext;
					// go back
					point = prevPoint;
					prevPoint = prevPoint.prev;
					// remove point and nextPoint
					point.next = nextPoint.next;
					nextPoint.next.prev = point;
					removedPoints += 2;
				} else if (Kernel.isGreater(0, (nextPoint.x - prevPoint.x)
						* (point.x - prevPoint.x) + (nextPoint.y - prevPoint.y)
						* (point.y - prevPoint.y))) {
					// next point is back old point
					debug(" next point is back old point - "
							+ (prevPoint.orientationToNext * 180 / Math.PI));
					if (prevPoint.orientationToNext > 0) {
						prevPoint.orientationToNext -= Math.PI;
					} else {
						prevPoint.orientationToNext += Math.PI;
					}
					// remove point
					prevPoint.next = nextPoint;
					nextPoint.prev = prevPoint;
					removedPoints++;
					point = nextPoint;
				} else {
					// next point is in same direction as old point
					// remove point
					prevPoint.next = nextPoint;
					nextPoint.prev = prevPoint;
					point = nextPoint;
				}

			} else {
				prevPoint = point;
				point = nextPoint;
			}

		}

		firstPoint = point; // in case old firstPoint has been removed

		if (DEBUG) {
			String s = "";
			for (point = firstPoint; point.next != firstPoint; point = point.next) {
				s += point.name + "("
						+ (point.orientationToNext * 180 / Math.PI) + "°), ";
			}
			s += point.name + "(" + (point.orientationToNext * 180 / Math.PI)
					+ "°)";
			debug(s);
		}

		debug(n + " - " + removedPoints);
		return n - removedPoints;
	}

	// ////////////////////////////////////
	// CONVEX POLYGON ?
	// ////////////////////////////////////

	public enum Convexity {
		CLOCKWISE, ANTI_CLOCKWISE, NOT
	}

	/**
	 * 
	 * @return true if the polygon is convex after simplification
	 */
	public Convexity checkIsConvex() {

		Point point1 = firstPoint;
		Point point2 = point1.next;
		double delta = point1.orientationToNext + Math.PI
				- point2.orientationToNext;
		if (delta < -Math.PI) {
			delta += 2 * Math.PI;
		} else if (delta > Math.PI) {
			delta -= 2 * Math.PI;
		}
		boolean positive = (delta > 0);
		debug(point1.name + "(" + (point1.orientationToNext * 180 / Math.PI)
				+ "°)");
		debug(point2.name + "(" + (point2.orientationToNext * 180 / Math.PI)
				+ "°)");
		debug("delta : " + (delta * 180 / Math.PI) + "°)");
		debug("positive : " + positive);
		boolean convex = true;
		point1 = point2;
		point2 = point1.next;
		int pointLengthMinus2 = -1;
		double deltaSum = delta;

		while (point1 != firstPoint && convex) {
			delta = point1.orientationToNext + Math.PI
					- point2.orientationToNext;
			if (delta < -Math.PI) {
				delta += 2 * Math.PI;
			} else if (delta > Math.PI) {
				delta -= 2 * Math.PI;
			}
			convex = positive ^ (delta < 0);
			debug(point2.name + "("
					+ (point2.orientationToNext * 180 / Math.PI) + "°) -- "
					+ "(" + (delta * 180 / Math.PI) + "°) -- " + convex);
			point1 = point2;
			point2 = point1.next;
			pointLengthMinus2++;
			deltaSum += delta;
		}

		// check if (angle sum) == (n-2)*pi
		debug((deltaSum * 180 / Math.PI) + " , " + (pointLengthMinus2 - 2)
				* 180);
		convex = convex
				&& Kernel.isEqual(Math.abs(deltaSum), pointLengthMinus2
						* Math.PI);

		if (convex) {
			if (positive) {
				return Convexity.ANTI_CLOCKWISE;
			}

			return Convexity.CLOCKWISE;
		}

		return Convexity.NOT;

	}

	// ////////////////////////////////////
	// INTERSECTIONS
	// ////////////////////////////////////

	/**
	 * cut a segment in two by this point
	 * 
	 * @param segment
	 *            segment
	 * @param pt
	 *            cutting point
	 * @throws TriangulationException
	 *             exception if cut after comparison failed
	 */
	private Segment cut(Segment segment, Point pt)
			throws TriangulationException {
		// cut the segment
		segment.removeFromPoints();
		Segment segment2 = new Segment(segment.orientation, pt,
				segment.rightPoint);
		segment.rightPoint = pt;
		segment.addToPoints();
		comparedSameOrientationSegment = null;
		segment2.addToPoints();
		segment2.usable = segment.usable;
		cutAfterComparisonToRight(segment2);
		debug(segment2.leftPoint.debugSegments());
		return segment2;
	}

	/**
	 * After adding a segment to the points, it may be redundant with an already
	 * existing segment in the left point
	 * 
	 * @param segment2
	 *            segment
	 * @throws TriangulationException
	 *             exception if segment2 is "zero segment" (end points equal)
	 */
	protected void cutAfterComparisonToRight(Segment segment2)
			throws TriangulationException {
		if (comparedSameOrientationSegment != null) {
			debug(segment2 + "," + comparedSameOrientationSegment + " : "
					+ comparedSameOrientationValue);
			if (segment2.rightPoint == segment2.leftPoint) {
				throw new TriangulationException(
						TriangulationException.Type.ZERO_SEGMENT);
			}
			if (comparedSameOrientationValue < 0) {
				// segment2 can be used once more
				Segment s = comparedSameOrientationSegment;
				comparedSameOrientationSegment = null;
				s.removeFromPoints();
				s.leftPoint = segment2.rightPoint;
				segment2.usable++;
				segment2.addToPoints(); // check why this is necessary

				comparedSameOrientationSegment = null;
				s.addToPoints();
				cutAfterComparisonToRight(s);
			} else if (comparedSameOrientationValue > 0) {
				// comparedSameOrientationSegment can be used once more
				Segment s = comparedSameOrientationSegment;
				comparedSameOrientationSegment = null;
				segment2.removeFromPoints();
				segment2.leftPoint = s.rightPoint;
				s.usable++;
				s.addToPoints(); // check why this is necessary

				comparedSameOrientationSegment = null;
				segment2.addToPoints();
				cutAfterComparisonToRight(segment2);
			} else {
				// same segment : add usability
				Segment s = comparedSameOrientationSegment;
				debug(segment2.hashCode() + " / " + s.hashCode());
				comparedSameOrientationSegment = null;
				// same segment : add usability
				segment2.usable += s.usable;
				segment2.removeFromPoints();
				s.removeFromPoints();
				comparedSameOrientationSegment = null;
				segment2.addToPoints();
				comparedSameOrientationSegment = null;
			}
		}
	}

	/**
	 * After adding a segment to the points, it may be redundant with an already
	 * existing segment in the left point
	 * 
	 * @param segment2
	 *            segment
	 * @throws TriangulationException
	 *             exception if segment2 is "zero segment" (end points equal)
	 */
	protected void cutAfterComparisonToLeft(Segment segment2)
			throws TriangulationException {
		if (comparedSameOrientationSegment != null) {
			debug(segment2 + "," + comparedSameOrientationSegment + " : "
					+ comparedSameOrientationValue);
			if (segment2.rightPoint == segment2.leftPoint) {
				throw new TriangulationException(
						TriangulationException.Type.ZERO_SEGMENT);
			}
			if (comparedSameOrientationValue > 0) {
				// comparedSameOrientationSegment can be used once more
				Segment s = comparedSameOrientationSegment;
				comparedSameOrientationSegment = null;
				s.removeFromPoints();
				s.rightPoint = segment2.leftPoint;
				segment2.usable++;
				segment2.addToPoints(); // check why this is necessary

				comparedSameOrientationSegment = null;
				s.addToPoints();
				cutAfterComparisonToLeft(s);
			} else if (comparedSameOrientationValue < 0) {
				// segment2 can be used once more
				Segment s = comparedSameOrientationSegment;
				comparedSameOrientationSegment = null;
				segment2.removeFromPoints();
				segment2.rightPoint = s.leftPoint;
				s.usable++;
				s.addToPoints(); // check why this is necessary

				comparedSameOrientationSegment = null;
				segment2.addToPoints();
				cutAfterComparisonToLeft(segment2);
			} else {
				Segment s = comparedSameOrientationSegment;
				debug(segment2.hashCode() + " / " + s.hashCode());
				comparedSameOrientationSegment = null;
				// same segment : add usability
				segment2.usable += s.usable;
				segment2.removeFromPoints();
				s.removeFromPoints();
				comparedSameOrientationSegment = null;
				segment2.addToPoints();
				comparedSameOrientationSegment = null;
			}
		}
	}

	/**
	 * set intersections. After this call, PolygonTriangulation has an array of
	 * 2D coords (pointsArray), and a list of polygons (polygonPointsList), each
	 * composed of its points in sweep order, connected by segments running
	 * right or left, eventually needing a diagonal.
	 * 
	 * @throws TriangulationException
	 *             exception if creating intersections fails
	 */
	public void setIntersections() throws TriangulationException {

		// create segments
		Point point;
		for (point = firstPoint; point.next != firstPoint; point = point.next) {
			createSegment(point);
		}
		createSegment(point);

		// store all points in sweep order
		// same points are merged
		// aligned segments to right are cut
		// aligned segments to left are ignored
		MyTreeSet<Point> pointSet = new MyTreeSet<Point>();

		for (point = firstPoint; point.next != firstPoint; point = point.next) {
			pointSet.add(point);
		}
		pointSet.add(point);

		// at this time, pointSet only contains different points, each points
		// have to-left / to-right segments with different orientations

		if (DEBUG) {
			error("================== before intersections ===================");
			for (Point pt : pointSet) {
				debug(pt.debugSegments());
			}
			error("================== END ===================");
		}

		if (pointSet.size() > 3) {

			// now compute intersections
			// TODO use a better storage than linear chained segments

			// top and bottom (dummy) segments
			Segment top = new Segment();
			Segment bottom = new Segment();
			bottom.above = top;
			top.below = bottom;

			for (Point pt = pointSet.first(); pt != pointSet.last(); pt = pointSet
					.higher(pt)) {

				String s = pt.name + " : ";
				/*
				 * for (Segment seg = bottom.above ; seg != top; seg =
				 * seg.above){ s+=seg.toString()+"("+seg.hashCode()+")"+","; }
				 * s+=" (before)"; debug(s);
				 * 
				 * s=pt.name+" : ";
				 */
				Segment above = null;
				Segment below = null;

				// debug(s);

				// remove to-left segments
				if (pt.toLeft != null && !pt.toLeft.isEmpty()) { // will put
																	// to-right
																	// segments
																	// in place
																	// of
																	// to-left
																	// segments
					above = pt.toLeft.first().above;
					below = pt.toLeft.last().below;
					// debug(pt.name+" : "+pt.toLeft.first()+"("+pt.toLeft.first().hashCode()+")"+" -- "+pt.toLeft.last()+"("+below+")");
					below.above = above;
					above.below = below;
					checkIntersection(below, above, pointSet);

					// check if new point is aligned with existing segment
					boolean go = true;
					for (Segment segment = bottom.above; segment != top && go; segment = segment.above) {
						double orientation = Math.atan2(pt.y
								- segment.leftPoint.y, pt.x
								- segment.leftPoint.x);
						// error(segment.leftPoint.name+pt.name+" : "+orientation);
						if (Kernel.isEqual(orientation, segment.orientation)) {
							error("(1)" + pt.name + " aligned with " + segment);

							// cut the segment
							cut(segment, pt);

							// remove new left segment
							above = segment.above;
							below = segment.below;
							below.above = above;
							above.below = below;
							segment = below;

						} else if (orientation < segment.orientation) {
							go = false;

						}
					}

				} else { // search for the correct place for to-right segments
					debug("search the correct place : " + pt.name);
					boolean go = true;
					for (Segment segment = bottom.above; segment != top && go; segment = segment.above) {
						// error(segment.leftPoint.name+segment.rightPoint.name+" : "+segment.orientation);
						double orientation = Math.atan2(pt.y
								- segment.leftPoint.y, pt.x
								- segment.leftPoint.x);
						// error(segment.leftPoint.name+pt.name+" : "+orientation);
						if (Kernel.isEqual(orientation, segment.orientation)) {
							error("(2)" + pt.name + " aligned with " + segment);

							// cut the segment
							cut(segment, pt);

							// remove new left segment
							above = segment.above;
							below = segment.below;
							below.above = above;
							above.below = below;

							// go = false;

						} else if (orientation < segment.orientation) { // found
																		// the
																		// place
							go = false;
							above = segment;
							below = above.below;
							error(below + "<" + pt.name + "<" + above);
						}
					}
					if (go) { // when there are no segment between top and
								// bottom
						above = top;
						below = above.below;
					}
				}

				// put to-right segments
				if (pt.toRight != null) {
					Segment oldBelow = below;
					for (Segment seg : pt.toRight) {
						// debug(seg+"("+seg.hashCode()+")");
						below.above = seg;
						seg.below = below;
						below = seg;
					}
					below.above = above;
					above.below = below;
					checkIntersection(oldBelow, oldBelow.above, pointSet);
					checkIntersection(below, below.above, pointSet);
				}

				if (DEBUG) {
					for (Segment seg = bottom.above; seg != top; seg = seg.above) {
						s += seg.toString() + ",";
					}
					debug(s);
				}
			}

			if (DEBUG) {
				error("================== after intersections ===================");
				for (Point pt : pointSet) {
					debug(pt.debugSegments());
				}
				error("================== END ===================");
			}

		}

		setNonSelfIntersecting(pointSet);
	}

	private void setNonSelfIntersecting(TreeSet<Point> pointSet)
			throws TriangulationException {

		// prepare points as an array
		if (pointsArray.length < maxPointIndex) {
			pointsArray = new GPoint2D.Double[maxPointIndex];
		}

		// now all intersections are computed, and points are correctly chained
		// by oriented segments
		// we can divide the polygon turning e.g. counter clock-wise

		polygonPointsList.clear();

		error("=========== non self-intersecting polygons ==============");

		while (!pointSet.isEmpty()) {
			PolygonPoints polygonPoints = new PolygonPoints(
					nonSelfIntersectingPolygonPointComparator);
			Point start = pointSet.first();
			Segment segStart = start.toRight.first();

			if (segStart.usable > 1) {
				debug("*** " + segStart + " : " + segStart.usable);
				segStart.usable = segStart.usable % 2;
			}

			if (segStart.usable == 0) { // check if not set to 0 just above
				segStart.removeFromPoints();
				if (start.hasNoSegment()) {
					pointSet.remove(start);
					pointsArray[start.id] = new GPoint2D.Double(start.x,
							start.y);
				}
				start = segStart.rightPoint; // check right point
				if (start.hasNoSegment()) {
					pointSet.remove(start);
					pointsArray[start.id] = new GPoint2D.Double(start.x,
							start.y);
				}

			} else {

				Point currentPoint;
				Point currentPointNew;
				Point nextPoint = start;
				Point nextPointNew = nextPoint.clone();
				Point startPointNew = nextPointNew;
				// polygonPoints.add(nextPointNew);

				Segment segment = segStart;
				Segment next = null;

				Running running = Running.RIGHT;

				while (running != Running.STOP) {
					segment.running = running;
					currentPoint = nextPoint;
					currentPointNew = nextPointNew;
					boolean needsDiagonal = false;
					debug(nextPoint.name + ", " + segment + "");
					if (running == Running.RIGHT) {
						nextPoint = segment.rightPoint;
						if (nextPoint == start) {
							running = Running.STOP;
							next = segStart;
						} else {
							next = nextPoint.toLeft.lower(segment);
							if (next == null) {
								if (nextPoint.toRight != null
										&& !nextPoint.toRight.isEmpty()) {
									next = nextPoint.toRight.last();
								}
								if (next == null) { // no to-right segment
									next = nextPoint.toLeft.last();
									running = Running.LEFT;
									needsDiagonal = needsDiagonal(segment, next);
								}
							} else {
								running = Running.LEFT;
								needsDiagonal = needsDiagonal(segment, next);
							}
						}
						debug("next : " + next);
					} else { // running == Running.LEFT
						nextPoint = segment.leftPoint;
						if (nextPoint == start
								&& (start.toRight.higher(segStart) == segment || segStart == segment)) { // check
																											// if
																											// there
																											// are
																											// no
																											// segment
																											// between
																											// current
																											// and
																											// segStart
							running = Running.STOP;
							next = segStart;
						} else {
							next = nextPoint.toRight.lower(segment);
							if (next == null) {
								if (nextPoint.toLeft != null
										&& !nextPoint.toLeft.isEmpty()) {
									next = nextPoint.toLeft.last();
								}
								if (next == null) { // no to-left segment
									next = nextPoint.toRight.last();
									running = Running.RIGHT;
									needsDiagonal = needsDiagonal(segment, next);
								}
							} else {
								running = Running.RIGHT;
								needsDiagonal = needsDiagonal(segment, next);
							}

						}
					}

					// remove this segment from left and right points
					switch (segment.usable) {
					case 1:
						segment.removeFromPoints();
						segment.usable--; // ensure to throw an exception if
											// this segment is used once more
						break;

					case 0: // should not happen
						throw new TriangulationException(
								TriangulationException.Type.DEAD_END);

					default:
						segment.usable--;
						Segment clone = segment.clone();
						clone.running = segment.running;
						segment = clone;
						break;
					}

					// reconfigure segment to new points
					if (running != Running.STOP) {
						nextPointNew = nextPoint.clone();
					} else {
						nextPointNew = startPointNew;
						// error(nextPointNew.debugSegments());
					}
					if (segment.running == Running.RIGHT) {
						segment.leftPoint = currentPointNew;
						segment.rightPoint = nextPointNew;
					} else {
						segment.leftPoint = nextPointNew;
						segment.rightPoint = currentPointNew;
					}
					// debug(segment+"");
					if (!segment.addToPoints()) {
						comparedSameSegment.usable++;
						// debug("not new : "+segment+", "+segment.usable);
						// debug(segment.hashCode()+" / "+comparedSameSegment.hashCode());
					}

					// says if the point needs a diagonal
					nextPointNew.needsDiagonal = needsDiagonal;
					polygonPoints.needsDiagonals = polygonPoints.needsDiagonals
							|| needsDiagonal;

					// add current point to current polygon, check if not
					// already in it
					nextNewPointForNonSelfIntersectingPolygon = nextPointNew;
					polygonPoints.add(nextPointNew);
					nextPointNew = nextNewPointForNonSelfIntersectingPolygon;
					// debug(nextPointNew.debugSegments());

					// remove current point if no more segment
					if (currentPoint.hasNoSegment()) {
						// debug(currentPoint.name+" : remove");
						pointSet.remove(currentPoint);
						pointsArray[currentPoint.id] = new GPoint2D.Double(
								currentPoint.x, currentPoint.y);
					} else {
						// debug(currentPoint.name+" : keep");
					}

					// go on with next segment
					segment = next;

				}

				if (start.hasNoSegment()) {
					// debug(start.name+" : remove");
					pointSet.remove(start);
					pointsArray[start.id] = new GPoint2D.Double(start.x,
							start.y);
				} else {
					// debug(start.name+" : keep");
				}

				// add current polygon to list
				polygonPointsList.add(polygonPoints);

				if (DEBUG) {
					debug("--------------------------------");
					for (Point p : polygonPoints) {
						debug(p.debugSegments());
					}
				}
			}

		}

		error("=========== END ==============");

	}

	private enum Running {
		RIGHT, LEFT, STOP
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @param pointSet
	 * @throws TriangulationException
	 *             exception if an intersection is a segment left point (should
	 *             not occur, unless due to numerical precision)
	 */
	final private void checkIntersection(Segment a, Segment b,
			TreeSet<Point> pointSet) throws TriangulationException {

		debug("check intersection : " + a + "-" + b);

		if (a.isDummy() || b.isDummy()) {
			return;
		}

		if (a.rightPoint == b.rightPoint) {
			return;
		}

		// ensure a and b have correct equation
		a.setEquation();
		b.setEquation();

		// calculate possible intersection point
		double x = a.y * b.z - a.z * b.y;
		double y = a.z * b.x - a.x * b.z;
		double z = a.x * b.y - a.y * b.x;

		debug(x + "," + y + "," + z);

		if (!Kernel.isZero(z)) {
			// create intersection point
			// Point pt = new Point(x/z, y/z);
			double x1 = x / z;
			double y1 = y / z;

			// check intersection point is inside segments
			int al, ar, bl, br;
			if ((al = a.leftPoint.compareTo(x1, y1)) > 0
					|| (ar = a.rightPoint.compareTo(x1, y1)) < 0
					|| (bl = b.leftPoint.compareTo(x1, y1)) > 0
					|| (br = b.rightPoint.compareTo(x1, y1)) < 0) {
				// point outside the segments : no intersection

			} else if (al == 0) { // happen only after some aligned points and
									// a.leftPoint is current point in sweep
									// line
				debug("al : " + a.leftPoint.name);
				throw new TriangulationException(
						TriangulationException.Type.LEFT_POINT_INTERSECTION);
				/*
				 * pt = a.leftPoint;
				 */
			} else if (ar == 0) {
				Point pt = a.rightPoint;
				error("ar : " + pt.name);
				cut(b, pt);

			} else if (bl == 0) { // happen only after some aligned points and
									// b.leftPoint is current point in sweep
									// line
				debug("bl : " + b.leftPoint.name + " " + a + "/" + b);
				throw new TriangulationException(
						TriangulationException.Type.LEFT_POINT_INTERSECTION);
				/*
				 * pt = b.leftPoint;
				 */
			} else if (br == 0) {
				Point pt = b.rightPoint;
				error("br : " + pt.name);

				cut(a, pt);

			} else { // point strictly inside the segments

				/*
				 * // check if point is strictly inside the segments if
				 * (pt.compareToOnly(a.leftPoint) > 0 &&
				 * pt.compareToOnly(a.rightPoint) < 0 &&
				 * pt.compareToOnly(b.leftPoint) > 0 &&
				 * pt.compareToOnly(b.rightPoint) < 0){
				 */
				Point pt = new Point(x / z, y / z, maxPointIndex);
				pt.name = Integer.toString(pt.id);
				maxPointIndex++;

				error(a + "-" + b);
				debug("inter : " + pt.name + " : " + pt.x + "," + pt.y);

				// remove old segments
				a.removeFromPoints();
				b.removeFromPoints();

				// create new segments
				Segment a2 = new Segment(a.orientation, pt, a.rightPoint);
				Segment b2 = new Segment(b.orientation, pt, b.rightPoint);
				a2.addToPoints();
				b2.addToPoints();
				a2.usable = a.usable;
				b2.usable = b.usable;

				// set old segments right point
				a.rightPoint = pt;
				b.rightPoint = pt;

				// re-add old segments (with correct right points)
				a.addToPoints();
				b.addToPoints();

				// says that old segments need an update for equation
				// a.equationNeedsUpdate();
				// b.equationNeedsUpdate();

				// add point to set
				pointSet.add(pt);

				// error(pt.debugSegments());
			}
		}

	}

	final private void createSegment(Point point) {

		// debug(point.name+", "+((int)
		// (point.orientationToNext*180/Math.PI))+"°, "+point.next.name);
		Segment segment;
		if (Kernel.isGreater(point.orientationToNext, -Math.PI / 2)
				&& Kernel.isGreaterEqual(Math.PI / 2, point.orientationToNext)) { // point
																					// is
																					// left
																					// point
			segment = new Segment(point.orientationToNext, point, point.next);
		} else { // point is right point
			segment = new Segment(
					getReverseOrientation(point.orientationToNext), point.next,
					point);
		}

		segment.addToPoints();
	}

	final static private double getReverseOrientation(double orientation) {
		if (orientation > 0) {
			return orientation - Math.PI;
		}

		return orientation + Math.PI;

	}

	// //////////////////////////////////////////////
	// TRIANGULATION
	// //////////////////////////////////////////////

	private enum Chain {
		BOTH, BELOW, ABOVE
	}

	/**
	 * triangulate since polygon has been cut into non-self-intersecting pieces.
	 * After that, fansList contains all fans that cover the polygon.
	 */
	public void triangulate() {

		fansList.clear();

		for (PolygonPoints polygonPoints : polygonPointsList) {
			triangulate(polygonPoints);
		}
	}

	/**
	 * triangulate a polygon : cut it into monotone pieces, then feed the fans
	 * list
	 * 
	 * @param polygonPoints
	 */
	private void triangulate(PolygonPoints polygonPoints) {

		if (polygonPoints.size() < 3) {
			// not a drawable polygon
			error("*** not a polygon ***");
			return;
		}

		// ////////////////////////////////////////////
		// set diagonals

		if (polygonPoints.needsDiagonals) {

			if (DEBUG) {
				String s = "set diagonals of ";
				for (Point pt : polygonPoints) {
					s += pt.name;
					if (pt.needsDiagonal) {
						s += "(*)";
					}
				}

				debug(s);
			}

			// top and bottom (dummy) segments
			Segment top = new Segment();
			Segment bottom = new Segment();
			bottom.above = top;
			top.below = bottom;

			for (Point pt : polygonPoints) {

				Segment above = null;
				Segment below = null;

				// remove to-left segments
				if (pt.toLeft != null && !pt.toLeft.isEmpty()) { // will put
																	// to-right
																	// segments
																	// in place
																	// of
																	// to-left
																	// segments
					above = pt.toLeft.first().above;
					below = pt.toLeft.last().below;
					below.above = above;
					above.below = below;

					if (pt.needsDiagonal) {
						// error("diagonal to right : "+below+"<"+pt.name+"<"+above);
						Point pt2;
						if (below.rightPoint.compareToOnly(above.rightPoint) < 0) {
							pt2 = below.rightPoint;
						} else {
							pt2 = above.rightPoint;
						}

						Segment diagonal = new Segment(Math.atan2(pt2.y - pt.y,
								pt2.x - pt.x), pt, pt2);
						diagonal.addToPoints();
						diagonal.usable++;

						// error("diagonal to right : "+diagonal);
					}

				} else { // search for the correct place for to-right segments
							// error(pt.name);
					boolean go = true;
					for (Segment segment = bottom.above; segment != top && go; segment = segment.above) {
						double orientation = Math.atan2(pt.y
								- segment.leftPoint.y, pt.x
								- segment.leftPoint.x);
						if (orientation < segment.orientation) { // found the
																	// place
							go = false;
							above = segment;
							below = above.below;
						}
					}
					if (go) { // when there are no segment between top and
								// bottom
						above = top;
						below = above.below;
					}

					if (pt.needsDiagonal) {
						// error("diagonal to left : "+below+"<"+pt.name+"<"+above);
						if (below.usable > 1) {
							below.removeFromPoints();
							below.rightPoint = pt;
							below.addToPoints();
							// error("below is diagonal, replace : "+below);
							// remove below
							below = below.below;
							below.above = above;
							above.below = below;
						} else if (above.usable > 1) {
							above.removeFromPoints();
							above.rightPoint = pt;
							above.addToPoints();
							// error("above is diagonal, replace : "+above);
							// remove above
							above = above.above;
							below.above = above;
							above.below = below;
						} else {
							Point pt2;
							if (below.leftPoint.compareToOnly(above.leftPoint) < 0) {
								pt2 = above.leftPoint;
							} else {
								pt2 = below.leftPoint;
							}
							Segment diagonal = new Segment(Math.atan2(pt.y
									- pt2.y, pt.x - pt2.x), pt2, pt);
							diagonal.addToPoints();
							diagonal.usable++;

							// error("diagonal to left : "+diagonal);
						}
					}

				}

				// put to-right segments
				if (pt.toRight != null) {
					for (Segment seg : pt.toRight) {
						below.above = seg;
						seg.below = below;
						below = seg;
					}
					below.above = above;
					above.below = below;
				}

			}
		}

		// ////////////////////////////////////////////
		// cut in monotone pieces (it may happen even if no diagonal, when some
		// points are re-used

		while (!polygonPoints.isEmpty()) {
			String s = "Monotone piece : ";

			Point start = polygonPoints.first();
			Point currentPoint = start;
			Point nextPoint;
			Segment segStart = start.toRight.first();
			Segment segment = segStart;
			Segment next = null;

			Running running = Running.RIGHT;
			Running oldRunning;

			while (running != Running.STOP) {
				oldRunning = running;
				if (running == Running.RIGHT) {
					nextPoint = segment.rightPoint;
					if (nextPoint == start) {
						running = Running.STOP;
						// next = segStart;
					} else {
						next = nextPoint.toLeft.lower(segment);
						if (next == null) {
							if (nextPoint.toRight != null
									&& !nextPoint.toRight.isEmpty()) {
								next = nextPoint.toRight.last();
							}
							if (next == null) { // no to-right segment
								next = nextPoint.toLeft.higher(segment);
								running = Running.LEFT;
							}
						} else {
							running = Running.LEFT;
						}

					}
				} else { // running == Running.LEFT
					nextPoint = segment.leftPoint;
					if (nextPoint == start) {
						running = Running.STOP;
						// next = segStart;
					} else {
						next = nextPoint.toRight.lower(segment);
						if (next == null) {
							if (nextPoint.toLeft != null
									&& !nextPoint.toLeft.isEmpty()) {
								next = nextPoint.toLeft.last();
							}
							if (next == null) { // no to-left segment
								next = nextPoint.toRight.higher(segment);
								running = Running.RIGHT;
							}
						} else {
							running = Running.RIGHT;
						}

					}
				}

				s += currentPoint.name;

				segment.removeFromPoints();
				if (oldRunning == Running.LEFT) {
					if (segment.usable > 1) {
						// debug("segment "+segment+" is diagonal, running left, keep point : "+nextPoint.name);
						segment.usable--; // usable once less, clone it
						Segment clone = segment.clone();
						clone.addToPoints();
						clone.usable = segment.usable;
					}

					if (running == Running.LEFT) {
						next.next = segment;
					}

				} else { // oldRunning == Running.RIGHT
					if (segment.usable > 1) {
						// debug("segment "+segment+" is diagonal, running right, keep point : "+currentPoint.name);
						segment.usable--; // usable once less, clone it
						Segment clone = segment.clone();
						clone.addToPoints();
						clone.usable = segment.usable;
					}

					if (running == Running.RIGHT) {
						segment.next = next;
					}
				}

				/*
				 * currentPoint.usable--; if (currentPoint.usable == 0){
				 * polygonPoints.remove(currentPoint); }
				 */

				if (currentPoint.hasNoSegment()) {
					polygonPoints.remove(currentPoint);
					debug("remove : " + currentPoint.name);
				} else {
					debug("keep : " + currentPoint.name);
				}

				segment = next;
				currentPoint = nextPoint;

			}

			/*
			 * s+="\nabove : "; for (Segment seg = segment ; seg != null ; seg =
			 * seg.next ){ s += seg+","; } s+="\nbelow : "; for (Segment seg =
			 * segStart ; seg != null ; seg = seg.next ){ s += seg+","; }
			 */

			if (start.hasNoSegment()) {
				polygonPoints.remove(start);
				debug("remove : " + start.name);
			} else {
				debug("keep : " + start.name);
			}

			debug(s);

			triangulate(segStart, segment);
		}

	}

	static final private boolean needsDiagonal(Segment seg1, Segment seg2) {
		// debug(seg1+"("+((int)
		// (seg1.orientation*180/Math.PI))+"°)"+","+seg2+"("+((int)
		// (seg2.orientation*180/Math.PI))+"°)");
		if (seg1.orientation < seg2.orientation) {
			return true;
		}
		return false;
	}

	public void triangulate(Segment firstBelow, Segment firstAbove) {

		// init stack
		Chain chain;
		Stack<Point> stack = new Stack<Point>();
		stack.push(firstAbove.leftPoint);

		Point pAbove = firstAbove.rightPoint;
		Point pBelow = firstBelow.rightPoint;
		if (pAbove.compareToOnly(pBelow) < 0) {
			// debug("above : "+pAbove.name);
			chain = Chain.ABOVE;
			stack.push(pAbove);
			firstAbove = firstAbove.next;
		} else {
			// debug("below : "+pBelow.name);
			chain = Chain.BELOW;
			stack.push(pBelow);
			firstBelow = firstBelow.next;
		}

		// loop
		while (firstAbove != null && firstBelow != null) {
			String s = "fan : ";
			// ArrayList<Integer> currentTriangleFan = new ArrayList<Integer>();
			TriangleFan currentTriangleFan;
			Point top = stack.peek();
			Point vi;
			Chain viChain;
			// debug(firstAbove+"/"+firstBelow);
			if (chain == Chain.ABOVE) { // top point is pAbove
				// if (firstAbove != null){
				pAbove = firstAbove.rightPoint;
				if (pAbove.compareToOnly(pBelow) < 0) { // next point is above
					vi = pAbove;
					viChain = Chain.ABOVE;
					firstAbove = firstAbove.next;
				} else { // next point is below
					vi = pBelow;
					viChain = Chain.BELOW;
					firstBelow = firstBelow.next;
				}
				/*
				 * }else{ // next point is below vi = pBelow; viChain =
				 * Chain.BELOW; firstBelow = firstBelow.next; }
				 */
			} else { // (chain == Chain.BELOW){ // top point is pBelow
						// if (firstBelow != null){
				pBelow = firstBelow.rightPoint;
				if (pBelow.compareToOnly(pAbove) < 0) { // next point is below
					vi = pBelow;
					viChain = Chain.BELOW;
					firstBelow = firstBelow.next;
				} else { // next point is above
					vi = pAbove;
					viChain = Chain.ABOVE;
					firstAbove = firstAbove.next;
				}
				/*
				 * }else{ // next point is above vi = pAbove; viChain =
				 * Chain.ABOVE; firstAbove = firstAbove.next; }
				 */
			}

			boolean clockWise = false;

			// boolean viBetween = vi > min && vi < max;
			// debugDiagonal("(vi > min && vi < max) , (top > min && top < max) : "+(vi
			// > min && vi < max)+","+(top > min && top < max),vi,top);
			if (viChain != chain) { // vi and top are not on the same chain
				debugDiagonal("case 2 ", top, vi);
				// debug("case 2, "+viChain+" : "+vi.name);
				if (viChain == Chain.ABOVE) {
					clockWise = true;
				}
				currentTriangleFan = new TriangleFan(vi.id, clockWise);
				s += vi.name;
				while (!stack.isEmpty()) {
					Point v = stack.pop();
					currentTriangleFan.add(v.id);
					s += v.name;
					debugDiagonal("diagonal : ", vi, v);
				}
				stack.push(top);
				stack.push(vi);

			} else { // vi and top are on the same chain
				debugDiagonal("case 1 ", top, vi);
				// debug("case 1, "+viChain+" : "+vi.name);
				if (viChain == Chain.BELOW) {
					clockWise = true;
				}
				currentTriangleFan = new TriangleFan(vi.id, clockWise);

				s += vi.name;

				// first correct point
				Point vk = stack.pop();
				currentTriangleFan.add(vk.id);
				s += vk.name;
				debugDiagonal("diagonal ", vi, vk);
				double dx2 = vk.x - vi.x;
				double dy2 = vk.y - vi.y;

				boolean go = true;
				while (!stack.isEmpty() && go) {
					double dx1 = dx2;
					double dy1 = dy2;
					Point v = stack.pop();
					dx2 = v.x - vi.x;
					dy2 = v.y - vi.y;
					if (Kernel.isGreater(dx1 * dy2, dx2 * dy1)
							^ (viChain != Chain.BELOW)) { // not same
															// orientation
						stack.push(v); // re-push v in stack
						go = false;
					} else {
						vk = v;
						currentTriangleFan.add(vk.id);
						s += vk.name;
						debugDiagonal("diagonal ", vi, vk);
					}
				}
				stack.push(vk);
				stack.push(vi);

			}

			if (currentTriangleFan.size() > 1) { // add fan only if at least 3
													// points
				fansList.add(currentTriangleFan);
				if (DEBUG) {
					if (clockWise) {
						error(s);
					} else {
						debug(s);
					}
				}
			}

			chain = viChain;

		}

		/*
		 * String s="fans: "; for (ArrayList<Point> fan : ret){ for (Point p :
		 * fan){ s+=p.name; } s+=", "; } debug(s);
		 */

	}

	final static private void debugDiagonal(String s, Point p1, Point p2) {
		debug(s + ": " + p1.name + "," + p2.name);
	}

	/**
	 * 
	 * @return list of list of points indices, which constitute triangle fans
	 *         covering the polygon
	 */
	public ArrayList<TriangleFan> getTriangleFans() {
		return fansList;

	}

	private Coords[] completeVertices = new Coords[0];
	private Coords[] corners = new Coords[8];

	/**
	 * 
	 * @param vertices
	 *            original points vertices
	 * @param cs
	 *            coord sys to compute 3D points for intersections
	 * @param length
	 *            vertices length
	 * @return complete 3D vertex array (with intersections)
	 */
	public Coords[] getCompleteVertices(Coords[] vertices, CoordSys cs,
			int length) {
		if (maxPointIndex == length) {
			return vertices;
		}

		if (completeVertices.length < maxPointIndex) {
			completeVertices = new Coords[maxPointIndex];
		}

		for (int i = 0; i < length; i++) {
			completeVertices[i] = vertices[i];
		}

		for (int i = length; i < maxPointIndex; i++) {
			GPoint2D.Double point = pointsArray[i];
			if (point != null) {
				completeVertices[i] = cs.getPoint(point.x, point.y);
			}
		}

		return completeVertices;
	}

	public void setCorners(Coords[] corners) {
		this.corners = corners;

	}
}
