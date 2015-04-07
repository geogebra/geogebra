/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoEllipseFociPoint.java
 * 
 * Ellipse with Foci A and B passing through point C
 *
 * Michael Borcherds
 * 2008-04-06
 * adapted from EllipseFociLength
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoEllipseHyperbolaFociPoint extends
		AlgoEllipseHyperbolaFociPointND {

	public AlgoEllipseHyperbolaFociPoint(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C, final int type) {
		super(cons, label, A, B, C, null, type);
	}

	public AlgoEllipseHyperbolaFociPoint(Construction cons, GeoPointND A,
			GeoPointND B, GeoPointND C, final int type) {

		super(cons, A, B, C, null, type);

	}

	@Override
	protected GeoConicND newGeoConic(Construction cons) {
		return new GeoConic(cons);
	}

	@Override
	protected GeoPoint getA2d() {
		return (GeoPoint) A;
	}

	@Override
	protected GeoPoint getB2d() {
		return (GeoPoint) B;
	}

	@Override
	protected GeoPoint getC2d() {
		return (GeoPoint) C;
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	@Override
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {

		if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			return LocusEquation.eqnHyperbolaFociPoint(geo, this, scope);
		}

		return LocusEquation.eqnEllipseFociPoint(geo, this, scope);
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

}
