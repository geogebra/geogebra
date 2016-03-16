package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoIntersectAbstract;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;

public class AlgoIntersectPlaneCurve extends AlgoIntersectAbstract {
	private GeoElement[] P;
	public AlgoIntersectPlaneCurve(Construction cons, String[] labels,
			GeoCurveCartesianND c, GeoCoordSys2D plane) {
		this(cons, labels, plane, c);
	}

	public AlgoIntersectPlaneCurve(Construction cons, String[] labels,
			GeoCoordSys2D plane, GeoCurveCartesianND c) {
		this(cons, plane, c);
		GeoElement.setLabels(labels, P);
	}

	public AlgoIntersectPlaneCurve(Construction cons, GeoCoordSys2D plane,
			GeoCurveCartesianND c) {
		super(cons);
	}

	public AlgoIntersectPlaneCurve(Construction cons) {
		super(cons);
	}

	private Coords[] intersection;

	@Override
	public void compute() {

		// intersect((GeoCoordSys2D) getFirtGeo(), c, P);

	}

	public final void intersect(GeoCoordSys2D plane, GeoConicND c,
			GeoPoint3D[] P) {
		// calc intersection line of the plane and the plane including the conic
		intersection = CoordMatrixUtil
				.intersectPlanes(plane.getCoordSys().getMatrixOrthonormal(), c
						.getCoordSys().getMatrixOrthonormal());

		// super.intersect(c, P);
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	protected void setInputOutput() {
		// TODO Auto-generated method stub

	}

	public GeoPoint3D[] getIntersectionPoints() {
		// TODO Auto-generated method stub
		return null;
	}

}
