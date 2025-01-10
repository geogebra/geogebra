/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoHyperbolaFociLength.java
 *
 * Created on 15. November 2001, 21:37
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
