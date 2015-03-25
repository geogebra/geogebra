/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

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
	protected GeoLocusND<MyPoint> newGeoLocus() {
		return new GeoLocus(cons);
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
		lastFarAway = isFarAway(lastX, lastY);
		lastFarAway2 = isFarAway2(lastX, lastY);
	}

	protected boolean isFarAway(double x, double y) {
		boolean farAway = (x > farXmax || x < farXmin || y > farYmax || y < farYmin);
		return farAway;
	}

	protected boolean isFarAway2(double x, double y) {
		boolean farAway = (x > farXmax2 || x < farXmin2 || y > farYmax2 || y < farYmin2);
		return farAway;
	}

	protected boolean distanceOK(GeoPointND QND) {
		boolean distanceOK, distanceOK2;
		GeoPoint Q = (GeoPoint) QND;
		if (lastFarAway && isFarAway(Q.inhomX, Q.inhomY)) {
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
			distanceOK = !nearToScreenRect.intersects(minX, minY, lengthX,
					lengthY);
		} else {
			distanceOK = distanceSmall(Q, false);
		}

		if (lastFarAway2 && isFarAway2(Q.inhomX, Q.inhomY)) {
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
			distanceOK2 = !nearToScreenRect2.intersects(minX, minY, lengthX,
					lengthY);
		} else {
			distanceOK2 = distanceSmall(Q, false);
		}

		return distanceOK && distanceOK2;
	}

	protected boolean distanceSmall(GeoPointND QND, boolean orInsteadOfAnd) {
		GeoPoint Q = (GeoPoint) QND;
		boolean distSmall = Math.abs(Q.inhomX - lastX) < maxXdist
				&& Math.abs(Q.inhomY - lastY) < maxYdist;
		boolean distSmall2 = Math.abs(Q.inhomX - lastX) < maxXdist2
				&& Math.abs(Q.inhomY - lastY) < maxYdist2;

		if (orInsteadOfAnd) {
			return (distSmall && visibleEV1) || (distSmall2 && visibleEV2);
		}

		return (distSmall || !visibleEV1) && (distSmall2 || !visibleEV2);
	}
	// TODO Consider locusequability

	@Override
	protected boolean areEqual(GeoPointND A, GeoPointND B, double minPrecision) {
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

}
