/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoConicFivePoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author Markus
 */
public class AlgoConicFivePoints extends AlgoElement
		implements SymbolicParametersBotanaAlgo {

	// #4156 these are rather arbitrary tradeoffs between compatibility and
	// numeric stability
	private static final double MULTIPLIER_MIN = 0.001;
	private static final double MULTIPLIER_MAX = 1000;
	protected GeoPoint[] P; // input five points
	protected GeoConicND conic; // output
	private boolean criticalCase; // true when 5 points is on a parabola

	private double[][] A;
	private double[][] B;
	private double[][] C;
	private double e1;
	private double e2;
	private GeoVec3D[] line;

	private PPolynomial[] botanaPolynomials;
	private PVariable[] botanaVars;

	protected void setInputPoints() {
		input = P;
	}

	protected GeoPoint[] createPoints2D(GeoPointND[] inputP) {
		return (GeoPoint[]) inputP;
	}

	/**
	 * @param cons
	 *            construction
	 * @param inputP
	 *            five input points
	 */
	public AlgoConicFivePoints(Construction cons, GeoPointND[] inputP) {
		super(cons);
		this.P = createPoints2D(inputP);
		conic = newGeoConic(cons);
		setInputOutput(); // for AlgoElement

		line = new GeoVec3D[4];
		for (int i = 0; i < 4; i++) {
			line[i] = new GeoLine(cons);
		}
		A = new double[3][3];
		B = new double[3][3];
		C = new double[3][3];
		checkCriticalCase();
		initCoords();
		compute();
		addIncidence();
	}

	/**
	 * init Coords values
	 */
	protected void initCoords() {
		// none here
	}

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @return output conic
	 */
	protected GeoConicND newGeoConic(Construction cons1) {
		return new GeoConic(cons1);
	}

	private void checkCriticalCase() {
		criticalCase = false;

		for (int i = 0; i < P.length; i++) {
			if (P[i].getIncidenceList() == null) {
				return;
			}
		}

		ArrayList<GeoElement> firstList = P[0].getIncidenceList();

		for (int j = 0; j < firstList.size(); j++) {
			if (firstList.get(j).isGeoConic()) {
				GeoConic p = (GeoConic) firstList.get(j);
				if (p.getType() == GeoConicNDConstants.CONIC_PARABOLA) {
					criticalCase = true;
					for (int i = 1; i < 5; i++) {
						if (!P[i].getIncidenceList().contains(p)) {
							criticalCase = false;
							break;
						}
					}
				}
			}

			if (criticalCase) {
				break;
			}
		}

	}

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		for (int i = 0; i < P.length; ++i) {
			P[i].addIncidence(conic, false);
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.Conic;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_CONIC_FIVE_POINTS;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		setInputPoints();

		super.setOutputLength(1);
		super.setOutput(0, conic);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return output conic
	 */
	public GeoConicND getConic() {
		return conic;
	}

	GeoPoint[] getPoints() {
		return P;
	}

	/**
	 * Method created for LocusEqu project.
	 * 
	 * @return a copy of inner array so it cannot be manipulated from outside.
	 */
	public GeoPoint[] getAllPoints() {
		GeoPoint[] copy = new GeoPoint[this.getPoints().length];
		System.arraycopy(this.getPoints(), 0, copy, 0, copy.length);
		return copy;
	}

	// compute conic through five points P[0] ... P[4]
	@Override
	public void compute() {
		// compute lines P0 P1, P2 P3,
		// P0 P2, P1 P3
		GeoVec3D.cross(P[0], P[1], line[0]);
		GeoVec3D.cross(P[2], P[3], line[1]);
		GeoVec3D.cross(P[0], P[2], line[2]);
		GeoVec3D.cross(P[1], P[3], line[3]);
		for (int i = 0; i < 4; i++) {
			normalizeLines(i);
		}
		// compute degenerate conics A = line[0] u line[1],
		// B = line[2] u line[3]
		degCone(line[0], line[1], A);
		degCone(line[2], line[3], B);

		e1 = evalMatrix(B, P[4]);
		e2 = -evalMatrix(A, P[4]);

		// try to avoid tiny/huge value for matrix
		if (shouldInvert(e1) && shouldInvert(e2)) {
			if (hugeForMatrix(e1, A) && hugeForMatrix(e2, A)) {
				e2 = e2 / e1;
				e1 = 1;
			} else {
				double tmp = e1;

				e1 = 1 / e2;
				e2 = 1 / tmp;
			}
		}
		linComb(A, B, e1, e2, C);

		/***
		 * critical case: five points lie on an unstable conic now only for
		 * parabola. Need more tests for: one line; two lines; one point; two
		 * points
		 */

		if (criticalCase) {
			conic.errDetS = Double.POSITIVE_INFINITY;
		} else {
			conic.errDetS = Kernel.MIN_PRECISION;
		}

		conic.setMatrix(C);
		// System.out.println(conic.getTypeString());

	}

	private void normalizeLines(int i) {
		double norm = Math.max(Math.abs(line[i].x),
				Math.max(Math.abs(line[i].y), Math.abs(line[i].z)));
		if (norm < 1) {
			line[i].x = line[i].x / norm;
			line[i].y = line[i].y / norm;
			line[i].z = line[i].z / norm;
		}
	}

	/**
	 * Compares a value with matrix entries TODO the 1E10 constant here is a bit
	 * arbitrary, see #5201
	 * 
	 * @param e12
	 *            eigenvalue
	 * @param M
	 *            matrix
	 * @return whether eigenvalue is too big
	 */
	private static boolean hugeForMatrix(double e12, double[][] M) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				// e12 is much bigger than any matrix entry
				if (!DoubleUtil.isZero(M[i][j], Kernel.MIN_PRECISION)
						&& Math.abs(e12) > 1E10 * M[i][j]) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean shouldInvert(double d) {
		return (!DoubleUtil.isZero(d) && Math.abs(d) < MULTIPLIER_MIN)
				|| Math.abs(d) > MULTIPLIER_MAX;
	}

	// compute degenerate conic from lines a, b
	// the result is written into A as a NON-SYMMETRIC Matrix
	final private static void degCone(GeoVec3D a, GeoVec3D b, double[][] A) {
		// A = a . b^t
		A[0][0] = a.x * b.x;
		A[0][1] = a.x * b.y;
		A[0][2] = a.x * b.z;
		A[1][0] = a.y * b.x;
		A[1][1] = a.y * b.y;
		A[1][2] = a.y * b.z;
		A[2][0] = a.z * b.x;
		A[2][1] = a.z * b.y;
		A[2][2] = a.z * b.z;
	}

	// computes P.A.P, where A is a (possibly not symmetric) 3x3 matrix
	final private static double evalMatrix(double[][] A, GeoPoint P) {
		return A[0][0] * P.x * P.x + A[1][1] * P.y * P.y + A[2][2] * P.z * P.z
				+ (A[0][1] + A[1][0]) * P.x * P.y
				+ (A[0][2] + A[2][0]) * P.x * P.z
				+ (A[1][2] + A[2][1]) * P.y * P.z;
	}

	// computes the linear combination C = l * A + m * B
	final private static void linComb(double[][] A, double[][] B, double l,
			double m, double[][] C) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				C[i][j] = l * A[i][j] + m * B[i][j];
			}
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("ConicThroughABCDE",
				"Conic through %0, %1, %2, %3, %4", P[0].getLabel(tpl),
				P[1].getLabel(tpl), P[2].getLabel(tpl), P[3].getLabel(tpl),
				P[4].getLabel(tpl));
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
		/*
		 * The poly will be in form a*x^2+b*y^2+c*x*y+d*x+e*y+f=0 where x and y
		 * are the Botana variables, and the other vars are obtained from the
		 * coordinates of the input points.
		 */
		if (botanaVars == null) {
			botanaVars = new PVariable[8];
			/* x,y,a,b,c,d,e,f */
			for (int i = 0; i < 8; i++) {
				botanaVars[i] = new PVariable(kernel);
			}
		}
		PVariable x = botanaVars[0];
		PVariable y = botanaVars[1];
		PVariable a = botanaVars[2];
		PVariable b = botanaVars[3];
		PVariable c = botanaVars[4];
		PVariable d = botanaVars[5];
		PVariable e = botanaVars[6];
		PVariable f = botanaVars[7];
		botanaPolynomials = new PPolynomial[6];
		/* one for the curve and 5 for the constraints */
		PPolynomial xp = new PPolynomial(x);
		PPolynomial yp = new PPolynomial(y);
		PPolynomial xx = PPolynomial.sqr(xp);
		PPolynomial yy = PPolynomial.sqr(yp);
		PPolynomial xy = xp.multiply(yp);
		PPolynomial ap = new PPolynomial(a);
		PPolynomial bp = new PPolynomial(b);
		PPolynomial cp = new PPolynomial(c);
		PPolynomial dp = new PPolynomial(d);
		PPolynomial ep = new PPolynomial(e);
		PPolynomial fp = new PPolynomial(f);
		botanaPolynomials[0] = ap.multiply(xx).add(bp.multiply(yy))
				.add(cp.multiply(xy)).add(dp.multiply(xp)).add(ep.multiply(yp))
				.add(fp);
		AlgoElement ae = geo.getParentAlgorithm();

		GeoPoint PA = (GeoPoint) ae.input[0];
		PPolynomial Ax = new PPolynomial(PA.getBotanaVars(PA)[0]);
		PPolynomial Ay = new PPolynomial(PA.getBotanaVars(PA)[1]);
		botanaPolynomials[1] = ap.multiply(PPolynomial.sqr(Ax))
				.add(bp.multiply(PPolynomial.sqr(Ay)))
				.add(cp.multiply(Ax).multiply(Ay)).add(dp.multiply(Ax))
				.add(ep.multiply(Ay)).add(fp);

		GeoPoint PB = (GeoPoint) ae.input[1];
		PPolynomial Bx = new PPolynomial(PB.getBotanaVars(PB)[0]);
		PPolynomial By = new PPolynomial(PB.getBotanaVars(PB)[1]);
		botanaPolynomials[2] = ap.multiply(PPolynomial.sqr(Bx))
				.add(bp.multiply(PPolynomial.sqr(By)))
				.add(cp.multiply(Bx).multiply(By)).add(dp.multiply(Bx))
				.add(ep.multiply(By)).add(fp);

		GeoPoint PC = (GeoPoint) ae.input[2];
		PPolynomial Cx = new PPolynomial(PC.getBotanaVars(PC)[0]);
		PPolynomial Cy = new PPolynomial(PC.getBotanaVars(PC)[1]);
		botanaPolynomials[3] = ap.multiply(PPolynomial.sqr(Cx))
				.add(bp.multiply(PPolynomial.sqr(Cy)))
				.add(cp.multiply(Cx).multiply(Cy)).add(dp.multiply(Cx))
				.add(ep.multiply(Cy)).add(fp);

		GeoPoint PD = (GeoPoint) ae.input[3];
		PPolynomial Dx = new PPolynomial(PD.getBotanaVars(PD)[0]);
		PPolynomial Dy = new PPolynomial(PD.getBotanaVars(PD)[1]);
		botanaPolynomials[4] = ap.multiply(PPolynomial.sqr(Dx))
				.add(bp.multiply(PPolynomial.sqr(Dy)))
				.add(cp.multiply(Dx).multiply(Dy)).add(dp.multiply(Dx))
				.add(ep.multiply(Dy)).add(fp);

		GeoPoint PE = (GeoPoint) ae.input[4];
		PPolynomial Ex = new PPolynomial(PE.getBotanaVars(PE)[0]);
		PPolynomial Ey = new PPolynomial(PE.getBotanaVars(PE)[1]);
		botanaPolynomials[5] = ap.multiply(PPolynomial.sqr(Ex))
				.add(bp.multiply(PPolynomial.sqr(Ey)))
				.add(cp.multiply(Ex).multiply(Ey)).add(dp.multiply(Ex))
				.add(ep.multiply(Ey)).add(fp);

		return botanaPolynomials;
	}
}
