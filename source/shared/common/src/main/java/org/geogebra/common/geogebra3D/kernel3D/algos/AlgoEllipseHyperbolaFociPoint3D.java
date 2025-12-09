/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoEllipseHyperbolaFociPointND;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;

public class AlgoEllipseHyperbolaFociPoint3D
		extends AlgoEllipseHyperbolaFociPointND {
	private GeoPoint A2d;
	private GeoPoint B2d;
	private GeoPoint C2d;
	private Coords project;

	public AlgoEllipseHyperbolaFociPoint3D(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C,
			GeoDirectionND orientation, final int type) {
		super(cons, label, A, B, C, orientation, type);
	}

	public AlgoEllipseHyperbolaFociPoint3D(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C, final int type) {
		this(cons, label, A, B, C, null, type);
	}

	@Override
	protected GeoConicND newGeoConic(Construction cons1) {
		GeoConic3D ret = new GeoConic3D(cons1);
		ret.setCoordSys(new CoordSys(2));
		return ret;
	}

	@Override
	protected void setInputOutput() {

		super.setInputOutput();

		A2d = new GeoPoint(cons);
		B2d = new GeoPoint(cons);
		C2d = new GeoPoint(cons);
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
	protected GeoPoint getC2d() {
		return C2d;
	}

	/**
	 * @param cs
	 *            ellipse coord sys
	 * @param Ac
	 *            first focus coords
	 * @param Bc
	 *            second focus coords
	 * @param Cc
	 *            point on ellipse coords
	 * @return true if coord sys is possible
	 */
	protected boolean setCoordSys(CoordSys cs, Coords Ac, Coords Bc,
			Coords Cc) {

		// set the coord sys
		cs.addPoint(Ac);
		cs.addPoint(Bc);
		cs.addPoint(Cc);

		return cs.makeOrthoMatrix(false, false);
	}

	@Override
	public void compute() {

		CoordSys cs = conic.getCoordSys();
		cs.resetCoordSys();

		Coords Ac = getFocus1().getInhomCoordsInD3();
		Coords Bc = getFocus2().getInhomCoordsInD3();
		Coords Cc = getConicPoint().getInhomCoordsInD3();

		if (!setCoordSys(cs, Ac, Bc, Cc)) {
			conic.setUndefined();
			return;
		}

		// project the points on the coord sys
		CoordMatrix4x4 matrix = cs.getMatrixOrthonormal();
		Ac.projectPlaneInPlaneCoords(matrix, project);
		A2d.setCoords(project.getX(), project.getY(), project.getW());
		Bc.projectPlaneInPlaneCoords(matrix, project);
		B2d.setCoords(project.getX(), project.getY(), project.getW());
		Cc.projectPlaneInPlaneCoords(matrix, project);
		C2d.setCoords(project.getX(), project.getY(), project.getW());

		super.compute();
	}

	@Override
	protected void initCoords() {
		project = new Coords(4);
	}

}
