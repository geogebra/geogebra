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

final class CurveLink {
	Curve curve;
	double ytop;
	double ybot;
	int etag;

	CurveLink next;

	public CurveLink(Curve curve, double ystart, double yend, int etag) {
		this.curve = curve;
		this.ytop = ystart;
		this.ybot = yend;
		this.etag = etag;
		if (ytop < curve.getYTop() || ybot > curve.getYBot()) {
			throw new RuntimeException("bad curvelink [" + ytop + "=>" + ybot + "] for " + curve);
		}
	}

	public boolean absorb(CurveLink link) {
		return absorb(link.curve, link.ytop, link.ybot, link.etag);
	}

	public boolean absorb(Curve curve, double ystart, double yend, int etag) {
		if (this.curve != curve || this.etag != etag || ybot < ystart || ytop > yend) {
			return false;
		}
		if (ystart < curve.getYTop() || yend > curve.getYBot()) {
			throw new RuntimeException("bad curvelink [" + ystart + "=>" + yend + "] for " + curve);
		}
		this.ytop = Math.min(ytop, ystart);
		this.ybot = Math.max(ybot, yend);
		return true;
	}

	public boolean isEmpty() {
		return (ytop == ybot);
	}

	public Curve getCurve() {
		return curve;
	}

	public Curve getSubCurve() {
		if (ytop == curve.getYTop() && ybot == curve.getYBot()) {
			return curve.getWithDirection(etag);
		}
		return curve.getSubCurve(ytop, ybot, etag);
	}

	public Curve getMoveto() {
		return new Order0(getXTop(), getYTop());
	}

	public double getXTop() {
		return curve.getXforY(ytop);
	}

	public double getYTop() {
		return ytop;
	}

	public double getXBot() {
		return curve.getXforY(ybot);
	}

	public double getYBot() {
		return ybot;
	}

	public double getX() {
		return curve.getXforY(ytop);
	}

	public int getEdgeTag() {
		return etag;
	}

	public void setNext(CurveLink link) {
		this.next = link;
	}

	public CurveLink getNext() {
		return next;
	}
}
