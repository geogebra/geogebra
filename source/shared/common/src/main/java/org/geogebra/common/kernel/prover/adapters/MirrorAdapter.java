package org.geogebra.common.kernel.prover.adapters;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.debug.Log;

public class MirrorAdapter extends ProverAdapter {

	public PPolynomial[] getBotanaPolynomials(GeoElementND geo, GeoElement inGeo,
			GeoLineND mirrorLine, GeoPointND mirrorPoint,
			GeoConicND mirrorCircle) throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (mirrorLine instanceof GeoLine) {

			GeoPoint P;
			// if we want to mirror a line to a line
			if (inGeo.isGeoLine()) {

				/* It's possible that inGeo already has Botana vars. */
				PVariable[] inGeoVars = ((GeoLine) inGeo).getBotanaVars(inGeo);
				PVariable[] mirrorLineVars = ((GeoLine) mirrorLine)
						.getBotanaVars(mirrorLine);

				if (inGeoVars != null && mirrorLineVars != null) {
					if (botanaVars == null) {
						botanaVars = new PVariable[8];
						// P' - mirror of P
						botanaVars[0] = new PVariable(geo.getKernel());
						botanaVars[1] = new PVariable(geo.getKernel());
						// Q' - mirror of Q
						botanaVars[2] = new PVariable(geo.getKernel());
						botanaVars[3] = new PVariable(geo.getKernel());
						// V1 - auxiliary point
						botanaVars[4] = new PVariable(geo.getKernel());
						botanaVars[5] = new PVariable(geo.getKernel());
						// V2 - auxiliary point
						botanaVars[6] = new PVariable(geo.getKernel());
						botanaVars[7] = new PVariable(geo.getKernel());
					}

					botanaPolynomials = new PPolynomial[8];

					// first we want to mirror P to line l

					PPolynomial p1 = new PPolynomial(inGeoVars[0]);
					PPolynomial p2 = new PPolynomial(inGeoVars[1]);
					PPolynomial v1_1 = new PPolynomial(botanaVars[4]);
					PPolynomial v1_2 = new PPolynomial(botanaVars[5]);
					PPolynomial p_1 = new PPolynomial(botanaVars[0]);
					PPolynomial p_2 = new PPolynomial(botanaVars[1]);

					// PV1 = V1P'
					botanaPolynomials[0] = v1_1.multiply(new PPolynomial(2))
							.subtract(p1).subtract(p_1);
					botanaPolynomials[1] = v1_2.multiply(new PPolynomial(2))
							.subtract(p2).subtract(p_2);

					PVariable[] A = new PVariable[2];
					// A - start point of mirrorLine
					A[0] = mirrorLineVars[0];
					A[1] = mirrorLineVars[1];
					PVariable[] B = new PVariable[2];
					// B - end point of mirrorLine
					B[0] = mirrorLineVars[2];
					B[1] = mirrorLineVars[3];

					// A, V1, B collinear
					botanaPolynomials[2] = PPolynomial.collinear(A[0], A[1],
							botanaVars[4], botanaVars[5], B[0], B[1]);

					// PV1 orthogonal AB
					botanaPolynomials[3] = PPolynomial.perpendicular(
							inGeoVars[0], inGeoVars[1], botanaVars[4],
							botanaVars[5], A[0], A[1], B[0], B[1]);

					// second we want to mirror Q to the mirrorLine

					PPolynomial q1 = new PPolynomial(inGeoVars[2]);
					PPolynomial q2 = new PPolynomial(inGeoVars[3]);
					PPolynomial v2_1 = new PPolynomial(botanaVars[6]);
					PPolynomial v2_2 = new PPolynomial(botanaVars[7]);
					PPolynomial q_1 = new PPolynomial(botanaVars[2]);
					PPolynomial q_2 = new PPolynomial(botanaVars[3]);

					// QV2 = V2Q'
					botanaPolynomials[4] = v2_1.multiply(new PPolynomial(2))
							.subtract(q1).subtract(q_1);
					botanaPolynomials[5] = v2_2.multiply(new PPolynomial(2))
							.subtract(q2).subtract(q_2);

					// A, V2, B collinear
					botanaPolynomials[6] = PPolynomial.collinear(A[0], A[1],
							botanaVars[6], botanaVars[7], B[0], B[1]);

					// QV2 orthogonal AB
					botanaPolynomials[7] = PPolynomial.perpendicular(
							inGeoVars[2], inGeoVars[3], botanaVars[6],
							botanaVars[7], A[0], A[1], B[0], B[1]);

					return botanaPolynomials;
				}
				throw new NoSymbolicParametersException();
			}
			// we want to mirror a point to a line
			else if (inGeo.isGeoPoint()) {
				P = (GeoPoint) inGeo;
				GeoLine l = (GeoLine) mirrorLine;

					PVariable[] vP = P.getBotanaVars(P);
					PVariable[] vL = l.getBotanaVars(l);

					if (botanaVars == null) {
						botanaVars = new PVariable[6];
						// C'
						botanaVars[0] = new PVariable(geo.getKernel());
						botanaVars[1] = new PVariable(geo.getKernel());
						// V
						botanaVars[2] = new PVariable(geo.getKernel());
						botanaVars[3] = new PVariable(geo.getKernel());
						// N
						botanaVars[4] = new PVariable(geo.getKernel());
						botanaVars[5] = new PVariable(geo.getKernel());
					}

					botanaPolynomials = new PPolynomial[6];

					PPolynomial v1 = new PPolynomial(botanaVars[2]);
					PPolynomial v2 = new PPolynomial(botanaVars[3]);
					PPolynomial c1 = new PPolynomial(vP[0]);
					PPolynomial c2 = new PPolynomial(vP[1]);
					PPolynomial c_1 = new PPolynomial(botanaVars[0]);
					PPolynomial c_2 = new PPolynomial(botanaVars[1]);

					// CV = VC'
					botanaPolynomials[0] = v1.multiply(new PPolynomial(2))
							.subtract(c_1).subtract(c1);
					botanaPolynomials[1] = v2.multiply(new PPolynomial(2))
							.subtract(c_2).subtract(c2);

					// points of mirrorLine
					PVariable[] A = new PVariable[2];
					A[0] = vL[0];
					A[1] = vL[1];
					PVariable[] B = new PVariable[2];
					B[0] = vL[2];
					B[1] = vL[3];

					// A,V,B collinear
					botanaPolynomials[2] = PPolynomial.collinear(A[0], A[1],
							B[0], B[1], botanaVars[2], botanaVars[3]);

					PPolynomial a1 = new PPolynomial(A[0]);
					PPolynomial a2 = new PPolynomial(A[1]);
					PPolynomial b1 = new PPolynomial(B[0]);
					PPolynomial b2 = new PPolynomial(B[1]);
					PPolynomial n1 = new PPolynomial(botanaVars[4]);
					PPolynomial n2 = new PPolynomial(botanaVars[5]);

					// CV orthogonal AB
					botanaPolynomials[3] = b1.subtract(a1).add(c2).subtract(n2);
					botanaPolynomials[4] = c1.subtract(b2).add(a2).subtract(n1);

					// C',N,V collinear
					botanaPolynomials[5] = PPolynomial.collinear(botanaVars[0],
							botanaVars[1], botanaVars[2], botanaVars[3],
							botanaVars[4], botanaVars[5]);

					return botanaPolynomials;
			}
			// mirror circle to line
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isCircle()) {
				GeoConic circle = (GeoConic) inGeo;
				GeoLine l = (GeoLine) mirrorLine;

					PVariable[] vCircle = circle.getBotanaVars(circle);
					PVariable[] vl = l.getBotanaVars(l);

					if (botanaVars == null) {
						botanaVars = new PVariable[12];
						// A' - mirror of center
						botanaVars[0] = new PVariable(geo.getKernel());
						botanaVars[1] = new PVariable(geo.getKernel());
						// B' - mirror of point on circle
						botanaVars[2] = new PVariable(geo.getKernel());
						botanaVars[3] = new PVariable(geo.getKernel());
						// V - midpoint of center and mirror of center
						botanaVars[4] = new PVariable(geo.getKernel());
						botanaVars[5] = new PVariable(geo.getKernel());
						// T - midpoint of point on circle and mirror of point
						// on circle
						botanaVars[6] = new PVariable(geo.getKernel());
						botanaVars[7] = new PVariable(geo.getKernel());
						// N1 - AN1 orthogonal CD
						botanaVars[8] = new PVariable(geo.getKernel());
						botanaVars[9] = new PVariable(geo.getKernel());
						// N2 - BN2 orthogonal CD
						botanaVars[10] = new PVariable(geo.getKernel());
						botanaVars[11] = new PVariable(geo.getKernel());
					}

					botanaPolynomials = new PPolynomial[12];

					PPolynomial v1 = new PPolynomial(botanaVars[4]);
					PPolynomial v2 = new PPolynomial(botanaVars[5]);
					PPolynomial a1 = new PPolynomial(vCircle[0]);
					PPolynomial a2 = new PPolynomial(vCircle[1]);
					PPolynomial a_1 = new PPolynomial(botanaVars[0]);
					PPolynomial a_2 = new PPolynomial(botanaVars[1]);

					// AV = VA'
					botanaPolynomials[0] = v1.multiply(new PPolynomial(2))
							.subtract(a_1).subtract(a1);
					botanaPolynomials[1] = v2.multiply(new PPolynomial(2))
							.subtract(a_2).subtract(a2);

					// C, V, D collinear
					botanaPolynomials[2] = PPolynomial.collinear(vl[0], vl[1],
							botanaVars[4], botanaVars[5], vl[2], vl[3]);

					PPolynomial c1 = new PPolynomial(vl[0]);
					PPolynomial c2 = new PPolynomial(vl[1]);
					PPolynomial d1 = new PPolynomial(vl[2]);
					PPolynomial d2 = new PPolynomial(vl[3]);
					PPolynomial n1_1 = new PPolynomial(botanaVars[8]);
					PPolynomial n1_2 = new PPolynomial(botanaVars[9]);

					// AV orthogonal CD
					botanaPolynomials[3] = d1.subtract(c1).add(a2)
							.subtract(n1_2);
					botanaPolynomials[4] = a1.subtract(d2).add(c2)
							.subtract(n1_1);

					// A', V, N1 collinear
					botanaPolynomials[5] = PPolynomial.collinear(botanaVars[0],
							botanaVars[1], botanaVars[4], botanaVars[5],
							botanaVars[8], botanaVars[9]);

					PPolynomial t1 = new PPolynomial(botanaVars[6]);
					PPolynomial t2 = new PPolynomial(botanaVars[7]);
					PPolynomial b1 = new PPolynomial(vCircle[2]);
					PPolynomial b2 = new PPolynomial(vCircle[3]);
					PPolynomial b_1 = new PPolynomial(botanaVars[2]);
					PPolynomial b_2 = new PPolynomial(botanaVars[3]);

					// BT = TB'
					botanaPolynomials[6] = t1.multiply(new PPolynomial(2))
							.subtract(b_1).subtract(b1);
					botanaPolynomials[7] = t2.multiply(new PPolynomial(2))
							.subtract(b_2).subtract(b2);

					// C, T, D collinear
					botanaPolynomials[8] = PPolynomial.collinear(vl[0], vl[1],
							botanaVars[6], botanaVars[7], vl[2], vl[3]);

					PPolynomial n2_1 = new PPolynomial(botanaVars[10]);
					PPolynomial n2_2 = new PPolynomial(botanaVars[11]);

					// BT orthogonal CD
					botanaPolynomials[9] = d1.subtract(c1).add(b2)
							.subtract(n2_2);
					botanaPolynomials[10] = b1.subtract(d2).add(c2)
							.subtract(n2_1);

					// B', T, N2 collinear
					botanaPolynomials[11] = PPolynomial.collinear(botanaVars[1],
							botanaVars[2], botanaVars[6], botanaVars[7],
							botanaVars[10], botanaVars[11]);

					return botanaPolynomials;

			}
			// mirror parabola about line
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isParabola()) {
				GeoConic parabola = (GeoConic) inGeo;
				GeoLine l = (GeoLine) mirrorLine;

					PVariable[] vparabola = parabola.getBotanaVars(parabola);
					PVariable[] vl = l.getBotanaVars(l);

					if (botanaVars == null) {
						botanaVars = new PVariable[22];
						// P' - point at parabola
						botanaVars[0] = new PVariable(geo.getKernel());
						botanaVars[1] = new PVariable(geo.getKernel());
						// T' - projection of P' at directirx
						botanaVars[2] = new PVariable(geo.getKernel());
						botanaVars[3] = new PVariable(geo.getKernel());
						// A' - mirror of star point of directrix
						botanaVars[4] = new PVariable(geo.getKernel());
						botanaVars[5] = new PVariable(geo.getKernel());
						// B' - mirror of end point of directrix
						botanaVars[6] = new PVariable(geo.getKernel());
						botanaVars[7] = new PVariable(geo.getKernel());
						// F' - mirror of focus point
						botanaVars[8] = new PVariable(geo.getKernel());
						botanaVars[9] = new PVariable(geo.getKernel());
						// V1 - midpoint of AA'
						botanaVars[10] = new PVariable(geo.getKernel());
						botanaVars[11] = new PVariable(geo.getKernel());
						// V2 - midpoint of BB'
						botanaVars[12] = new PVariable(geo.getKernel());
						botanaVars[13] = new PVariable(geo.getKernel());
						// V3 - midpoint of CC'
						botanaVars[14] = new PVariable(geo.getKernel());
						botanaVars[15] = new PVariable(geo.getKernel());
						// N1 - AN1 orthogonal M1M2 (mirror line)
						botanaVars[16] = new PVariable(geo.getKernel());
						botanaVars[17] = new PVariable(geo.getKernel());
						// N2 - BN2 orthogonal M1M2
						botanaVars[18] = new PVariable(geo.getKernel());
						botanaVars[19] = new PVariable(geo.getKernel());
						// N3 - CN3 orthogonal M1M2
						botanaVars[20] = new PVariable(geo.getKernel());
						botanaVars[21] = new PVariable(geo.getKernel());
					}

					botanaPolynomials = new PPolynomial[21];

					PPolynomial a1 = new PPolynomial(vparabola[4]);
					PPolynomial a2 = new PPolynomial(vparabola[5]);
					PPolynomial v1_1 = new PPolynomial(botanaVars[10]);
					PPolynomial v1_2 = new PPolynomial(botanaVars[11]);
					PPolynomial a_1 = new PPolynomial(botanaVars[4]);
					PPolynomial a_2 = new PPolynomial(botanaVars[5]);
					PPolynomial n1_1 = new PPolynomial(botanaVars[16]);
					PPolynomial n1_2 = new PPolynomial(botanaVars[17]);
					PPolynomial m1_1 = new PPolynomial(vl[0]);
					PPolynomial m1_2 = new PPolynomial(vl[1]);
					PPolynomial m2_1 = new PPolynomial(vl[2]);
					PPolynomial m2_2 = new PPolynomial(vl[3]);

					// 6 equations to define A'
					// AV1 = V1A'
					botanaPolynomials[0] = new PPolynomial(2).multiply(v1_1)
							.subtract(a1).subtract(a_1);
					botanaPolynomials[1] = new PPolynomial(2).multiply(v1_2)
							.subtract(a2).subtract(a_2);

					// A', V1, N1 collinear
					botanaPolynomials[2] = PPolynomial.collinear(botanaVars[4],
							botanaVars[5], botanaVars[10], botanaVars[11],
							botanaVars[16], botanaVars[17]);

					// M1, V1, M2 collinear
					botanaPolynomials[3] = PPolynomial.collinear(vl[0], vl[1],
							botanaVars[10], botanaVars[11], vl[2], vl[3]);

					// AN1 orthogonal M1M2
					botanaPolynomials[4] = m2_1.subtract(m1_1).add(a2)
							.subtract(n1_2);
					botanaPolynomials[5] = a1.subtract(m2_2).add(m1_2)
							.subtract(n1_1);

					PPolynomial b1 = new PPolynomial(vparabola[6]);
					PPolynomial b2 = new PPolynomial(vparabola[7]);
					PPolynomial v2_1 = new PPolynomial(botanaVars[12]);
					PPolynomial v2_2 = new PPolynomial(botanaVars[13]);
					PPolynomial b_1 = new PPolynomial(botanaVars[6]);
					PPolynomial b_2 = new PPolynomial(botanaVars[7]);
					PPolynomial n2_1 = new PPolynomial(botanaVars[18]);
					PPolynomial n2_2 = new PPolynomial(botanaVars[19]);

					// 6 equations to define B'
					// BV2 = V2B'
					botanaPolynomials[6] = new PPolynomial(2).multiply(v2_1)
							.subtract(b1).subtract(b_1);
					botanaPolynomials[7] = new PPolynomial(2).multiply(v2_2)
							.subtract(b2).subtract(b_2);

					// B', V2, N2 collinear
					botanaPolynomials[8] = PPolynomial.collinear(botanaVars[6],
							botanaVars[7], botanaVars[12], botanaVars[13],
							botanaVars[18], botanaVars[19]);

					// M1, V2, M2 collinear
					botanaPolynomials[9] = PPolynomial.collinear(vl[0], vl[1],
							botanaVars[12], botanaVars[13], vl[2], vl[3]);

					// BN2 orthogonal M1M2
					botanaPolynomials[10] = m2_1.subtract(m1_1).add(b2)
							.subtract(n2_2);
					botanaPolynomials[11] = b1.subtract(m2_2).add(m1_2)
							.subtract(n2_1);

					PPolynomial f1 = new PPolynomial(vparabola[8]);
					PPolynomial f2 = new PPolynomial(vparabola[9]);
					PPolynomial v3_1 = new PPolynomial(botanaVars[14]);
					PPolynomial v3_2 = new PPolynomial(botanaVars[15]);
					PPolynomial f_1 = new PPolynomial(botanaVars[8]);
					PPolynomial f_2 = new PPolynomial(botanaVars[9]);
					PPolynomial n3_1 = new PPolynomial(botanaVars[20]);
					PPolynomial n3_2 = new PPolynomial(botanaVars[21]);

					// 6 equations to define F'
					// FV3 = V3F'
					botanaPolynomials[12] = new PPolynomial(2).multiply(v3_1)
							.subtract(f1).subtract(f_1);
					botanaPolynomials[13] = new PPolynomial(2).multiply(v3_2)
							.subtract(f2).subtract(f_2);

					// F', V3, N3 collinear
					botanaPolynomials[14] = PPolynomial.collinear(botanaVars[8],
							botanaVars[9], botanaVars[14], botanaVars[15],
							botanaVars[20], botanaVars[21]);

					// M1, V3, M2 collinear
					botanaPolynomials[15] = PPolynomial.collinear(vl[0], vl[1],
							botanaVars[14], botanaVars[15], vl[2], vl[3]);

					// FN3 orthogonal M1M2
					botanaPolynomials[16] = m2_1.subtract(m1_1).add(f2)
							.subtract(n3_2);
					botanaPolynomials[17] = f1.subtract(m2_2).add(m1_2)
							.subtract(n3_1);

					// 3 equations to define parabola
					// |F'P'| = |P'T'|
					botanaPolynomials[18] = PPolynomial.equidistant(
							botanaVars[8], botanaVars[9], botanaVars[0],
							botanaVars[1], botanaVars[2], botanaVars[3]);

					// A', T', B' collinear
					botanaPolynomials[19] = PPolynomial.collinear(botanaVars[4],
							botanaVars[5], botanaVars[2], botanaVars[3],
							botanaVars[6], botanaVars[7]);

					// P'T' orthogonal A'B'
					botanaPolynomials[20] = PPolynomial.perpendicular(
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
			// invalid object to reflect about line
			throw new NoSymbolicParametersException();

		}
		// case mirroring GeoElement about point
		else if (mirrorPoint instanceof GeoPoint) {

			// mirror point about point
			if (inGeo.isGeoPoint()) {
				GeoPoint P1 = (GeoPoint) inGeo;
				GeoPoint P2 = (GeoPoint) mirrorPoint;

					PVariable[] vP1 = P1.getBotanaVars(P1);
					PVariable[] vP2 = P2.getBotanaVars(P2);

					if (botanaVars == null) {
						botanaVars = new PVariable[2];
						// A' - mirror of point
						botanaVars[0] = new PVariable(geo.getKernel());
						botanaVars[1] = new PVariable(geo.getKernel());
					}

					botanaPolynomials = new PPolynomial[2];

					PPolynomial a1 = new PPolynomial(vP1[0]);
					PPolynomial a2 = new PPolynomial(vP1[1]);
					PPolynomial b1 = new PPolynomial(vP2[0]);
					PPolynomial b2 = new PPolynomial(vP2[1]);
					PPolynomial a_1 = new PPolynomial(botanaVars[0]);
					PPolynomial a_2 = new PPolynomial(botanaVars[1]);

					// AB vector = BA' vector
					botanaPolynomials[0] = b1.multiply(new PPolynomial(2))
							.subtract(a1).subtract(a_1);
					botanaPolynomials[1] = b2.multiply(new PPolynomial(2))
							.subtract(a2).subtract(a_2);

					return botanaPolynomials;
			}
			// mirror line about point
			else if (inGeo.isGeoLine()) {
				GeoLine l = (GeoLine) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

					PVariable[] vl = l.getBotanaVars(l);
					PVariable[] vP = P.getBotanaVars(P);

					if (botanaVars == null) {
						botanaVars = new PVariable[4];
						// A' - mirror of start point
						botanaVars[0] = new PVariable(geo.getKernel());
						botanaVars[1] = new PVariable(geo.getKernel());
						// B' - mirror of end point
						botanaVars[2] = new PVariable(geo.getKernel());
						botanaVars[3] = new PVariable(geo.getKernel());
					}

					botanaPolynomials = new PPolynomial[4];

					PPolynomial p1 = new PPolynomial(vP[0]);
					PPolynomial p2 = new PPolynomial(vP[1]);
					PPolynomial a1 = new PPolynomial(vl[0]);
					PPolynomial a2 = new PPolynomial(vl[1]);
					PPolynomial a_1 = new PPolynomial(botanaVars[0]);
					PPolynomial a_2 = new PPolynomial(botanaVars[1]);
					PPolynomial b1 = new PPolynomial(vl[2]);
					PPolynomial b2 = new PPolynomial(vl[3]);
					PPolynomial b_1 = new PPolynomial(botanaVars[2]);
					PPolynomial b_2 = new PPolynomial(botanaVars[3]);

					// AP vector = PA' vector
					botanaPolynomials[0] = p1.subtract(a1)
							.subtract(a_1.subtract(p1));
					botanaPolynomials[1] = p2.subtract(a2)
							.subtract(a_2.subtract(p2));

					// BP vector = PB' vector
					botanaPolynomials[2] = p1.subtract(b1)
							.subtract(b_1.subtract(p1));
					botanaPolynomials[3] = p2.subtract(b2)
							.subtract(b_2.subtract(p2));

					return botanaPolynomials;
			}
			// mirror circle about point
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isCircle()) {
				GeoConic circle = (GeoConic) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

					PVariable[] vCircle = circle.getBotanaVars(circle);
					PVariable[] vP = P.getBotanaVars(P);

					if (botanaVars == null) {
						botanaVars = new PVariable[4];
						// A' - mirror of center
						botanaVars[0] = new PVariable(geo.getKernel());
						botanaVars[1] = new PVariable(geo.getKernel());
						// B' - mirror of point on the circle
						botanaVars[2] = new PVariable(geo.getKernel());
						botanaVars[3] = new PVariable(geo.getKernel());
					}

					botanaPolynomials = new PPolynomial[4];

					PPolynomial p1 = new PPolynomial(vP[0]);
					PPolynomial p2 = new PPolynomial(vP[1]);
					PPolynomial a1 = new PPolynomial(vCircle[0]);
					PPolynomial a2 = new PPolynomial(vCircle[1]);
					PPolynomial a_1 = new PPolynomial(botanaVars[0]);
					PPolynomial a_2 = new PPolynomial(botanaVars[1]);
					PPolynomial b1 = new PPolynomial(vCircle[2]);
					PPolynomial b2 = new PPolynomial(vCircle[3]);
					PPolynomial b_1 = new PPolynomial(botanaVars[2]);
					PPolynomial b_2 = new PPolynomial(botanaVars[3]);

					// AP vector = PA' vector
					botanaPolynomials[0] = p1.subtract(a1)
							.subtract(a_1.subtract(p1));
					botanaPolynomials[1] = p2.subtract(a2)
							.subtract(a_2.subtract(p2));

					// BP vector = PB' vector
					botanaPolynomials[2] = p1.subtract(b1)
							.subtract(b_1.subtract(p1));
					botanaPolynomials[3] = p2.subtract(b2)
							.subtract(b_2.subtract(p2));

					return botanaPolynomials;
			}
			// mirror parabola about point
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isParabola()) {

				GeoConic parabola = (GeoConic) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

					PVariable[] vparabola = parabola.getBotanaVars(parabola);
					PVariable[] vP = P.getBotanaVars(P);
					if (botanaVars == null) {
						botanaVars = new PVariable[10];
						// P' - mirror of point on parabola
						botanaVars[0] = new PVariable(geo.getKernel());
						botanaVars[1] = new PVariable(geo.getKernel());
						// T' - mirror of projection point of P' at A'B'
						botanaVars[2] = new PVariable(geo.getKernel());
						botanaVars[3] = new PVariable(geo.getKernel());
						// A' - mirror of start point of directrix
						botanaVars[4] = new PVariable(geo.getKernel());
						botanaVars[5] = new PVariable(geo.getKernel());
						// B' - mirror of end point of directrix
						botanaVars[6] = new PVariable(geo.getKernel());
						botanaVars[7] = new PVariable(geo.getKernel());
						// F' - mirror of focus point
						botanaVars[8] = new PVariable(geo.getKernel());
						botanaVars[9] = new PVariable(geo.getKernel());
					}

					botanaPolynomials = new PPolynomial[13];

					PPolynomial p1 = new PPolynomial(vparabola[0]);
					PPolynomial p2 = new PPolynomial(vparabola[1]);
					PPolynomial t1 = new PPolynomial(vparabola[2]);
					PPolynomial t2 = new PPolynomial(vparabola[3]);
					PPolynomial a1 = new PPolynomial(vparabola[4]);
					PPolynomial a2 = new PPolynomial(vparabola[5]);
					PPolynomial b1 = new PPolynomial(vparabola[6]);
					PPolynomial b2 = new PPolynomial(vparabola[7]);
					PPolynomial f1 = new PPolynomial(vparabola[8]);
					PPolynomial f2 = new PPolynomial(vparabola[9]);
					PPolynomial p_1 = new PPolynomial(botanaVars[0]);
					PPolynomial p_2 = new PPolynomial(botanaVars[1]);
					PPolynomial t_1 = new PPolynomial(botanaVars[2]);
					PPolynomial t_2 = new PPolynomial(botanaVars[3]);
					PPolynomial a_1 = new PPolynomial(botanaVars[4]);
					PPolynomial a_2 = new PPolynomial(botanaVars[5]);
					PPolynomial b_1 = new PPolynomial(botanaVars[6]);
					PPolynomial b_2 = new PPolynomial(botanaVars[7]);
					PPolynomial f_1 = new PPolynomial(botanaVars[8]);
					PPolynomial f_2 = new PPolynomial(botanaVars[9]);
					PPolynomial m1 = new PPolynomial(vP[0]);
					PPolynomial m2 = new PPolynomial(vP[1]);

					// 10 equations for coordinates of mirrored points
					// PM vector = MP' vector
					botanaPolynomials[0] = new PPolynomial(2).multiply(m1)
							.subtract(p1).subtract(p_1);
					botanaPolynomials[1] = new PPolynomial(2).multiply(m2)
							.subtract(p2).subtract(p_2);

					// TM vector = MT' vector
					botanaPolynomials[2] = new PPolynomial(2).multiply(m1)
							.subtract(t1).subtract(t_1);
					botanaPolynomials[3] = new PPolynomial(2).multiply(m2)
							.subtract(t2).subtract(t_2);

					// AM vector = MA' vector
					botanaPolynomials[4] = new PPolynomial(2).multiply(m1)
							.subtract(a1).subtract(a_1);
					botanaPolynomials[5] = new PPolynomial(2).multiply(m2)
							.subtract(a2).subtract(a_2);

					// BM vector = MB' vector
					botanaPolynomials[6] = new PPolynomial(2).multiply(m1)
							.subtract(b1).subtract(b_1);
					botanaPolynomials[7] = new PPolynomial(2).multiply(m2)
							.subtract(b2).subtract(b_2);

					// FM vector = MF' vector
					botanaPolynomials[8] = new PPolynomial(2).multiply(m1)
							.subtract(f1).subtract(f_1);
					botanaPolynomials[9] = new PPolynomial(2).multiply(m2)
							.subtract(f2).subtract(f_2);

					// 3 equations as definition of mirrored parabola
					// |F'P'| = |P'T'|
					botanaPolynomials[10] = PPolynomial.equidistant(
							botanaVars[8], botanaVars[9], botanaVars[0],
							botanaVars[1], botanaVars[2], botanaVars[3]);

					// A',T',B' collinear
					botanaPolynomials[11] = PPolynomial.collinear(botanaVars[4],
							botanaVars[5], botanaVars[2], botanaVars[3],
							botanaVars[6], botanaVars[7]);

					// P'T' orthogonal A'B'
					botanaPolynomials[12] = PPolynomial.perpendicular(
							botanaVars[0], botanaVars[1], botanaVars[2],
							botanaVars[3], botanaVars[4], botanaVars[5],
							botanaVars[6], botanaVars[7]);

					return botanaPolynomials;
			}
			// mirror ellipse about point
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isEllipse()) {
				GeoConic ellipse = (GeoConic) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

					PVariable[] vellipse = ellipse.getBotanaVars(ellipse);
					PVariable[] vP = P.getBotanaVars(P);

					// the two focus points are the same
					if (vellipse[6] == vellipse[8]
							&& vellipse[7] == vellipse[9]) {
						// handle ellipse as circle
						if (botanaVars == null) {
							botanaVars = new PVariable[4];
							// A' - mirror of the center point
							botanaVars[0] = new PVariable(geo.getKernel());
							botanaVars[1] = new PVariable(geo.getKernel());
							// C' - mirror of the point at circle
							botanaVars[2] = new PVariable(geo.getKernel());
							botanaVars[3] = new PVariable(geo.getKernel());
						}

						botanaPolynomials = new PPolynomial[4];

						PPolynomial a1 = new PPolynomial(vellipse[0]);
						PPolynomial a2 = new PPolynomial(vellipse[1]);
						PPolynomial c1 = new PPolynomial(vellipse[2]);
						PPolynomial c2 = new PPolynomial(vellipse[3]);
						PPolynomial p1 = new PPolynomial(vP[0]);
						PPolynomial p2 = new PPolynomial(vP[1]);
						PPolynomial a_1 = new PPolynomial(botanaVars[0]);
						PPolynomial a_2 = new PPolynomial(botanaVars[1]);
						PPolynomial c_1 = new PPolynomial(botanaVars[2]);
						PPolynomial c_2 = new PPolynomial(botanaVars[3]);

						// AP vector = PA' vector
						botanaPolynomials[0] = new PPolynomial(2).multiply(p1)
								.subtract(a1).subtract(a_1);
						botanaPolynomials[1] = new PPolynomial(2).multiply(p2)
								.subtract(a2).subtract(a_2);

						// CP vector = PC' vector
						botanaPolynomials[2] = new PPolynomial(2).multiply(p1)
								.subtract(c1).subtract(c_1);
						botanaPolynomials[3] = new PPolynomial(2).multiply(p2)
								.subtract(c2).subtract(c_2);

					} else {
						if (botanaVars == null) {
							botanaVars = new PVariable[12];
							// P' - mirror of second point on ellipse
							botanaVars[0] = new PVariable(geo.getKernel());
							botanaVars[1] = new PVariable(geo.getKernel());
							// auxiliary variables
							botanaVars[2] = new PVariable(geo.getKernel());
							botanaVars[3] = new PVariable(geo.getKernel());
							botanaVars[4] = new PVariable(geo.getKernel());
							botanaVars[5] = new PVariable(geo.getKernel());
							// A' - mirror of first focus point
							botanaVars[6] = new PVariable(geo.getKernel());
							botanaVars[7] = new PVariable(geo.getKernel());
							// B' - mirror of second focus point
							botanaVars[8] = new PVariable(geo.getKernel());
							botanaVars[9] = new PVariable(geo.getKernel());
							// C' - mirror of point on ellipse
							botanaVars[10] = new PVariable(geo.getKernel());
							botanaVars[11] = new PVariable(geo.getKernel());
						}

						botanaPolynomials = new PPolynomial[13];

						PPolynomial p1 = new PPolynomial(vellipse[0]);
						PPolynomial p2 = new PPolynomial(vellipse[1]);
						PPolynomial a1 = new PPolynomial(vellipse[6]);
						PPolynomial a2 = new PPolynomial(vellipse[7]);
						PPolynomial b1 = new PPolynomial(vellipse[8]);
						PPolynomial b2 = new PPolynomial(vellipse[9]);
						PPolynomial c1 = new PPolynomial(vellipse[10]);
						PPolynomial c2 = new PPolynomial(vellipse[11]);
						PPolynomial m1 = new PPolynomial(vP[0]);
						PPolynomial m2 = new PPolynomial(vP[1]);
						PPolynomial p_1 = new PPolynomial(botanaVars[0]);
						PPolynomial p_2 = new PPolynomial(botanaVars[1]);
						PPolynomial a_1 = new PPolynomial(botanaVars[6]);
						PPolynomial a_2 = new PPolynomial(botanaVars[7]);
						PPolynomial b_1 = new PPolynomial(botanaVars[8]);
						PPolynomial b_2 = new PPolynomial(botanaVars[9]);
						PPolynomial c_1 = new PPolynomial(botanaVars[10]);
						PPolynomial c_2 = new PPolynomial(botanaVars[11]);
						PPolynomial d1 = new PPolynomial(botanaVars[2]);
						PPolynomial d2 = new PPolynomial(botanaVars[3]);
						PPolynomial e1 = new PPolynomial(botanaVars[4]);
						PPolynomial e2 = new PPolynomial(botanaVars[5]);

						// 8 equations for coordinates of mirrored points
						// PM vector = MP' vector
						botanaPolynomials[0] = new PPolynomial(2).multiply(m1)
								.subtract(p1).subtract(p_1);
						botanaPolynomials[1] = new PPolynomial(2).multiply(m2)
								.subtract(p2).subtract(p_2);

						// AM vector = MA' vector
						botanaPolynomials[2] = new PPolynomial(2).multiply(m1)
								.subtract(a1).subtract(a_1);
						botanaPolynomials[3] = new PPolynomial(2).multiply(m2)
								.subtract(a2).subtract(a_2);

						// BM vector = MB' vector
						botanaPolynomials[4] = new PPolynomial(2).multiply(m1)
								.subtract(b1).subtract(b_1);
						botanaPolynomials[5] = new PPolynomial(2).multiply(m2)
								.subtract(b2).subtract(b_2);

						// CM vector = MC' vector
						botanaPolynomials[6] = new PPolynomial(2).multiply(m1)
								.subtract(c1).subtract(c_1);
						botanaPolynomials[7] = new PPolynomial(2).multiply(m2)
								.subtract(c2).subtract(c_2);

						// 5 equations as definition of ellipse
						// d1+d2 = e1+e2
						botanaPolynomials[8] = d1.add(d2).subtract(e1)
								.subtract(e2);

						// d1^2=Polynomial.sqrDistance(a_1,a_2,c_1,c_2)
						botanaPolynomials[9] = PPolynomial
								.sqrDistance(botanaVars[6], botanaVars[7],
										botanaVars[8], botanaVars[9])
								.subtract(d1.multiply(d1));

						// d2^2=Polynomial.sqrDistance(b_1,b_2,c_1,c_2)
						botanaPolynomials[10] = PPolynomial
								.sqrDistance(botanaVars[6], botanaVars[7],
										botanaVars[8], botanaVars[9])
								.subtract(d2.multiply(d2));

						// e1^2=Polynomial.sqrDistance(a_1,a_2,p_1,p_2)
						botanaPolynomials[11] = PPolynomial
								.sqrDistance(botanaVars[6], botanaVars[7],
										botanaVars[0], botanaVars[1])
								.subtract(e1.multiply(e1));

						// e2^2=Polynomial.sqrDistance(b_1,b_2,p_1,p_2)
						botanaPolynomials[12] = PPolynomial
								.sqrDistance(botanaVars[8], botanaVars[9],
										botanaVars[0], botanaVars[1])
								.subtract(e2.multiply(e2));

					}
					return botanaPolynomials;
			}
			// mirror hyperbola about point
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isHyperbola()) {
				GeoConic hyperbola = (GeoConic) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

					PVariable[] vhyperbola = hyperbola.getBotanaVars(hyperbola);
					PVariable[] vP = P.getBotanaVars(P);

					if (botanaVars == null) {
						botanaVars = new PVariable[12];
						// P' - mirror of second point at hyperbola
						botanaVars[0] = new PVariable(geo.getKernel());
						botanaVars[1] = new PVariable(geo.getKernel());
						// auxiliary variables
						botanaVars[2] = new PVariable(geo.getKernel());
						botanaVars[3] = new PVariable(geo.getKernel());
						botanaVars[4] = new PVariable(geo.getKernel());
						botanaVars[5] = new PVariable(geo.getKernel());
						// A' - mirror of first focus point
						botanaVars[6] = new PVariable(geo.getKernel());
						botanaVars[7] = new PVariable(geo.getKernel());
						// B' - mirror of second focus point
						botanaVars[8] = new PVariable(geo.getKernel());
						botanaVars[9] = new PVariable(geo.getKernel());
						// C' - mirror of second point at hyperbola
						botanaVars[10] = new PVariable(geo.getKernel());
						botanaVars[11] = new PVariable(geo.getKernel());
					}

					botanaPolynomials = new PPolynomial[13];

					PPolynomial p1 = new PPolynomial(vhyperbola[0]);
					PPolynomial p2 = new PPolynomial(vhyperbola[1]);
					PPolynomial a1 = new PPolynomial(vhyperbola[6]);
					PPolynomial a2 = new PPolynomial(vhyperbola[7]);
					PPolynomial b1 = new PPolynomial(vhyperbola[8]);
					PPolynomial b2 = new PPolynomial(vhyperbola[9]);
					PPolynomial c1 = new PPolynomial(vhyperbola[10]);
					PPolynomial c2 = new PPolynomial(vhyperbola[11]);
					PPolynomial m1 = new PPolynomial(vP[0]);
					PPolynomial m2 = new PPolynomial(vP[1]);
					PPolynomial p_1 = new PPolynomial(botanaVars[0]);
					PPolynomial p_2 = new PPolynomial(botanaVars[1]);
					PPolynomial a_1 = new PPolynomial(botanaVars[6]);
					PPolynomial a_2 = new PPolynomial(botanaVars[7]);
					PPolynomial b_1 = new PPolynomial(botanaVars[8]);
					PPolynomial b_2 = new PPolynomial(botanaVars[9]);
					PPolynomial c_1 = new PPolynomial(botanaVars[10]);
					PPolynomial c_2 = new PPolynomial(botanaVars[11]);
					PPolynomial d1 = new PPolynomial(botanaVars[2]);
					PPolynomial d2 = new PPolynomial(botanaVars[3]);
					PPolynomial e1 = new PPolynomial(botanaVars[4]);
					PPolynomial e2 = new PPolynomial(botanaVars[5]);

					// 8 equations for mirrored points
					// PM vector = MP' vector
					botanaPolynomials[0] = new PPolynomial(2).multiply(m1)
							.subtract(p1).subtract(p_1);
					botanaPolynomials[1] = new PPolynomial(2).multiply(m2)
							.subtract(p2).subtract(p_2);

					// AM vector = MA' vector
					botanaPolynomials[2] = new PPolynomial(2).multiply(m1)
							.subtract(a1).subtract(a_1);
					botanaPolynomials[3] = new PPolynomial(2).multiply(m2)
							.subtract(a2).subtract(a_2);

					// BM vector = MB' vector
					botanaPolynomials[4] = new PPolynomial(2).multiply(m1)
							.subtract(b1).subtract(b_1);
					botanaPolynomials[5] = new PPolynomial(2).multiply(m2)
							.subtract(b2).subtract(b_2);

					// CM vector = MC' vector
					botanaPolynomials[6] = new PPolynomial(2).multiply(m1)
							.subtract(c1).subtract(c_1);
					botanaPolynomials[7] = new PPolynomial(2).multiply(m2)
							.subtract(c2).subtract(c_2);

					// 5 equations as definition of hyperbola
					// d1-d2 = e1-e2
					botanaPolynomials[8] = d1.subtract(d2).subtract(e1).add(e2);

					// d1^2=Polynomial.sqrDistance(a_1,a_2,c_1,c_2)
					botanaPolynomials[9] = PPolynomial
							.sqrDistance(botanaVars[6], botanaVars[7],
									botanaVars[10], botanaVars[11])
							.subtract(d1.multiply(d1));

					// d2^2=Polynomial.sqrDistance(b_1,b_2,c_1,c_2)
					botanaPolynomials[10] = PPolynomial
							.sqrDistance(botanaVars[8], botanaVars[9],
									botanaVars[10], botanaVars[11])
							.subtract(d2.multiply(d2));

					// e1^2=Polynomial.sqrDistance(a_1,a_2,p_1,p_2)
					botanaPolynomials[3] = PPolynomial
							.sqrDistance(botanaVars[6], botanaVars[7],
									botanaVars[0], botanaVars[1])
							.subtract(e1.multiply(e1));

					// e2^2=Polynomial.sqrDistance(b_1,b_2,p_1,p_2)
					botanaPolynomials[4] = PPolynomial
							.sqrDistance(botanaVars[8], botanaVars[9],
									botanaVars[0], botanaVars[1])
							.subtract(e2.multiply(e2));

					return botanaPolynomials;
			}
			// invalid object to reflect about point
			throw new NoSymbolicParametersException();

		} else if (mirrorCircle instanceof GeoConic) {

			// mirror point about circle
			if (inGeo.isGeoPoint()) {

				GeoPoint P = (GeoPoint) inGeo;
				GeoConic c = (GeoConic) mirrorCircle;

					PVariable[] vP = P.getBotanaVars(P);
					PVariable[] vc = c.getBotanaVars(c);

					if (botanaVars == null) {
						botanaVars = new PVariable[8];
						// B' - mirror of point
						botanaVars[0] = new PVariable(geo.getKernel());
						botanaVars[1] = new PVariable(geo.getKernel());
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

					botanaPolynomials = new PPolynomial[2];

					PPolynomial o1 = new PPolynomial(vc[0]);
					PPolynomial o2 = new PPolynomial(vc[1]);
					PPolynomial a1 = new PPolynomial(vc[2]);
					PPolynomial a2 = new PPolynomial(vc[3]);
					PPolynomial b1 = new PPolynomial(vP[0]);
					PPolynomial b2 = new PPolynomial(vP[1]);
					PPolynomial b_1 = new PPolynomial(botanaVars[0]);
					PPolynomial b_2 = new PPolynomial(botanaVars[1]);

					// r^2
					PPolynomial oa = (a1.subtract(o1)).multiply(a1.subtract(o1))
							.add((a2.subtract(o2)).multiply(a2.subtract(o2)));
					// (x-x_0)^2 + (y-y_0)^2
					PPolynomial denominator = (b1.subtract(o1))
							.multiply(b1.subtract(o1))
							.add((b2.subtract(o2)).multiply(b2.subtract(o2)));

					// formula for the coordinates of inverse point
					// from: http://mathworld.wolfram.com/Inversion.html
					botanaPolynomials[0] = oa.multiply(b1.subtract(o1))
							.add((o1.subtract(b_1)).multiply(denominator));

					botanaPolynomials[1] = oa.multiply(b2.subtract(o2))
							.add((o2.subtract(b_2)).multiply(denominator));

					return botanaPolynomials;
			}
			// mirror line about circle
			else if (inGeo.isGeoLine()) {
				Log.debug("mirroring line about circle not implemented");
				throw new NoSymbolicParametersException();
			}
			// mirror circle about circle
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isCircle()) {

				GeoConic circle = (GeoConic) inGeo;
				GeoConic mirrorcircle = (GeoConic) mirrorCircle;

					PVariable[] vcircle = circle.getBotanaVars(circle);
					PVariable[] vmirrorcircle = mirrorcircle
							.getBotanaVars(mirrorcircle);

					if (botanaVars == null) {
						botanaVars = new PVariable[4];
						// A' - center of mirrored circle
						botanaVars[0] = new PVariable(geo.getKernel());
						botanaVars[1] = new PVariable(geo.getKernel());
						// B' - mirror of point of circle (B)
						botanaVars[2] = new PVariable(geo.getKernel());
						botanaVars[3] = new PVariable(geo.getKernel());
					}

					botanaPolynomials = new PPolynomial[4];

					// (A,B) - circle to mirror
					PPolynomial a1 = new PPolynomial(vcircle[0]);
					PPolynomial a2 = new PPolynomial(vcircle[1]);
					PPolynomial b1 = new PPolynomial(vcircle[2]);
					PPolynomial b2 = new PPolynomial(vcircle[3]);

					// (O,C) - circle to mirror about
					PPolynomial o1 = new PPolynomial(vmirrorcircle[0]);
					PPolynomial o2 = new PPolynomial(vmirrorcircle[1]);
					PPolynomial c1 = new PPolynomial(vmirrorcircle[2]);
					PPolynomial c2 = new PPolynomial(vmirrorcircle[3]);

					// (A',B') - mirrored circle
					PPolynomial a_1 = new PPolynomial(botanaVars[0]);
					PPolynomial a_2 = new PPolynomial(botanaVars[1]);
					PPolynomial b_1 = new PPolynomial(botanaVars[2]);
					PPolynomial b_2 = new PPolynomial(botanaVars[3]);

					// k^2 - circle power of (O,C) circle
					PPolynomial oc = (c1.subtract(o1)).multiply(c1.subtract(o1))
							.add((c2.subtract(o2)).multiply(c2.subtract(o2)));

					// a^2 - circle power of (A,B) circle
					PPolynomial ab = (b1.subtract(a1)).multiply(b1.subtract(a1))
							.add((b2.subtract(a2)).multiply(b2.subtract(a2)));

					// (x-x_0)^2 + (y-y_0)^2 - a^2
					PPolynomial denominator1 = (a1.subtract(o1))
							.multiply(a1.subtract(o1))
							.add((a2.subtract(o2)).multiply(a2.subtract(o2)))
							.subtract(ab);

					// formula for the coordinates of the center of inverse
					// circle
					// from: http://mathworld.wolfram.com/Inversion.html
					botanaPolynomials[0] = oc.multiply(a1.subtract(o1))
							.add((o1.subtract(a_1)).multiply(denominator1));
					botanaPolynomials[1] = oc.multiply(a2.subtract(o2))
							.add((o2.subtract(a_2)).multiply(denominator1));

					PPolynomial denominator2 = (b1.subtract(o1))
							.multiply(b1.subtract(o1))
							.add((b2.subtract(o2)).multiply(b2.subtract(o2)));

					// formula for the coordinates of inverse point
					// from: http://mathworld.wolfram.com/Inversion.html
					botanaPolynomials[2] = oc.multiply(b1.subtract(o1))
							.add((o1.subtract(b_1)).multiply(denominator2));
					botanaPolynomials[3] = oc.multiply(b2.subtract(o2))
							.add((o2.subtract(b_2)).multiply(denominator2));

					return botanaPolynomials;

			} else {
				// invalid object to mirror about circle
				throw new NoSymbolicParametersException();
			}

		} else {
			// invalid object to mirror about
			throw new NoSymbolicParametersException();
		}
	}
}
