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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoElement3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.main.App;

/**
 *
 * @author ggb3D
 * @version
 * 
 *          Calculate the GeoPoint3D intersection of two coord sys (eg line and
 *          plane).
 * 
 */
public class AlgoIntersectPlanes extends AlgoIntersectCoordSys {

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
	public AlgoIntersectPlanes(Construction cons, String label,
			GeoPlaneND cs1, GeoPlaneND cs2) {

		super(cons, label, (GeoElement) cs1, (GeoElement) cs2);

	}

	public AlgoIntersectPlanes(Construction cons, GeoPlaneND cs1,
			GeoPlaneND cs2) {

		super(cons, (GeoElement) cs1, (GeoElement) cs2);

	}
	
	private Coords o, vn, vn1, vn2;

	@Override
	protected GeoElement3D createIntersection(Construction cons) {

		GeoLine3D ret = new GeoLine3D(cons, true);
		o = new Coords(0, 0, 0, 1);
		vn = new Coords(0, 0, 0, 0);
		
		vn1 = new Coords(0, 0, 0, 0);
		vn2 = new Coords(0, 0, 0, 0);

		return ret;

	}

	// /////////////////////////////////////////////
	// COMPUTE

	@Override
	public void compute() {

		GeoPlane3D p1 = (GeoPlane3D) getCS1();
		GeoPlane3D p2 = (GeoPlane3D) getCS2();
		
		Coords v1 = ((GeoPlane3D) getCS1()).getCoordSys().getEquationVector();
		Coords v2 = ((GeoPlane3D) getCS2()).getCoordSys().getEquationVector();
				
		vn.setCrossProduct(v1, v2);
		
		if (vn.isZero()){
			getIntersection().setUndefined();
			return;
		}
		
		
		Coords o1 = ((GeoPlane3D) getCS1()).getCoordSys().getOrigin();
		Coords o2 = ((GeoPlane3D) getCS2()).getCoordSys().getOrigin();
		vn1.setCrossProduct(v1, vn);
		vn2.setCrossProduct(v2, vn);
		o2.projectPlane(vn, vn1, vn2, o1, o);
		

		

		// update line
		GeoLine3D l = (GeoLine3D) getIntersection();
		
		l.setCoord(o, vn);

	}

	public static GeoLine3D getIntersectPlanePlane(GeoCoordSys2D cs1,
			GeoCoordSys2D cs2) {

		Coords[] intersection = CoordMatrixUtil.intersectPlanes(cs1
				.getCoordSys().getMatrixOrthonormal(), cs2.getCoordSys()
				.getMatrixOrthonormal());

		// update line
		Construction c = cs1.toGeoElement().getConstruction();
		c.getKernel().setSilentMode(true);
		GeoLine3D l = new GeoLine3D(c, intersection[0], intersection[1]);
		c.getKernel().setSilentMode(false);
		return l;
	}

	public static GeoLine3D getIntersectPlanePlane(Construction cons,
			CoordSys cs1, CoordSys cs2) {

		Coords[] intersection = CoordMatrixUtil.intersectPlanes(
				cs1.getMatrixOrthonormal(), cs2.getMatrixOrthonormal());

		// update line
		cons.getKernel().setSilentMode(true);
		GeoLine3D l = new GeoLine3D(cons, intersection[0], intersection[1]);
		cons.getKernel().setSilentMode(false);
		return l;
	}

	public static int RESULTCATEGORY_NA = -1;
	public static int RESULTCATEGORY_GENERAL = 1;
	public static int RESULTCATEGORY_PARALLEL = 2;
	public static int RESULTCATEGORY_CONTAINED = 3;

	// TODO optimize it, using the coefficients of planes directly
	public static int getConfigPlanePlane(GeoCoordSys2D plane1,
			GeoCoordSys2D plane2) {
		// normal vectors of plane1,2 are parallel
		if (plane1.getDirectionInD3().crossProduct(plane2.getDirectionInD3())
				.isZero()) {
			// one normal vector is perpendicular to the difference of the two
			// two origins
			if (Kernel.isZero(plane2.getCoordSys().getOrigin()
					.sub(plane1.getCoordSys().getOrigin())
					.dotproduct(plane1.getDirectionInD3()))) {
				return RESULTCATEGORY_CONTAINED;
			} else {
				return RESULTCATEGORY_PARALLEL;
			}
		} else {
			return RESULTCATEGORY_GENERAL;
		}
	}

	// TODO optimize it, using the coefficients of planes directly
	public static int getConfigPlanePlane(CoordSys cs1, CoordSys cs2) {

		if (cs1.getDimension() != 2 || cs2.getDimension() != 2)
			return RESULTCATEGORY_NA;

		// normal vectors of plane1,2 are parallel
		if (cs1.getNormal().crossProduct(cs2.getNormal()).isZero()) {
			// one normal vector is perpendicular to the difference of the two
			// two origins
			if (Kernel.isZero(cs2.getOrigin().sub(cs1.getOrigin())
					.dotproduct(cs1.getNormal()))) {
				return RESULTCATEGORY_CONTAINED;
			} else {
				return RESULTCATEGORY_PARALLEL;
			}
		} else {
			return RESULTCATEGORY_GENERAL;
		}
	}

	@Override
	protected String getIntersectionTypeString() {
		return "IntersectionLineOfAB";
	}

	@Override
	public final Commands getClassName() {
		return Commands.IntersectPath;
	}

}
