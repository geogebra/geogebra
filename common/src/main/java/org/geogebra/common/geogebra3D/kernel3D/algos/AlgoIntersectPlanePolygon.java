package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.TreeMap;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.HasSegments;

public class AlgoIntersectPlanePolygon extends AlgoIntersectLinePolygon3D {

	private GeoPlane3D plane;

	public AlgoIntersectPlanePolygon(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolygon p) {
		super(c, labels, plane, p);

	}

	@Override
	protected void setFirstInput(GeoElement geo) {
		this.plane = (GeoPlane3D) geo;

	}

	@Override
	protected GeoElement getFirstInput() {
		return (GeoElement) plane;
	}

	@Override
	protected void setIntersectionLine() {

		Coords[] intersection = CoordMatrixUtil.intersectPlanes(plane
				.getCoordSys().getMatrixOrthonormal(), ((GeoPolygon) p)
				.getCoordSys().getMatrixOrthonormal());

		o1 = intersection[0];
		d1 = intersection[1];

	}

	@Override
	protected void intersectionsCoords(HasSegments p,
			TreeMap<Double, Coords> newCoords) {

		// intersection line is contained in polygon plane by definition
		intersectionsCoordsContained(p, newCoords);
	}

	@Override
	protected boolean checkParameter(double t1) {
		return true;
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

}
