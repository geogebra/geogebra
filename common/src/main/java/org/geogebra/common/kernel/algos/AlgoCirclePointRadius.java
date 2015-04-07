/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCirclePointRadius.java
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
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 * 
 * @author Markus added TYPE_SEGMENT Michael Borcherds 2008-03-14
 * @version
 */
public class AlgoCirclePointRadius extends AlgoSphereNDPointRadius implements
		AlgoCirclePointRadiusInterface {

	public AlgoCirclePointRadius(Construction cons, String label, GeoPoint M,
			NumberValue r) {

		super(cons, label, M, r);
	}

	public AlgoCirclePointRadius(Construction cons, String label, GeoPoint M,
			GeoSegment segment, boolean dummy) {

		super(cons, label, M, segment, dummy);
	}

	public AlgoCirclePointRadius(Construction cons, GeoPoint M, NumberValue r) {

		super(cons, M, r);

	}

	AlgoCirclePointRadius(Construction cons, GeoPoint M, GeoSegment rgeo) {

		super(cons, M, rgeo);
	}

	@Override
	protected GeoQuadricND createSphereND(Construction cons) {
		return new GeoConic(cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.Circle;
	}

	@Override
	public int getRelatedModeID() {
		switch (super.getType()) {
		case AlgoSphereNDPointRadius.TYPE_RADIUS:
			return EuclidianConstants.MODE_CIRCLE_POINT_RADIUS;
		default:
			return EuclidianConstants.MODE_COMPASSES;
		}
	}

	public GeoConic getCircle() {
		return (GeoConic) getSphereND();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("CircleWithCenterAandRadiusB",
				getM().getLabel(tpl), getRGeo().getLabel(tpl));
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnCirclePointRadius(geo, this, scope);
	}
}
