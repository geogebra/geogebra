/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCircleThreePoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.adapters.BotanaCircleThreePoints;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

//import geogebra.kernel.kernelND.GeoConicND;

/**
 * 
 * @author Markus
 */
public class AlgoCircleThreePoints extends AlgoElement
		implements SymbolicParametersBotanaAlgo {

	private GeoPointND A; // input
	private GeoPointND B; // input
	private GeoPointND C; // input
	// protected GeoConicND circle; // output
	protected GeoConicND circle; // output

	// line bisectors
	private GeoLine s0;
	private GeoLine s1;
	private GeoPoint center;
	private double[] det = new double[3];
	private double ax;
	private double ay;
	private double bx;
	private double by;
	private double cx;
	private double cy;
	private double ABx;
	private double ABy;
	private double ACx;
	private double ACy;
	private double BCx;
	private double BCy;
	private double maxDet;
	private int casenr;
	private BotanaCircleThreePoints botanaParams;

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param C
	 *            third point
	 */
	public AlgoCircleThreePoints(Construction cons, GeoPointND A, GeoPointND B,
			GeoPointND C) {

		super(cons);

		setPoints(A, B, C);

		createCircle();
		circle.addPointOnConic(getA()); // move into setIncidence();
		circle.addPointOnConic(getB());
		circle.addPointOnConic(getC());

		// temp: line bisectors
		s0 = new GeoLine(cons);
		s1 = new GeoLine(cons);

		center = new GeoPoint(cons);

		setInputOutput(); // for AlgoElement

		compute();
		setIncidence();

		circle.setToSpecific();
	}

	private void setIncidence() {
		if (A instanceof GeoPoint) {
			((GeoPoint) A).addIncidence(circle, false);
		}
		if (B instanceof GeoPoint) {
			((GeoPoint) B).addIncidence(circle, false);
		}
		if (C instanceof GeoPoint) {
			((GeoPoint) C).addIncidence(circle, false);
		}

	}

	/**
	 * set the three points of the circle to A, B, C
	 * 
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param C
	 *            third point
	 */
	protected void setPoints(GeoPointND A, GeoPointND B, GeoPointND C) {

		this.A = A;
		this.B = B;
		this.C = C;
	}

	/**
	 * create the object circle
	 */
	protected void createCircle() {
		circle = new GeoConic(cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.Circle;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_CIRCLE_THREE_POINTS;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		setInput();
		setOutput();
		setDependencies(); // done by AlgoElement
	}

	protected void setInput() {
		input = new GeoElement[3];
		input[0] = A.toGeoElement();
		input[1] = B.toGeoElement();
		input[2] = C.toGeoElement();
	}

	protected void setOutput() {
		setOnlyOutput(circle);
	}

	// public GeoConicND getCircle() {
	public GeoConicND getCircle() {
		return circle;
	}

	public GeoPoint getA() {
		return (GeoPoint) A;
	}

	public GeoPoint getB() {
		return (GeoPoint) B;
	}

	public GeoPoint getC() {
		return (GeoPoint) C;
	}

	// compute circle through A, B, C
	@Override
	public void compute() {
		// A, B or C undefined
		if (!getA().isFinite() || !getB().isFinite() || !getC().isFinite()) {
			circle.setUndefined();
			return;
		}

		// get inhomogeneous coords of points
		ax = getA().inhomX;
		ay = getA().inhomY;
		bx = getB().inhomX;
		by = getB().inhomY;
		cx = getC().inhomX;
		cy = getC().inhomY;

		// Log.debug("\n"+ax+","+ay+"\n"+bx+","+by+"\n"+cx+","+cy);

		// same points
		if (DoubleUtil.isEqual(ax, bx) && DoubleUtil.isEqual(ay, by)) { // A = B
			if (DoubleUtil.isEqual(ax, cx) && DoubleUtil.isEqual(ay, cy)) { // A = B = C
				circle.setCircle(getA(), 0.0); // single point
				return;
			} // else{ // A = B <> C
			ACx = cx - ax;
			ACy = cy - ay;
			center.setCoords(-ACy, ACx, 0.0d);
			circle.setCircle(center, getA());
			return;
			// }
		} else if (DoubleUtil.isEqual(ax, cx) && DoubleUtil.isEqual(ay, cy)) { // A = C
																		// <> B
			ABx = bx - ax;
			ABy = by - ay;
			center.setCoords(-ABy, ABx, 0.0d);
			circle.setCircle(center, getA());
			return;
		} else if (DoubleUtil.isEqual(bx, cx) && DoubleUtil.isEqual(by, cy)) { // B = C
																		// <> A
			ACx = cx - ax;
			ACy = cy - ay;
			center.setCoords(-ACy, ACx, 0.0d);
			circle.setCircle(center, getA());
			return;
		}

		// calc vectors AB, AC, BC
		ABx = bx - ax;
		ABy = by - ay;
		ACx = cx - ax;
		ACy = cy - ay;
		BCx = cx - bx;
		BCy = cy - by;

		double lengthAB = MyMath.length(ABx, ABy);
		double lengthAC = MyMath.length(ACx, ACy);
		double lengthBC = MyMath.length(BCx, BCy);

		// find the two bisectors with max intersection angle
		// i.e. maximum abs of determinant of directions
		// max( abs(det(AB, AC)), abs(det(AC, BC)), abs(det(AB, BC)) )
		det[0] = Math.abs(ABx * ACy - ABy * ACx) / (lengthAB * lengthAC);
		// AB, AC
		det[1] = Math.abs(ACx * BCy - ACy * BCx) / (lengthAC * lengthBC);
		// AC, BC
		det[2] = Math.abs(ABx * BCy - ABy * BCx) / (lengthAB * lengthBC);
		// AB, BC

		// take ip[0] as init minimum and find minimum case
		maxDet = det[0];
		casenr = 0;
		if (det[1] > maxDet) {
			casenr = 1;
			maxDet = det[1];
		}
		if (det[2] > maxDet) {
			casenr = 2;
			maxDet = det[2];
		}

		// A, B, C are collinear: set M to infinite point
		// in perpendicular direction of AB
		if (DoubleUtil.isZero(maxDet)) {
			center.setCoords(-ABy, ABx, 0.0d);
			circle.setCircle(center, getA());
		}
		// standard case
		else {
			// intersect two line bisectors according to casenr
			switch (casenr) {
			default:
			case 0: // bisectors of AB, AC
				s0.x = ABx;
				s0.y = ABy;
				s0.z = -((ax + bx) * s0.x + (ay + by) * s0.y) / 2.0;

				s1.x = ACx;
				s1.y = ACy;
				s1.z = -((ax + cx) * s1.x + (ay + cy) * s1.y) / 2.0;
				break;

			case 1: // bisectors of AC, BC
				s1.x = ACx;
				s1.y = ACy;
				s1.z = -((ax + cx) * s1.x + (ay + cy) * s1.y) / 2.0;

				s0.x = BCx;
				s0.y = BCy;
				s0.z = -((bx + cx) * s0.x + (by + cy) * s0.y) / 2.0;
				break;

			case 2: // bisectors of AB, BC
				s0.x = ABx;
				s0.y = ABy;
				s0.z = -((ax + bx) * s0.x + (ay + by) * s0.y) / 2.0;

				s1.x = BCx;
				s1.y = BCy;
				s1.z = -((bx + cx) * s1.x + (by + cy) * s1.y) / 2.0;
				break;
			}

			// intersect line bisectors to get midpoint
			GeoVec3D.cross(s0, s1, center);
			circle.setCircle(center, center.distance(getA()));
		}
	}

	@Override
	public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("CircleThroughABC",
				"Circle through %0, %1, %2", A.getLabel(tpl),
				B.getLabel(tpl), C.getLabel(tpl));
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		if (botanaParams == null) {
			botanaParams = new BotanaCircleThreePoints();
		}
		return botanaParams.getBotanaVars();
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {

		if (botanaParams == null) {
			botanaParams = new BotanaCircleThreePoints();
		}
		return botanaParams.getPolynomials(getInput());
	}

}
