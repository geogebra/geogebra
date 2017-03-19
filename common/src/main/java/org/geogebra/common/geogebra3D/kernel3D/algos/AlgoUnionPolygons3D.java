package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoPolygonOperation.PolyOperation;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoPoly;

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
			GeoPoly inPoly0, GeoPoly inPoly1) {

		super(cons, labels, inPoly0, inPoly1, PolyOperation.UNION);
		initialize(null);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Union;
	}
}
