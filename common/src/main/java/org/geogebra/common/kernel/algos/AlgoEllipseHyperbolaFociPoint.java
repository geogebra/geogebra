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
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoEllipseHyperbolaFociPoint extends
		AlgoEllipseHyperbolaFociPointND implements SymbolicParametersBotanaAlgo {

	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;

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

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (type == GeoConicNDConstants.CONIC_ELLIPSE) {

			GeoPoint F1 = getA2d();
			GeoPoint F2 = getB2d();
			GeoPoint Q = getC2d();

			if (F1 != null && F2 != null && Q != null) {
				Variable[] vA = F1.getBotanaVars(F1);
				Variable[] vB = F2.getBotanaVars(F2);
				Variable[] vC = Q.getBotanaVars(Q);

				// if the 2 focus points are equal
				// handel the ellipse as a circle
				if (vA[0] == vB[0] && vB[1] == vB[1]) {
					if (botanaVars == null) {
						botanaVars = new Variable[4];
						// center
						botanaVars[0] = vA[0];
						botanaVars[1] = vA[1];
						// point on the circle
						botanaVars[2] = vC[0];
						botanaVars[3] = vC[1];
					}
					return botanaPolynomials;
				}
				if (botanaVars == null) {
					botanaVars = new Variable[12];
					// P - point of ellipse
					botanaVars[0] = new Variable();
					botanaVars[1] = new Variable();
					// D
					botanaVars[2] = new Variable();
					botanaVars[3] = new Variable();
					// E
					botanaVars[4] = new Variable();
					botanaVars[5] = new Variable();
					// A - focus point
					botanaVars[6] = vA[0];
					botanaVars[7] = vA[1];
					// B - focus point
					botanaVars[8] = vB[0];
					botanaVars[9] = vB[1];
					// C - point on ellipse
					botanaVars[10] = vC[0];
					botanaVars[11] = vC[1];
				}

				botanaPolynomials = new Polynomial[5];

				Polynomial d1 = new Polynomial(botanaVars[2]);
				Polynomial d2 = new Polynomial(botanaVars[3]);
				Polynomial e1 = new Polynomial(botanaVars[4]);
				Polynomial e2 = new Polynomial(botanaVars[5]);

				// d1+d2 = e1+e2
				botanaPolynomials[0] = d1.add(d2).subtract(e1).subtract(e2);

				// d1^2=Polynomial.sqrDistance(a1,a2,c1,c2)
				botanaPolynomials[1] = Polynomial.sqrDistance(vA[0], vA[1],
						vC[0], vC[1]).subtract(d1.multiply(d1));

				// d2^2=Polynomial.sqrDistance(b1,b2,c1,c2)
				botanaPolynomials[2] = Polynomial.sqrDistance(vB[0], vB[1],
						vC[0], vC[1]).subtract(d2.multiply(d2));

				// e1^2=Polynomial.sqrDistance(a1,a2,p1,p2)
				botanaPolynomials[3] = Polynomial.sqrDistance(vA[0], vA[1],
						botanaVars[0], botanaVars[1]).subtract(e1.multiply(e1));

				// e2^2=Polynomial.sqrDistance(b1,b2,p1,p2)
				botanaPolynomials[4] = Polynomial.sqrDistance(vB[0], vB[1],
						botanaVars[0], botanaVars[1]).subtract(e2.multiply(e2));

				return botanaPolynomials;

			}
			throw new NoSymbolicParametersException();

		} else if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			GeoPoint F1 = getA2d();
			GeoPoint F2 = getB2d();
			GeoPoint Q = getC2d();

			if (F1 != null && F2 != null && Q != null) {
				Variable[] vA = F1.getBotanaVars(F1);
				Variable[] vB = F2.getBotanaVars(F2);
				Variable[] vC = Q.getBotanaVars(Q);

				if (botanaVars == null) {
					botanaVars = new Variable[12];
					// P - point of hyperbola
					botanaVars[0] = new Variable();
					botanaVars[1] = new Variable();
					// D
					botanaVars[2] = new Variable();
					botanaVars[3] = new Variable();
					// E
					botanaVars[4] = new Variable();
					botanaVars[5] = new Variable();
					// A 
					botanaVars[6] = vA[0];
					botanaVars[7] = vA[1];
					// B 
					botanaVars[8] = vB[0];
					botanaVars[9] = vB[1];
					// C 
					botanaVars[10] = vC[0];
					botanaVars[11] = vC[1];
				}

				botanaPolynomials = new Polynomial[5];

				Polynomial d1 = new Polynomial(botanaVars[2]);
				Polynomial d2 = new Polynomial(botanaVars[3]);
				Polynomial e1 = new Polynomial(botanaVars[4]);
				Polynomial e2 = new Polynomial(botanaVars[5]);

				// d1+d2 = e1+e2
				botanaPolynomials[0] = d1.subtract(d2).subtract(e1).add(e2);

				// d1^2=Polynomial.sqrDistance(a1,a2,c1,c2)
				botanaPolynomials[1] = Polynomial.sqrDistance(vA[0], vA[1],
						vC[0], vC[1]).subtract(d1.multiply(d1));

				// d2^2=Polynomial.sqrDistance(b1,b2,c1,c2)
				botanaPolynomials[2] = Polynomial.sqrDistance(vB[0], vB[1],
						vC[0], vC[1]).subtract(d2.multiply(d2));

				// e1^2=Polynomial.sqrDistance(a1,a2,p1,p2)
				botanaPolynomials[3] = Polynomial.sqrDistance(vA[0], vA[1],
						botanaVars[0], botanaVars[1]).subtract(e1.multiply(e1));

				// e2^2=Polynomial.sqrDistance(b1,b2,p1,p2)
				botanaPolynomials[4] = Polynomial.sqrDistance(vB[0], vB[1],
						botanaVars[0], botanaVars[1]).subtract(e2.multiply(e2));
			return botanaPolynomials;
			}
			throw new NoSymbolicParametersException();

		} else {
			throw new NoSymbolicParametersException();
		}
	}

}
