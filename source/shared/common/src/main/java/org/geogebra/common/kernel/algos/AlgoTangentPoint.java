/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.AlgoIntersectND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * Two tangents through point P to conic section c
 */
public class AlgoTangentPoint extends AlgoTangentPointND
		implements SymbolicParametersBotanaAlgo {

	private HashMap<GeoElementND, PPolynomial[]> botanaPolynomials;
	private HashMap<GeoElementND, PVariable[]> botanaVars;

	public AlgoTangentPoint(Construction cons, String[] labels, GeoPointND P,
			GeoConicND c) {
		super(cons, labels, P, c);
	}

	@Override
	protected boolean isIntersectionPointIncident() {
		// Too low precision causes tangent not touching the conic GGB-1018
		return c.isIntersectionPointIncident((GeoPoint) P,
				Kernel.STANDARD_PRECISION) || P.getIncidenceList().contains(c);
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
	 * Inits the helping intersection algorithm to take the current position of
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
	 * Inits the helping intersection algorithm to take the current position of
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

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		return botanaVars.get(geo);
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {

		/*
		 * Don't cache this. The equations may be different if the tangent point
		 * is on the curve.
		 */

		/* Create the Botana objects first. */
		if (botanaPolynomials == null) {
			botanaPolynomials = new HashMap<>();
		}
		if (botanaVars == null) {
			botanaVars = new HashMap<>();
		}

		// source for tangent point of conics:
		// www.cs.usfca.edu/~cruse/math109s06/tangents.ppt
		if (c.isCircle()) {
			GeoPoint point = this.getPoint();
			GeoConic circle = this.getConic();

			PVariable[] vPoint = point.getBotanaVars(point);
			PVariable[] vcircle = circle.getBotanaVars(circle);

			// is tangent point on circle?
			if (isIntersectionPointIncident()) {
				PVariable[] botanaVarsThis = new PVariable[4];
				if (getBotanaVars(geo) == null) {
					// tangent point
					botanaVarsThis[0] = vPoint[0];
					botanaVarsThis[1] = vPoint[1];
					// point on the tangent line
					botanaVarsThis[2] = new PVariable(kernel);
					botanaVarsThis[3] = new PVariable(kernel);
					botanaVars.put(geo, botanaVarsThis);
				} else {
					botanaVarsThis = getBotanaVars(geo);
				}

				PPolynomial[] botanaPolynomialsThis = new PPolynomial[2];
				// rotating the center of vcircle
				// around vpoint by 90 degrees
				// to get a point on the tangent line
				botanaPolynomialsThis[0] = new PPolynomial(botanaVarsThis[1])
						.subtract(new PPolynomial(vcircle[1]))
						.subtract(new PPolynomial(botanaVarsThis[2]))
						.add(new PPolynomial(botanaVarsThis[0]));
				botanaPolynomialsThis[1] = new PPolynomial(vcircle[0])
						.subtract(new PPolynomial(botanaVarsThis[0]))
						.subtract(new PPolynomial(botanaVarsThis[3]))
						.add(new PPolynomial(botanaVarsThis[1]));
				botanaPolynomials.put(geo, botanaPolynomialsThis);
				return botanaPolynomialsThis;

			}

			// tangent point is not on the circle

			PVariable[] botanaVarsThis = new PVariable[6];
			if (getBotanaVars(geo) == null) {

				// T - tangent point of circle
				botanaVarsThis[0] = new PVariable(kernel);
				botanaVarsThis[1] = new PVariable(kernel);
				// A
				botanaVarsThis[2] = vPoint[0];
				botanaVarsThis[3] = vPoint[1];
				// M - midpoint of OE
				botanaVarsThis[4] = new PVariable(kernel);
				botanaVarsThis[5] = new PVariable(kernel);
				botanaVars.put(geo, botanaVarsThis);
			} else {
				botanaVarsThis = getBotanaVars(geo);
			}

			PPolynomial[] botanaPolynomialsThis = null;
			/*
			 * Force a criterion that the two tangent points must differ. See
			 * AlgoIntersectConics.java.
			 */
			PVariable[] botanaVarsOther;
			Iterator<Entry<GeoElementND, PVariable[]>> it = botanaVars
					.entrySet().iterator();
			boolean found = false;
			while (it.hasNext()) {
				Entry<GeoElementND, PVariable[]> entry = it.next();
				GeoElementND otherGeo = entry.getKey();
				/*
				 * This should be at most one element. There is one element if
				 * we found the second tangent point, otherwise (for the first
				 * tangent point) there is no otherGeo yet, so we will not
				 * create any polynomials here (yet).
				 */
				if (!otherGeo.equals(geo)) {
					botanaPolynomialsThis = new PPolynomial[5];
					botanaVarsOther = entry.getValue();
					botanaPolynomialsThis[4] = (PPolynomial
							.sqrDistance(botanaVarsThis[0], botanaVarsThis[1],
									botanaVarsOther[0], botanaVarsOther[1])
							.multiply(new PPolynomial(new PVariable(kernel))))
									.subtract(new PPolynomial(1));
					found = true;
				}
			}
			if (!found) {
				botanaPolynomialsThis = new PPolynomial[4];
			}

			PPolynomial m1 = new PPolynomial(botanaVarsThis[4]);
			PPolynomial m2 = new PPolynomial(botanaVarsThis[5]);
			PPolynomial e1 = new PPolynomial(vPoint[0]);
			PPolynomial e2 = new PPolynomial(vPoint[1]);
			PPolynomial o1 = new PPolynomial(vcircle[0]);
			PPolynomial o2 = new PPolynomial(vcircle[1]);

			// M midpoint of EO
			botanaPolynomialsThis[0] = new PPolynomial(2).multiply(m1)
					.subtract(o1).subtract(e1);
			botanaPolynomialsThis[1] = new PPolynomial(2).multiply(m2)
					.subtract(o2).subtract(e2);

			// MT = ME
			botanaPolynomialsThis[2] = PPolynomial.equidistant(
					botanaVarsThis[0], botanaVarsThis[1], botanaVarsThis[4],
					botanaVarsThis[5], vPoint[0], vPoint[1]);

			// OT = OB                     # NO-TYPO
			botanaPolynomialsThis[3] = PPolynomial.equidistant(
					botanaVarsThis[0], botanaVarsThis[1], vcircle[0],
					vcircle[1], vcircle[2], vcircle[3]);
			botanaPolynomials.put(geo, botanaPolynomialsThis);
			return botanaPolynomialsThis;
		}

		if (c.isParabola()) {
			GeoPoint point = this.getPoint();
			GeoConic parabola = this.getConic();

				PVariable[] vPoint = point.getBotanaVars(point);
				PVariable[] vparabola = parabola.getBotanaVars(parabola);

				// is tangent point on the parabola?
				if (isIntersectionPointIncident()) {

					PVariable[] botanaVarsThis = new PVariable[4];
					if (getBotanaVars(geo) == null) {
						// M - midpoint of FT'
						botanaVarsThis[0] = new PVariable(kernel);
						botanaVarsThis[1] = new PVariable(kernel);
						// T - tangent point
						botanaVarsThis[2] = vPoint[0];
						botanaVarsThis[3] = vPoint[1];
						// the line MT will be the tangent
						botanaVars.put(geo, botanaVarsThis);
					} else {
						botanaVarsThis = getBotanaVars(geo);
					}

					PPolynomial[] botanaPolynomialsThis = new PPolynomial[4];

					PPolynomial m1 = new PPolynomial(botanaVarsThis[0]);
					PPolynomial m2 = new PPolynomial(botanaVarsThis[1]);
					// coordinates of focus point of parabola
					PPolynomial f1 = new PPolynomial(vparabola[8]);
					PPolynomial f2 = new PPolynomial(vparabola[9]);
					// coordinates of T' (feet point on the directrix for T)
					PVariable t_1 = new PVariable(kernel);
					PVariable t_2 = new PVariable(kernel);

					PPolynomial t_1p = new PPolynomial(t_1);
					PPolynomial t_2p = new PPolynomial(t_2);

					// M midpoint of FT'
					botanaPolynomialsThis[0] = new PPolynomial(2).multiply(m1)
							.subtract(f1).subtract(t_1p);
					botanaPolynomialsThis[1] = new PPolynomial(2).multiply(m2)
							.subtract(f2).subtract(t_2p);

					// T' is a feet point (we need to declare it)
					botanaPolynomialsThis[2] = PPolynomial.collinear(t_1, t_2,
							vparabola[4], vparabola[5], vparabola[6],
							vparabola[7]);
					// TT' = TF
					botanaPolynomialsThis[3] = PPolynomial.equidistant(t_1, t_2,
							vPoint[0], vPoint[1], vparabola[8], vparabola[9]);

					botanaPolynomials.put(geo, botanaPolynomialsThis);
					return botanaPolynomialsThis;
				}

				/* We use that the mirror F' of the focus about the tangent PT lies
				 * on the directrix. Therefore the external point P is equidistant
				 * from F and F'. This implies that F' lies on a circle with center
				 * P and radius FP, on the directrix. Finally PT=PM where M
				 * is the midpoint of FF'.
				 *
				 * This computation is, however, inaccurate. If P=M, there can be
				 * infinitely many lines defined. Therefore we explicitly compute
				 * T by using the fact that the line F'T is perpendicular to the directrix.
				 */

                PVariable[] botanaVarsThis = new PVariable[4];
                if (getBotanaVars(geo) == null) {
                    // T - tangent point
                    botanaVarsThis[0] = new PVariable(kernel);
                    botanaVarsThis[1] = new PVariable(kernel);
                    // P - external point
                    botanaVarsThis[2] = vPoint[0];
                    botanaVarsThis[3] = vPoint[1];
                    // the line PT will be the tangent
                    botanaVars.put(geo, botanaVarsThis);
                } else {
                    botanaVarsThis = getBotanaVars(geo);
                }

            PPolynomial[] botanaPolynomialsThis = new PPolynomial[5];

            // coordinates of F'
            PVariable f_1 = new PVariable(kernel);
            PVariable f_2 = new PVariable(kernel);

            // F' is on the directrix (we need to declare it)
            botanaPolynomialsThis[0] = PPolynomial.collinear(f_1, f_2,
                    vparabola[4], vparabola[5], vparabola[6],
                    vparabola[7]);
            // PF' = PF
            botanaPolynomialsThis[1] = PPolynomial.equidistant(f_1, f_2,
                    vPoint[0], vPoint[1], vparabola[8], vparabola[9]);
            // FF' is perpendicular to PT
            botanaPolynomialsThis[2] = PPolynomial.perpendicular(vparabola[8], vparabola[9],
                    f_1, f_2, botanaVarsThis[2], botanaVarsThis[3],
                    botanaVarsThis[0], botanaVarsThis[1]);
            // F'T is perpendicular to the directrix
            botanaPolynomialsThis[3] = PPolynomial.perpendicular(f_1, f_2,
                    botanaVarsThis[0], botanaVarsThis[1], vparabola[4], vparabola[5],
                    vparabola[6], vparabola[7]);
			// T=P is not allowed
			botanaPolynomialsThis[4] = (PPolynomial
					.sqrDistance(botanaVarsThis[0], botanaVarsThis[1],
							botanaVarsThis[2], botanaVarsThis[3])
					.multiply(new PPolynomial(new PVariable(kernel))))
					.subtract(new PPolynomial(1));

			botanaPolynomials.put(geo, botanaPolynomialsThis);
            return botanaPolynomialsThis;
		}

		// Ellipse and hyperbola cannot be distinguished.
		if (c.isEllipse() || c.isHyperbola()) {
			GeoPoint point = this.getPoint();
			GeoConic ellipse = this.getConic();

				PVariable[] vPoint = point.getBotanaVars(point);
				PVariable[] vellipse = ellipse.getBotanaVars(ellipse);

				// is tangent point on the ellipse/hyperbola?
				if (isIntersectionPointIncident()) {
					PVariable[] botanaVarsThis = new PVariable[6];
					if (getBotanaVars(geo) == null) {

						// M - tangent point
						botanaVarsThis[0] = new PVariable(kernel);
						botanaVarsThis[1] = new PVariable(kernel);
						// T - point on ellipse/hyperbola
						botanaVarsThis[2] = vPoint[0];
						botanaVarsThis[3] = vPoint[1];
						// D
						botanaVarsThis[4] = new PVariable(kernel);
						botanaVarsThis[5] = new PVariable(kernel);
						botanaVars.put(geo, botanaVarsThis);

					} else {
						botanaVarsThis = getBotanaVars(geo);
					}

					PPolynomial[] botanaPolynomialsThis = new PPolynomial[4];

					PPolynomial m1 = new PPolynomial(botanaVarsThis[0]);
					PPolynomial m2 = new PPolynomial(botanaVarsThis[1]);
					// coordinates of second focus point of ellipse/hyperbola
					PPolynomial f21 = new PPolynomial(vellipse[8]);
					PPolynomial f22 = new PPolynomial(vellipse[9]);
					// coordinates of D
					PPolynomial d1 = new PPolynomial(botanaVarsThis[4]);
					PPolynomial d2 = new PPolynomial(botanaVarsThis[5]);

					// F_1,T,D collinear
					botanaPolynomialsThis[0] = PPolynomial.collinear(vellipse[6],
							vellipse[7], vPoint[0], vPoint[1], botanaVarsThis[4],
							botanaVarsThis[5]);

					// F_2T = TD
					botanaPolynomialsThis[1] = PPolynomial.equidistant(vellipse[8],
							vellipse[9], vPoint[0], vPoint[1], botanaVarsThis[4],
							botanaVarsThis[5]);

					// M midpoint of F_2D
					botanaPolynomialsThis[2] = new PPolynomial(2).multiply(m1)
							.subtract(f21).subtract(d1);
					botanaPolynomialsThis[3] = new PPolynomial(2).multiply(m2)
							.subtract(f22).subtract(d2);
					botanaPolynomials.put(geo, botanaPolynomialsThis);

					return botanaPolynomialsThis;
				}
			/*
			 * If not, we compute a tangent line. Note that this is usually
			 * resource heavy and results in a much wider set of curves, see
			 * tangents-ellipse-hyperbola2 in the art-plotter benchmark. TODO:
			 * check the equations, maybe there is some hope to improve this.
			 */
			PVariable[] botanaVarsThis = new PVariable[6];
			if (getBotanaVars(geo) == null) {
				// M - the other tangent point
				botanaVarsThis[0] = new PVariable(kernel);
				botanaVarsThis[1] = new PVariable(kernel);
				// P - this point on the tangent
				botanaVarsThis[2] = vPoint[0];
				botanaVarsThis[3] = vPoint[1];
				// D
				botanaVarsThis[4] = new PVariable(kernel);
				botanaVarsThis[5] = new PVariable(kernel);
				// T is inherited from vellipse[0] and vellipse[1]
				botanaVars.put(geo, botanaVarsThis);
			} else {
				botanaVarsThis = getBotanaVars(geo);
			}

			PPolynomial[] botanaPolynomialsThis = new PPolynomial[5];

			PPolynomial m1 = new PPolynomial(botanaVarsThis[0]);
			PPolynomial m2 = new PPolynomial(botanaVarsThis[1]);
			// coordinates of second focus point of ellipse/hyperbola
			PPolynomial f21 = new PPolynomial(vellipse[8]);
			PPolynomial f22 = new PPolynomial(vellipse[9]);
			// coordinates of D
			PPolynomial d1 = new PPolynomial(botanaVarsThis[4]);
			PPolynomial d2 = new PPolynomial(botanaVarsThis[5]);

			// F_1,T,D collinear
			botanaPolynomialsThis[0] = PPolynomial.collinear(vellipse[6],
					vellipse[7], vellipse[0], vellipse[1], botanaVarsThis[4],
					botanaVarsThis[5]);

			// F_2T = TD
			botanaPolynomialsThis[1] = PPolynomial.equidistant(vellipse[8],
					vellipse[9], vellipse[0], vellipse[1], botanaVarsThis[4],
					botanaVarsThis[5]);

			// M midpoint of F_2D
			botanaPolynomialsThis[2] = new PPolynomial(2).multiply(m1).subtract(f21)
					.subtract(d1);
			botanaPolynomialsThis[3] = new PPolynomial(2).multiply(m2).subtract(f22)
					.subtract(d2);

			// T,M,P collinear
			botanaPolynomialsThis[4] = PPolynomial.collinear(vellipse[0],
					vellipse[1], botanaVarsThis[0], botanaVarsThis[1], botanaVarsThis[2],
					botanaVarsThis[3]);
			botanaPolynomials.put(geo, botanaPolynomialsThis);
			return botanaPolynomialsThis;

		}
		throw new NoSymbolicParametersException();

		// TODO: implement the remaining cases
	}

}
