package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.TreeMap;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.HasSegments;

public class AlgoIntersectPlanePolyhedron extends AlgoIntersectLinePolygon3D {

	private GeoPlane3D plane;

	public AlgoIntersectPlanePolyhedron(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolyhedron p) {
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

		/*
		 * Coords[] intersection = CoordMatrixUtil.intersectPlanes(
		 * plane.getCoordSys().getMatrixOrthonormal(),
		 * currentFace.getCoordSys().getMatrixOrthonormal());
		 * 
		 * o1 = intersection[0]; d1 = intersection[1];
		 */

	}

	// private GeoPolygon currentFace;

	@Override
	protected void intersectionsCoords(HasSegments p,
			TreeMap<Double, Coords> newCoords) {

		/*
		 * GeoPolyhedron polyh = (GeoPolyhedron) p; TreeSet<GeoPolygon> polygons
		 * = new TreeSet<GeoPolygon>();
		 * polygons.addAll(polyh.getPolygonsLinked());
		 * polygons.addAll(polyh.getPolygons()); currentFace = polygons.first();
		 * polygons.remove(currentFace); App.debug(currentFace); TreeMap<Double,
		 * Coords> currentFaceCoords = new TreeMap<Double, Coords>();
		 * intersectionsCoordsContained(currentFace, currentFaceCoords); if
		 * (currentFaceCoords.size()>0){ Object[] points =
		 * currentFaceCoords.values().toArray(); Coords b = (Coords) points[0];
		 * for (int i=1; i<points.length; i++){ Coords a = b; b = (Coords)
		 * points[i]; App.debug("\na=\n"+a); App.debug("\nb=\n"+b); Coords c2D =
		 * currentFace.getNormalProjection(a.add(b).mul(0.5))[1];
		 * App.debug(currentFace.isInRegion(c2D.getX(), c2D.getY())); } }
		 */

		for (int i = 0; i < p.getSegments().length; i++) {
			GeoSegmentND seg = p.getSegments()[i];

			Coords coords = intersectionCoords(seg);
			if (coords != null)
				newCoords.put((double) i, coords);

		}
	}

	private Coords intersectionCoords(GeoSegmentND seg) {
		Coords o = seg.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords d = seg.getPointInD(3, 1).getInhomCoordsInSameDimension().sub(o);

		Coords globalCoords = new Coords(4);
		Coords inPlaneCoords = new Coords(4);
		o.projectPlaneThruV(plane.getCoordSys().getMatrixOrthonormal(), d,
				globalCoords, inPlaneCoords);

		// check if projection is intersection point
		if (!Kernel.isZero(globalCoords.getW())
				&& seg.respectLimitedPath(-inPlaneCoords.get(3)))
			return globalCoords;

		return null;
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
