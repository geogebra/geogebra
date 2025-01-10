package org.geogebra.common.kernel.implicit;

import java.util.LinkedList;
import java.util.ListIterator;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;

class PointList {
	MyPoint start;
	MyPoint end;
	LinkedList<MyPoint> pts = new LinkedList<>();

	public PointList(MyPoint start, MyPoint end) {
		this.start = start;
		this.end = end;
		this.start.setLineTo(false);
		this.end.setLineTo(true);
	}

	public void mergeTo(PointList pl) {
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

	public void extendBack(MyPoint p) {
		p.setLineTo(false);
		this.start.setLineTo(true);
		this.pts.addFirst(start);
		this.start = p;
	}

	public void extendFront(MyPoint p) {
		p.setLineTo(true);
		this.pts.addLast(this.end);
		this.end = p;
	}
}
