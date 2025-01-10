package org.geogebra.common.kernel.discrete.delaunay;

import java.util.Comparator;

import org.geogebra.common.util.DoubleUtil;

class ComparePoint implements Comparator<PointDt> {
	private int _flag;

	public ComparePoint(int i) {
		_flag = i;
	}

	/** compare between two points. */
	@Override
	public int compare(PointDt d1, PointDt d2) {
		int ans = 0;
		if (d1 != null && d2 != null) {
			if (_flag == 0) {
				if (greaterThan(d1.x, d2.x)) {
					return 1;
				}
				if (lessThan(d1.x, d2.x)) {
					return -1;
				}
				// x1 == x2
				if (greaterThan(d1.y, d2.y)) {
					return 1;
				}
				if (lessThan(d1.y, d2.y)) {
					return -1;
				}
			} else if (_flag == 1) {
				if (greaterThan(d1.x, d2.x)) {
					return -1;
				}
				if (lessThan(d1.x, d2.x)) {
					return 1;
				}
				// x1 == x2
				if (greaterThan(d1.y, d2.y)) {
					return -1;
				}
				if (lessThan(d1.y, d2.y)) {
					return 1;
				}
			} else if (_flag == 2) {
				if (greaterThan(d1.y, d2.y)) {
					return 1;
				}
				if (lessThan(d1.y, d2.y)) {
					return -1;
				}
				// y1 == y2
				if (greaterThan(d1.x, d2.x)) {
					return 1;
				}
				if (d1.x < d2.x) {
					return -1;
				}

			} else if (_flag == 3) {
				if (greaterThan(d1.y, d2.y)) {
					return -1;
				}
				if (d1.y < d2.y) {
					return 1;
				}
				// y1 == y2
				if (greaterThan(d1.x, d2.x)) {
					return -1;
				}
				if (lessThan(d1.x, d2.x)) {
					return 1;
				}
			}
		} else {
			if (d1 == null && d2 == null) {
				return 0;
			}
			if (d1 == null && d2 != null) {
				return 1;
			}
			if (d1 != null && d2 == null) {
				return -1;
			}
		}
		return ans;
	}

	public static boolean greaterThan(double x, double y) {
		return DoubleUtil.isGreater(x, y);
	}

	public static boolean lessThan(double x, double y) {
		return DoubleUtil.isGreater(y, x);
	}

	public static boolean equals(double x, double y) {
		return DoubleUtil.isEqual(x, y);
	}

}
