package org.geogebra.common.kernel.implicit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Base class for quad-tree algorithms
 * 
 */
abstract class QuadTree {
	private final GeoImplicitCurve geoImplicitCurve;

	/**
	 * it would be better to adjust LIST_THRESHOLD based on platform
	 */
	public int LIST_THRESHOLD = 48;
	/**
	 * Leftmost x coordinate
	 */
	protected double x;
	/**
	 * Topmost y coordinate
	 */
	protected double y;
	/**
	 * Width of the display
	 */
	protected double w;
	/**
	 * Height of the display
	 */
	protected double h;
	/**
	 * pixel per unit in x-axis direction
	 */
	protected double scaleX;
	/**
	 * pixel per unit in y-axis direction
	 */
	protected double scaleY;
	/**
	 * Array of locus point
	 */
	protected ArrayList<MyPoint> locusPoints;
	/**
	 * List of the open segments
	 */
	private LinkedList<PointList> openList = new LinkedList<PointList>();
	/**
	 * Temporary point array
	 */
	private MyPoint[] pts = new MyPoint[2];
	private PointList p1, p2;
	private MyPoint temp;
	private ListIterator<PointList> itr1, itr2;

	/**
	 * Create a QuadTree instance
	 * 
	 * @param geoImplicitCurve
	 *            the curve equation
	 */
	public QuadTree(GeoImplicitCurve geoImplicitCurve) {
		this.geoImplicitCurve = geoImplicitCurve;

	}

	/**
	 * 
	 * @param x
	 *            first number
	 * @param y
	 *            second number
	 * @return true of x and y have opposite sign or one of them is zero
	 */
	private static boolean hasOppositeSignOrZero(double x, double y) {
		return x * y <= 0;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private static boolean hasOppositeSign(double x, double y) {
		return x * y < Kernel.STANDARD_PRECISION;
	}

	/**
	 * clear old list and start new list of segments
	 */
	public void abortList() {
		itr1 = openList.listIterator();
		while (itr1.hasNext()) {
			p1 = itr1.next();
			locusPoints.add(p1.start);
			locusPoints.addAll(p1.points);
			locusPoints.add(p1.end);
		}
		openList.clear();
	}

	private static boolean equal(MyPoint q1, MyPoint q2) {
		return Kernel.isEqual(q1.x, q2.x, 1e-10)
				&& Kernel.isEqual(q1.y, q2.y, 1e-10);
	}

	/**
	 * build a segment and add it to GeoLine
	 * 
	 * @param r
	 *            rectangle
	 * @return status
	 */
	public int addSegment(Rect r) {
		int status = createSegment(r);
		if (status == Consts.VALID) {
			if (pts == null || pts[0] == null || pts[1] == null) {
				return Consts.VALID;
			}
			if (pts[0].x > pts[1].x) {
				temp = pts[0];
				pts[0] = pts[1];
				pts[1] = temp;
			}
			itr1 = openList.listIterator();
			itr2 = openList.listIterator();
			boolean flag1 = false, flag2 = false;
			while (itr1.hasNext()) {
				p1 = itr1.next();
				if (equal(pts[1], p1.start)) {
					flag1 = true;
					break;
				}
			}

			while (itr2.hasNext()) {
				p2 = itr2.next();
				if (equal(pts[0], p2.end)) {
					flag2 = true;
					break;
				}
			}

			if (flag1 && flag2) {
				itr1.remove();
				p2.mergeTo(p1);
			} else if (flag1) {
				p1.extendBack(pts[0]);
			} else if (flag2) {
				p2.extendFront(pts[1]);
			} else {
				openList.addFirst(new PointList(pts[0], pts[1]));
			}
			if (openList.size() > LIST_THRESHOLD) {
				abortList();
			}
		}
		return status;
	}

	/**
	 * Create line from (x1, y1) to (x2, y2)
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return status
	 */
	private int createLine(double x1, double y1, double x2, double y2) {
		this.pts[0] = new MyPoint(x1, y1, false);
		this.pts[1] = new MyPoint(x2, y2, true);
		return Consts.VALID;
	}

	private boolean createPoint(Rect r, int e, int k) {
		double xi, yi, v;
		switch (e) {
		case 0:
			xi = interpolate(r.evals[0], r.evals[1], r.x1(), r.x2());
			v = geoImplicitCurve.derivativeX(r.x1(), r.y1());
			if (!hasOppositeSign(v, r.evals[0])) {
				return false;
			}
			this.pts[k] = new MyPoint(xi, r.y1(), k != 0);
			return true;
		case 2:
			xi = interpolate(r.evals[3], r.evals[2], r.x1(), r.x2());
			v = geoImplicitCurve.derivativeX(r.x1(), r.y2());
			if (!hasOppositeSign(v, r.evals[3])) {
				return false;
			}
			this.pts[k] = new MyPoint(xi, r.y2(), k != 0);
			return true;
		case 1:
			yi = interpolate(r.evals[1], r.evals[2], r.y1(), r.y2());
			v = geoImplicitCurve.derivativeY(r.x2(), r.y1());
			if (!hasOppositeSign(v, r.evals[1])) {
				return false;
			}
			this.pts[k] = new MyPoint(r.x2(), yi, k != 0);
			return true;
		case 3:
			yi = interpolate(r.evals[0], r.evals[3], r.y1(), r.y2());
			v = geoImplicitCurve.derivativeY(r.x1(), r.y1());
			if (!hasOppositeSign(v, r.evals[0])) {
				return false;
			}
			this.pts[k] = new MyPoint(r.x1(), yi, k != 0);
			return true;
		}
		return false;
	}

	/**
	 * Create line by interpolating two edges
	 * 
	 * @return status
	 */
	private int createLine(Rect r, int e1, int e2) {
		if (createPoint(r, e1, 0) && createPoint(r, e2, 1)) {
			r.shares |= (1 << (0x4 | e1)) | (1 << (0x4 | e2));
			return Consts.VALID;
		}
		return Consts.EMPTY;
	}

	/**
	 * Create line by from (x, y) to interpolating edge e
	 * 
	 * @return status
	 */
	private int createLine(Rect r, int e, double x1, double y1) {
		if (createPoint(r, e, 0)) {
			this.pts[1] = new MyPoint(x1, y1, true);
			r.shares |= (1 << (0x4 | e));
			return Consts.VALID;
		}
		return Consts.EMPTY;
	}

	private int createSegment(Rect r) {
		if (r.is(Consts.EMPTY)) {
			return Consts.EMPTY;
		}
		double x1 = r.x1(), x2 = r.x2(), y1 = r.y1(), y2 = r.y2();
		double tl = r.evals[0], tr = r.evals[1], br = r.evals[2],
				bl = r.evals[3];

		switch (r.zero) {
		case 0:
			if (r.neg == r.pos && !hasOppositeSignOrZero(tl, br)) {
				return Consts.AMBIGUOUS;
			}
			int k = 0;
			int[] e = { 0, 0 };
			for (int i = 0; i < 4; i++) {
				if (hasOppositeSignOrZero(r.evals[i], r.evals[(i + 1) & 0x3])) {
					e[k++] = i;
				}
			}
			return createLine(r, e[0], e[1]);
		case 1:
			if (r.neg == 3 || r.pos == 3) {
				if (tl == 0.0) {
					return createLine(x1, y1, x1, y1);
				}
				if (tr == 0.0) {
					return createLine(x2, y1, x2, y1);
				}
				if (bl == 0.0) {
					return createLine(x1, y2, x1, y2);
				}
				if (br == 0.0) {
					return createLine(x2, y2, x2, y2);
				}
			}
			if (tl == 0.0) {
				if (hasOppositeSignOrZero(bl, br)) {
					if (hasOppositeSignOrZero(tr, br)) {
						return createLine(r, Consts.RIGHT, Consts.BOTTOM);
					}
					return createLine(r, Consts.BOTTOM, x1, y1);
				}
				if (hasOppositeSignOrZero(tr, br)) {
					return createLine(x1, y1, x2, Consts.RIGHT);
				}
				return Consts.EMPTY;
			}
			if (tr == 0.0) {
				if (hasOppositeSignOrZero(bl, br)) {
					if (hasOppositeSignOrZero(tl, bl)) {
						return createLine(r, Consts.BOTTOM, Consts.LEFT);
					}
					return createLine(r, Consts.BOTTOM, x2, y1);
				}
				if (hasOppositeSignOrZero(tl, bl)) {
					return createLine(r, Consts.LEFT, x2, y1);
				}
				return Consts.EMPTY;
			}
			if (br == 0.0) {
				if (hasOppositeSignOrZero(tl, bl)) {
					if (hasOppositeSignOrZero(tl, tr)) {
						return createLine(r, Consts.TOP, Consts.LEFT);
					}
					return createLine(r, Consts.LEFT, x2, y2);
				}
				if (hasOppositeSignOrZero(tl, tr)) {
					return createLine(r, Consts.TOP, x2, y2);
				}
				return Consts.EMPTY;
			}
			if (bl == 0.0) {
				if (hasOppositeSignOrZero(tl, tr)) {
					if (hasOppositeSignOrZero(tr, br)) {
						return createLine(r, Consts.TOP, Consts.RIGHT);
					}
					return createLine(r, Consts.TOP, x1, y2);
				}
				if (hasOppositeSignOrZero(tr, br)) {
					return createLine(r, Consts.RIGHT, x1, y2);
				}
			}
			return Consts.EMPTY;
		case 2:
			if (r.pos == 2 || r.neg == 2) {
				if (tl == 0.0) {
					if (tr == 0.0) {
						return createLine(x1, y1, x2, y1);
					}
					if (bl == 0.0) {
						return createLine(x1, y1, x1, y2);
					}
				}
				if (br == 0.0) {
					if (tr == 0.0) {
						return createLine(x2, y1, x2, y2);
					}
					if (bl == 0.0) {
						return createLine(x1, y2, x2, y2);
					}
				}
			} else {
				if (tl == 0.0 && br == 0.0) {
					return createLine(x1, y1, x2, y2);
				}
				if (bl == 0.0 && tr == 0.0) {
					return createLine(x1, y2, x2, y1);
				}
			}
			return Consts.EMPTY;
		}
		return Consts.EMPTY;
	}

	/**
	 * force to redraw the rectangular area bounded by (startX, startY,
	 * startX + w, startY + h)
	 * 
	 * @param startX
	 *            starting x coordinate
	 * @param startY
	 *            starting y coordinate
	 * @param width
	 *            width of the rectangular view
	 * @param height
	 *            height of the rectangular view
	 * @param slX
	 *            scaleX
	 * @param slY
	 *            scaleY
	 */
	public void updatePath(double startX, double startY, double width,
			double height, double slX, double slY) {
		this.x = startX;
		this.y = startY;
		this.w = width;
		this.h = height;
		this.scaleX = slX;
		this.scaleY = slY;
		this.locusPoints = this.geoImplicitCurve.getLocus().getPoints();
		this.updatePath();
		this.abortList();
	}

	/**
	 * Polish a point on path
	 * 
	 * @param pt
	 *            point
	 */
	public void polishPointOnPath(GeoPointND pt) {
		pt.setUndefined();
	}

	/**
	 * List of the probable points which might lie on the path
	 * 
	 * @param other
	 *            Implicit Curve
	 * @param n
	 *            Number of output point
	 * @return List of points
	 */
	public List<Coords> probablePoints(GeoImplicitCurve other, int n) {
		double xMin = Math.max(x, other.quadTree.x);
		double yMin = Math.max(y, other.quadTree.y);
		double xMax = Math.min(x + w, other.quadTree.x + w);
		double yMax = Math.min(y + h, other.quadTree.y + h);
		return GeoImplicitCurve.probableInitialPoints(this.geoImplicitCurve.getExpression(), other.getExpression(),
				xMin, yMin, xMax, yMax, n);
	}

	/**
	 * Update the current path (Force redraw)
	 */
	public abstract void updatePath();

	private class PointList {
		MyPoint start;
		MyPoint end;
		LinkedList<MyPoint> points = new LinkedList<MyPoint>();

		public PointList(MyPoint start, MyPoint end) {
			this.start = start;
			this.end = end;
			this.start.lineTo = false;
			this.end.lineTo = true;
		}

		public void mergeTo(PointList pl) {
			this.points.addLast(this.end);
			if (pl == this) {
				MyPoint startCopy = new MyPoint(this.start.x, this.start.y,
						true);
				this.points.addLast(startCopy);
				return;
			}
			pl.start.lineTo = true;
			this.points.addLast(pl.start);
			this.end = pl.end;
			int s1 = this.points.size(), s2 = pl.points.size();

			if (s2 == 0) {
				return;
			}

			if (s1 < s2) {
				ListIterator<MyPoint> itr = this.points.listIterator(s1 - 1);
				while (itr.hasPrevious()) {
					pl.points.addFirst(itr.previous());
				}
				this.points = pl.points;
			} else {
				ListIterator<MyPoint> itr = pl.points.listIterator();
				while (itr.hasNext()) {
					this.points.addLast(itr.next());
				}
			}
		}

		public void extendBack(MyPoint p) {
			p.lineTo = false;
			this.start.lineTo = true;
			this.points.addFirst(start);
			this.start = p;
		}

		public void extendFront(MyPoint p) {
			p.lineTo = true;
			this.points.addLast(this.end);
			this.end = p;
		}
	}

	private static double interpolate(double fa, double fb, double p1,
			double p2) {
		double r = -fb / (fa - fb);
		if (r >= 0 && r <= 1) {
			return r * (p1 - p2) + p2;
		}
		return (p1 + p2) * 0.5;
	}
}