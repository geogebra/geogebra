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

package org.geogebra.common.kernel.implicit;

import java.util.ListIterator;

import org.geogebra.common.kernel.MyPoint;

public class CompleteContourLinker extends ContourLinker {

	@Override
	public void link(MyPoint p0, MyPoint p1, boolean canSwap) {
		MyPoint[] pts = new MyPoint[]{p0, p1};
		if (canSwap && pts[0].x > pts[1].x) {
			MyPoint temp = pts[0];
			pts[0] = pts[1];
			pts[1] = temp;
		}
		PointList p0Start = segmentStartsWith(pts[0]);
		PointList p0End = segmentEndsWith(pts[0]);

		PointList p1Start = segmentStartsWith(pts[1]);
		PointList p1End = segmentEndsWith(pts[1]);
		connect(p0Start, p0End, p1Start, p1End, pts);
	}

	private void connect(PointList p0Start, PointList p0End,
			PointList p1Start, PointList p1End, MyPoint[] pts) {
		if (p1Start != null && p0End != null) {
			remove(p1Start, p0End);
			p0End.mergeTo(p1Start);
		} else if (p0Start != null && p1Start != null) {
			remove(p0Start, p1Start);
			p1Start.reverse();
			p1Start.mergeTo(p0Start);
		} else if (p0Start != null && p1End != null) {
			remove(p0Start, p1End);
			p1End.mergeTo(p0Start);
		} else if (p0End != null && p1End != null) {
			remove(p0End, p1End);
			p0End.reverse();
			p1End.mergeTo(p0End);
		} else if (p1Start != null) {
			p1Start.extendBack(pts[0]);
		} else if (p0End != null) {
			p0End.extendFront(pts[1]);
		} else if (p0Start != null) {
			p0Start.extendBack(pts[1]);
		} else if (p1End != null) {
			p1End.extendFront(pts[0]);
		} else {
			addFirst(new PointList(pts[0], pts[1]));
		}
	}

	private void remove(PointList pointList, PointList anotherList) {
		if (pointList != anotherList) {
			remove(pointList);
		}
	}

	private PointList segmentStartsWith(MyPoint point) {
		ListIterator<PointList> iterator = listIterator();
		PointList list;
		while (iterator.hasNext()) {
			list = iterator.next();
			if (equal(point, list.start)) {
				return list;
			}
		}
		return null;
	}

	private PointList segmentEndsWith(MyPoint point) {
		ListIterator<PointList> iterator = listIterator();
		PointList list;
		while (iterator.hasNext()) {
			list = iterator.next();
			if (equal(point, list.end)) {
				return list;
			}
		}
		return null;
	}

}
