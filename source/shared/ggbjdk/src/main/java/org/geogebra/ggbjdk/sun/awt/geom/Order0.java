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

import org.geogebra.common.awt.GPathIterator;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle2D;

final class Order0 extends Curve {
	private double x;
	private double y;

	public Order0(double x, double y) {
		super(INCREASING);
		this.x = x;
		this.y = y;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public double getXTop() {
		return x;
	}

	@Override
	public double getYTop() {
		return y;
	}

	@Override
	public double getXBot() {
		return x;
	}

	@Override
	public double getYBot() {
		return y;
	}

	@Override
	public double getXMin() {
		return x;
	}

	@Override
	public double getXMax() {
		return x;
	}

	@Override
	public double getX0() {
		return x;
	}

	@Override
	public double getY0() {
		return y;
	}

	@Override
	public double getX1() {
		return x;
	}

	@Override
	public double getY1() {
		return y;
	}

	@Override
	public double getXforY(double y) {
		return y;
	}

	@Override
	public double getTforY(double y) {
		return 0;
	}

	@Override
	public double getXforT(double t) {
		return x;
	}

	@Override
	public double getYforT(double t) {
		return y;
	}

	@Override
	public double dXforT(double t, int deriv) {
		return 0;
	}

	@Override
	public double dYforT(double t, int deriv) {
		return 0;
	}

	@Override
	public double nextVertical(double t0, double t1) {
		return t1;
	}

	@Override
	public int crossingsFor(double x, double y) {
		return 0;
	}

	@Override
	public boolean accumulateCrossings(Crossings c) {
		return (x > c.getXLo() && x < c.getXHi() && y > c.getYLo() && y < c.getYHi());
	}

	@Override
	public void enlarge(Rectangle2D r) {
		r.add(x, y);
	}

	@Override
	public Curve getSubCurve(double ystart, double yend, int dir) {
		return this;
	}

	@Override
	public Curve getReversedCurve() {
		return this;
	}

	@Override
	public int getSegment(double coords[]) {
		coords[0] = x;
		coords[1] = y;
		return GPathIterator.SEG_MOVETO;
	}
}
