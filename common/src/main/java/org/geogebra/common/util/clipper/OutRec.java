package org.geogebra.common.util.clipper;

class OutRec {
	int Idx;

	boolean isHole;

	boolean isOpen;
	OutRec firstLeft; // see comments in clipper.pas
	OutPt pts;
	OutPt bottomPt;
	PolyNode polyNode;

	public double area() {
		OutPt op = pts;
		if (op == null) {
			return 0;
		}
		double a = 0;
		do {
			a = a + (op.prev.getPt().getX() + op.getPt().getX())
					* (op.prev.getPt().getY() - op.getPt().getY());
			op = op.next;
		} while (op != pts);
		return a * 0.5;
	}

	public void fixHoleLinkage() {
		// skip if an outermost polygon or
		// already already points to the correct FirstLeft ...
		if (firstLeft == null
				|| isHole != firstLeft.isHole && firstLeft.pts != null) {
			return;
		}

		OutRec orfl = firstLeft;
		while (orfl != null && (orfl.isHole == isHole || orfl.pts == null)) {
			orfl = orfl.firstLeft;
		}
		firstLeft = orfl;
	}

	public OutPt getPoints() {
		return pts;
	}

	public void setPoints(OutPt pts) {
		this.pts = pts;
	}

	public OutRec parseFirstLeft() {
		OutRec ret = this;
		while (ret != null && ret.pts == null) {
			ret = ret.firstLeft;
		}
		return ret;
	}

}