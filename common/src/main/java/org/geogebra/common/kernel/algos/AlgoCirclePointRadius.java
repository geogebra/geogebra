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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 * 
 * @author Markus added TYPE_SEGMENT Michael Borcherds 2008-03-14
 */
public class AlgoCirclePointRadius extends AlgoSphereNDPointRadius implements
		AlgoCirclePointRadiusInterface, SymbolicParametersBotanaAlgo {

	private Variable[] botanaVars;
	private Polynomial[] botanaPolynomials;

	public AlgoCirclePointRadius(Construction cons, String label, GeoPoint M,
			GeoNumberValue r) {

		super(cons, label, M, r);
	}

	public AlgoCirclePointRadius(Construction cons, String label, GeoPoint M,
			GeoSegment segment, boolean dummy) {

		super(cons, label, M, segment, dummy);
	}

	public AlgoCirclePointRadius(Construction cons, GeoPoint M,
			GeoNumberValue r) {

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

	public Variable[] getBotanaVars(GeoElementND geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		
		GeoPoint P = (GeoPoint) this.getInput(0);
		GeoNumeric num = null;
		if (this.getInput(1) instanceof GeoNumeric) {
			num = (GeoNumeric) this.getInput(1);
		}

		if (P == null || num == null) {
			throw new NoSymbolicParametersException();
		}
		
		if (botanaVars == null) {
				Variable[] centerBotanaVars = P.getBotanaVars(P);
				botanaVars = new Variable[5];
				// center
				botanaVars[0] = centerBotanaVars[0];
				botanaVars[1] = centerBotanaVars[1];
				// point on circle
				botanaVars[2] = new Variable();
				botanaVars[3] = new Variable();				
				// radius
				botanaVars[4] = new Variable();
		}
		
		botanaPolynomials = new Polynomial[2];
		Variable[] radiusBotanaVars = num.getBotanaVars(num);
		// r^2
		Polynomial sqrR = Polynomial.sqr(new Polynomial(botanaVars[4]));
		// define circle
		botanaPolynomials[0] = Polynomial.sqrDistance(botanaVars[0],
				botanaVars[1], botanaVars[2], botanaVars[3]).subtract(sqrR);
		// define radius
		botanaPolynomials[1] = Polynomial
				.sqrDistance(radiusBotanaVars[0], radiusBotanaVars[1],
						radiusBotanaVars[2], radiusBotanaVars[3])
				.subtract(sqrR);
		
		return botanaPolynomials;

		
	}
}
