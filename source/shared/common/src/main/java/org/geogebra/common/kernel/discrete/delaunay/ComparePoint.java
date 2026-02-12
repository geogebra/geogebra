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

package org.geogebra.common.kernel.discrete.delaunay;

import java.util.Comparator;

import org.geogebra.common.util.DoubleUtil;

class ComparePoint implements Comparator<PointDt> {
	private int _flag;

	ComparePoint(int i) {
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

	static boolean greaterThan(double x, double y) {
		return DoubleUtil.isGreater(x, y);
	}

	static boolean lessThan(double x, double y) {
		return DoubleUtil.isGreater(y, x);
	}

	static boolean equals(double x, double y) {
		return DoubleUtil.isEqual(x, y);
	}

}
