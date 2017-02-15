/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAngularBisectorLines.java
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
 * Angle bisectors between two lines
 * 
 * @author Markus
 */
public class AlgoAngularBisectorLines extends AlgoElement
		implements SymbolicParametersBotanaAlgo {

	private GeoLine g, h; // input
	private GeoLine[] bisector; // output

	// temp
	private double gx, gy, hx, hy, wx, wy, bx, by, lenH, lenG, length, ip;
	private GeoVector[] wv; // direction of bisector line bisector
	private GeoPoint B; // intersection point of g, h
	private boolean infiniteB;
	private int index;

	private PPolynomial[] botanaPolynomials;
	private PVariable[] botanaVars;

	/**
	 * Creates new AlgoAngularBisectorLines
	 * 
	 * @param cons
	 * @param label
	 * @param g
	 * @param h
	 */
	AlgoAngularBisectorLines(Construction cons, String label, GeoLine g,
			GeoLine h) {
		this(cons, g, h);
		GeoElement.setLabels(label, bisector);
	}

	public AlgoAngularBisectorLines(Construction cons, String[] labels,
			GeoLine g, GeoLine h) {
		this(cons, g, h);
		GeoElement.setLabels(labels, bisector);
	}

	@Override
	public Commands getClassName() {
		return Commands.AngularBisector;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ANGULAR_BISECTOR;
	}

	AlgoAngularBisectorLines(Construction cons, GeoLine g, GeoLine h) {
		super(cons);
		this.g = g;
		this.h = h;
		bisector = new GeoLine[2];
		bisector[0] = new GeoLine(cons);
		bisector[1] = new GeoLine(cons);
		setInputOutput(); // for AlgoElement

		wv = new GeoVector[2];
		wv[0] = new GeoVector(cons);
		wv[0].setCoords(0, 0, 0);
		wv[1] = new GeoVector(cons);
		wv[1].setCoords(0, 0, 0);
		B = new GeoPoint(cons);

		bisector[0].setStartPoint(B);
		bisector[1].setStartPoint(B);

		// compute bisectors of lines g, h
		compute();
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {
		input = new GeoElement[2];
		input[0] = g;
		input[1] = h;

		super.setOutput(bisector);
		setDependencies(); // done by AlgoElement
	}

	public GeoLine[] getLines() {
		return bisector;
	}

	// Made public for LocusEqu
	public GeoLine getg() {
		return g;
	}

	// Made public for LocusEqu
	public GeoLine geth() {
		return h;
	}

	// Made public for LocusEqu
	public GeoPoint getB() {
		return B;
	}

	@Override
	public boolean isNearToAlgorithm() {
		return true;
	}

	@Override
	public final void compute() {
		// calc intersection B of g and h
		GeoVec3D.cross(g, h, B);
		infiniteB = B.isInfinite();

		// (gx, gy) is direction of g = B v A
		gx = g.y;
		gy = -g.x;
		lenG = MyMath.length(gx, gy);
		gx /= lenG;
		gy /= lenG;

		// (hx, hy) is direction of h = B v C
		hx = h.y;
		hy = -h.x;
		lenH = MyMath.length(hx, hy);
		hx /= lenH;
		hy /= lenH;

		// set direction vector of bisector: (wx, wy)
		if (infiniteB) {
			// if B is at infinity then g and h are parallel
			// and the bisector line has same direction as g (or h)

			// calc z value of line in the middle of g, h
			// orientation of g, h may differ: 2 cases
			if (gx * hx + gy * hy > 0) { // same orientation
				index = 0; // set first bisector
				bisector[index].z = (g.z / lenG + h.z / lenH) / 2.0;
			} else { // different orientation
				index = 1; // set second bisector
				bisector[index].z = (g.z / lenG - h.z / lenH) / 2.0;
			}

			// take direction of g as proposed direction for bisector
			wx = gx;
			wy = gy;

			if (kernel.isContinuous()) {
				// init old direction of bisectors
				if (bisector[0].isDefined()) {
					wv[0].x = bisector[0].y;
					wv[0].y = -bisector[0].x;
				}
				if (bisector[1].isDefined()) {
					wv[1].x = bisector[1].y;
					wv[1].y = -bisector[1].x;
				}

				// NEAR TO RELATIONSHIP
				// check orientation: take smallest change!!!
				if (wv[index].x * wx + wv[index].y * wy >= 0) {
					wv[index].x = wx;
					wv[index].y = wy;
				} else { // angle > 180degrees, change orientation
					wv[index].x = -wx;
					wv[index].y = -wy;
					bisector[index].z = -bisector[index].z;
				}
			} else {
				// non continuous
				wv[index].x = wx;
				wv[index].y = wy;
			}

			// set direction vector of bisector
			bisector[index].x = -wv[index].y;
			bisector[index].y = wv[index].x;
			// ohter bisector is undefined
			bisector[1 - index].setUndefined();
		}
		// standard case: B is not at infinity
		else {
			// calc direction vector (wx, wy) of angular bisector
			// check if angle between vectors is > 90degrees
			ip = gx * hx + gy * hy;
			if (ip >= 0.0) { // angle < 90degrees
				// standard case
				wx = gx + hx;
				wy = gy + hy;
			} else { // ip <= 0.0, angle > 90degrees
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
			length = MyMath.length(wx, wy);
			wx /= length;
			wy /= length;

			if (kernel.isContinuous()) {
				// init old direction of bisectors
				if (bisector[0].isDefined()) {
					wv[0].x = bisector[0].y;
					wv[0].y = -bisector[0].x;
				}
				if (bisector[1].isDefined()) {
					wv[1].x = bisector[1].y;
					wv[1].y = -bisector[1].x;
				}

				// check orientations: take smallest change!!!
				// first bisector: relativ to (wx, wy)
				if (wv[0].x * wx + wv[0].y * wy >= 0) {
					wv[0].x = wx;
					wv[0].y = wy;
				} else { // angle > 180 degree change orientation
					wv[0].x = -wx;
					wv[0].y = -wy;
				}
				// second bisector: relativ to (-wy, wx)
				if (wv[1].y * wx - wv[1].x * wy >= 0) {
					wv[1].x = -wy;
					wv[1].y = wx;
				} else { // angle > 180 degree change orientation
					wv[1].x = wy;
					wv[1].y = -wx;
				}
			} else {
				// non continuous
				wv[0].x = wx;
				wv[0].y = wy;
				wv[1].x = -wy;
				wv[1].y = wx;
			}

			// calc B's coords
			bx = B.inhomX;
			by = B.inhomY;

			// set first bisector through B
			bisector[0].x = -wv[0].y;
			bisector[0].y = wv[0].x;
			bisector[0].z = -(bx * bisector[0].x + by * bisector[0].y);

			// set second bisector perpendicular to first through B
			bisector[1].x = -wv[1].y;
			bisector[1].y = wv[1].x;
			bisector[1].z = -(bx * bisector[1].x + by * bisector[1].y);
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("AngleBisectorOfAB", g.getLabel(tpl),
				h.getLabel(tpl));
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

		GeoLine lg = getg();
		GeoLine lh = geth();

		if (lg != null && lh != null) {
			/*
			 * We need to compute this.B symbolically since it is not computed
			 * automatically in this class.
			 */
			PVariable[] varsB, varsLg, varsLh;
			varsB = (this.B).getBotanaVars(this.B);
			varsLg = lg.getBotanaVars(lg);
			varsLh = lh.getBotanaVars(lh);
			PPolynomial[] polysB = (this.B).getBotanaPolynomials(this.B);

			if (polysB == null) {
				// if already exists, let's use it, if not, create a new one
				polysB = new PPolynomial[2];
				polysB[0] = PPolynomial.collinear(varsB[0], varsB[1], varsLg[0],
						varsLg[1], varsLg[2], varsLg[3]);
				polysB[1] = PPolynomial.collinear(varsB[0], varsB[1], varsLh[0],
						varsLh[1], varsLh[2], varsLh[3]);
			}

			PVariable[] vA = new PVariable[2];
			vA[0] = varsLg[0];
			vA[1] = varsLg[1];
			PVariable[] vB = new PVariable[2];
			vB[0] = varsLh[0];
			vB[1] = varsLh[1];
			PVariable[] vC = varsB;

			botanaPolynomials = new PPolynomial[4];
			botanaPolynomials[2] = polysB[0];
			botanaPolynomials[3] = polysB[1];

			// from now on we use the equations from
			// AlgoAngularBisectorPoints

			if (botanaVars == null) {
				botanaVars = new PVariable[4];
				// M
				botanaVars[0] = new PVariable(kernel);
				botanaVars[1] = new PVariable(kernel);
				// A
				botanaVars[2] = vC[0];
				botanaVars[3] = vC[1];
			}

			PPolynomial a1 = new PPolynomial(vA[0]);
			PPolynomial a2 = new PPolynomial(vA[1]);
			PPolynomial b1 = new PPolynomial(vB[0]);
			PPolynomial b2 = new PPolynomial(vB[1]);
			PPolynomial c1 = new PPolynomial(vC[0]);
			PPolynomial c2 = new PPolynomial(vC[1]);
			PPolynomial m1 = new PPolynomial(botanaVars[0]); // d1
			PPolynomial m2 = new PPolynomial(botanaVars[1]); // d2

			// A,M,B collinear (needed for easing computations)
			botanaPolynomials[0] = PPolynomial.collinear(vA[0], vA[1], vB[0],
					vB[1], botanaVars[0], botanaVars[1]);

			// (b1-c1)*(c1-d1)
			PPolynomial p1 = b1.subtract(c1).multiply(c1.subtract(m1));
			// (b2-c2)*(c2-d2)
			PPolynomial p2 = b2.subtract(c2).multiply(c2.subtract(m2));
			// (a1-c1)^2+(a2-c2)^2
			PPolynomial p3 = (PPolynomial.sqr(a1.subtract(c1)))
					.add(PPolynomial.sqr(a2.subtract(c2)));
			// (a1-c1)*(c1-d1)
			PPolynomial p4 = a1.subtract(c1).multiply(c1.subtract(m1));
			// (a2-c2)*(c2-d2)
			PPolynomial p5 = a2.subtract(c2).multiply(c2.subtract(m2));
			// (b1-c1)^2+(b2-c2)^2
			PPolynomial p6 = PPolynomial.sqr(b1.subtract(c1))
					.add(PPolynomial.sqr(b2.subtract(c2)));
			// ((b1-c1)*(c1-d1)+(b2-c2)*(c2-d2))^2*((a1-c1)^2+(a2-c2)^2)
			// -((a1-c1)*(c1-d1)+(a2-c2)*(c2-d2))^2*((b1-c1)^2+(b2-c2)^2)
			botanaPolynomials[1] = PPolynomial.sqr((p1.add(p2))).multiply(p3)
					.subtract(PPolynomial.sqr(p4.add(p5)).multiply(p6));

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();

	}
}
