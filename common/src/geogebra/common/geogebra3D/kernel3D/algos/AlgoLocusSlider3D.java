/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.awt.GRectangle2D;
import geogebra.common.geogebra3D.kernel3D.MyPoint3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoLocus3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoLocusSliderND;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * locus line for Q dependent on P
 */
public class AlgoLocusSlider3D extends AlgoLocusSliderND<MyPoint3D> {

	double[] maxZdist, farZmin, farZmax;

	private static int MAX_Z_PIXEL_DIST = MAX_X_PIXEL_DIST;



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

	@Override protected void setMaxDistances(int i) {
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
	protected void createStartPos(Construction cons) {
		QstartPos = new GeoPoint3D(cons);
	}

	@Override
	protected GeoLocus3D newGeoLocus(Construction cons) {
		return new GeoLocus3D(cons);
	}





	@Override
	protected boolean distanceOK(GeoPointND point, GRectangle2D rectangle) {

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
		double lengthX = coords.getX() - lastX;
		double lengthY = coords.getY() - lastY;
		double lengthZ = coords.getY() - lastZ;
		if (coords.getX() < minX)
			minX = coords.getX();
		if (coords.getY() < minY)
			minY = coords.getY();
		if (coords.getZ() < minZ)
			minZ = coords.getZ();
		if (lengthX < 0)
			lengthX = -lengthX;
		if (lengthY < 0)
			lengthY = -lengthY;
		if (lengthZ < 0)
			lengthZ = -lengthZ;
		return !rectangle.intersects(minX, minY, lengthX, lengthY);
		// TODO
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

	private double lastZ;

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
		return ((GeoElement) p1).isEqual(((GeoElement) p2));
	}

	@Override
	protected MyPoint3D[] createQCopyCache() {
		return new MyPoint3D[paramCache.length];
	}

	@Override
	protected void setQCopyCache(MyPoint3D copy, GeoPointND point) {
		Coords coords = point.getInhomCoordsInD3();
		copy.setX(coords.getX());
		copy.setY(coords.getY());
		copy.setZ(coords.getZ());
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

	// TODO Consider locusequability

}
