package org.geogebra.common.util.clipper;

import org.geogebra.common.util.clipper.Point.DoublePoint;

/**
 * modified to be compatible with double
 */
class Join {
	OutPt outPt1;
	OutPt outPt2;
	private DoublePoint offPt;

	public DoublePoint getOffPt() {
		return offPt;
	}

	public void setOffPt(DoublePoint offPt) {
		this.offPt = offPt;
	}

}