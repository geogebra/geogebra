/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.awt.GRectangle2D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoLocusND;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * locus line for Q dependent on P where P is a slider
 */
public class AlgoLocusSlider extends AlgoLocusSliderND<MyPoint> implements
		AlgoLocusSliderInterface {


	public AlgoLocusSlider(Construction cons, String label, GeoPoint Q,
			GeoNumeric P) {
		super(cons, label, Q, P);

	}

	@Override
	protected GeoLocusND<MyPoint> newGeoLocus(Construction cons2) {
		return new GeoLocus(cons2);
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

	protected boolean isFarAway(double x, double y, int i) {
		boolean farAway = (x > farXmax[i] || x < farXmin[i] || y > farYmax[i] || y < farYmin[i]);
		return farAway;
	}

	@Override
	protected boolean distanceOK(GeoPointND QND, GRectangle2D rec) {
		GeoPoint Q = (GeoPoint) QND;
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
		return !rec.intersects(minX, minY, lengthX,
					lengthY);


		
	}

	@Override
	protected boolean distanceSmall(GeoPointND QND, boolean orInsteadOfAnd) {
		GeoPoint Q = (GeoPoint) QND;

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
	// TODO Consider locusequability

	@Override
	protected boolean areEqual(GeoPointND A, GeoPointND B) {
		return ((GeoPoint) A).isEqual(B.toGeoElement(), Kernel.MIN_PRECISION);
	}

	@Override
	protected boolean differentFromLast(GeoPointND qcopy2) {
		GeoPoint Qcopy = (GeoPoint) qcopy2;
		return Qcopy.inhomX != lastX || Qcopy.inhomY != lastY;
	}

	@Override
	protected MyPoint newCache() {
		return new MyPoint();
	}

	@Override
	protected MyPoint[] createQCopyCache() {
		return new MyPoint[paramCache.length];
	}

	@Override
	protected void createStartPos(Construction cons) {
		this.QstartPos = new GeoPoint(cons);
	}

	@Override
	protected void setQCopyCache(MyPoint t, GeoPointND qCopy2) {
		t.setX(((GeoPoint) qCopy2).inhomX);
		t.setY(((GeoPoint) qCopy2).inhomY);

	}

	@Override
	protected boolean isFarAway(GeoPointND point, int i) {
		return isFarAway(((GeoPoint) point).inhomX, ((GeoPoint) point).inhomY,
				i);
	}

}
