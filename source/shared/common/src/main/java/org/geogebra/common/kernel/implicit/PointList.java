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

import java.util.LinkedList;
import java.util.ListIterator;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;

class PointList {
	MyPoint start;
	MyPoint end;
	LinkedList<MyPoint> pts = new LinkedList<>();

	PointList(MyPoint start, MyPoint end) {
		this.start = start;
		this.end = end;
		this.start.setLineTo(false);
		this.end.setLineTo(true);
	}

	void mergeTo(PointList pl) {
		this.pts.addLast(this.end);
		if (pl == this) {
			MyPoint startCopy = new MyPoint(this.start.x, this.start.y,
					SegmentType.LINE_TO);
			this.pts.addLast(startCopy);
			return;
		}
		pl.start.setLineTo(true);
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

	void extendBack(MyPoint p) {
		p.setLineTo(false);
		this.start.setLineTo(true);
		this.pts.addFirst(start);
		this.start = p;
	}

	void extendFront(MyPoint p) {
		p.setLineTo(true);
		this.pts.addLast(this.end);
		this.end = p;
	}
}