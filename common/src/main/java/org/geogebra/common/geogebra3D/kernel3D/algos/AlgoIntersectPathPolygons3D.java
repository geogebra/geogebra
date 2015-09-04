package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;

/**
 * AlgoElement class for finding intersection path(region) between two 3D
 * polygons
 * 
 * @author thilina
 *
 */
public class AlgoIntersectPathPolygons3D extends AlgoPolygonOperations3D {

	/**
	 * common constructor
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels string array
	 * @param inPoly0
	 *            first input polygon
	 * @param inPoly1
	 *            second input polygon
	 * @param poly0
	 *            plane of the first input polygon
	 * @param poly1
	 *            plane of the second input polygon
	 */
	public AlgoIntersectPathPolygons3D(Construction cons, String[] labels,
			GeoPolygon3D inPoly0, GeoPolygon3D inPoly1) {

		super(cons, labels, inPoly0, inPoly1, PolyOperation.INTERSECTION);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.IntersectPath;
	}

}
