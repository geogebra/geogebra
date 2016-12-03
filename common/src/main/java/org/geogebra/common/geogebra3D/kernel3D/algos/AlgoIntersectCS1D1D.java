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
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 *
 * @author ggb3D
 * 
 *          Calculate the GeoPoint3D intersection of two coord sys (eg line and
 *          plane).
 * 
 */
public class AlgoIntersectCS1D1D extends AlgoIntersectCoordSys {

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of point
	 * @param cs1
	 *            first coord sys
	 * @param cs2
	 *            second coord sys
	 */
	public AlgoIntersectCS1D1D(Construction cons, String label, GeoLineND cs1,
			GeoLineND cs2) {

		super(cons, label, cs1, cs2, false);

	}

	// /////////////////////////////////////////////
	// COMPUTE

	@Override
	public void compute() {

		if (!outputIsDefined())
			return;

		GeoLineND line1 = (GeoLineND) getCS1();
		GeoLineND line2 = (GeoLineND) getCS2();

		Coords o1 = line1.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords d1 = line1.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(o1);
		Coords o2 = line2.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords d2 = line2.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(o2);

		Coords[] project = CoordMatrixUtil.nearestPointsFromTwoLines(o1, d1,
				o2, d2);

		GeoPoint3D p = (GeoPoint3D) getIntersection();

		if (Double.isNaN(project[2].get(1))) { // infinite point
			if (getCS1().isGeoSegment() || getCS1().isGeoRay()
					|| getCS2().isGeoSegment() || getCS2().isGeoRay())
				p.setUndefined();
			else { // set coords to direction only when lines
				p.setCoords(project[0]);
				p.updateCoords();
			}
		} else if (project[0].equalsForKernel(project[1],
				Kernel.STANDARD_PRECISION)) {

			double t1 = project[2].get(1); // parameter on line 1
			double t2 = project[2].get(2); // parameter on line 2

			if (t1 > line1.getMinParameter() - Kernel.STANDARD_PRECISION
					&& t1 < line1.getMaxParameter() + Kernel.STANDARD_PRECISION
					&& t2 > line2.getMinParameter() - Kernel.STANDARD_PRECISION
					&& t2 < line2.getMaxParameter() + Kernel.STANDARD_PRECISION)
				p.setCoords(project[0]);
			else
				p.setUndefined();
		} else
			p.setUndefined();

	}

	@Override
	public final Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	protected String getIntersectionTypeString() {
		return "IntersectionPointOfAB";
	}

}
