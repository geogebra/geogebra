/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAngularBisector.java
 *
 * Created on 26. Oktober 2001
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoAngularBisectorPoints extends AlgoElement implements
		SymbolicParametersBotanaAlgo {

	private GeoPoint A, B, C; // input
	private GeoLine bisector; // output

	// temp
	private GeoLine g, h;
	private GeoVector wv; // direction of line bisector

	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;

	/**
	 * Creates new AlgoLineBisector
	 * 
	 * @param cons
	 * @param label
	 * @param A
	 * @param B
	 * @param C
	 */
	public AlgoAngularBisectorPoints(Construction cons, String label,
			GeoPoint A, GeoPoint B, GeoPoint C) {
		super(cons);
		this.A = A;
		this.B = B;
		this.C = C;
		bisector = new GeoLine(cons);
		bisector.setStartPoint(B);
		setInputOutput(); // for AlgoElement

		g = new GeoLine(cons);
		h = new GeoLine(cons);
		wv = new GeoVector(cons);
		wv.setCoords(0, 0, 0);

		// compute bisector of angle(A, B, C)
		compute();
		bisector.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.AngularBisector;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ANGULAR_BISECTOR;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = A;
		input[1] = B;
		input[2] = C;

		setOutputLength(1);
		setOutput(0, bisector);
		setDependencies(); // done by AlgoElement
	}

	public GeoLine getLine() {
		return bisector;
	}

	// Made public for LocusEqu
	public GeoPoint getA() {
		return A;
	}

	// Made public for LocusEqu
	public GeoPoint getB() {
		return B;
	}

	// Made public for LocusEqu
	public GeoPoint getC() {
		return C;
	}

	@Override
	public final void compute() {
		boolean infiniteB = B.isInfinite();

		// compute lines g = B v A, h = B v C
		GeoVec3D.cross(B, A, g);
		GeoVec3D.cross(B, C, h);

		// (gx, gy) is direction of g = B v A
		double gx = g.y;
		double gy = -g.x;
		double lenG = MyMath.length(gx, gy);
		gx /= lenG;
		gy /= lenG;

		// (hx, hy) is direction of h = B v C
		double hx = h.y;
		double hy = -h.x;
		double lenH = MyMath.length(hx, hy);
		hx /= lenH;
		hy /= lenH;

		// set direction vector of bisector: (wx, wy)
		double wx, wy;
		if (infiniteB) {
			// if B is at infinity then g and h are parallel
			// and the bisector line has same direction as g (and h)
			wx = gx;
			wy = gy;

			// calc z value of line in the middle of g, h
			bisector.z = (g.z / lenG + h.z / lenH) / 2.0;

			// CONTINUITY handling
			if (kernel.isContinuous()) {
				// init old direction vector
				if (bisector.isDefined()) {
					wv.x = bisector.y;
					wv.y = -bisector.x;
				}

				// check orientation: take smallest change!!!
				if (wv.x * wx + wv.y * wy >= 0) {
					wv.x = wx;
					wv.y = wy;
				} else { // angle > 180degrees, change orientation
					wv.x = -wx;
					wv.y = -wy;
					bisector.z = -bisector.z;
				}
			} else {
				wv.x = wx;
				wv.y = wy;
			}

			// set direction vector
			bisector.x = -wv.y;
			bisector.y = wv.x;
		}
		// standard case: B is not at infinity
		else {
			// calc direction vector (wx, wy) of angular bisector
			// check if angle between vectors is > 90 degrees
			double ip = gx * hx + gy * hy;
			if (ip >= 0.0) { // angle < 90 degrees
				// standard case
				wx = gx + hx;
				wy = gy + hy;
			} else { // ip <= 0.0, angle > 90 degrees
						// BC - BA is a normalvector of the bisector
				wx = hy - gy;
				wy = gx - hx;

				// if angle > 180 degree change orientation of direction
				// det(g,h) < 0
				if (gx * hy < gy * hx) {
					wx = -wx;
					wy = -wy;
				}
			}

			// make (wx, wy) a unit vector
			double length = MyMath.length(wx, wy);
			wx /= length;
			wy /= length;

			// CONTINUITY handling
			if (kernel.isContinuous()) {
				// init old direction vector
				if (bisector.isDefined()) {
					wv.x = bisector.y;
					wv.y = -bisector.x;
				}

				// check orientation: take smallest change compared to old
				// direction vector wv !!!
				if (wv.x * wx + wv.y * wy >= 0) {
					wv.x = wx;
					wv.y = wy;
				} else { // angle > 180 degrees, change orientation
					wv.x = -wx;
					wv.y = -wy;
				}
			} else {
				wv.x = wx;
				wv.y = wy;
			}

			// set bisector
			bisector.x = -wv.y;
			bisector.y = wv.x;
			bisector.z = -(B.inhomX * bisector.x + B.inhomY * bisector.y);
		}
		// Application.debug("bisector = (" + bisector.x + ", " + bisector.y +
		// ", " + bisector.z + ")\n");
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("AngleBisectorOfABC", A.getLabel(tpl),
				B.getLabel(tpl), C.getLabel(tpl));

	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnAngularBisectorPoints(geo, this, scope);
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (A != null && B != null && C != null) {
			Variable[] vA = A.getBotanaVars(A);
			Variable[] vB = C.getBotanaVars(C);
			Variable[] vC = B.getBotanaVars(B);

			if (botanaVars == null) {
				botanaVars = new Variable[4];
				// M
				botanaVars[0] = new Variable();
				botanaVars[1] = new Variable();
				// A
				botanaVars[2] = vC[0];
				botanaVars[3] = vC[1];
			}

			botanaPolynomials = new Polynomial[1];

			Polynomial a1 = new Polynomial(vA[0]);
			Polynomial a2 = new Polynomial(vA[1]);
			Polynomial b1 = new Polynomial(vB[0]);
			Polynomial b2 = new Polynomial(vB[1]);
			Polynomial c1 = new Polynomial(vC[0]);
			Polynomial c2 = new Polynomial(vC[1]);
			Polynomial m1 = new Polynomial(botanaVars[0]);
			Polynomial m2 = new Polynomial(botanaVars[1]);
			
			// ((yM - yC)(xA - xC) - (yA - yC)(xM - xC))
			Polynomial nominator1 = (m2.subtract(c2)).multiply(a1.subtract(c1)).subtract((a2.subtract(c2)).multiply(m1.subtract(c1)));
			
			// ((xA - xC)(xM - xC) + (yA - yC)(yM - yC))
			Polynomial denominator1 = (a1.subtract(c1)).multiply(m1.subtract(c1)).add((a2.subtract(c2)).multiply(m2.subtract(c2)));
			
			// ((yB - yM)(xC - xM) - (yC - yM)(xB - xM))
			Polynomial nominator2 = (b2.subtract(m2)).multiply(c1.subtract(m1)).subtract((c2.subtract(m2)).multiply(b1.subtract(m1)));
						
			// ((xC - xM)(xB - xM) + (yC - yM)(yB - yM))
			Polynomial denominator2 = (c1.subtract(m1)).multiply(b1.subtract(m1)).add((c2.subtract(m2)).multiply(b2.subtract(m2)));

			// nominator1 * denominator2 = denominator1 * nominator2
			botanaPolynomials[0] = nominator1.multiply(denominator2).subtract(denominator1.multiply(nominator2));
			
			// A,M,B collinear
		//	botanaPolynomials[0] = Polynomial.collinear(vA[0], vA[1], vB[0],
		//			vB[1], botanaVars[0], botanaVars[1]);

			// (b1-c1)*(c1-d1)
		//	Polynomial p1 = (b1.subtract(c1)).multiply(c1.subtract(m1));
			// (b2-c2)*(c2-d2)
		//	Polynomial p2 = (b2.subtract(c2)).multiply(c2.subtract(m2));
			// (a1-c1)^2+(a2-c2)^2
		//	Polynomial p3 = ((a1.subtract(c1)).multiply(a1.subtract(c1)))
			// .add((a2.subtract(c2)).multiply(a2.subtract(c2)));
			// (a1-c1)*(c1-d1)
		//	Polynomial p4 = (a1.subtract(c1)).multiply(c1.subtract(m1));
			// (a2-c2)*(c2-d2)
		//	Polynomial p5 = (a2.subtract(c2)).multiply(c2.subtract(m2));
			// (b1-c1)^2+(b2-c2)^2
		//	Polynomial p6 = ((b1.subtract(c1)).multiply(b1.subtract(c1)))
		//			.add((b2.subtract(c2)).multiply(b2.subtract(c2)));
			// ((b1-c1)*(c1-d1)+(b2-c2)*(c2-d2))^2*((a1-c1)^2+(a2-c2)^2)
			// -((a1-c1)*(c1-d1)+(a2-c2)*(c2-d2))^2*((b1-c1)^2+(b2-c2)^2)
		//	botanaPolynomials[1] = (p1.add(p2)).multiply(p1.add(p2))
		//			.multiply(p3).subtract((p4.add(p5)).multiply(p4.add(p5)))
			//		.multiply(p6);

			// Polynomial m_1 = new Polynomial(botanaVars[2]);
			// Polynomial m_2 = new Polynomial(botanaVars[3]);

			// Polynomial a_1 = b2.subtract(a2);
			// Polynomial b_1 = a1.subtract(b1);
			// Polynomial c_1 = (a1.multiply(b2)).subtract(b1.multiply(a2));
			// Polynomial a_2 = c2.subtract(a2);
			// Polynomial b_2 = a1.subtract(c1);
			// Polynomial c_2 = (a1.multiply(c2)).subtract(c1.multiply(a2));

			// botanaPolynomials[0] = a_1.add(a_2).add(a2).subtract(m2);
			// botanaPolynomials[1] = b_1.add(b_2).add(m1).subtract(a1);
			// botanaPolynomials[2] = c_1.add(c_2).add(m1.multiply(a2))
			// .subtract(a1.multiply(m2));
			// botanaPolynomials[3] = a1.add(b_1).add(b_2).subtract(m_1);
			// botanaPolynomials[4] =
			// a2.subtract(a_1).subtract(a_2).subtract(m_2);

			return botanaPolynomials;

		}
		throw new NoSymbolicParametersException();


	}
}
