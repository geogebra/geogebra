/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Creates all angles of a polygon.
 */

public class AlgoAnglePolygon extends AlgoAnglePolygonND {

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            angle labels
	 * @param poly
	 *            polygon
	 */
	public AlgoAnglePolygon(Construction cons, String[] labels,
			GeoPolygon poly) {
		this(cons, poly);
		// if only one label (e.g. "A"), new labels will be A_1, A_2, ...
		setLabels(labels);

		update();
	}

	/**
	 * @param cons
	 *            construction
	 * @param poly
	 *            polygon
	 * 
	 */
	public AlgoAnglePolygon(Construction cons, GeoPolygon poly) {
		super(cons);
		setPolyAndOrientation(poly, null);
		algoAngle = newAlgoAnglePoints(cons);
		outputAngles = createOutputPoints();
		setInputOutput(); // for AlgoElement
		compute();
	}

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @return helper algo
	 */
	@Override
	protected AlgoAnglePointsND newAlgoAnglePoints(Construction cons1) {
		return new AlgoAnglePoints(cons1);
	}

	// ///////////////////////////////
	// TRICKS FOR XOY PLANE
	// ///////////////////////////////

	@Override
	protected int getInputLengthForXML() {
		return getInputLengthForXMLMayNeedXOYPlane();
	}

	@Override
	protected int getInputLengthForCommandDescription() {
		return getInputLengthForCommandDescriptionMayNeedXOYPlane();
	}

	@Override
	public GeoElementND getInput(int i) {
		return getInputMaybeXOYPlane(i);
	}

}
