/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * locus line for Q dependent on P
 */
public class AlgoLocus extends AlgoLocusND<MyPoint> {

	public AlgoLocus(Construction cons, GeoPointND Q, GeoPointND P,
			int min_steps, boolean registerCE) {
		super(cons, Q, P, min_steps, registerCE);
	}

	@Override
	protected void createStartPos(Construction cons) {
		QstartPos = new GeoPoint(cons);
		PstartPos = new GeoPoint(cons);
	}

	@Override
	protected GeoLocus newGeoLocus(Construction cons) {
		return new GeoLocus(cons);
	}

	public AlgoLocus(Construction cons, String label, GeoPointND Q, GeoPointND P) {
		super(cons, label, Q, P);
	}

	@Override
	protected boolean isFarAway(GeoPointND point, int i) {
		return isFarAway(((GeoPoint) point).inhomX, ((GeoPoint) point).inhomY,
				i);
	}

	@Override
	protected boolean distanceOK(GeoPointND point, GRectangle2D rectangle) {

		GeoPoint Q = (GeoPoint) point;

		// if last point Q' was far away and Q is far away
		// then the distance is probably OK (return true),
		// so we probably don't need smaller step,
		// except if the rectangle of the segment Q'Q
		// intersects the near to screen rectangle
		// (it will probably not be on the screen anyway)
		double minX = lastX;
		double minY = lastY;
		double lengthX = Q.inhomX - lastX;
		double lengthY = Q.inhomY - lastY;
		if (Q.inhomX < minX)
			minX = Q.inhomX;
		if (Q.inhomY < minY)
			minY = Q.inhomY;
		if (lengthX < 0)
			lengthX = -lengthX;
		if (lengthY < 0)
			lengthY = -lengthY;
		return !rectangle.intersects(minX, minY, lengthX, lengthY);
	}

	@Override
	protected boolean distanceSmall(GeoPointND point, boolean orInsteadOfAnd) {

		GeoPoint Q = (GeoPoint) point;

		boolean[] distSmall = new boolean[3];
		for (int i = 0; i < distSmall.length; i++) {
			distSmall[i] = Math.abs(Q.inhomX - lastX) < maxXdist[i]
					&& Math.abs(Q.inhomY - lastY) < maxYdist[i];
		}

		if (orInsteadOfAnd) {
			for (int i = 0; i < distSmall.length; i++) {
				if (distSmall[i] && visibleEV[i]) {
					return true;
				}
			}
			return false;
		}

		for (int i = 0; i < distSmall.length; i++) {
			if (!distSmall[i] && visibleEV[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void insertPoint(GeoPointND point, boolean lineTo) {
		insertPoint(((GeoPoint) point).inhomX, ((GeoPoint) point).inhomY,
				lineTo);
	}

	private void insertPoint(double x, double y, boolean lineTo) {
		pointCount++;

		// Application.debug("insertPoint: " + x + ", " + y + ", lineto: " +
		// lineTo);
		((GeoLocus) locus).insertPoint(x, y, lineTo);
		lastX = x;
		lastY = y;
		for (int i = 0; i < lastFarAway.length; i++) {
			lastFarAway[i] = isFarAway(lastX, lastY, i);
		}
	}

	private boolean isFarAway(double x, double y, int i) {
		boolean farAway = (x > farXmax[i] || x < farXmin[i] || y > farYmax[i] || y < farYmin[i]);
		return farAway;
	}

	@Override
	protected boolean differentFromLast(GeoPointND point) {
		return ((GeoPoint) point).inhomX != lastX
				|| ((GeoPoint) point).inhomY != lastY;
	}

	@Override
	protected boolean areEqual(GeoPointND p1, GeoPointND p2) {
		return ((GeoPoint) p1).isEqual(((GeoPoint) p2), Kernel.MIN_PRECISION);
	}

	@Override
	protected MyPoint[] createQCopyCache(int length) {
		return new MyPoint[length];
	}

	@Override
	protected void setQCopyCache(MyPoint copy, GeoPointND point) {
		copy.setX(((GeoPoint) point).inhomX);
		copy.setY(((GeoPoint) point).inhomY);
	}

	@Override
	protected MyPoint newCache() {
		return new MyPoint();
	}

	// TODO Consider locusequability

}
