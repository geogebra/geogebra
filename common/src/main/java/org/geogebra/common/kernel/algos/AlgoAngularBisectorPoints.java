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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 */
public class AlgoAngularBisectorPoints extends AlgoElement
		implements SymbolicParametersBotanaAlgo {

	private GeoPoint A; // input
	private GeoPoint B; // input
	private GeoPoint C; // input
	private GeoLine bisector; // output

	// temp
	private GeoLine g;
	private GeoLine h;
	private GeoVector wv; // direction of line bisector

	private PPolynomial[] botanaPolynomials;
	private PVariable[] botanaVars;

	/**
	 * Creates new AlgoLineBisector
	 * 
	 * @param cons
	 *            construction
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 */
	public AlgoAngularBisectorPoints(Construction cons,
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

	/**
	 * @return output line
	 */
	public GeoLine getLine() {
		return bisector;
	}

	/** @return input point (leg) */
	public GeoPoint getA() {
		return A;
	}

	/** @return input point (vertex) */
	public GeoPoint getB() {
		return B;
	}

	/** @return input point (leg) */
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
		return getLoc().getPlainDefault("AngleBisectorOfABC",
				"Angle bisector of %0, %1, %2", A.getLabel(tpl),
				B.getLabel(tpl), C.getLabel(tpl));

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

		if (A != null && B != null && C != null) {
			// Initial input: B is the vertex of the angle, A and C are the other points.
			PVariable[] vA = A.getBotanaVars(A);
			PVariable[] vB = C.getBotanaVars(C);
			PVariable[] vC = B.getBotanaVars(B);
			// Now, in our notation: C is the vertex of the angle, A and B are the other points.
			// The output will be: Line(C,M). So we will create a point M.
			// Then we create a rhombus including points C and A, and add the rest of its
			// points are S and S'. S' is not stored, but the midpoint of the rhombus
			// will be denoted by M. Now M=Midpoint(A,S).

			// This idea was taken from Recio-Dalzotto 2009, p. 231,
			// https://www.researchgate.net/publication/226017744_On_Protocols_for_the_Automated_Discovery_of_Theorems_in_Elementary_Geometry,
			// but here we do it more generally.

			if (botanaVars == null) {
				botanaVars = new PVariable[6];
				// M, the midpoint of the rhombus
				botanaVars[0] = new PVariable(kernel);
				botanaVars[1] = new PVariable(kernel);
				// C, that is, the vertex of the angle.
				botanaVars[2] = vC[0];
				botanaVars[3] = vC[1];
				// S, a helper point.
				botanaVars[4] = new PVariable(kernel);
				botanaVars[5] = new PVariable(kernel);
			}

			botanaPolynomials = new PPolynomial[4];

			PPolynomial a1 = new PPolynomial(vA[0]);
			PPolynomial a2 = new PPolynomial(vA[1]);
			PPolynomial m1 = new PPolynomial(botanaVars[0]);
			PPolynomial m2 = new PPolynomial(botanaVars[1]);
			PPolynomial s1 = new PPolynomial(botanaVars[4]);
			PPolynomial s2 = new PPolynomial(botanaVars[5]);

			PPolynomial p1 = PPolynomial.sqrDistance(vA[0], vA[1], vC[0], vC[1]);
			PPolynomial p2 = PPolynomial.sqrDistance(botanaVars[4], botanaVars[5], vC[0], vC[1]);
			botanaPolynomials[0] = p1.subtract(p2);
			botanaPolynomials[1] = PPolynomial.collinear(vC[0], vC[1],
					botanaVars[4], botanaVars[5], vB[0], vB[1]);
			botanaPolynomials[2] = m1.add(m1).subtract(a1).subtract(s1);
			botanaPolynomials[3] = m2.add(m2).subtract(a2).subtract(s2);

			return botanaPolynomials;

		}
		throw new NoSymbolicParametersException();

	}
}
