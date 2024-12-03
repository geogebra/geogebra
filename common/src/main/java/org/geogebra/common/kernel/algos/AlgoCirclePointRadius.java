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
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * 
 * @author Markus added TYPE_SEGMENT Michael Borcherds 2008-03-14
 */
public class AlgoCirclePointRadius extends AlgoSphereNDPointRadius implements
		AlgoCirclePointRadiusInterface, SymbolicParametersBotanaAlgo {

	private PVariable[] botanaVars;
	private PPolynomial[] botanaPolynomials;

	/**
	 * @param cons
	 *            construction
	 * @param M
	 *            center
	 * @param r
	 *            radius
	 */
	public AlgoCirclePointRadius(Construction cons, GeoPoint M, GeoNumberValue r) {
		super(cons, M, r);
	}

	AlgoCirclePointRadius(Construction cons, GeoPoint M, GeoSegment rgeo) {
		super(cons, M, rgeo);
	}

	@Override
	protected GeoQuadricND createSphereND(Construction cons1) {
		return new GeoConic(cons1);
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

	/**
	 * @return resulting conic
	 */
	public GeoConic getCircle() {
		return (GeoConic) getSphereND();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("CircleWithCenterAandRadiusB",
				"Circle with center %0 and radius %1",
				getM().getLabel(tpl), getRGeo().getLabel(tpl));
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		GeoPoint P = (GeoPoint) this.getInput(0);

		/* SPECIAL CASE 1: radius is a segment */
		if (this.getInput(1) instanceof GeoSegment) {
			/*
			 * Here we do the full work for this segment. It would be nicer to
			 * put this code into GeoSegment but we need to use the square of
			 * the length of the segment in this special case.
			 */
			GeoSegment s = (GeoSegment) this.getInput(1);
			if (botanaVars == null) {
				PVariable[] centerBotanaVars = P.getBotanaVars(P);
				botanaVars = new PVariable[4];
				// center P
				botanaVars[0] = centerBotanaVars[0];
				botanaVars[1] = centerBotanaVars[1];
				// point C on the circle
				botanaVars[2] = new PVariable(kernel);
				botanaVars[3] = new PVariable(kernel);
			}
			GeoPoint A = s.getStartPoint();
			GeoPoint B = s.getEndPoint();
			PVariable[] ABotanaVars = A.getBotanaVars(A);
			PVariable[] BBotanaVars = B.getBotanaVars(B);

			botanaPolynomials = new PPolynomial[2];
			// C-P == B-A <=> C-P-B+A == 0
			botanaPolynomials[0] = new PPolynomial(botanaVars[2])
					.subtract(new PPolynomial(botanaVars[0]))
					.subtract(new PPolynomial(BBotanaVars[0]))
					.add(new PPolynomial(ABotanaVars[0]));
			botanaPolynomials[1] = new PPolynomial(botanaVars[3])
					.subtract(new PPolynomial(botanaVars[1]))
					.subtract(new PPolynomial(BBotanaVars[1]))
					.add(new PPolynomial(ABotanaVars[1]));
			// done for both coordinates!
			return botanaPolynomials;
		}

		/* SPECIAL CASE 2: radius is an expression */

		GeoNumeric num = null;
		if (this.getInput(1) instanceof GeoNumeric) {
			num = (GeoNumeric) this.getInput(1);
		}
		if (P == null || num == null) {
			throw new NoSymbolicParametersException();
		}

		if (botanaVars == null) {
			PVariable[] centerBotanaVars = P.getBotanaVars(P);
			botanaVars = new PVariable[5];
			// center
			botanaVars[0] = centerBotanaVars[0];
			botanaVars[1] = centerBotanaVars[1];
			// point on circle
			botanaVars[2] = new PVariable(kernel);
			botanaVars[3] = new PVariable(kernel);
			// radius
			botanaVars[4] = new PVariable(kernel);
		}

		PPolynomial[] extraPolys = null;
		if (num.getParentAlgorithm() instanceof AlgoDependentNumber) {
			extraPolys = num.getBotanaPolynomials(num);
			botanaPolynomials = new PPolynomial[extraPolys.length + 1];
		} else {
			botanaPolynomials = new PPolynomial[1];
		}

		/*
		 * Note that we read the Botana variables just after reading the Botana
		 * polynomials since the variables are set after the polys are set.
		 */
		PVariable[] radiusBotanaVars = num.getBotanaVars(num);
		int k = 0;
		// r^2
		PPolynomial sqrR = PPolynomial.sqr(new PPolynomial(radiusBotanaVars[0]));
		// define radius
		if (extraPolys != null) {
			botanaPolynomials = new PPolynomial[extraPolys.length + 1];
			for (k = 0; k < extraPolys.length; k++) {
				botanaPolynomials[k] = extraPolys[k];
			}
		}

		// define circle
		botanaPolynomials[k] = PPolynomial.sqrDistance(botanaVars[0],
				botanaVars[1], botanaVars[2], botanaVars[3]).subtract(sqrR);

		return botanaPolynomials;

	}

	@Override
	public boolean hasOnlyFreeInputPoints(EuclidianViewInterfaceSlim view) {
		return view.getFreeInputPoints(this).size() == 1;
	}
}
