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

package org.geogebra.ggbjdk.sun.awt.geom;

import com.google.j2objc.annotations.Weak;

final class Edge {
	static final int INIT_PARTS = 4;
	static final int GROW_PARTS = 10;

	Curve curve;
	int ctag;
	int etag;
	double activey;
	int equivalence;

	public Edge(Curve c, int ctag) {
		this(c, ctag, AreaOp.ETAG_IGNORE);
	}

	public Edge(Curve c, int ctag, int etag) {
		this.curve = c;
		this.ctag = ctag;
		this.etag = etag;
	}

	public Curve getCurve() {
		return curve;
	}

	public int getCurveTag() {
		return ctag;
	}

	public int getEdgeTag() {
		return etag;
	}

	public void setEdgeTag(int etag) {
		this.etag = etag;
	}

	public int getEquivalence() {
		return equivalence;
	}

	public void setEquivalence(int eq) {
		equivalence = eq;
	}

	@Weak
	private Edge lastEdge;

	private int lastResult;
	private double lastLimit;

	public int compareTo(Edge other, double yrange[]) {
		if (other == lastEdge && yrange[0] < lastLimit) {
			if (yrange[1] > lastLimit) {
				yrange[1] = lastLimit;
			}
			return lastResult;
		}
		if (this == other.lastEdge && yrange[0] < other.lastLimit) {
			if (yrange[1] > other.lastLimit) {
				yrange[1] = other.lastLimit;
			}
			return 0 - other.lastResult;
		}
		// long start = System.currentTimeMillis();
		int ret = curve.compareTo(other.curve, yrange);
		// long end = System.currentTimeMillis();
		/*
		System.out.println("compare: "+
											((System.identityHashCode(this) <
												System.identityHashCode(other))
												? this+" to "+other
												: other+" to "+this)+
											" == "+ret+" at "+yrange[1]+
											" in "+(end-start)+"ms");
		 */
		lastEdge = other;
		lastLimit = yrange[1];
		lastResult = ret;
		return ret;
	}

	public void record(double yend, int etag) {
		this.activey = yend;
		this.etag = etag;
	}

	public boolean isActiveFor(double y, int etag) {
		return (this.etag == etag && this.activey >= y);
	}

	@Override
	public String toString() {
		return ("Edge[" + curve + ", "
				+ (ctag == AreaOp.CTAG_LEFT ? "L" : "R")
				+ ", "
				+ (etag == AreaOp.ETAG_ENTER ? "I" : (etag == AreaOp.ETAG_EXIT ? "O" : "N"))
				+ "]");
	}
}
