package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.TreeMap;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.HasSegments;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

public class AlgoIntersectPlanePolyhedron extends AlgoIntersectLinePolygon3D {

	private GeoPlane3D plane;

	/**
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param plane
	 *            plane
	 * @param p
	 *            polyhedron
	 */
	public AlgoIntersectPlanePolyhedron(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolyhedron p) {
		super(c, labels, plane, p);
	}

	@Override
	protected void setFirstInput(GeoElementND geo) {
		this.plane = (GeoPlane3D) geo;

	}

	@Override
	protected GeoElement getFirstInput() {
		return plane;
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
	protected void intersectionsCoords(HasSegments poly,
			TreeMap<Double, Coords> newCoords) {

		/*
		 * GeoPolyhedron polyh = (GeoPolyhedron) p; TreeSet<GeoPolygon> polygons
		 * = new TreeSet<GeoPolygon>();
		 * polygons.addAll(polyh.getPolygonsLinked());
		 * polygons.addAll(polyh.getPolygons()); currentFace = polygons.first();
		 * polygons.remove(currentFace); Log.debug(currentFace); TreeMap<Double,
		 * Coords> currentFaceCoords = new TreeMap<Double, Coords>();
		 * intersectionsCoordsContained(currentFace, currentFaceCoords); if
		 * (currentFaceCoords.size()>0){ Object[] points =
		 * currentFaceCoords.values().toArray(); Coords b = (Coords) points[0];
		 * for (int i=1; i<points.length; i++){ Coords a = b; b = (Coords)
		 * points[i]; Log.debug("\na=\n"+a); Log.debug("\nb=\n"+b); Coords c2D =
		 * currentFace.getNormalProjection(a.add(b).mul(0.5))[1];
		 * Log.debug(currentFace.isInRegion(c2D.getX(), c2D.getY())); } }
		 */

		for (int i = 0; i < poly.getSegments().length; i++) {
			GeoSegmentND seg = poly.getSegments()[i];

			Coords coords = intersectionCoords(seg);
			if (coords != null) {
				newCoords.put((double) i, coords);
			}

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
		if (!DoubleUtil.isZero(globalCoords.getW())
				&& seg.respectLimitedPath(-inPlaneCoords.get(3))) {
			return globalCoords;
		}

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
