package org.geogebra.common.util.clipper;

import org.geogebra.common.util.clipper.Point.DoublePoint;

class OutPt {
	int idx;
	// private LongPoint pt;
	DoublePoint pt;
	OutPt next;

	OutPt prev;

	public static OutRec getLowerMostRec(OutRec outRec1, OutRec outRec2) {
		// work out which polygon fragment has the correct hole state ...
		if (outRec1.bottomPt == null) {
			outRec1.bottomPt = outRec1.pts.getBottomPt();
		}
		if (outRec2.bottomPt == null) {
			outRec2.bottomPt = outRec2.pts.getBottomPt();
		}
		final OutPt bPt1 = outRec1.bottomPt;
		final OutPt bPt2 = outRec2.bottomPt;
		if (bPt1.getPt().getY() > bPt2.getPt().getY()) {
			return outRec1;
		} else if (bPt1.getPt().getY() < bPt2.getPt().getY()) {
			return outRec2;
		} else if (bPt1.getPt().getX() < bPt2.getPt().getX()) {
			return outRec1;
		} else if (bPt1.getPt().getX() > bPt2.getPt().getX()) {
			return outRec2;
		} else if (bPt1.next == bPt1) {
			return outRec2;
		} else if (bPt2.next == bPt2) {
			return outRec1;
		} else if (isFirstBottomPt(bPt1, bPt2)) {
			return outRec1;
		} else {
			return outRec2;
		}
	}

	private static boolean isFirstBottomPt(OutPt btmPt1, OutPt btmPt2) {
		OutPt p = btmPt1.prev;
		while (p.getPt().equals(btmPt1.getPt()) && !p.equals(btmPt1)) {
			p = p.prev;
		}
		final double dx1p = Math
				.abs(DoublePoint.getDeltaX(btmPt1.getPt(), p.getPt()));
		p = btmPt1.next;
		while (p.getPt().equals(btmPt1.getPt()) && !p.equals(btmPt1)) {
			p = p.next;
		}
		final double dx1n = Math
				.abs(DoublePoint.getDeltaX(btmPt1.getPt(), p.getPt()));

		p = btmPt2.prev;
		while (p.getPt().equals(btmPt2.getPt()) && !p.equals(btmPt2)) {
			p = p.prev;
		}
		final double dx2p = Math
				.abs(DoublePoint.getDeltaX(btmPt2.getPt(), p.getPt()));
		p = btmPt2.next;
		while (p.getPt().equals(btmPt2.getPt()) && p.equals(btmPt2)) {
			p = p.next;
		}
		final double dx2n = Math
				.abs(DoublePoint.getDeltaX(btmPt2.getPt(), p.getPt()));
		return dx1p >= dx2p && dx1p >= dx2n || dx1n >= dx2p && dx1n >= dx2n;
	}

	public OutPt duplicate(boolean InsertAfter) {
		final OutPt result = new OutPt();
		// result.setPt( new LongPoint( getPt() ) );
		result.setPt(new DoublePoint(getPt()));
		result.idx = idx;
		if (InsertAfter) {
			result.next = next;
			result.prev = this;
			next.prev = result;
			next = result;
		} else {
			result.prev = prev;
			result.next = this;
			prev.next = result;
			prev = result;
		}
		return result;
	}

	OutPt getBottomPt() {
		OutPt dups = null;
		OutPt p = next;
		OutPt pp = this;
		while (p != pp) {
			if (p.getPt().getY() > pp.getPt().getY()) {
				pp = p;
				dups = null;
			} else if (p.getPt().getY() == pp.getPt().getY()
					&& p.getPt().getX() <= pp.getPt().getX()) {
				if (p.getPt().getX() < pp.getPt().getX()) {
					dups = null;
					pp = p;
				} else {
					if (p.next != pp && p.prev != pp) {
						dups = p;
					}
				}
			}
			p = p.next;
		}
		if (dups != null) {
			// there appears to be at least 2 vertices at bottomPt so ...
			while (dups != p) {
				if (!isFirstBottomPt(p, dups)) {
					pp = dups;
				}
				dups = dups.next;
				while (!dups.getPt().equals(pp.getPt())) {
					dups = dups.next;
				}
			}
		}
		return pp;
	}

	public int getPointCount() {

		int result = 0;
		OutPt p = this;
		do {
			result++;
			p = p.next;
		} while (p != this && p != null);
		return result;
	}

	/**
	 * modified to be compatible with double
	 */
	public DoublePoint getPt() {
		return pt;
	}

	public void setPt(DoublePoint pt) {
		this.pt = pt;
	}

	public void reversePolyPtLinks() {

		OutPt pp1;
		OutPt pp2;
		pp1 = this;
		do {
			pp2 = pp1.next;
			pp1.next = pp1.prev;
			pp1.prev = pp2;
			pp1 = pp2;
		} while (pp1 != this);
	}

}