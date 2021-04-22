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
import org.geogebra.common.kernel.geos.LabelManager;
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

	private GeoLine g; // input
	private GeoLine h; // input
	private GeoLine[] bisector; // output

	// temp
	private double gx;
	private double gy;
	private double hx;
	private double hy;
	private double wx;
	private double wy;
	private double bx;
	private double by;
	private double lenH;
	private double lenG;
	private double length;
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
	 *            construction
	 * @param label
	 *            output label
	 * @param g
	 *            first line
	 * @param h
	 *            second line
	 */
	AlgoAngularBisectorLines(Construction cons, String label, GeoLine g,
			GeoLine h) {
		this(cons, g, h);
		LabelManager.setLabels(label, bisector);
	}

	/**
	 * Creates new AlgoAngularBisectorLines
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param g
	 *            first line
	 * @param h
	 *            second line
	 */
	public AlgoAngularBisectorLines(Construction cons, String[] labels,
			GeoLine g, GeoLine h) {
		this(cons, g, h);
		LabelManager.setLabels(labels, bisector);
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

	/**
	 * @return output lines
	 */
	public GeoLine[] getLines() {
		return bisector;
	}

	/** @return first line */
	public GeoLine getg() {
		return g;
	}

	/** @return second line */
	public GeoLine geth() {
		return h;
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
			double ip = gx * hx + gy * hy;
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
		return getLoc().getPlainDefault("AngleBisectorOfAB",
				"Angle bisector of %0, %1", g.getLabel(tpl),
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
			PVariable[] vC, varsLg, varsLh;
			vC = (this.B).getBotanaVars(this.B);
			varsLg = lg.getBotanaVars(lg);
			varsLh = lh.getBotanaVars(lh);
			PPolynomial[] polysC = (this.B).getBotanaPolynomials(this.B);
			PVariable[] vA = new PVariable[2];
			PVariable[] vB = new PVariable[2];
			int polysNeeded = 6;

			// Special cases: when the 4 points cover only 3 points.
			if (lg.startPoint.equals(lh.startPoint)) {
				vC[0] = varsLg[0];
				vC[1] = varsLg[1];
				vA[0] = varsLg[2];
				vA[1] = varsLg[3];
				vB[0] = varsLh[2];
				vB[1] = varsLh[3];
				polysNeeded = 4;
			}
			if (lg.startPoint.equals(lh.endPoint)) {
				vC[0] = varsLg[0];
				vC[1] = varsLg[1];
				vA[0] = varsLg[2];
				vA[1] = varsLg[3];
				vB[0] = varsLh[0];
				vB[1] = varsLh[1];
				polysNeeded = 4;
			}
			if (lg.endPoint.equals(lh.endPoint)) {
				vC[0] = varsLg[2];
				vC[1] = varsLg[3];
				vA[0] = varsLg[0];
				vA[1] = varsLg[1];
				vB[0] = varsLh[0];
				vB[1] = varsLh[1];
				polysNeeded = 4;
			}
			if (lg.endPoint.equals(lh.startPoint)) {
				vC[0] = varsLg[2];
				vC[1] = varsLg[3];
				vA[0] = varsLg[0];
				vA[1] = varsLg[1];
				vB[0] = varsLh[2];
				vB[1] = varsLh[3];
				polysNeeded = 4;
			}

			// Otherwise we need the intersection point of the two lines:
			if (polysNeeded == 6) {
				polysC = new PPolynomial[2];
				polysC[0] = PPolynomial.collinear(vC[0], vC[1], varsLg[0],
						varsLg[1], varsLg[2], varsLg[3]);
				polysC[1] = PPolynomial.collinear(vC[0], vC[1], varsLh[0],
						varsLh[1], varsLh[2], varsLh[3]);
				// Any of the start/endpoint of the lines will be okay to use:
				vA[0] = varsLg[0];
				vA[1] = varsLg[1];
				vB[0] = varsLh[0];
				vB[1] = varsLh[1];
				// In some exotic cases the above may not work.
				// If g=AB, h=CD, and C is lying on AB, then selecting C for the
				// representative of h is the wrong choice. To avoid this we
				// should check if there is numerical collinearity between ABC:
				// (A former workaround is to change the role of D and C manually
				// in the construction.)
				if (GeoPoint.collinearND(g.startPoint, g.endPoint, h.startPoint)) {
					vB[0] = varsLh[2];
					vB[1] = varsLh[3];
				} else { // and vice versa
					if (GeoPoint.collinearND(h.startPoint, h.endPoint, g.startPoint)) {
						vA[0] = varsLg[2];
						vA[1] = varsLg[3];
					}
				}
			}

			if (varsLg != null && B != null && varsLh != null) {
				// Initial input: B is the vertex of the angle, A and C are the other points.
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

				botanaPolynomials = new PPolynomial[polysNeeded];

				PPolynomial a1 = new PPolynomial(vA[0]);
				PPolynomial a2 = new PPolynomial(vA[1]);
				PPolynomial m1 = new PPolynomial(botanaVars[0]);
				PPolynomial m2 = new PPolynomial(botanaVars[1]);
				PPolynomial s1 = new PPolynomial(botanaVars[4]);
				PPolynomial s2 = new PPolynomial(botanaVars[5]);

				PPolynomial p1 = PPolynomial.sqrDistance(vA[0], vA[1], vC[0], vC[1]);
				PPolynomial p2 = PPolynomial.sqrDistance(botanaVars[4], botanaVars[5], vC[0], vC[1]);
				botanaPolynomials[0] = p1.subtract(p2);
				botanaPolynomials[1] = PPolynomial.collinear(vC[0], vC[1], botanaVars[4], botanaVars[5], vB[0], vB[1]);
				botanaPolynomials[2] = m1.add(m1).subtract(a1).subtract(s1);
				botanaPolynomials[3] = m2.add(m2).subtract(a2).subtract(s2);
				if (polysNeeded == 6) {
					botanaPolynomials[4] = polysC[0];
					botanaPolynomials[5] = polysC[1];
				}

				return botanaPolynomials;
			}

		}
		throw new NoSymbolicParametersException();

	}
}
