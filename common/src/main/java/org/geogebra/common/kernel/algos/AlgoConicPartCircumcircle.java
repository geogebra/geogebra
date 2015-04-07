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
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Circle arc or sector defined by three points.
 */
public class AlgoConicPartCircumcircle extends AlgoConicPartCircumcircleND {

	public AlgoConicPartCircumcircle(Construction cons, String label,
			GeoPoint A, GeoPoint B, GeoPoint C, int type) {
		super(cons, label, A, B, C, type);
	}

	public AlgoConicPartCircumcircle(Construction cons, GeoPoint A, GeoPoint B,
			GeoPoint C, int type) {
		super(cons, A, B, C, type);
	}

	@Override
	protected AlgoCircleThreePoints getAlgo() {
		return new AlgoCircleThreePoints(cons, getA(), getB(), getC());
	}

	@Override
	protected GeoConicPart createConicPart(Construction cons, int type) {
		return new GeoConicPart(cons, type);
	}

	/**
	 * Method for LocusEqu.
	 * 
	 * @return first point.
	 */
	@Override
	final public GeoPoint getA() {
		return (GeoPoint) A;
	}

	/**
	 * Method for LocusEqu.
	 * 
	 * @return second point.
	 */
	@Override
	final public GeoPoint getB() {
		return (GeoPoint) B;
	}

	/**
	 * Method for LocusEqu.
	 * 
	 * @return third point.
	 */
	@Override
	final public GeoPoint getC() {
		return (GeoPoint) C;
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	@Override
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnCircumcircleArc(geo, this, scope);
	}

	@Override
	public GeoConicPart getConicPart() {
		return (GeoConicPart) super.getConicPart();
	}

	@Override
	protected void computeSinglePoint() {
		GeoPoint midpoint = getA();
		GeoConic.setSinglePoint((GeoConic) conicPart, midpoint.inhomX,
				midpoint.inhomY);
		super.computeSinglePoint();
	}

}
