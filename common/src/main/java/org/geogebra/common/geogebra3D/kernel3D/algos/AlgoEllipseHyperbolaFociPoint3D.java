package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoEllipseHyperbolaFociPointND;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoEllipseHyperbolaFociPoint3D extends
		AlgoEllipseHyperbolaFociPointND {

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
	protected GeoConicND newGeoConic(Construction cons) {
		GeoConic3D ret = new GeoConic3D(cons);
		ret.setCoordSys(new CoordSys(2));
		return ret;
	}

	private GeoPoint A2d, B2d, C2d;

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
	protected boolean setCoordSys(CoordSys cs, Coords Ac, Coords Bc, Coords Cc) {

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

		Coords Ac = A.getInhomCoordsInD3();
		Coords Bc = B.getInhomCoordsInD3();
		Coords Cc = C.getInhomCoordsInD3();

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

	private Coords project;

	@Override
	protected void initCoords() {
		project = new Coords(4);
	}

}
