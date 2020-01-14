/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoEllipseFociLength.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoConicFociLengthND;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * 
 * @author Markus
 */
public abstract class AlgoConicFociLength3D extends AlgoConicFociLengthND {

	protected GeoDirectionND orientation;
	private GeoPoint A2d;
	private GeoPoint B2d;
	private Coords project;

	protected AlgoConicFociLength3D(Construction cons, String label,
			GeoPointND A, GeoPointND B,
			GeoNumberValue a, GeoDirectionND orientation) {
		super(cons, label, A, B, a, orientation);
	}

	@Override
	protected void setOrientation(GeoDirectionND orientation) {
		this.orientation = orientation;
	}

	@Override
	protected GeoConicND newGeoConic(Construction cons0) {
		GeoConic3D ret = new GeoConic3D(cons0);
		ret.setCoordSys(new CoordSys(2));
		return ret;
	}

	@Override
	protected void setInput() {
		input = new GeoElement[4];
		input[0] = (GeoElement) A;
		input[1] = (GeoElement) B;
		input[2] = ageo;
		input[3] = (GeoElement) orientation;
	}

	@Override
	protected void setInputOutput() {
		super.setInputOutput();

		A2d = new GeoPoint(cons);
		B2d = new GeoPoint(cons);
	}

	@Override
	protected GeoPoint getA2d() {
		return A2d;
	}

	@Override
	protected GeoPoint getB2d() {
		return B2d;
	}

	@Override
	public void compute() {
		if (orientation == kernel.getSpace()) {
			conic.setUndefined();
			return;
		}

		Coords vn = orientation.getDirectionInD3();

		if (vn.isZero()) {
			conic.setUndefined();
			return;
		}

		CoordSys cs = conic.getCoordSys();
		cs.resetCoordSys();

		Coords Ac = A.getInhomCoordsInD3();
		Coords Bc = B.getInhomCoordsInD3();

		Coords d1 = Bc.sub(Ac);

		// check if line (AB) and vn are orthogonal
		if (!DoubleUtil.isZero(d1.dotproduct(vn))) {
			conic.setUndefined();
			return;
		}

		// set the coord sys
		cs.addPoint(Ac);
		cs.addVector(d1);
		cs.addVector(vn.crossProduct4(d1));

		if (!cs.makeOrthoMatrix(false, false)) {
			conic.setUndefined();
			return;
		}

		// project the points on the coord sys
		CoordMatrix4x4 matrix = cs.getMatrixOrthonormal();
		Ac.projectPlaneInPlaneCoords(matrix, project);
		A2d.setCoords(project.getX(), project.getY(), project.getW());
		Bc.projectPlaneInPlaneCoords(matrix, project);
		B2d.setCoords(project.getX(), project.getY(), project.getW());

		super.compute();
	}

	@Override
	protected void initCoords() {
		project = new Coords(4);
	}
}
