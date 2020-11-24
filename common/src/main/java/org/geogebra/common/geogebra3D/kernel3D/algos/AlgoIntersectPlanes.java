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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.HasShortSyntax;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.matrix.CoordSys;
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
public class AlgoIntersectPlanes extends AlgoIntersectCoordSys
		implements HasShortSyntax {
	/** unknown */
	public static final int RESULTCATEGORY_NA = -1;
	/** intersecting */
	public static final int RESULTCATEGORY_GENERAL = 1;
	/** parallel */
	public static final int RESULTCATEGORY_PARALLEL = 2;
	/** identical */
	public static final int RESULTCATEGORY_CONTAINED = 3;

	private boolean shortSyntax = false;
	private Coords o;
	private Coords vn;
	private Coords vnn;
	private Coords vn1;
	private Coords vn2;

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
	public AlgoIntersectPlanes(Construction cons, String label, GeoPlaneND cs1,
			GeoPlaneND cs2) {

		super(cons, label, cs1, cs2, false);
	}

	/**
	 * @param cons
	 *            construction
	 * @param cs1
	 *            first plane
	 * @param cs2
	 *            second plane
	 */
	public AlgoIntersectPlanes(Construction cons, GeoPlaneND cs1,
			GeoPlaneND cs2) {
		super(cons, cs1, cs2, false);
	}

	@Override
	protected GeoElement3D createIntersection(Construction cons1) {
		GeoLine3D ret = new GeoLine3D(cons1, true);
		o = new Coords(0, 0, 0, 1);
		vn = new Coords(0, 0, 0, 0);
		vnn = new Coords(0, 0, 0, 0);

		vn1 = new Coords(0, 0, 0, 0);
		vn2 = new Coords(0, 0, 0, 0);

		return ret;
	}

	// /////////////////////////////////////////////
	// COMPUTE

	@Override
	public void compute() {

		CoordSys p1 = ((GeoPlane3D) getCS1()).getCoordSys();
		CoordSys p2 = ((GeoPlane3D) getCS2()).getCoordSys();

		Coords v1 = p1.getEquationVector();
		Coords v2 = p2.getEquationVector();

		vn.setCrossProduct4(v1, v2);

		if (vn.isZero()) {
			getIntersection().setUndefined();
			return;
		}

		vnn.set3(vn);
		vnn.setW(0);
		vnn.normalize();
		vn1.setCrossProduct4(v1, vnn);
		vn1.normalize();
		vn2.setCrossProduct4(v2, vnn);
		vn2.normalize();
		o.setW(1);
		Coords o1 = p1.getOrigin();
		Coords o2 = p2.getOrigin();
		o2.projectPlane(vnn, vn1, vn2, o1, o);

		// update line
		GeoLine3D l = (GeoLine3D) getIntersection();

		l.setCoord(o, vn);
	}

	/**
	 * TODO optimize it, using the coefficients of planes directly
	 * 
	 * @param cs1
	 *            first plane
	 * @param cs2
	 *            second plane
	 * @return whether one plane is contained in the other
	 */
	public static boolean isIntersectionContained(CoordSys cs1, CoordSys cs2) {

		return getConfigPlanePlane(cs1, cs2) == RESULTCATEGORY_CONTAINED;
	}

	private static int getConfigPlanePlane(CoordSys cs1, CoordSys cs2) {

		if (cs1.getDimension() != 2 || cs2.getDimension() != 2) {
			return RESULTCATEGORY_NA;
		}

		// normal vectors of plane1,2 are parallel
		if (cs1.getNormal().crossProduct(cs2.getNormal()).isZero()) {
			// one normal vector is perpendicular to the difference of the two
			// two origins
			if (DoubleUtil.isZero(cs2.getOrigin().sub(cs1.getOrigin())
					.dotproduct(cs1.getNormal()))) {
				return RESULTCATEGORY_CONTAINED;
			}
			return RESULTCATEGORY_PARALLEL;
		}
		return RESULTCATEGORY_GENERAL;
	}

	@Override
	protected String getIntersectionTypeString() {
		return "IntersectionLineOfAB";
	}

	@Override
	public final Commands getClassName() {
		return Commands.IntersectPath;
	}

	@Override
	final public String getDefinition(StringTemplate tpl) {
		if (shortSyntax) {
			return "(" + getCS1().getLabel(tpl) + "," + getCS2().getLabel(tpl)
				+ ")";
		}
		return super.getDefinition(tpl);
	}

	@Override
	public void setShortSyntax(boolean b) {
		this.shortSyntax = b;
	}

	@Override
	protected boolean hasExpXML(String cmdName) {
		return shortSyntax;
	}

	@Override
	final public String toExpString(StringTemplate tpl) {
		return getDefinition(tpl);
	}

}
