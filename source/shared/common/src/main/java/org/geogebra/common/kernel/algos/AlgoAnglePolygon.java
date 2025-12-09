/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
	 *            output labels
	 * @param poly
	 *            polygon
	 */
	public AlgoAnglePolygon(Construction cons, String[] labels,
							GeoPolygon poly) {
		this(cons, labels, poly, false);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            angle labels
	 * @param poly
	 *            polygon
	 * @param internalAngle
	 *            whether to return internal angles
	 */
	public AlgoAnglePolygon(Construction cons, String[] labels,
			GeoPolygon poly, boolean internalAngle) {
		this(cons, poly, internalAngle);
		// if only one label (e.g. "A"), new labels will be A_1, A_2, ...
		outputAngles.setLabelsMulti(labels);

		update();
	}

	/**
	 * @param cons
	 *            construction
	 * @param poly
	 *            polygon
	 */
	public AlgoAnglePolygon(Construction cons, GeoPolygon poly) {
		this(cons, poly, false);
	}

	/**
	 * @param cons
	 *            construction
	 * @param poly
	 *            polygon
	 * @param internalAngle
	 *            whether to return internal angle
	 * 
	 */
	public AlgoAnglePolygon(Construction cons, GeoPolygon poly, boolean internalAngle) {
		super(cons, poly, null, internalAngle);
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
		if (internalAngle) {
			return super.getInputLengthForXML();
		}
		return getInputLengthForXMLMayNeedXOYPlane();
	}

	@Override
	protected int getInputLengthForCommandDescription() {
		if (internalAngle) {
			return super.getInputLengthForCommandDescription();
		}
		return getInputLengthForCommandDescriptionMayNeedXOYPlane();
	}

	@Override
	public GeoElementND getInput(int i) {
		if (internalAngle) {
			return super.getInput(i);
		}
		return getInputMaybeXOYPlane(i);
	}

}
