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

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Hyperbola for given foci and first semi-axis length
 * 
 * @author Markus
 */
public class AlgoHyperbolaFociLength extends AlgoConicFociLength {
	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            focus
	 * @param B
	 *            focus
	 * @param a
	 *            major halfaxis
	 */
	public AlgoHyperbolaFociLength(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoNumberValue a) {
		super(cons, label, A, B, a);
	}

	@Override
	public Commands getClassName() {
		return Commands.Hyperbola;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS;
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
