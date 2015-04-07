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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;

/**
 * Creates all angles of a polygon.
 */

public class AlgoAnglePolygon extends AlgoAnglePolygonND {

	public AlgoAnglePolygon(Construction cons, String[] labels, GeoPolygon poly) {
		this(cons, labels, poly, null);
	}

	public AlgoAnglePolygon(Construction cons, String[] labels,
			GeoPolygon poly, GeoDirectionND orientation) {
		this(cons, poly, orientation);
		// if only one label (e.g. "A"), new labels will be A_1, A_2, ...
		setLabels(labels);

		update();
	}

	AlgoAnglePolygon(Construction cons, GeoPolygon p, GeoDirectionND orientation) {
		super(cons);
		setPolyAndOrientation(p, orientation);
		algoAngle = newAlgoAnglePoints(cons);
		outputAngles = createOutputPoints();
		setInputOutput(); // for AlgoElement
		compute();
	}

	/**
	 * 
	 * @param cons
	 * @return helper algo
	 */
	protected AlgoAnglePointsND newAlgoAnglePoints(Construction cons) {
		return new AlgoAnglePoints(cons);
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
	public GeoElement getInput(int i) {
		return getInputMaybeXOYPlane(i);
	}

	// TODO Consider locusequability
}
