/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

//

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoPolygon;

/**
 * Computes intersection of two polygons
 * 
 * @author George Sturr
 *
 */
public class AlgoPolygonIntersection extends AlgoPolygonOperation {

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels for output
	 * @param inPoly0
	 *            first input polygon
	 * @param inPoly1
	 *            second input polygon
	 */
	public AlgoPolygonIntersection(Construction cons, String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1) {

		super(cons, labels, inPoly0, inPoly1, PolyOperation.INTERSECTION);
		initialize(null);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels for output
	 * @param inPoly0
	 *            first input polygon
	 * @param inPoly1
	 *            second input polygon
	 * @param outputSizes
	 *            sizes of the results of the operation. Consist of polygonSize,
	 *            pointSize, segmentSize
	 */
	public AlgoPolygonIntersection(Construction cons, String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1, int[] outputSizes) {

		super(cons, labels, inPoly0, inPoly1, PolyOperation.INTERSECTION);
		initialize(outputSizes);
	}

	@Override
	public Commands getClassName() {
		return Commands.IntersectPath;
	}

}