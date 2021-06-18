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
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.MyMath;

/**
 * locus line for Q dependent on P
 */
public class AlgoLocus extends AlgoLocusND<MyPoint> {

	public AlgoLocus(Construction cons, GeoPointND Q, GeoPointND P,
			int min_steps, boolean registerCE) {
		super(cons, Q, P, min_steps, registerCE);
	}

	@Override
	protected void createStartPos(Construction cons1) {
		startQPos = new GeoPoint(cons1);
		startPPos = new GeoPoint(cons1);
	}

	@Override
	protected GeoLocus newGeoLocus(Construction cons1) {
		return new GeoLocus(cons1);
	}

	public AlgoLocus(Construction cons, String label, GeoPointND Q,
			GeoPointND P) {
		super(cons, label, Q, P);
	}

	@Override
	protected boolean isFarAway(GeoPointND point, int i) {
		return isFarAway(((GeoPoint) point).inhomX, ((GeoPoint) point).inhomY,
				i);
	}

	@Override
	protected boolean distanceOK(GeoPointND point, int i) {
		double[] min = { this.farXmin[i], farYmin[i] };
		double[] max = { this.farXmax[i], farYmax[i] };
		GRectangle2D rectangle = AwtFactory.getPrototype().newRectangle2D();
		rectangle.setRect(min[0], min[1], max[0] - min[0], max[1] - min[1]);
		GeoPoint Q = (GeoPoint) point;

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
		((GeoLocus) locus).insertPoint(x, y, lineTo ? SegmentType.LINE_TO
				: SegmentType.MOVE_TO);
		lastX = x;
		lastY = y;
		for (int i = 0; i < lastFarAway.length; i++) {
			lastFarAway[i] = isFarAway(lastX, lastY, i);
		}
	}

	private boolean isFarAway(double x, double y, int i) {
		boolean farAway = (x > farXmax[i] || x < farXmin[i] || y > farYmax[i]
				|| y < farYmin[i]);
		return farAway;
	}

	@Override
	protected boolean differentFromLast(GeoPointND point) {
		return ((GeoPoint) point).inhomX != lastX
				|| ((GeoPoint) point).inhomY != lastY;
	}

	@Override
	protected boolean areEqual(GeoPointND p1, GeoPointND p2) {
		return ((GeoPoint) p1).isEqual(p2, Kernel.MIN_PRECISION);
	}

	@Override
	protected MyPoint[] createQCopyCache(int length) {
		return new MyPoint[length];
	}

	@Override
	protected void setQCopyCache(MyPoint copy, GeoPointND point) {
		copy.setLocation(((GeoPoint) point).inhomX, ((GeoPoint) point).inhomY);
	}

	@Override
	protected MyPoint newCache() {
		return new MyPoint();
	}

}
