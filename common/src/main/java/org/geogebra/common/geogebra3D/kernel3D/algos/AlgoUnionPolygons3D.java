package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;

/**
 * AlgoElement class for finding UNION (region union) of two 3D polygons
 * 
 * @author thilina
 *
 */
public class AlgoUnionPolygons3D extends AlgoPolygonOperations3D {
	/**
	 * common constructor
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            string[] labels
	 * @param inPoly0
	 *            input polygons
	 * @param inPoly1
	 *            input polygons
	 */
	public AlgoUnionPolygons3D(Construction cons, String[] labels,
			GeoPolygon3D inPoly0, GeoPolygon3D inPoly1) {

		super(cons, labels, inPoly0, inPoly1, PolyOperation.UNION);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Union;
	}
}
