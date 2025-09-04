package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;

/**
 * AlgoElement creating a GeoPolygon3D
 * 
 * @author ggb3D
 *
 */
public class AlgoPolygon3D extends AlgoPolygon {

	/**
	 * Constructor with points
	 * 
	 * @param cons
	 *            the construction
	 * @param label
	 *            names of the polygon and segments
	 * @param points
	 *            vertices of the polygon
	 * @param vertices
	 *            list of vertices
	 */
	public AlgoPolygon3D(Construction cons, String[] label, GeoPointND[] points,
			GeoList vertices) {
		this(cons, label, points, true, vertices);

	}

	/**
	 * @param cons
	 *            the construction
	 * @param labels
	 *            names of the polygon and the segments
	 * @param points
	 *            vertices of the polygon
	 * @param createSegments
	 *            says if the polygon has to creates its edges (3D only)
	 * @param vertices
	 *            list of vertices
	 */
	public AlgoPolygon3D(Construction cons, String[] labels,
			GeoPointND[] points, boolean createSegments, GeoList vertices) {
		super(cons, labels, points, vertices, null, createSegments, null, null);

	}

	/**
	 * @param cons
	 *            the construction
	 * @param points
	 *            vertices of the polygon
	 * @param createSegments
	 *            says if the polygon has to creates its edges (3D only)
	 * @param polyhedron
	 *            polyhedron (when segment is part of)
	 */
	public AlgoPolygon3D(Construction cons, GeoPointND[] points,
			boolean createSegments, GeoElement polyhedron) {
		super(cons, points, null, null, createSegments, polyhedron, null);

	}

	/**
	 * create the polygon
	 * 
	 * @param createSegments
	 *            says if the polygon has to creates its edges (3D only)
	 */
	@Override
	protected void createPolygon(boolean createSegments) {
		if (points == null) {
			int size = geoList.size();
			points = new GeoPointND[size];
			for (int i = 0; i < size; i++) {
				points[i] = (GeoPointND) geoList.get(i);
			}
		}
		poly = new GeoPolygon3D(cons, points, cs2D, createSegments);
		if (polyhedron != null) {
			((GeoPolygon3D) poly).setIsPartOfClosedSurface(true);
			poly.addMeta(polyhedron);
		}
	}

	@Override
	public void compute() {
		if (geoList != null) {
			updatePointArray();
			calcArea();

			// update region coord sys
			poly.updateRegionCS();
		} else {
			// check if a coord sys is possible
			if (((GeoPolygon3D) poly).updateCoordSys()) {
				super.compute();
			} else {
				poly.setUndefined();
			}
		}

	}

	@Override
	public void calcCentroid(GeoPoint p) {
		// TODO
		Log.debug("centroid unimplemented for 3D polys");
		p.setUndefined();

	}

}
