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
 * Base class for quadtree algorithms
 */
abstract class QuadTree {
	/**
	 * 
	 */
	private final GeoImplicitCurve geoImplicitCurve;

	/**
	 * All corners are inside / outside
	 */
	@SuppressWarnings("unused")
	public static final int T0000 = 0;

	/**
	 * only bottom left corner is inside / outside
	 */
	public static final int T0001 = 1;

	/**
	 * bottom right corner is inside / outside
	 */
	public static final int T0010 = 2;

	/**
	 * both corners at the bottom are inside / outside
	 */
	public static final int T0011 = 3;

	/**
	 * top left corner is inside / outside
	 */
	public static final int T0100 = 4;

	/**
	 * opposite corners are inside / outside. NOTE: This configuration is
	 * regarded as invalid
	 */
	public static final int T0101 = 5;

	/**
	 * both the corners at the left are inside / outside
	 */
	public static final int T0110 = 6;

	/**
	 * only top left corner is inside / outside
	 */
	public static final int T0111 = 7;

	/**
	 * invalid configuration. expression value is undefined / infinity for
	 * at least one of the corner
	 */
	public static final int T_INV = -1;
	public static final int EMPTY = 0;
	public static final int FINISHED = Integer.MAX_VALUE;
	public static final int VALID = 1;
	/**
	 * it would be better to adjust LIST_THRESHOLD based on platform
	 */
	public int LIST_THRESHOLD = 48;
	protected double x;
	protected double y;
	protected double w;
	protected double h;
	protected double scaleX;
	protected double scaleY;
	protected ArrayList<MyPoint> locusPoints;
	private LinkedList<PointList> openList = new LinkedList<PointList>();
	private MyPoint[] pts = new MyPoint[2];
	private PointList p1, p2;
	private MyPoint temp;
	private ListIterator<PointList> itr1, itr2;

	public QuadTree(GeoImplicitCurve geoImplicitCurve) {
		this.geoImplicitCurve = geoImplicitCurve;

	}

	public int config(Rect r) {
		int config = 0;
		for (int i = 0; i < 4; i++) {
			config = (config << 1) | sign(r.evals[i]);
		}
		return config >= 8 ? (~config) & 0xf : config;
	}

	/**
	 * 
	 * @param val
	 *            value to check
	 * @return the sign depending on the value. if value is infinity or NaN
	 *         it returns T_INV, otherwise it returns 1 for +ve value 0
	 *         otherwise
	 */
	public int sign(double val) {
		if (Double.isInfinite(val) || Double.isNaN(val)) {
			return T_INV;
		} else if (val > 0.0) {
			return 1;
		} else {
			return 0;
		}
	}

	public void abortList() {
		itr1 = openList.listIterator();
		while (itr1.hasNext()) {
			p1 = itr1.next();
			locusPoints.add(p1.start);
			locusPoints.addAll(p1.pts);
			locusPoints.add(p1.end);
		}
		openList.clear();
	}

	private boolean equal(MyPoint q1, MyPoint q2) {
		return Kernel.isEqual(q1.x, q2.x, 1e-10)
				&& Kernel.isEqual(q1.y, q2.y, 1e-10);
	}

	public int addSegment(Rect r) {
		int status = createSegment(r);
		if (status == VALID) {
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

	public int createSegment(Rect r) {
		int gridType = config(r);
		if (gridType == T0101 || gridType == T_INV) {
			return gridType;
		}

		double x1 = r.x1(), x2 = r.x2(), y1 = r.y1(), y2 = r.y2();
		double tl = r.evals[0], tr = r.evals[1], br = r.evals[2], bl = r.evals[3];
		double q1 = 0.0, q2 = 0.0;

		switch (gridType) {
		// one or three corners are inside / outside
		case T0001:
			pts[0] = new MyPoint(x1, GeoImplicitCurve.interpolate(bl, tl, y2, y1), false);
			pts[1] = new MyPoint(GeoImplicitCurve.interpolate(bl, br, x1, x2), y2, true);
			q1 = Math.min(Math.abs(bl), Math.abs(tl));
			q2 = Math.min(Math.abs(bl), Math.abs(br));
			break;

		case T0010:
			pts[0] = new MyPoint(x2, GeoImplicitCurve.interpolate(br, tr, y2, y1), false);
			pts[1] = new MyPoint(GeoImplicitCurve.interpolate(br, bl, x2, x1), y2, true);
			q1 = Math.min(Math.abs(br), Math.abs(tr));
			q2 = Math.min(Math.abs(br), Math.abs(bl));
			break;

		case T0100:
			pts[0] = new MyPoint(x2, GeoImplicitCurve.interpolate(tr, br, y1, y2), false);
			pts[1] = new MyPoint(GeoImplicitCurve.interpolate(tr, tl, x2, x1), y1, true);
			q1 = Math.min(Math.abs(tr), Math.abs(br));
			q2 = Math.min(Math.abs(tr), Math.abs(tl));
			break;

		case T0111:
			pts[0] = new MyPoint(x1, GeoImplicitCurve.interpolate(tl, bl, y1, y2), false);
			pts[1] = new MyPoint(GeoImplicitCurve.interpolate(tl, tr, x1, x2), y1, true);
			q1 = Math.min(Math.abs(bl), Math.abs(tl));
			q2 = Math.min(Math.abs(tl), Math.abs(tr));
			break;

		// two consecutive corners are inside / outside
		case T0011:
			pts[0] = new MyPoint(x1, GeoImplicitCurve.interpolate(tl, bl, y1, y2), false);
			pts[1] = new MyPoint(x2, GeoImplicitCurve.interpolate(tr, br, y1, y2), true);
			q1 = Math.min(Math.abs(tl), Math.abs(bl));
			q2 = Math.min(Math.abs(tr), Math.abs(br));
			break;

		case T0110:
			pts[0] = new MyPoint(GeoImplicitCurve.interpolate(tl, tr, x1, x2), y1, false);
			pts[1] = new MyPoint(GeoImplicitCurve.interpolate(bl, br, x1, x2), y2, true);
			q1 = Math.min(Math.abs(tl), Math.abs(tr));
			q2 = Math.min(Math.abs(bl), Math.abs(br));
			break;
		default:
			return EMPTY;
		}
		// check continuity of the function between P1 and P2
		double p = Math.abs(this.geoImplicitCurve.evaluateImplicitCurve(pts[0].x, pts[0].y));
		double q = Math.abs(this.geoImplicitCurve.evaluateImplicitCurve(pts[1].x, pts[1].y));
		if ((p <= q1 && q <= q2)) {
			return VALID;
		}
		return EMPTY;
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

	public void polishPointOnPath(GeoPointND pt) {
		pt.setUndefined();
	}

	public List<Coords> probablePoints(GeoImplicitCurve other, int n) {
		double xMin = Math.max(x, other.quadTree.x);
		double yMin = Math.max(y, other.quadTree.y);
		double xMax = Math.min(x + w, other.quadTree.x + w);
		double yMax = Math.min(y + h, other.quadTree.y + h);
		return GeoImplicitCurve.probableInitialPoints(this.geoImplicitCurve.getExpression(), other.getExpression(),
				xMin, yMin, xMax, yMax, n);
	}

	public int edgeConfig(Rect r) {
		int config = (intersect(r.evals[0], r.evals[1]) << 3)
				| (intersect(r.evals[1], r.evals[2]) << 2)
				| (intersect(r.evals[2], r.evals[3]) << 1)
				| (intersect(r.evals[3], r.evals[0]));
		if (config == 15 || config == 0) {
			return EMPTY;
		}
		return config;
	}

	/**
	 * 
	 * @param c1
	 *            the value of curve at one of the square vertices
	 * @param c2
	 *            the value of curve at the other vertex
	 * @return true if the edge connecting two vertices intersect with curve
	 *         segment
	 */
	private int intersect(double c1, double c2) {
		if (c1 * c2 <= 0.0) {
			return 1;
		}
		return 0;
	}

	public abstract void updatePath();

	class PointList {
		MyPoint start;
		MyPoint end;
		LinkedList<MyPoint> pts = new LinkedList<MyPoint>();

		public PointList(MyPoint start, MyPoint end) {
			this.start = start;
			this.end = end;
			this.start.lineTo = false;
			this.end.lineTo = true;
		}

		public void mergeTo(PointList pl) {
			this.pts.addLast(this.end);
			if (pl == this) {
				MyPoint startCopy = new MyPoint(this.start.x, this.start.y,
						true);
				this.pts.addLast(startCopy);
				return;
			}
			pl.start.lineTo = true;
			this.pts.addLast(pl.start);
			this.end = pl.end;
			int s1 = this.pts.size(), s2 = pl.pts.size();

			if (s2 == 0) {
				return;
			}

			if (s1 < s2) {
				ListIterator<MyPoint> itr = this.pts.listIterator(s1 - 1);
				while (itr.hasPrevious()) {
					pl.pts.addFirst(itr.previous());
				}
				this.pts = pl.pts;
			} else {
				ListIterator<MyPoint> itr = pl.pts.listIterator();
				while (itr.hasNext()) {
					this.pts.addLast(itr.next());
				}
			}
		}

		public void extendBack(MyPoint p) {
			p.lineTo = false;
			this.start.lineTo = true;
			this.pts.addFirst(start);
			this.start = p;
		}

		public void extendFront(MyPoint p) {
			p.lineTo = true;
			this.pts.addLast(this.end);
			this.end = p;
		}
	}
}