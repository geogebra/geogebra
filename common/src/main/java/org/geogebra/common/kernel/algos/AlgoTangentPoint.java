/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.AlgoIntersectND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 * Two tangents through point P to conic section c
 */
public class AlgoTangentPoint extends AlgoTangentPointND implements SymbolicParametersBotanaAlgo{

	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;
	
	public AlgoTangentPoint(Construction cons, String[] labels, GeoPointND P,
			GeoConicND c) {
		super(cons, labels, P, c);
	}

	@Override
	protected boolean isIntersectionPointIncident() {
		return c.isIntersectionPointIncident((GeoPoint) P, Kernel.MIN_PRECISION)
				|| P.getIncidenceList().contains(c);
	}

	@Override
	protected void setPolar() {
		// the tangents are computed by intersecting the
		// polar line of P with c
		polar = new GeoLine(cons);
		c.polarLine((GeoPoint) P, polar);
		algoIntersect = new AlgoIntersectLineConic(cons, polar, (GeoConic) c);
		// this is only an internal Algorithm that shouldn't be in the
		// construction list
		cons.removeFromConstructionList(algoIntersect);
		tangentPoints = algoIntersect.getIntersectionPoints();
	}

	@Override
	protected void setTangentFromPolar(int i) {
		((GeoLine) tangents[i]).setCoords(polar);
	}

	@Override
	protected void setTangents() {
		tangents = new GeoLine[2];
		tangents[0] = new GeoLine(cons);
		tangents[1] = new GeoLine(cons);
		((GeoLine) tangents[0]).setStartPoint((GeoPoint) P);
		((GeoLine) tangents[1]).setStartPoint((GeoPoint) P);
	}

	// Made public for LocusEqu
	public GeoPoint getPoint() {
		return (GeoPoint) P;
	}

	// Made public for LocusEqu
	public GeoConic getConic() {
		return (GeoConic) c;
	}

	/**
	 * Inits the helping interesection algorithm to take the current position of
	 * the lines into account. This is important so the the tangent lines are
	 * not switched after loading a file
	 */
	@Override
	public void initForNearToRelationship() {
		// if first tangent point is not on first tangent,
		// we switch the intersection points

		initForNearToRelationship(tangentPoints, tangents[0], algoIntersect);
	}

	/**
	 * Inits the helping interesection algorithm to take the current position of
	 * the lines into account. This is important so the the tangent lines are
	 * not switched after loading a file
	 *
	 * @param tangentPoints
	 *            tangent points
	 * @param tangent
	 *            tangent line
	 * @param algoIntersect
	 *            algo used
	 */
	static public void initForNearToRelationship(GeoPointND[] tangentPoints,
			GeoLineND tangent, AlgoIntersectND algoIntersect) {
		// if first tangent point is not on first tangent,
		// we switch the intersection points

		GeoPoint firstTangentPoint = (GeoPoint) tangentPoints[0];

		if (!((GeoLine) tangent).isOnFullLine(firstTangentPoint,
				Kernel.MIN_PRECISION)) {
			algoIntersect.initForNearToRelationship();

			// remember first point
			double px = firstTangentPoint.x;
			double py = firstTangentPoint.y;
			double pz = firstTangentPoint.z;

			// first = second
			algoIntersect.setIntersectionPoint(0, tangentPoints[1]);

			// second = first
			tangentPoints[1].setCoords(px, py, pz);
			algoIntersect.setIntersectionPoint(1, tangentPoints[1]);
		}
	}

	@Override
	protected void updatePolarLine() {
		c.polarLine((GeoPoint) P, polar);
	}

	@Override
	protected void updateTangents() {
		// calc tangents through tangentPoints
		GeoVec3D.lineThroughPoints((GeoPoint) P, (GeoPoint) tangentPoints[0],
				(GeoLine) tangents[0]);
		GeoVec3D.lineThroughPoints((GeoPoint) P, (GeoPoint) tangentPoints[1],
				(GeoLine) tangents[1]);
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (c.isCircle()) {
			GeoPoint point = this.getPoint();
			GeoConic circle = this.getConic();

			if (point != null && circle != null) {
				Variable[] vPoint = point.getBotanaVars(point);
				Variable[] vcircle = circle.getBotanaVars(circle);

				Polynomial pointDistCircle = Polynomial.equidistant(vPoint[0],
						vPoint[1], vcircle[0], vcircle[1], vcircle[2],
						vcircle[3]);
				
				// tangent point is on circle
				if (pointDistCircle.isZero()) {
					if (botanaVars == null) {
						botanaVars = new Variable[6];
						// T
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// A - tangent point on circle
						botanaVars[2] = vPoint[0];
						botanaVars[3] = vPoint[1];
						// C - A midpoint of OC
						botanaVars[4] = new Variable();
						botanaVars[5] = new Variable();
					}

					botanaPolynomials = new Polynomial[4];

					Polynomial c1 = new Polynomial(botanaVars[4]);
					Polynomial c2 = new Polynomial(botanaVars[5]);
					Polynomial a1 = new Polynomial(vPoint[0]);
					Polynomial a2 = new Polynomial(vPoint[1]);
					Polynomial o1 = new Polynomial(vcircle[0]);
					Polynomial o2 = new Polynomial(vcircle[1]);

					// A midpoint of OC
					botanaPolynomials[0] = new Polynomial(2).multiply(a1)
							.subtract(o1).subtract(c1);
					botanaPolynomials[1] = new Polynomial(2).multiply(a2)
							.subtract(o2).subtract(c2);

					// TA = AC
					botanaPolynomials[2] = Polynomial.equidistant(
							botanaVars[0], botanaVars[1], vPoint[0], vPoint[1],
							botanaVars[4], botanaVars[5]);

					// AT orthogonal OC
					botanaPolynomials[3] = Polynomial.perpendicular(vPoint[0],
							vPoint[1],
							botanaVars[0], botanaVars[1], vcircle[0],
							vcircle[1], botanaVars[4], botanaVars[5]);

				} else {
					if (botanaVars == null) {
						botanaVars = new Variable[6];
						// T - tangent point of circle
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// A
						botanaVars[2] = vPoint[0];
						botanaVars[3] = vPoint[1];
						// M - midpoint of OE
						botanaVars[4] = new Variable();
						botanaVars[5] = new Variable();
					}

					botanaPolynomials = new Polynomial[4];

					Polynomial m1 = new Polynomial(botanaVars[4]);
					Polynomial m2 = new Polynomial(botanaVars[5]);
					Polynomial e1 = new Polynomial(vPoint[0]);
					Polynomial e2 = new Polynomial(vPoint[1]);
					Polynomial o1 = new Polynomial(vcircle[0]);
					Polynomial o2 = new Polynomial(vcircle[1]);
			
					// M midpoint of EO
					botanaPolynomials[0] = new Polynomial(2).multiply(m1)
						.subtract(o1).subtract(e1);
					botanaPolynomials[1] = new Polynomial(2).multiply(m2)
						.subtract(o2).subtract(e2);

					// MT = ME
					botanaPolynomials[2] = Polynomial.equidistant(
							botanaVars[0],
						botanaVars[1], botanaVars[4], botanaVars[5], vPoint[0],
						vPoint[1]);

					// OT = OB
					botanaPolynomials[3] = Polynomial.equidistant(
							botanaVars[0],
						botanaVars[1], vcircle[0], vcircle[1], vcircle[2],
						vcircle[3]);
				}
				return botanaPolynomials;

			}
			throw new NoSymbolicParametersException();
		}
		if (c.isParabola()) {
			GeoPoint point = this.getPoint();
			GeoConic parabola = this.getConic();

			if (point != null && parabola != null) {
				Variable[] vPoint = point.getBotanaVars(point);
				Variable[] vparabola = parabola.getBotanaVars(parabola);

				if (botanaVars == null) {
					botanaVars = new Variable[4];
					// M - midpoint of FT'
					botanaVars[0] = new Variable();
					botanaVars[1] = new Variable();
					// T - tangent point
					botanaVars[2] = vPoint[0];
					botanaVars[3] = vPoint[1];
				}

				botanaPolynomials = new Polynomial[2];

				Polynomial m1 = new Polynomial(botanaVars[0]);
				Polynomial m2 = new Polynomial(botanaVars[1]);
				// coordinates of focus point of parabola
				Polynomial f1 = new Polynomial(vparabola[8]);
				Polynomial f2 = new Polynomial(vparabola[9]);
				// coordinates of T'
				Polynomial t_1 = new Polynomial(vPoint[2]);
				Polynomial t_2 = new Polynomial(vPoint[3]);

				// M midpoint of FT'
				botanaPolynomials[0] = new Polynomial(2).multiply(m1)
						.subtract(f1).subtract(t_1);
				botanaPolynomials[1] = new Polynomial(2).multiply(m2)
						.subtract(f2).subtract(t_2);

				return botanaPolynomials;

			}
			throw new NoSymbolicParametersException();

		}
		throw new NoSymbolicParametersException();
	}

}
