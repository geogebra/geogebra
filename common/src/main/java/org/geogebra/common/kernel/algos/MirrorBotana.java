package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.main.App;

public class MirrorBotana {
	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;

	public Polynomial[] getBotanaPolynomials(GeoElementND geo,
			GeoElement inGeo, GeoLineND mirrorLine, GeoPointND mirrorPoint,
			GeoConicND mirrorCircle) throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (mirrorLine instanceof GeoLine) {

			GeoPoint P, Q;
			// if we want to mirror a line to a line
			if (inGeo.isGeoLine()) {
				P = ((GeoLine) inGeo).startPoint;
				Q = ((GeoLine) inGeo).endPoint;
				GeoLine l = (GeoLine) mirrorLine;

				if (P != null && Q != null && l != null) {
					Variable[] vP = P.getBotanaVars(P);
					Variable[] vQ = Q.getBotanaVars(Q);
					Variable[] vL = l.getBotanaVars(l);

					if (botanaVars == null) {
						botanaVars = new Variable[8];
						// P' - mirror of P
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// Q' - mirror of Q
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
						// V1 - auxiliary point
						botanaVars[4] = new Variable();
						botanaVars[5] = new Variable();
						// V2 - auxiliary point
						botanaVars[6] = new Variable();
						botanaVars[7] = new Variable();
					}

					botanaPolynomials = new Polynomial[8];

					// first we want to mirror P to line l

					Polynomial p1 = new Polynomial(vP[0]);
					Polynomial p2 = new Polynomial(vP[1]);
					Polynomial v1_1 = new Polynomial(botanaVars[4]);
					Polynomial v1_2 = new Polynomial(botanaVars[5]);
					Polynomial p_1 = new Polynomial(botanaVars[0]);
					Polynomial p_2 = new Polynomial(botanaVars[1]);

					// PV1 = V1P'
					botanaPolynomials[0] = v1_1.multiply(new Polynomial(2))
							.subtract(p1).subtract(p_1);
					botanaPolynomials[1] = v1_2.multiply(new Polynomial(2))
							.subtract(p2).subtract(p_2);

					Variable[] A = new Variable[2];
					// A - start point of mirrorLine
					A[0] = vL[0];
					A[1] = vL[1];
					Variable[] B = new Variable[2];
					// B - end point of mirrorLine
					B[0] = vL[2];
					B[1] = vL[3];

					// A, V1, B collinear
					botanaPolynomials[2] = Polynomial.collinear(A[0], A[1],
							botanaVars[4], botanaVars[5], B[0], B[1]);

					// PV1 orthogonal AB
					botanaPolynomials[3] = Polynomial.perpendicular(vP[0],
							vP[1], botanaVars[4], botanaVars[5], A[0], A[1],
							B[0], B[1]);

					// second we want to mirror Q to line l

					Polynomial q1 = new Polynomial(vQ[0]);
					Polynomial q2 = new Polynomial(vQ[1]);
					Polynomial v2_1 = new Polynomial(botanaVars[6]);
					Polynomial v2_2 = new Polynomial(botanaVars[7]);
					Polynomial q_1 = new Polynomial(botanaVars[2]);
					Polynomial q_2 = new Polynomial(botanaVars[3]);

					// QV2 = V2Q'
					botanaPolynomials[4] = v2_1.multiply(new Polynomial(2))
							.subtract(q1).subtract(q_1);
					botanaPolynomials[5] = v2_2.multiply(new Polynomial(2))
							.subtract(q2).subtract(q_2);

					// A, V2, B collinear
					botanaPolynomials[6] = Polynomial.collinear(A[0], A[1],
							botanaVars[6], botanaVars[7], B[0], B[1]);

					// QV2 orthogonal AB
					botanaPolynomials[7] = Polynomial.perpendicular(vQ[0],
							vQ[1], botanaVars[6], botanaVars[7], A[0], A[1],
							B[0], B[1]);

					return botanaPolynomials;
				}
				throw new NoSymbolicParametersException();
			}
			// we want to mirror a point to a line
			else if (inGeo.isGeoPoint()) {
				P = (GeoPoint) inGeo;
				GeoLine l = (GeoLine) mirrorLine;

				if (P != null && l != null) {
					Variable[] vP = P.getBotanaVars(P);
					Variable[] vL = l.getBotanaVars(l);

					if (botanaVars == null) {
						botanaVars = new Variable[6];
						// C'
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// V
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
						// N
						botanaVars[4] = new Variable();
						botanaVars[5] = new Variable();
					}

					botanaPolynomials = new Polynomial[6];

					Polynomial v1 = new Polynomial(botanaVars[2]);
					Polynomial v2 = new Polynomial(botanaVars[3]);
					Polynomial c1 = new Polynomial(vP[0]);
					Polynomial c2 = new Polynomial(vP[1]);
					Polynomial c_1 = new Polynomial(botanaVars[0]);
					Polynomial c_2 = new Polynomial(botanaVars[1]);

					// CV = VC'
					botanaPolynomials[0] = v1.multiply(new Polynomial(2))
							.subtract(c_1).subtract(c1);
					botanaPolynomials[1] = v2.multiply(new Polynomial(2))
							.subtract(c_2).subtract(c2);

					// points of mirrorLine
					Variable[] A = new Variable[2];
					A[0] = vL[0];
					A[1] = vL[1];
					Variable[] B = new Variable[2];
					B[0] = vL[2];
					B[1] = vL[3];

					// A,V,B collinear
					botanaPolynomials[2] = Polynomial.collinear(A[0], A[1],
							B[0], B[1], botanaVars[2], botanaVars[3]);

					Polynomial a1 = new Polynomial(A[0]);
					Polynomial a2 = new Polynomial(A[1]);
					Polynomial b1 = new Polynomial(B[0]);
					Polynomial b2 = new Polynomial(B[1]);
					Polynomial n1 = new Polynomial(botanaVars[4]);
					Polynomial n2 = new Polynomial(botanaVars[5]);

					// CV orthogonal AB
					botanaPolynomials[3] = b1.subtract(a1).add(c2).subtract(n2);
					botanaPolynomials[4] = c1.subtract(b2).add(a2).subtract(n1);

					// C',N,V collinear
					botanaPolynomials[5] = Polynomial.collinear(botanaVars[0],
							botanaVars[1], botanaVars[2], botanaVars[3],
							botanaVars[4], botanaVars[5]);

					return botanaPolynomials;
				}
				throw new NoSymbolicParametersException();
			}
			// mirror circle to line
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isCircle()) {
				GeoConic circle = (GeoConic) inGeo;
				GeoLine l = (GeoLine) mirrorLine;

				if (circle != null && l != null) {
					Variable[] vCircle = circle.getBotanaVars(circle);
					Variable[] vl = l.getBotanaVars(l);

					if (botanaVars == null) {
						botanaVars = new Variable[12];
						// A' - mirror of center
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// B' - mirror of point on circle
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
						// V - midpoint of center and mirror of center
						botanaVars[4] = new Variable();
						botanaVars[5] = new Variable();
						// T - midpoint of point on circle and mirror of point
						// on circle
						botanaVars[6] = new Variable();
						botanaVars[7] = new Variable();
						// N1 - AN1 orthogonal CD
						botanaVars[8] = new Variable();
						botanaVars[9] = new Variable();
						// N2 - BN2 orthogonal CD
						botanaVars[10] = new Variable();
						botanaVars[11] = new Variable();
					}

					botanaPolynomials = new Polynomial[12];

					Polynomial v1 = new Polynomial(botanaVars[4]);
					Polynomial v2 = new Polynomial(botanaVars[5]);
					Polynomial a1 = new Polynomial(vCircle[0]);
					Polynomial a2 = new Polynomial(vCircle[1]);
					Polynomial a_1 = new Polynomial(botanaVars[0]);
					Polynomial a_2 = new Polynomial(botanaVars[1]);

					// AV = VA'
					botanaPolynomials[0] = v1.multiply(new Polynomial(2))
							.subtract(a_1).subtract(a1);
					botanaPolynomials[1] = v2.multiply(new Polynomial(2))
							.subtract(a_2).subtract(a2);

					// C, V, D collinear
					botanaPolynomials[2] = Polynomial.collinear(vl[0], vl[1],
							botanaVars[4], botanaVars[5], vl[2], vl[3]);

					Polynomial c1 = new Polynomial(vl[0]);
					Polynomial c2 = new Polynomial(vl[1]);
					Polynomial d1 = new Polynomial(vl[2]);
					Polynomial d2 = new Polynomial(vl[3]);
					Polynomial n1_1 = new Polynomial(botanaVars[8]);
					Polynomial n1_2 = new Polynomial(botanaVars[9]);

					// AV orthogonal CD
					botanaPolynomials[3] = d1.subtract(c1).add(a2)
							.subtract(n1_2);
					botanaPolynomials[4] = a1.subtract(d2).add(c2)
							.subtract(n1_1);

					// A', V, N1 collinear
					botanaPolynomials[5] = Polynomial.collinear(botanaVars[0],
							botanaVars[1], botanaVars[4], botanaVars[5],
							botanaVars[8], botanaVars[9]);

					Polynomial t1 = new Polynomial(botanaVars[6]);
					Polynomial t2 = new Polynomial(botanaVars[7]);
					Polynomial b1 = new Polynomial(vCircle[2]);
					Polynomial b2 = new Polynomial(vCircle[3]);
					Polynomial b_1 = new Polynomial(botanaVars[2]);
					Polynomial b_2 = new Polynomial(botanaVars[3]);

					// BT = TB'
					botanaPolynomials[6] = t1.multiply(new Polynomial(2))
							.subtract(b_1).subtract(b1);
					botanaPolynomials[7] = t2.multiply(new Polynomial(2))
							.subtract(b_2).subtract(b2);

					// C, T, D collinear
					botanaPolynomials[8] = Polynomial.collinear(vl[0], vl[1],
							botanaVars[6], botanaVars[7], vl[2], vl[3]);

					Polynomial n2_1 = new Polynomial(botanaVars[10]);
					Polynomial n2_2 = new Polynomial(botanaVars[11]);

					// BT orthogonal CD
					botanaPolynomials[9] = d1.subtract(c1).add(b2)
							.subtract(n2_2);
					botanaPolynomials[10] = b1.subtract(d2).add(c2)
							.subtract(n2_1);

					// B', T, N2 collinear
					botanaPolynomials[11] = Polynomial.collinear(botanaVars[1],
							botanaVars[2], botanaVars[6], botanaVars[7],
							botanaVars[10], botanaVars[11]);

					return botanaPolynomials;
				}
				throw new NoSymbolicParametersException();
			}
			// mirror parabola about line
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isParabola()) {
				GeoConic parabola = (GeoConic) inGeo;
				GeoLine l = (GeoLine) mirrorLine;

				if (parabola != null && l != null) {
					Variable[] vparabola = parabola.getBotanaVars(parabola);
					Variable[] vl = l.getBotanaVars(l);

					if (botanaVars == null) {
						botanaVars = new Variable[22];
						// P' - point at parabola
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// T' - projection of P' at directirx
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
						// A' - mirror of star point of directrix
						botanaVars[4] = new Variable();
						botanaVars[5] = new Variable();
						// B' - mirror of end point of directrix
						botanaVars[6] = new Variable();
						botanaVars[7] = new Variable();
						// F' - mirror of focus point
						botanaVars[8] = new Variable();
						botanaVars[9] = new Variable();
						// V1 - midpoint of AA'
						botanaVars[10] = new Variable();
						botanaVars[11] = new Variable();
						// V2 - midpoint of BB'
						botanaVars[12] = new Variable();
						botanaVars[13] = new Variable();
						// V3 - midpoint of CC'
						botanaVars[14] = new Variable();
						botanaVars[15] = new Variable();
						// N1 - AN1 orthogonal M1M2 (mirror line)
						botanaVars[16] = new Variable();
						botanaVars[17] = new Variable();
						// N2 - BN2 orthogonal M1M2
						botanaVars[18] = new Variable();
						botanaVars[19] = new Variable();
						// N3 - CN3 orthogonal M1M2
						botanaVars[20] = new Variable();
						botanaVars[21] = new Variable();
					}

					botanaPolynomials = new Polynomial[21];

					Polynomial a1 = new Polynomial(vparabola[4]);
					Polynomial a2 = new Polynomial(vparabola[5]);
					Polynomial v1_1 = new Polynomial(botanaVars[10]);
					Polynomial v1_2 = new Polynomial(botanaVars[11]);
					Polynomial a_1 = new Polynomial(botanaVars[4]);
					Polynomial a_2 = new Polynomial(botanaVars[5]);
					Polynomial n1_1 = new Polynomial(botanaVars[16]);
					Polynomial n1_2 = new Polynomial(botanaVars[17]);
					Polynomial m1_1 = new Polynomial(vl[0]);
					Polynomial m1_2 = new Polynomial(vl[1]);
					Polynomial m2_1 = new Polynomial(vl[2]);
					Polynomial m2_2 = new Polynomial(vl[3]);

					// 6 equations to define A'
					// AV1 = V1A'
					botanaPolynomials[0] = new Polynomial(2).multiply(v1_1)
							.subtract(a1).subtract(a_1);
					botanaPolynomials[1] = new Polynomial(2).multiply(v1_2)
							.subtract(a2).subtract(a_2);

					// A', V1, N1 collinear
					botanaPolynomials[2] = Polynomial.collinear(botanaVars[4],
							botanaVars[5], botanaVars[10], botanaVars[11],
							botanaVars[16], botanaVars[17]);

					// M1, V1, M2 collinear
					botanaPolynomials[3] = Polynomial.collinear(vl[0], vl[1],
							botanaVars[10], botanaVars[11], vl[2], vl[3]);

					// AN1 orthogonal M1M2
					botanaPolynomials[4] = m2_1.subtract(m1_1).add(a2)
							.subtract(n1_2);
					botanaPolynomials[5] = a1.subtract(m2_2).add(m1_2)
							.subtract(n1_1);

					Polynomial b1 = new Polynomial(vparabola[6]);
					Polynomial b2 = new Polynomial(vparabola[7]);
					Polynomial v2_1 = new Polynomial(botanaVars[12]);
					Polynomial v2_2 = new Polynomial(botanaVars[13]);
					Polynomial b_1 = new Polynomial(botanaVars[6]);
					Polynomial b_2 = new Polynomial(botanaVars[7]);
					Polynomial n2_1 = new Polynomial(botanaVars[18]);
					Polynomial n2_2 = new Polynomial(botanaVars[19]);

					// 6 equations to deifne B'
					// BV2 = V2B'
					botanaPolynomials[6] = new Polynomial(2).multiply(v2_1)
							.subtract(b1).subtract(b_1);
					botanaPolynomials[7] = new Polynomial(2).multiply(v2_2)
							.subtract(b2).subtract(b_2);

					// B', V2, N2 collinear
					botanaPolynomials[8] = Polynomial.collinear(botanaVars[6],
							botanaVars[7], botanaVars[12], botanaVars[13],
							botanaVars[18], botanaVars[19]);

					// M1, V2, M2 collinear
					botanaPolynomials[9] = Polynomial.collinear(vl[0], vl[1],
							botanaVars[12], botanaVars[13], vl[2], vl[3]);

					// BN2 orthogonal M1M2
					botanaPolynomials[10] = m2_1.subtract(m1_1).add(b2)
							.subtract(n2_2);
					botanaPolynomials[11] = b1.subtract(m2_2).add(m1_2)
							.subtract(n2_1);

					Polynomial f1 = new Polynomial(vparabola[8]);
					Polynomial f2 = new Polynomial(vparabola[9]);
					Polynomial v3_1 = new Polynomial(botanaVars[14]);
					Polynomial v3_2 = new Polynomial(botanaVars[15]);
					Polynomial f_1 = new Polynomial(botanaVars[8]);
					Polynomial f_2 = new Polynomial(botanaVars[9]);
					Polynomial n3_1 = new Polynomial(botanaVars[20]);
					Polynomial n3_2 = new Polynomial(botanaVars[21]);

					// 6 equations to define F'
					// FV3 = V3F'
					botanaPolynomials[12] = new Polynomial(2).multiply(v3_1)
							.subtract(f1).subtract(f_1);
					botanaPolynomials[13] = new Polynomial(2).multiply(v3_2)
							.subtract(f2).subtract(f_2);

					// F', V3, N3 collinear
					botanaPolynomials[14] = Polynomial.collinear(botanaVars[8],
							botanaVars[9], botanaVars[14], botanaVars[15],
							botanaVars[20], botanaVars[21]);

					// M1, V3, M2 collinear
					botanaPolynomials[15] = Polynomial.collinear(vl[0], vl[1],
							botanaVars[14], botanaVars[15], vl[2], vl[3]);

					// FN3 orthogonal M1M2
					botanaPolynomials[16] = m2_1.subtract(m1_1).add(f2)
							.subtract(n3_2);
					botanaPolynomials[17] = f1.subtract(m2_2).add(m1_2)
							.subtract(n3_1);

					// 3 equations to define parabola
					// |F'P'| = |P'T'|
					botanaPolynomials[18] = Polynomial.equidistant(
							botanaVars[8], botanaVars[9], botanaVars[0],
							botanaVars[1], botanaVars[2], botanaVars[3]);

					// A', T', B' collinear
					botanaPolynomials[19] = Polynomial.collinear(botanaVars[4],
							botanaVars[5], botanaVars[2], botanaVars[3],
							botanaVars[6], botanaVars[7]);

					// P'T' orthogonal A'B'
					botanaPolynomials[20] = Polynomial.perpendicular(
							botanaVars[0], botanaVars[1], botanaVars[2],
							botanaVars[3], botanaVars[4], botanaVars[5],
							botanaVars[6], botanaVars[7]);

					// 2 more equation for F' and T'
					// |FP|^2 = |F'P'|^2
					// botanaPolynomials[21] = Polynomial.sqrDistance(
					// vparabola[8], vparabola[9], vparabola[0],
					// vparabola[1]).subtract(
					// Polynomial
					// .sqrDistance(botanaVars[8], botanaVars[9],
					// botanaVars[0], botanaVars[1]));

					// |PT|^2 = |P'T'|^2
					// botanaPolynomials[22] = Polynomial.sqrDistance(
					// vparabola[0], vparabola[1], vparabola[2],
					// vparabola[3]).subtract(
					// Polynomial
					// .sqrDistance(botanaVars[0], botanaVars[1],
					// botanaVars[2], botanaVars[3]));

					return botanaPolynomials;

				}
				throw new NoSymbolicParametersException();
			}
			// invalid object to reflect about line
			throw new NoSymbolicParametersException();

		}
		// case mirroring GeoElement about point
		else if (mirrorPoint instanceof GeoPoint) {

			// mirror point about point
			if (inGeo.isGeoPoint()) {
				GeoPoint P1 = (GeoPoint) inGeo;
				GeoPoint P2 = (GeoPoint) mirrorPoint;

				if (P1 != null && P2 != null) {
					Variable[] vP1 = P1.getBotanaVars(P1);
					Variable[] vP2 = P2.getBotanaVars(P2);

					if (botanaVars == null) {
						botanaVars = new Variable[2];
						// A' - mirror of point
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
					}

					botanaPolynomials = new Polynomial[2];

					Polynomial a1 = new Polynomial(vP1[0]);
					Polynomial a2 = new Polynomial(vP1[1]);
					Polynomial b1 = new Polynomial(vP2[0]);
					Polynomial b2 = new Polynomial(vP2[1]);
					Polynomial a_1 = new Polynomial(botanaVars[0]);
					Polynomial a_2 = new Polynomial(botanaVars[1]);

					// AB vector = BA' vector
					botanaPolynomials[0] = b1.multiply(new Polynomial(2))
							.subtract(a1).subtract(a_1);
					botanaPolynomials[1] = b2.multiply(new Polynomial(2))
							.subtract(a2).subtract(a_2);

					return botanaPolynomials;
				}
				throw new NoSymbolicParametersException();
			}
			// mirror line about point
			else if (inGeo.isGeoLine()) {
				GeoLine l = (GeoLine) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

				if (l != null && P != null) {
					Variable[] vl = l.getBotanaVars(l);
					Variable[] vP = P.getBotanaVars(P);

					if (botanaVars == null) {
						botanaVars = new Variable[4];
						// A' - mirror of start point
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// B' - mirror of end point
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
					}

					botanaPolynomials = new Polynomial[4];

					Polynomial p1 = new Polynomial(vP[0]);
					Polynomial p2 = new Polynomial(vP[1]);
					Polynomial a1 = new Polynomial(vl[0]);
					Polynomial a2 = new Polynomial(vl[1]);
					Polynomial a_1 = new Polynomial(botanaVars[0]);
					Polynomial a_2 = new Polynomial(botanaVars[1]);
					Polynomial b1 = new Polynomial(vl[2]);
					Polynomial b2 = new Polynomial(vl[3]);
					Polynomial b_1 = new Polynomial(botanaVars[2]);
					Polynomial b_2 = new Polynomial(botanaVars[3]);

					// AP vector = PA' vector
					botanaPolynomials[0] = p1.subtract(a1).subtract(
							a_1.subtract(p1));
					botanaPolynomials[1] = p2.subtract(a2).subtract(
							a_2.subtract(p2));

					// BP vector = PB' vector
					botanaPolynomials[2] = p1.subtract(b1).subtract(
							b_1.subtract(p1));
					botanaPolynomials[3] = p2.subtract(b2).subtract(
							b_2.subtract(p2));

					return botanaPolynomials;
				}
				throw new NoSymbolicParametersException();
			}
			// mirror circle about point
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isCircle()) {
				GeoConic circle = (GeoConic) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

				if (circle != null && P != null) {
					Variable[] vCircle = circle.getBotanaVars(circle);
					Variable[] vP = P.getBotanaVars(P);

					if (botanaVars == null) {
						botanaVars = new Variable[4];
						// A' - mirror of center
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// B' - mirror of point on the circle
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
					}

					botanaPolynomials = new Polynomial[4];

					Polynomial p1 = new Polynomial(vP[0]);
					Polynomial p2 = new Polynomial(vP[1]);
					Polynomial a1 = new Polynomial(vCircle[0]);
					Polynomial a2 = new Polynomial(vCircle[1]);
					Polynomial a_1 = new Polynomial(botanaVars[0]);
					Polynomial a_2 = new Polynomial(botanaVars[1]);
					Polynomial b1 = new Polynomial(vCircle[2]);
					Polynomial b2 = new Polynomial(vCircle[3]);
					Polynomial b_1 = new Polynomial(botanaVars[2]);
					Polynomial b_2 = new Polynomial(botanaVars[3]);

					// AP vector = PA' vector
					botanaPolynomials[0] = p1.subtract(a1).subtract(
							a_1.subtract(p1));
					botanaPolynomials[1] = p2.subtract(a2).subtract(
							a_2.subtract(p2));

					// BP vector = PB' vector
					botanaPolynomials[2] = p1.subtract(b1).subtract(
							b_1.subtract(p1));
					botanaPolynomials[3] = p2.subtract(b2).subtract(
							b_2.subtract(p2));

					return botanaPolynomials;
				}
				throw new NoSymbolicParametersException();
			}
			// mirror parabola about point
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isParabola()) {

				GeoConic parabola = (GeoConic) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

				if (parabola != null && P != null) {
					Variable[] vparabola = parabola.getBotanaVars(parabola);
					Variable[] vP = P.getBotanaVars(P);
					if (botanaVars == null) {
						botanaVars = new Variable[10];
						// P' - mirror of point on parabola
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// T' - mirror of projection point of P' at A'B'
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
						// A' - mirror of start point of directrix
						botanaVars[4] = new Variable();
						botanaVars[5] = new Variable();
						// B' - mirror of end point of directrix
						botanaVars[6] = new Variable();
						botanaVars[7] = new Variable();
						// F' - mirror of focus point
						botanaVars[8] = new Variable();
						botanaVars[9] = new Variable();
					}

					botanaPolynomials = new Polynomial[13];

					Polynomial p1 = new Polynomial(vparabola[0]);
					Polynomial p2 = new Polynomial(vparabola[1]);
					Polynomial t1 = new Polynomial(vparabola[2]);
					Polynomial t2 = new Polynomial(vparabola[3]);
					Polynomial a1 = new Polynomial(vparabola[4]);
					Polynomial a2 = new Polynomial(vparabola[5]);
					Polynomial b1 = new Polynomial(vparabola[6]);
					Polynomial b2 = new Polynomial(vparabola[7]);
					Polynomial f1 = new Polynomial(vparabola[8]);
					Polynomial f2 = new Polynomial(vparabola[9]);
					Polynomial p_1 = new Polynomial(botanaVars[0]);
					Polynomial p_2 = new Polynomial(botanaVars[1]);
					Polynomial t_1 = new Polynomial(botanaVars[2]);
					Polynomial t_2 = new Polynomial(botanaVars[3]);
					Polynomial a_1 = new Polynomial(botanaVars[4]);
					Polynomial a_2 = new Polynomial(botanaVars[5]);
					Polynomial b_1 = new Polynomial(botanaVars[6]);
					Polynomial b_2 = new Polynomial(botanaVars[7]);
					Polynomial f_1 = new Polynomial(botanaVars[8]);
					Polynomial f_2 = new Polynomial(botanaVars[9]);
					Polynomial m1 = new Polynomial(vP[0]);
					Polynomial m2 = new Polynomial(vP[1]);

					// 10 equations for coordinates of mirrored points
					// PM vector = MP' vector
					botanaPolynomials[0] = new Polynomial(2).multiply(m1)
							.subtract(p1).subtract(p_1);
					botanaPolynomials[1] = new Polynomial(2).multiply(m2)
							.subtract(p2).subtract(p_2);

					// TM vector = MT' vector
					botanaPolynomials[2] = new Polynomial(2).multiply(m1)
							.subtract(t1).subtract(t_1);
					botanaPolynomials[3] = new Polynomial(2).multiply(m2)
							.subtract(t2).subtract(t_2);

					// AM vector = MA' vector
					botanaPolynomials[4] = new Polynomial(2).multiply(m1)
							.subtract(a1).subtract(a_1);
					botanaPolynomials[5] = new Polynomial(2).multiply(m2)
							.subtract(a2).subtract(a_2);

					// BM vector = MB' vector
					botanaPolynomials[6] = new Polynomial(2).multiply(m1)
							.subtract(b1).subtract(b_1);
					botanaPolynomials[7] = new Polynomial(2).multiply(m2)
							.subtract(b2).subtract(b_2);

					// FM vector = MF' vector
					botanaPolynomials[8] = new Polynomial(2).multiply(m1)
							.subtract(f1).subtract(f_1);
					botanaPolynomials[9] = new Polynomial(2).multiply(m2)
							.subtract(f2).subtract(f_2);

					// 3 equations as definition of mirrored parabola
					// |F'P'| = |P'T'|
					botanaPolynomials[10] = Polynomial.equidistant(
							botanaVars[8], botanaVars[9], botanaVars[0],
							botanaVars[1], botanaVars[2], botanaVars[3]);

					// A',T',B' collinear
					botanaPolynomials[11] = Polynomial.collinear(botanaVars[4],
							botanaVars[5], botanaVars[2], botanaVars[3],
							botanaVars[6], botanaVars[7]);

					// P'T' orthogonal A'B'
					botanaPolynomials[12] = Polynomial.perpendicular(
							botanaVars[0], botanaVars[1], botanaVars[2],
							botanaVars[3], botanaVars[4], botanaVars[5],
							botanaVars[6], botanaVars[7]);

					return botanaPolynomials;
				}
				throw new NoSymbolicParametersException();
			}
			// mirror ellipse about point
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isEllipse()) {
				GeoConic ellipse = (GeoConic) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

				if (ellipse != null && P != null) {
					Variable[] vellipse = ellipse.getBotanaVars(ellipse);
					Variable[] vP = P.getBotanaVars(P);

					// the two focus points are the same
					if (vellipse[6] == vellipse[8]
							&& vellipse[7] == vellipse[9]) {
						// handle ellipse as circle
						if (botanaVars == null) {
							botanaVars = new Variable[4];
							// A' - mirror of the center point
							botanaVars[0] = new Variable();
							botanaVars[1] = new Variable();
							// C' - mirror of the point at circle
							botanaVars[2] = new Variable();
							botanaVars[3] = new Variable();
						}

						botanaPolynomials = new Polynomial[4];

						Polynomial a1 = new Polynomial(vellipse[0]);
						Polynomial a2 = new Polynomial(vellipse[1]);
						Polynomial c1 = new Polynomial(vellipse[2]);
						Polynomial c2 = new Polynomial(vellipse[3]);
						Polynomial p1 = new Polynomial(vP[0]);
						Polynomial p2 = new Polynomial(vP[1]);
						Polynomial a_1 = new Polynomial(botanaVars[0]);
						Polynomial a_2 = new Polynomial(botanaVars[1]);
						Polynomial c_1 = new Polynomial(botanaVars[2]);
						Polynomial c_2 = new Polynomial(botanaVars[3]);

						// AP vector = PA' vector
						botanaPolynomials[0] = new Polynomial(2).multiply(p1)
								.subtract(a1).subtract(a_1);
						botanaPolynomials[1] = new Polynomial(2).multiply(p2)
								.subtract(a2).subtract(a_2);

						// CP vector = PC' vector
						botanaPolynomials[2] = new Polynomial(2).multiply(p1)
								.subtract(c1).subtract(c_1);
						botanaPolynomials[3] = new Polynomial(2).multiply(p2)
								.subtract(c2).subtract(c_2);

					} else {
						if (botanaVars == null) {
							botanaVars = new Variable[12];
							// P' - mirror of second point on ellipse
							botanaVars[0] = new Variable();
							botanaVars[1] = new Variable();
							// auxiliary variables
							botanaVars[2] = new Variable();
							botanaVars[3] = new Variable();
							botanaVars[4] = new Variable();
							botanaVars[5] = new Variable();
							// A' - mirror of first focus point
							botanaVars[6] = new Variable();
							botanaVars[7] = new Variable();
							// B' - mirror of second focus point
							botanaVars[8] = new Variable();
							botanaVars[9] = new Variable();
							// C' - mirror of point on ellipse
							botanaVars[10] = new Variable();
							botanaVars[11] = new Variable();
						}

						botanaPolynomials = new Polynomial[13];

						Polynomial p1 = new Polynomial(vellipse[0]);
						Polynomial p2 = new Polynomial(vellipse[1]);
						Polynomial a1 = new Polynomial(vellipse[6]);
						Polynomial a2 = new Polynomial(vellipse[7]);
						Polynomial b1 = new Polynomial(vellipse[8]);
						Polynomial b2 = new Polynomial(vellipse[9]);
						Polynomial c1 = new Polynomial(vellipse[10]);
						Polynomial c2 = new Polynomial(vellipse[11]);
						Polynomial m1 = new Polynomial(vP[0]);
						Polynomial m2 = new Polynomial(vP[1]);
						Polynomial p_1 = new Polynomial(botanaVars[0]);
						Polynomial p_2 = new Polynomial(botanaVars[1]);
						Polynomial a_1 = new Polynomial(botanaVars[6]);
						Polynomial a_2 = new Polynomial(botanaVars[7]);
						Polynomial b_1 = new Polynomial(botanaVars[8]);
						Polynomial b_2 = new Polynomial(botanaVars[9]);
						Polynomial c_1 = new Polynomial(botanaVars[10]);
						Polynomial c_2 = new Polynomial(botanaVars[11]);
						Polynomial d1 = new Polynomial(botanaVars[2]);
						Polynomial d2 = new Polynomial(botanaVars[3]);
						Polynomial e1 = new Polynomial(botanaVars[4]);
						Polynomial e2 = new Polynomial(botanaVars[5]);

						// 8 equations for coordinates of mirrored points
						// PM vector = MP' vector
						botanaPolynomials[0] = new Polynomial(2).multiply(m1)
								.subtract(p1).subtract(p_1);
						botanaPolynomials[1] = new Polynomial(2).multiply(m2)
								.subtract(p2).subtract(p_2);

						// AM vector = MA' vector
						botanaPolynomials[2] = new Polynomial(2).multiply(m1)
								.subtract(a1).subtract(a_1);
						botanaPolynomials[3] = new Polynomial(2).multiply(m2)
								.subtract(a2).subtract(a_2);

						// BM vector = MB' vector
						botanaPolynomials[4] = new Polynomial(2).multiply(m1)
								.subtract(b1).subtract(b_1);
						botanaPolynomials[5] = new Polynomial(2).multiply(m2)
								.subtract(b2).subtract(b_2);

						// CM vector = MC' vector
						botanaPolynomials[6] = new Polynomial(2).multiply(m1)
								.subtract(c1).subtract(c_1);
						botanaPolynomials[7] = new Polynomial(2).multiply(m2)
								.subtract(c2).subtract(c_2);

						// 5 equations as definition of ellipse
						// d1+d2 = e1+e2
						botanaPolynomials[8] = d1.add(d2).subtract(e1)
								.subtract(e2);

						// d1^2=Polynomial.sqrDistance(a_1,a_2,c_1,c_2)
						botanaPolynomials[9] = Polynomial.sqrDistance(
								botanaVars[6], botanaVars[7], botanaVars[8],
								botanaVars[9]).subtract(d1.multiply(d1));

						// d2^2=Polynomial.sqrDistance(b_1,b_2,c_1,c_2)
						botanaPolynomials[10] = Polynomial.sqrDistance(
								botanaVars[6], botanaVars[7], botanaVars[8],
								botanaVars[9]).subtract(d2.multiply(d2));

						// e1^2=Polynomial.sqrDistance(a_1,a_2,p_1,p_2)
						botanaPolynomials[11] = Polynomial.sqrDistance(
								botanaVars[6], botanaVars[7], botanaVars[0],
								botanaVars[1]).subtract(e1.multiply(e1));

						// e2^2=Polynomial.sqrDistance(b_1,b_2,p_1,p_2)
						botanaPolynomials[12] = Polynomial.sqrDistance(
								botanaVars[8], botanaVars[9], botanaVars[0],
								botanaVars[1]).subtract(e2.multiply(e2));

					}
					return botanaPolynomials;
				}
				throw new NoSymbolicParametersException();
			}
			// mirror hyperbola about point
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isHyperbola()) {
				GeoConic hyperbola = (GeoConic) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

				if (hyperbola != null && P != null) {
					Variable[] vhyperbola = hyperbola.getBotanaVars(hyperbola);
					Variable[] vP = P.getBotanaVars(P);

					if (botanaVars == null) {
						botanaVars = new Variable[12];
						// P' - mirror of second point at hyperbola
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// auxiliary variables
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
						botanaVars[4] = new Variable();
						botanaVars[5] = new Variable();
						// A' - mirror of first focus point
						botanaVars[6] = new Variable();
						botanaVars[7] = new Variable();
						// B' - mirror of second focus point
						botanaVars[8] = new Variable();
						botanaVars[9] = new Variable();
						// C' - mirror of second point at hyperbola
						botanaVars[10] = new Variable();
						botanaVars[11] = new Variable();
					}

					botanaPolynomials = new Polynomial[13];

					Polynomial p1 = new Polynomial(vhyperbola[0]);
					Polynomial p2 = new Polynomial(vhyperbola[1]);
					Polynomial a1 = new Polynomial(vhyperbola[6]);
					Polynomial a2 = new Polynomial(vhyperbola[7]);
					Polynomial b1 = new Polynomial(vhyperbola[8]);
					Polynomial b2 = new Polynomial(vhyperbola[9]);
					Polynomial c1 = new Polynomial(vhyperbola[10]);
					Polynomial c2 = new Polynomial(vhyperbola[11]);
					Polynomial m1 = new Polynomial(vP[0]);
					Polynomial m2 = new Polynomial(vP[1]);
					Polynomial p_1 = new Polynomial(botanaVars[0]);
					Polynomial p_2 = new Polynomial(botanaVars[1]);
					Polynomial a_1 = new Polynomial(botanaVars[6]);
					Polynomial a_2 = new Polynomial(botanaVars[7]);
					Polynomial b_1 = new Polynomial(botanaVars[8]);
					Polynomial b_2 = new Polynomial(botanaVars[9]);
					Polynomial c_1 = new Polynomial(botanaVars[10]);
					Polynomial c_2 = new Polynomial(botanaVars[11]);
					Polynomial d1 = new Polynomial(botanaVars[2]);
					Polynomial d2 = new Polynomial(botanaVars[3]);
					Polynomial e1 = new Polynomial(botanaVars[4]);
					Polynomial e2 = new Polynomial(botanaVars[5]);

					// 8 equations for mirrored points
					// PM vector = MP' vector
					botanaPolynomials[0] = new Polynomial(2).multiply(m1)
							.subtract(p1).subtract(p_1);
					botanaPolynomials[1] = new Polynomial(2).multiply(m2)
							.subtract(p2).subtract(p_2);

					// AM vector = MA' vector
					botanaPolynomials[2] = new Polynomial(2).multiply(m1)
							.subtract(a1).subtract(a_1);
					botanaPolynomials[3] = new Polynomial(2).multiply(m2)
							.subtract(a2).subtract(a_2);

					// BM vector = MB' vector
					botanaPolynomials[4] = new Polynomial(2).multiply(m1)
							.subtract(b1).subtract(b_1);
					botanaPolynomials[5] = new Polynomial(2).multiply(m2)
							.subtract(b2).subtract(b_2);

					// CM vector = MC' vector
					botanaPolynomials[6] = new Polynomial(2).multiply(m1)
							.subtract(c1).subtract(c_1);
					botanaPolynomials[7] = new Polynomial(2).multiply(m2)
							.subtract(c2).subtract(c_2);

					// 5 equations as definition of hyperbola
					// d1-d2 = e1-e2
					botanaPolynomials[8] = d1.subtract(d2).subtract(e1).add(e2);

					// d1^2=Polynomial.sqrDistance(a_1,a_2,c_1,c_2)
					botanaPolynomials[9] = Polynomial.sqrDistance(
							botanaVars[6], botanaVars[7], botanaVars[10],
							botanaVars[11]).subtract(d1.multiply(d1));

					// d2^2=Polynomial.sqrDistance(b_1,b_2,c_1,c_2)
					botanaPolynomials[10] = Polynomial.sqrDistance(
							botanaVars[8], botanaVars[9], botanaVars[10],
							botanaVars[11]).subtract(d2.multiply(d2));

					// e1^2=Polynomial.sqrDistance(a_1,a_2,p_1,p_2)
					botanaPolynomials[3] = Polynomial.sqrDistance(
							botanaVars[6], botanaVars[7], botanaVars[0],
							botanaVars[1]).subtract(e1.multiply(e1));

					// e2^2=Polynomial.sqrDistance(b_1,b_2,p_1,p_2)
					botanaPolynomials[4] = Polynomial.sqrDistance(
							botanaVars[8], botanaVars[9], botanaVars[0],
							botanaVars[1]).subtract(e2.multiply(e2));

					return botanaPolynomials;
				}
				throw new NoSymbolicParametersException();
			}
			// invalid object to reflect about point
			throw new NoSymbolicParametersException();

		} else if (mirrorCircle instanceof GeoConic) {

			// mirror point about circle
			if (inGeo.isGeoPoint()) {

				GeoPoint P = (GeoPoint) inGeo;
				GeoConic c = (GeoConic) mirrorCircle;

				if (P != null && c != null) {
					Variable[] vP = P.getBotanaVars(P);
					Variable[] vc = c.getBotanaVars(c);

					if (botanaVars == null) {
						botanaVars = new Variable[8];
						// B' - mirror of point
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// B - point to mirror
						botanaVars[2] = vP[0];
						botanaVars[3] = vP[1];
						// O - center of circle
						botanaVars[4] = vc[0];
						botanaVars[5] = vc[1];
						// A - point on circle
						botanaVars[6] = vc[2];
						botanaVars[7] = vc[3];
					}

					botanaPolynomials = new Polynomial[2];

					Polynomial o1 = new Polynomial(vc[0]);
					Polynomial o2 = new Polynomial(vc[1]);
					Polynomial a1 = new Polynomial(vc[2]);
					Polynomial a2 = new Polynomial(vc[3]);
					Polynomial b1 = new Polynomial(vP[0]);
					Polynomial b2 = new Polynomial(vP[1]);
					Polynomial b_1 = new Polynomial(botanaVars[0]);
					Polynomial b_2 = new Polynomial(botanaVars[1]);

					// r^2
					Polynomial oa = (a1.subtract(o1)).multiply(a1.subtract(o1))
							.add((a2.subtract(o2)).multiply(a2.subtract(o2)));
					// (x-x_0)^2 + (y-y_0)^2
					Polynomial denominator = (b1.subtract(o1)).multiply(
							b1.subtract(o1)).add(
							(b2.subtract(o2)).multiply(b2.subtract(o2)));

					// formula for the coordinates of inverse point
					// from: http://mathworld.wolfram.com/Inversion.html
					botanaPolynomials[0] = oa.multiply(b1.subtract(o1)).add(
							(o1.subtract(b_1)).multiply(denominator));

					botanaPolynomials[1] = oa.multiply(b2.subtract(o2)).add(
							(o2.subtract(b_2)).multiply(denominator));

					return botanaPolynomials;
				}
				throw new NoSymbolicParametersException();
			}
			// mirror line about circle
			else if (inGeo.isGeoLine()) {
				App.debug("mirroring line about circle not implemented");
				throw new NoSymbolicParametersException();
			}
			// mirror circle about circle
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isCircle()) {

				GeoConic circle = (GeoConic) inGeo;
				GeoConic mirrorcircle = (GeoConic) mirrorCircle;

				if (circle != null && mirrorcircle != null) {
					Variable[] vcircle = circle.getBotanaVars(circle);
					Variable[] vmirrorcircle = mirrorcircle
							.getBotanaVars(mirrorcircle);

					if (botanaVars == null) {
						botanaVars = new Variable[4];
						// A' - center of mirrored circle
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// B' - mirror of point of circle (B)
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
					}

					botanaPolynomials = new Polynomial[4];

					// (A,B) - circle to mirror
					Polynomial a1 = new Polynomial(vcircle[0]);
					Polynomial a2 = new Polynomial(vcircle[1]);
					Polynomial b1 = new Polynomial(vcircle[2]);
					Polynomial b2 = new Polynomial(vcircle[3]);

					// (O,C) - circle to mirror about
					Polynomial o1 = new Polynomial(vmirrorcircle[0]);
					Polynomial o2 = new Polynomial(vmirrorcircle[1]);
					Polynomial c1 = new Polynomial(vmirrorcircle[2]);
					Polynomial c2 = new Polynomial(vmirrorcircle[3]);

					// (A',B') - mirrored circle
					Polynomial a_1 = new Polynomial(botanaVars[0]);
					Polynomial a_2 = new Polynomial(botanaVars[1]);
					Polynomial b_1 = new Polynomial(botanaVars[2]);
					Polynomial b_2 = new Polynomial(botanaVars[3]);

					// k^2 - circle power of (O,C) circle
					Polynomial oc = (c1.subtract(o1)).multiply(c1.subtract(o1))
							.add((c2.subtract(o2)).multiply(c2.subtract(o2)));

					// a^2 - circle power of (A,B) circle
					Polynomial ab = (b1.subtract(a1)).multiply(b1.subtract(a1))
							.add((b2.subtract(a2)).multiply(b2.subtract(a2)));

					// (x-x_0)^2 + (y-y_0)^2 - a^2
					Polynomial denominator1 = (a1.subtract(o1))
							.multiply(a1.subtract(o1))
							.add((a2.subtract(o2)).multiply(a2.subtract(o2)))
							.subtract(ab);

					// formula for the coordinates of the center of inverse
					// circle
					// from: http://mathworld.wolfram.com/Inversion.html
					botanaPolynomials[0] = oc.multiply(a1.subtract(o1)).add(
							(o1.subtract(a_1)).multiply(denominator1));
					botanaPolynomials[1] = oc.multiply(a2.subtract(o2)).add(
							(o2.subtract(a_2)).multiply(denominator1));

					Polynomial denominator2 = (b1.subtract(o1)).multiply(
							b1.subtract(o1)).add(
							(b2.subtract(o2)).multiply(b2.subtract(o2)));

					// formula for the coordinates of inverse point
					// from: http://mathworld.wolfram.com/Inversion.html
					botanaPolynomials[2] = oc.multiply(b1.subtract(o1)).add(
							(o1.subtract(b_1)).multiply(denominator2));
					botanaPolynomials[3] = oc.multiply(b2.subtract(o2)).add(
							(o2.subtract(b_2)).multiply(denominator2));

					return botanaPolynomials;

				}
				throw new NoSymbolicParametersException();

			} else {
				// invalid object to mirror about circle
				throw new NoSymbolicParametersException();
			}

		} else {
			// invalid object to mirror about
			throw new NoSymbolicParametersException();
		}
	}

	public Variable[] getBotanaVars() {
		return botanaVars;
	}
}
