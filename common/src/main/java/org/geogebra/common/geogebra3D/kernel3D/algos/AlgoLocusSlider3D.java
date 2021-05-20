/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.MyPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLocus3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoLocusSliderND;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.MyMath;

/**
 * locus line for Q dependent on P
 */
public class AlgoLocusSlider3D extends AlgoLocusSliderND<MyPoint3D> {

	private double[] maxZdist;
	private double[] farZmin;
	private double[] farZmax;
	private double lastZ;

	private static int MAX_Z_PIXEL_DIST = MAX_X_PIXEL_DIST;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param Q
	 *            locus point
	 * @param P
	 *            moving point
	 */
	public AlgoLocusSlider3D(Construction cons, String label, GeoPointND Q,
			GeoNumeric P) {
		super(cons, label, Q, P);
	}

	@Override
	protected void createMaxDistances() {
		super.createMaxDistances();
		maxZdist = new double[3];
		farZmin = new double[3];
		farZmax = new double[3];
	}

	@Override
	protected void setMaxDistances(int i) {
		super.setMaxDistances(i);
		if (i == 2) { // 3D view
			maxZdist[i] = MAX_Z_PIXEL_DIST / kernel.getZscale(i);
			double zmin = kernel.getZmin(i);
			double zmax = kernel.getZmax(i);

			double widthRW = zmax - zmin;

			farZmin[i] = zmin - widthRW / 2;
			farZmax[i] = zmax + widthRW / 2;

		} else {
			maxZdist[i] = Double.POSITIVE_INFINITY;
			// we don't check z for 2D // views

			farZmin[i] = Double.NEGATIVE_INFINITY;
			farZmax[i] = Double.POSITIVE_INFINITY;
		}

	}

	@Override
	protected void createStartPos(Construction cons1) {
		startQPos = new GeoPoint3D(cons1);
	}

	@Override
	protected GeoLocus3D newGeoLocus(Construction cons1) {
		return new GeoLocus3D(cons1);
	}

	@Override
	protected boolean distanceOK(GeoPointND point, int i) {
		double[] min = { farXmin[i], farYmin[i], farZmin[i] };
		double[] max = { farXmax[i], farYmax[i], farZmax[i] };
		Coords coords = point.getInhomCoordsInD3();

		// if last point Q' was far away and Q is far away
		// then the distance is probably OK (return true),
		// so we probably don't need smaller step,
		// except if the rectangle of the segment Q'Q
		// intersects the near to screen rectangle
		// (it will probably not be on the screen anyway)
		double minX = lastX;
		double minY = lastY;
		double minZ = lastZ;

		double maxX = coords.getX();
		double maxY = coords.getY();
		double maxZ = coords.getZ();
		if (coords.getX() < minX) {
			minX = coords.getX();
			maxX = lastX;
		}
		if (coords.getY() < minY) {
			minY = coords.getY();
			maxY = lastY;
		}
		if (coords.getZ() < minZ) {
			minZ = coords.getZ();
			maxZ = lastZ;
		}

		boolean ok2d = !MyMath.intervalsIntersect(minX, maxX, min[0], max[0])
				|| !MyMath.intervalsIntersect(minY, maxY, min[1], max[1]);
		return i < 2 ? ok2d
				: (ok2d || !MyMath.intervalsIntersect(minZ, maxZ, min[2],
						max[2]));
	}

	@Override
	protected boolean distanceSmall(GeoPointND point, boolean orInsteadOfAnd) {

		Coords coords = point.getInhomCoordsInD3();

		boolean[] distSmall = new boolean[3];
		for (int i = 0; i < distSmall.length; i++) {
			distSmall[i] = Math.abs(coords.getX() - lastX) < maxXdist[i]
					&& Math.abs(coords.getY() - lastY) < maxYdist[i]
					&& Math.abs(coords.getZ() - lastZ) < maxZdist[i];
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
		Coords coords = point.getInhomCoordsInD3();
		insertPoint(coords.getX(), coords.getY(), coords.getZ(), lineTo);
	}

	private void insertPoint(double x, double y, double z, boolean lineTo) {
		pointCount++;

		// Application.debug("insertPoint: " + x + ", " + y + ", lineto: " +
		// lineTo);
		((GeoLocus3D) locus).insertPoint(x, y, z, lineTo);
		lastX = x;
		lastY = y;
		lastZ = z;
		for (int i = 0; i < lastFarAway.length; i++) {
			lastFarAway[i] = isFarAway(lastX, lastY, lastZ, i);
		}
	}

	private boolean isFarAway(double x, double y, double z, int i) {
		boolean farAway = (x > farXmax[i] || x < farXmin[i] || y > farYmax[i]
				|| y < farYmin[i] || z > farZmax[i] || z < farZmin[i]);
		return farAway;
	}

	@Override
	protected boolean differentFromLast(GeoPointND point) {
		Coords coords = point.getInhomCoordsInD3();
		return coords.getX() != lastX || coords.getY() != lastY
				|| coords.getZ() != lastZ;
	}

	@Override
	protected boolean areEqual(GeoPointND p1, GeoPointND p2) {
		return ((GeoElement) p1).isEqual(p2);
	}

	@Override
	protected MyPoint3D[] createQCopyCache() {
		return new MyPoint3D[paramCache.length];
	}

	@Override
	protected void setQCopyCache(MyPoint3D copy, GeoPointND point) {
		Coords coords = point.getInhomCoordsInD3();
		copy.setLocation(coords.getX(), coords.getY(), coords.getZ());
	}

	@Override
	protected MyPoint3D newCache() {
		return new MyPoint3D();
	}

	@Override
	protected boolean isFarAway(GeoPointND point, int i) {
		Coords coords = point.getInhomCoordsInD3();
		return isFarAway(coords.getX(), coords.getY(), coords.getZ(), i);
	}

}
