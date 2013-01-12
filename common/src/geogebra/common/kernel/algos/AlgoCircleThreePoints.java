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

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.util.MyMath;

//import geogebra.kernel.kernelND.GeoConicND;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoCircleThreePoints extends AlgoElement implements SymbolicParametersBotanaAlgo {

	private GeoPointND A, B, C; // input
	// protected GeoConicND circle; // output
	protected GeoConicND circle; // output
	private Variable[] botanaVars;
	private Polynomial[] botanaPolynomials;

	// line bisectors
	private GeoLine s0, s1;
	private GeoPoint center;
	private double[] det = new double[3];
	transient private double ax, ay, bx, by, cx, cy, ABx, ABy, ACx, ACy, BCx,
			BCy, maxDet;
	transient private int casenr;

	public AlgoCircleThreePoints(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		this(cons, A, B, C);
		circle.setLabel(label);
	}

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
	}

	private void setIncidence() {
		if (A instanceof GeoPoint)
			((GeoPoint) A).addIncidence( circle);
		if (B instanceof GeoPoint)
			((GeoPoint) B).addIncidence( circle);
		if (C instanceof GeoPoint)
			((GeoPoint) C).addIncidence( circle);

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
		input[0] =  A.toGeoElement();
		input[1] =  B.toGeoElement();
		input[2] =  C.toGeoElement();
	}

	protected void setOutput() {
		super.setOutputLength(1);
		super.setOutput(0,  circle);
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

		// get inhomogenous coords of points
		ax = getA().inhomX;
		ay = getA().inhomY;
		bx = getB().inhomX;
		by = getB().inhomY;
		cx = getC().inhomX;
		cy = getC().inhomY;

		// A = B = C
		if (Kernel.isEqual(ax, bx) && Kernel.isEqual(ax, cx)
				&& Kernel.isEqual(ay, by)
				&& Kernel.isEqual(ay, cy)) {
			circle.setCircle(getA(), 0.0); // single point
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
		if (Kernel.isZero(maxDet)) {
			center.setCoords(-ABy, ABx, 0.0d);
			circle.setCircle(center, getA());
		}
		// standard case
		else {
			// intersect two line bisectors according to casenr
			switch (casenr) {
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
		return app.getPlain("CircleThroughABC", A.getLabel(tpl), B.getLabel(tpl),
				C.getLabel(tpl));
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		Variable[] circle1vars = new Variable[2];
		Variable[] circle2vars = new Variable[2];
		Variable[] circle3vars = new Variable[2];
		circle1vars = ((SymbolicParametersBotanaAlgo) input[0]).getBotanaVars(input[0]);
		
		if (botanaVars == null) {
			botanaVars = new Variable[4];
			// Virtual center:
			botanaVars[0] = new Variable();
			botanaVars[1] = new Variable();
			// Point on the circle:
			botanaVars[2] = circle1vars[0];
			botanaVars[3] = circle1vars[1];
		}
		Variable[] centerVars = {botanaVars[0], botanaVars[1]};
		circle2vars = ((SymbolicParametersBotanaAlgo) input[1]).getBotanaVars(input[1]);
		circle3vars = ((SymbolicParametersBotanaAlgo) input[2]).getBotanaVars(input[2]);

		botanaPolynomials = new Polynomial[2];
		// AO=OB
		botanaPolynomials[0] = Polynomial.equidistant(circle1vars[0], circle1vars[1], 
				centerVars[0], centerVars[1], circle2vars[0], circle2vars[1]);
		// AO=OC
		botanaPolynomials[1] = Polynomial.equidistant(circle1vars[0], circle1vars[1], 
				centerVars[0], centerVars[1], circle3vars[0], circle3vars[1]);
	
		return botanaPolynomials;
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
	
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo, EquationScopeInterface scope) {
		return LocusEquation.eqnCircleThreePoints(geo, this, scope);
	}
}
