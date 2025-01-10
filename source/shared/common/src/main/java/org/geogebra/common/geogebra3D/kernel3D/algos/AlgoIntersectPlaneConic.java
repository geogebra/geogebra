package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.Coords;

public class AlgoIntersectPlaneConic extends AlgoIntersectConic3D {
	private Coords[] intersection;

	/**
	 * @param cons
	 *            construction
	 * @param plane
	 *            plane
	 * @param c
	 *            conic
	 */
	public AlgoIntersectPlaneConic(Construction cons, GeoCoordSys2D plane,
			GeoConicND c) {
		super(cons, (GeoElement) plane, c);
	}

	/**
	 * @param cons
	 *            construction
	 */
	public AlgoIntersectPlaneConic(Construction cons) {
		super(cons);
	}

	@Override
	public void compute() {
		intersect((GeoCoordSys2D) getFirstGeo(), c, P);
	}

	/**
	 * @param plane
	 *            plane
	 * @param conic
	 *            conic
	 * @param points
	 *            output point
	 */
	public final void intersect(GeoCoordSys2D plane, GeoConicND conic,
			GeoPoint3D[] points) {
		// calc intersection line of the plane and the plane including the conic
		intersection = CoordMatrixUtil.intersectPlanes(
				plane.getCoordSys().getMatrixOrthonormal(),
				conic.getCoordSys().getMatrixOrthonormal());

		super.intersect(conic, points);
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	protected Coords getFirstGeoStartInhomCoords() {
		return intersection[0];
	}

	@Override
	protected Coords getFirstGeoDirectionInD3() {
		return intersection[1];
	}

	@Override
	protected boolean getFirstGeoRespectLimitedPath(Coords p) {
		return true;
	}

	@Override
	protected void checkIsOnFirstGeo(GeoPoint3D p) {
		// nothing to do here
	}

}
