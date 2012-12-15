package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.HasSegments;

/**
 * intersect line and polyhedron
 * @author mathieu
 *
 */
public class AlgoIntersectLinePolyhedron3D extends AlgoIntersectLinePolygon3D{

	/**
	 * constructor
	 * @param c construction
	 * @param labels labels
	 * @param g line
	 * @param p polyhedron
	 */
	public AlgoIntersectLinePolyhedron3D(Construction c, String[] labels,
			GeoLineND g, GeoPolyhedron p) {
		super(c, labels, (GeoElement) g, (HasSegments) p);
	}

}
