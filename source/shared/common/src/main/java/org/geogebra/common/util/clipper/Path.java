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

package org.geogebra.common.util.clipper;

import java.util.ArrayList;
import java.util.Collections;

import org.geogebra.common.util.clipper.Point.DoublePoint;

/**
 * A convenience class representing a list of {@code DoublePoint}.
 *
 * @author Tobias Mahlmann
 *
 */
public class Path extends ArrayList<DoublePoint> {
	/** random */
	private static final long serialVersionUID = -7120161578077546673L;

	private static OutPt excludeOp(OutPt op) {
		final OutPt result = op.prev;
		result.next = op.next;
		op.next.prev = result;
		result.idx = 0;
		return result;
	}

	public Path() {
		super();

	}

	/**
	 * @param cnt capacity
	 */
	public Path(int cnt) {
		super(cnt);
	}

	/**
	 * @return area
	 */
	public double area() {
		final int cnt = size();
		if (cnt < 3) {
			return 0;
		}
		double a = 0;
		for (int i = 0, j = cnt - 1; i < cnt; ++i) {
			// a += ((double) get( j ).getX() + get( i ).getX()) * ((double)
			// get( j ).getY() - get( i ).getY());
			a += (get(j).getX() + get(i).getX())
					* (get(j).getY() - get(i).getY());
			j = i;
		}
		return -a * 0.5;
	}

	/**
	 * modified to be compatible with double
	 */
	@SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
	public int isPointInPolygon(DoublePoint pt) {
		// returns 0 if false, +1 if true, -1 if pt ON polygon boundary
		// See "The Point in Polygon Problem for Arbitrary Polygons" by Hormann
		// & Agathos
		// http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.88.5498&rep=rep1&type=pdf
		int result = 0;
		final int cnt = size();
		if (cnt < 3) {
			return 0;
		}
		DoublePoint ip = get(0);
		for (int i = 1; i <= cnt; ++i) {
			final DoublePoint ipNext = i == cnt ? get(0) : get(i);
			if (ipNext.getY() == pt.getY()) {
				if (ipNext.getX() == pt.getX() || ip.getY() == pt.getY()
						&& ipNext.getX() > pt.getX() == ip.getX() < pt.getX()) {
					return -1;
				}
			}
			if (ip.getY() < pt.getY() != ipNext.getY() < pt.getY()) {
				if (ip.getX() >= pt.getX()) {
					if (ipNext.getX() > pt.getX()) {
						result = 1 - result;
					} else {
						final double d = (ip.getX() - pt.getX())
								* (ipNext.getY() - pt.getY())
								- (ipNext.getX() - pt.getX())
										* (ip.getY() - pt.getY());
						if (d == 0) {
							return -1;
						} else if (d > 0 == ipNext.getY() > ip.getY()) {
							result = 1 - result;
						}
					}
				} else {
					if (ipNext.getX() > pt.getX()) {
						final double d = (ip.getX() - pt.getX())
								* (ipNext.getY() - pt.getY())
								- (ipNext.getX() - pt.getX())
										* (ip.getY() - pt.getY());
						if (d == 0) {
							return -1;
						} else if (d > 0 == ipNext.getY() > ip.getY()) {
							result = 1 - result;
						}
					}
				}
			}
			ip = ipNext;
		}
		return result;
	}

	public boolean orientation() {
		return area() >= 0;
	}

	/**
	 * Reverse the path.
	 */
	public void reverse() {
		Collections.reverse(this);
	}

	/**
	 * modified to be compatible with double
	 */
	public Path translatePath(DoublePoint delta) {
		final Path outPath = new Path(size());
		for (int i = 0; i < size(); i++) {
			outPath.add(new DoublePoint(get(i).getX() + delta.getX(),
					get(i).getY() + delta.getY()));
		}
		return outPath;
	}
}
