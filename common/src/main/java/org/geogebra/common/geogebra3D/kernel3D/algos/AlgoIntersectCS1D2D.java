/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author ggb3D
 * 
 *         Calculate the GeoPoint3D intersection of two coord sys (eg line and
 *         plane).
 * 
 */
public class AlgoIntersectCS1D2D extends AlgoIntersectCoordSys {

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of point
	 * @param cs1
	 *            line
	 * @param cs2
	 *            plane / ...
	 * 
	 * @param swapInputs
	 *            may swap inputs order
	 */
	public AlgoIntersectCS1D2D(Construction cons, String label, GeoLineND cs1,
			GeoCoordSys2D cs2, boolean swapInputs) {

		super(cons, label, cs1, cs2, swapInputs);
	}

	// /////////////////////////////////////////////
	// COMPUTE

	@Override
	public void compute() {

		GeoLineND line = (GeoLineND) getCS1();
		GeoCoordSys2D cs2D = (GeoCoordSys2D) getCS2();

		Coords o = line.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords d = line.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(o);
		Coords globalCoords = new Coords(4);
		Coords inPlaneCoords = new Coords(4);
		o.projectPlaneThruV(cs2D.getCoordSys().getMatrixOrthonormal(), d,
				globalCoords, inPlaneCoords);

		GeoPoint3D p = (GeoPoint3D) getIntersection();

		// check if the point is in the line (segment or half-line)
		// and if the point is in the region (polygon, ...)
		if (-inPlaneCoords.get(3) > line.getMinParameter()
				- Kernel.MAX_PRECISION
				&& -inPlaneCoords.get(3) < line.getMaxParameter()
						+ Kernel.MAX_PRECISION
				&& cs2D.isInRegion(inPlaneCoords.get(1),
						inPlaneCoords.get(2))) {
			p.setCoords(globalCoords);
		} else {
			p.setUndefined();
		}
	}

	/**
	 * configurations line/plane
	 */
	public static enum ConfigLinePlane {
		/** general case */
		GENERAL,
		/** line parallel to plane */
		PARALLEL,
		/** line contained in plane */
		CONTAINED
	}

	// TODO optimize it
	/**
	 * 
	 * @param line
	 *            line
	 * @param plane
	 *            plane
	 * @return config line/plane (general/parallel/contained)
	 */
	public static ConfigLinePlane getConfigLinePlane(GeoLineND line,
			GeoCoordSys2D plane) {
		if (DoubleUtil.isZero(
				line.getDirectionInD3().dotproduct(plane.getDirectionInD3()))) {
			if (DoubleUtil.isZero(
					line.getPointInD(3, 0).getInhomCoordsInSameDimension()
							.sub(plane.getCoordSys().getOrigin())
							.dotproduct(plane.getDirectionInD3()))) {
				return ConfigLinePlane.CONTAINED;
			}
			return ConfigLinePlane.PARALLEL;
		}
		return ConfigLinePlane.GENERAL;
	}

	/**
	 * almost a clone of #compute()
	 * 
	 * @param line
	 *            line
	 * @param cs2D
	 *            plane
	 * @param globalCoords
	 *            global coords
	 * @param inPlaneCoords
	 *            plane coords of intersection
	 * @return global intersection coordinates or null
	 */
	public static Coords getIntersectLinePlane(GeoLineND line,
			GeoCoordSys2D cs2D, Coords globalCoords, Coords inPlaneCoords) {

		Coords o = line.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords d = line.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(o);
		o.projectPlaneThruV(cs2D.getCoordSys().getMatrixOrthonormal(), d,
				globalCoords, inPlaneCoords);

		// check if the point is in the line (segment or half-line)
		// and if the point is in the region (polygon, ...)
		if (line.respectLimitedPath(-inPlaneCoords.get(3)) && cs2D
				.isInRegion(inPlaneCoords.get(1), inPlaneCoords.get(2))) {
			return globalCoords;
		}
		return null;
	}

	@Override
	protected String getIntersectionTypeString() {
		return "IntersectionPointOfAB";
	}

	@Override
	public final Commands getClassName() {
		return Commands.Intersect;
	}

}
