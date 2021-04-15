/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.MyMath;

/**
 * locus line for Q dependent on P where P is a slider
 */
public class AlgoLocusSlider extends AlgoLocusSliderND<MyPoint> {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            abel
	 * @param Q
	 *            locus point
	 * @param slider
	 *            slider
	 */
	public AlgoLocusSlider(Construction cons, String label, GeoPoint Q,
			GeoNumeric slider) {
		super(cons, label, Q, slider);
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
		((GeoLocus) locus).insertPoint(x, y, lineTo ? SegmentType.LINE_TO
				: SegmentType.MOVE_TO);
		lastX = x;
		lastY = y;
		for (int i = 0; i < lastFarAway.length; i++) {
			lastFarAway[i] = isFarAway(lastX, lastY, i);
		}

	}

	protected boolean isFarAway(double x, double y, int i) {
		boolean farAway = (x > farXmax[i] || x < farXmin[i] || y > farYmax[i]
				|| y < farYmin[i]);
		return farAway;
	}

	@Override
	protected boolean distanceOK(GeoPointND QND, int i) {
		double[] min = { farXmin[i], farYmin[i] };
		double[] max = { farXmax[i], farYmax[i] };
		GeoPoint Q = (GeoPoint) QND;
		// if last point Q' was far away and Q is far away
		// then the distance is probably OK (return true),
		// so we probably don't need smaller step,
		// except if the rectangle of the segment Q'Q
		// intersects the near to screen rectangle
		// (it will probably not be on the screen anyway)

		double minX = lastX;
		double minY = lastY;

		double maxX = Q.inhomX;
		double maxY = Q.inhomY;
		if (Q.getInhomX() < minX) {
			minX = Q.getInhomX();
			maxX = lastX;
		}
		if (Q.getInhomY() < minY) {
			minY = Q.getInhomY();
			maxY = lastY;
		}

		return !MyMath.intervalsIntersect(minX, maxX, min[0], max[0])
				|| !MyMath.intervalsIntersect(minY, maxY, min[1], max[1]);
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
	protected void createStartPos(Construction cons1) {
		this.startQPos = new GeoPoint(cons1);
	}

	@Override
	protected void setQCopyCache(MyPoint t, GeoPointND qCopy2) {
		t.setLocation(((GeoPoint) qCopy2).inhomX, ((GeoPoint) qCopy2).inhomY);
	}

	@Override
	protected boolean isFarAway(GeoPointND point, int i) {
		return isFarAway(((GeoPoint) point).inhomX, ((GeoPoint) point).inhomY,
				i);
	}

}
