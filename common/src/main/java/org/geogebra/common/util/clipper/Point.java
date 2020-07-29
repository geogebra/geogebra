package org.geogebra.common.util.clipper;

import java.util.Comparator;

import org.geogebra.common.util.DoubleUtil;

public abstract class Point<T extends Number & Comparable<T>> {
	private final static NumberComparator NUMBER_COMPARATOR = new NumberComparator();

	protected T x;

	protected T y;

	protected T z;

	public static class DoublePoint extends Point<Double> {
		public static double getDeltaX(DoublePoint pt1, DoublePoint pt2) {
			if (pt1.getY() == pt2.getY()) {
				return Edge.HORIZONTAL;
			}
			return (pt2.getX() - pt1.getX()) / (pt2.getY() - pt1.getY());
		}

		public DoublePoint() {
			this(0, 0);
		}

		public DoublePoint(double x, double y) {
			this(x, y, 0);
		}

		public DoublePoint(double x, double y, double z) {
			super(x, y, z);
		}

		public DoublePoint(DoublePoint other) {
			super(other);
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getZ() {
			return z;
		}
	}

	private static class NumberComparator<T extends Number & Comparable<T>>
			implements Comparator<T> {

		@Override
		public int compare(T a, T b) throws ClassCastException {
			return a.compareTo(b);
		}
	}

	static boolean arePointsClose(Point<? extends Number> pt1,
			Point<? extends Number> pt2, double distSqrd) {
		final double dx = pt1.x.doubleValue() - pt2.x.doubleValue();
		final double dy = pt1.y.doubleValue() - pt2.y.doubleValue();
		return dx * dx + dy * dy <= distSqrd;
	}

	static double distanceFromLineSqrd(Point<? extends Number> pt,
			Point<? extends Number> ln1, Point<? extends Number> ln2) {
		// The equation of a line in general form (a x + b y + c = 0)
		// given 2 points (x1,y1) & (x2,y2) is ...
		// (y1 - y2)x + (x2 - x1)y + (y2 - y1)x1 - (x2 - x1)y1 = 0
		// A = (y1 - y2); B = (x2 - x1); c = (y2 - y1)x1 - (x2 - x1)y1
		// perpendicular distance of point (x0,y0) is
		// (a x0 + b y0 + c) / sqrt(a^2 + b^2)
		// see https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
		final double A = ln1.y.doubleValue() - ln2.y.doubleValue();
		final double B = ln2.x.doubleValue() - ln1.x.doubleValue();
		double C = A * ln1.x.doubleValue() + B * ln1.y.doubleValue();
		C = A * pt.x.doubleValue() + B * pt.y.doubleValue() - C;
		return C * C / (A * A + B * B);
	}

	/**
	 * modified to be compatible with double
	 */
	static DoublePoint getUnitNormal(DoublePoint pt1, DoublePoint pt2) {
		double dx = pt2.x - pt1.x;
		double dy = pt2.y - pt1.y;
		if (dx == 0 && dy == 0) {
			return new DoublePoint();
		}

		final double f = 1 * 1.0 / Math.sqrt(dx * dx + dy * dy);
		dx *= f;
		dy *= f;

		return new DoublePoint(dy, -dx);
	}

	/**
	 * modified to be compatible with double
	 */
	protected static boolean isPt2BetweenPt1AndPt3(DoublePoint pt1,
			DoublePoint pt2, DoublePoint pt3) {
		if (pt1.equals(pt3) || pt1.equals(pt2) || pt3.equals(pt2)) {
			return false;
		} else if (pt1.x != pt3.x) {
			return pt2.x > pt1.x == pt2.x < pt3.x;
		} else {
			return pt2.y > pt1.y == pt2.y < pt3.y;
		}
	}

	/**
	 * modified to be compatible with double
	 */
	protected static boolean slopesEqual(DoublePoint pt1, DoublePoint pt2,
			DoublePoint pt3) {
		return (pt1.y - pt2.y) * (pt2.x - pt3.x)
				- (pt1.x - pt2.x) * (pt2.y - pt3.y) == 0;
	}

	/**
	 * modified to be compatible with double
	 */
	protected static boolean slopesEqual(DoublePoint pt1, DoublePoint pt2,
			DoublePoint pt3, DoublePoint pt4) {
		return (pt1.y - pt2.y) * (pt3.x - pt4.x)
				- (pt1.x - pt2.x) * (pt3.y - pt4.y) == 0;
	}

	/**
	 * modified to be compatible with double
	 */
	static boolean slopesNearCollinear(DoublePoint pt1, DoublePoint pt2,
			DoublePoint pt3, double distSqrd) {
		// this function is more accurate when the point that's GEOMETRICALLY
		// between the other 2 points is the one that's tested for distance.
		// nb: with 'spikes', either pt1 or pt3 is geometrically between the
		// other pts
		if (Math.abs(pt1.x - pt2.x) > Math.abs(pt1.y - pt2.y)) {
			if (pt1.x > pt2.x == pt1.x < pt3.x) {
				return distanceFromLineSqrd(pt1, pt2, pt3) < distSqrd;
			} else if (pt2.x > pt1.x == pt2.x < pt3.x) {
				return distanceFromLineSqrd(pt2, pt1, pt3) < distSqrd;
			} else {
				return distanceFromLineSqrd(pt3, pt1, pt2) < distSqrd;
			}
		}
		if (pt1.y > pt2.y == pt1.y < pt3.y) {
			return distanceFromLineSqrd(pt1, pt2, pt3) < distSqrd;
		} else if (pt2.y > pt1.y == pt2.y < pt3.y) {
			return distanceFromLineSqrd(pt2, pt1, pt3) < distSqrd;
		} else {
			return distanceFromLineSqrd(pt3, pt1, pt2) < distSqrd;
		}
	}

	protected Point(Point<T> pt) {
		this(pt.x, pt.y, pt.z);
	}

	protected Point(T x, T y, T z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof DoublePoint) {
			final DoublePoint a = (DoublePoint) obj;
			return DoubleUtil.isEqual((Double)x, a.getX())
					&& DoubleUtil.isEqual((Double)y, a.getY());
		}
		if (obj instanceof Point<?>) {
			final Point<?> a = (Point<?>) obj;
			return NUMBER_COMPARATOR.compare(x, a.x) == 0
					&& NUMBER_COMPARATOR.compare(y, a.y) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return x.hashCode() + y.hashCode() * 37 + z.hashCode() * 41;
	}

	public void set(Point<T> other) {
		x = other.x;
		y = other.y;
		z = other.z;
	}

	public void setX(T x) {
		this.x = x;
	}

	public void setY(T y) {
		this.y = y;
	}

	public void setZ(T z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

}// end struct IntPoint