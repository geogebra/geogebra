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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.matrix.CoordMatrix;

/**
 *
 * @author ggb3D
 * 
 */
public class AlgoIntersectPlaneQuadric extends AlgoElement3D {

	// inputs
	/** plane */
	protected GeoCoordSys2D plane;
	/** second coord sys */
	protected GeoQuadricND quadric;

	// output
	/** intersection */
	protected GeoConic3D conic;

	private CoordMatrix cm = new CoordMatrix(3, 3);
	private CoordMatrix tmpMatrix = new CoordMatrix(3, 4);
	private CoordMatrix parametricMatrix;

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param plane
	 *            plane
	 * @param quadric
	 *            quadric
	 * @param addToCons
	 *            whether to add to cons
	 */
	AlgoIntersectPlaneQuadric(Construction cons, GeoCoordSys2D plane,
			GeoQuadricND quadric, boolean addToCons) {

		super(cons, addToCons);

		this.plane = plane;
		this.quadric = quadric;

		conic = newConic(cons);

		// end
		if (addToCons) {
			end();
		}
	}

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param plane
	 *            plane
	 * @param quadric
	 *            quadric
	 */
	AlgoIntersectPlaneQuadric(Construction cons, GeoCoordSys2D plane,
			GeoQuadricND quadric) {

		this(cons, plane, quadric, true);

	}

	/**
	 * end of contructor for this algo
	 */
	protected void end() {
		setInputOutput(new GeoElement[] { plane.toGeoElement(), quadric },
				new GeoElement[] { conic });
	}

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @return new conic for intersection
	 */
	protected GeoConic3D newConic(Construction cons1) {
		return new GeoConic3D(cons1, true);
	}

	/**
	 * return the intersection
	 * 
	 * @return the intersection
	 */
	public GeoConic3D getConic() {
		return conic;
	}

	// /////////////////////////////////////////////
	// COMPUTE

	@Override
	public void compute() {
		conic.setCoordSys(plane.getCoordSys());
		if (!quadric.isDefined() || !plane.isDefined()) {
			conic.setUndefined();
			return;
		}
		intersectPlaneQuadric(plane, quadric, conic);
	}

	private void intersectPlaneQuadric(GeoCoordSys2D inputPlane,
			GeoQuadricND inputQuad, GeoConic3D outputConic) {
		if (parametricMatrix == null) {
			parametricMatrix = new CoordMatrix(4, 3);
		}
		CoordMatrix qm = inputQuad.getSymetricMatrix();
		CoordMatrix pm = inputPlane.getCoordSys()
				.getParametricMatrix(parametricMatrix);

		// sets the conic matrix from plane and quadric matrix
		cm.setMul(tmpMatrix.setMulT1(pm, qm), pm);

		// Application.debug("pm=\n"+pm+"\nqm=\n"+qm+"\ncm=\n"+cm);

		outputConic.setCoordSys(inputPlane.getCoordSys());
		outputConic.setMatrix(cm);

	}

	@Override
	public Commands getClassName() {
		return Commands.IntersectPath;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();

		sb.append(getLoc().getPlain("IntersectionCurveOfAB",
				plane.getLabel(tpl), quadric.getLabel(tpl)));

		return sb.toString();
	}

}
