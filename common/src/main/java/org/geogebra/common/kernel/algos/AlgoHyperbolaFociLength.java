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
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Hyperbola for given foci and first semi-axis length
 * 
 * @author Markus
 * @version
 */
public class AlgoHyperbolaFociLength extends AlgoConicFociLength {

	public AlgoHyperbolaFociLength(Construction cons, String label,
			GeoPointND A, GeoPointND B, NumberValue a) {
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

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnHyperbolaFociLength(geo, this, scope);
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

	@Override
	final public String toString(StringTemplate tpl) {

		return getLoc().getPlain(
						conic.isEllipse() || conic.isCircle() ? "EllipseWithFociABandFirstAxisLengthC"
						: "HyperbolaWithFociABandFirstAxisLengthC",
				A.getLabel(tpl), B.getLabel(tpl),
				a.toGeoElement().getLabel(tpl));
	}
}
