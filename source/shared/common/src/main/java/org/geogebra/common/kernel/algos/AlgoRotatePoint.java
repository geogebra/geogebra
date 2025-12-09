/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.Rotatable;
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
public class AlgoRotatePoint extends AlgoTransformation
		implements SymbolicParametersBotanaAlgo {

	private GeoPointND Q;
	private Rotatable out;
	private GeoNumberValue angle;

	private PVariable[] botanaVars;
	private PPolynomial[] botanaPolynomials;

	/**
	 * Creates new unlabeled point rotation algo
	 * 
	 * @param cons
	 *            construction
	 * @param A
	 *            rotated geo
	 * @param angle
	 *            angle
	 * @param Q
	 *            rotation center
	 */
	public AlgoRotatePoint(Construction cons, GeoElement A,
			GeoNumberValue angle, GeoPointND Q) {
		super(cons);
		this.angle = angle;
		this.Q = Q;

		inGeo = A;

		outGeo = getResultTemplate(inGeo);
		if (outGeo instanceof Rotatable) {
			out = (Rotatable) outGeo;
		}

		setInputOutput();
		compute();
		if (inGeo.isGeoFunction()) {
			cons.registerEuclidianViewCE(this);
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.Rotate;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ROTATE_BY_ANGLE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = inGeo;
		input[1] = angle.toGeoElement();
		input[2] = (GeoElement) Q;

		setOnlyOutput(outGeo);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the rotated point
	 * 
	 * @return rotated point
	 */
	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	// calc rotated point
	@Override
	public final void compute() {
		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}
		if (inGeo instanceof GeoFunction) {
			((GeoFunction) inGeo)
					.toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else {
			setOutGeo();
		}
		if (!outGeo.isDefined()) {
			return;
		}
		out.rotate(angle, Q);
		if (inGeo.isLimitedPath()) {
			this.transformLimitedPath(inGeo, outGeo);
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("ARotatedByAngleB",
				"%0 rotated by angle %1", inGeo.getLabel(tpl),
				angle.getLabel(tpl));
	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList)
				&& (outGeo instanceof Rotatable)) {
			out = (Rotatable) outGeo;
		}
	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if (geo instanceof GeoFunction) {
			return new GeoCurveCartesian(cons);
		}
		return super.getResultTemplate(geo);
	}

	@Override
	public double getAreaScaleFactor() {
		return 1;
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {

		/*
		 * This polynomial cannot be cached, because the polynomial depends on
		 * the rotation angle. If the user changes the angle, the polynomial
		 * should be recomputed. Currently we cannot force recomputing a Botana
		 * polynomial externally, thus here we simply recompute the polynomial
		 * each time it is used. TODO: Consider caching the angle here (but not
		 * the poly).
		 */

		// point to rotate
		GeoPoint A = (GeoPoint) Q;
		// point around the rotation is processed
		GeoPoint B = (GeoPoint) input[0];

		if (A != null && B != null) {
			PVariable[] vA = A.getBotanaVars(A);
			PVariable[] vB = B.getBotanaVars(B);

			if (botanaVars == null) {
				botanaVars = new PVariable[8];
				// A' - rotation point
				botanaVars[0] = new PVariable(kernel);
				botanaVars[1] = new PVariable(kernel);
				// A - point around the rotation is processed
				botanaVars[2] = vA[0];
				botanaVars[3] = vA[1];
				// B - point to rotate
				botanaVars[4] = vB[0];
				botanaVars[5] = vB[1];
				// t1 = sqrt(3)
				botanaVars[6] = new PVariable(kernel);
				// t2 = sqrt(2)
				botanaVars[7] = new PVariable(kernel);
			}

			/*
			 * Currently some typical angles are implemented. 15/75/105/165
			 * degrees could also be easily done. TODO: See
			 * https://en.wikipedia.org/wiki/
			 * Trigonometric_constants_expressed_in_real_radicals for a list of
			 * possible angles with algebraic sin/cos values.
			 * 
			 * In many cases we cannot distinguish between some directions, that
			 * is, e.g. +60 and -60 are the same. See Zoltan's diss, p. 92 for
			 * some basic descriptions why. (The full details are not disclosed
			 * there but here in the comments below!)
			 * 
			 * Note that in the non-distinguishable cases symmetry is not always
			 * axial. For example, +45 and -135 are paired, because sin(45) and
			 * cos(45) are simultaneously sqrt(2)/2=t. In this case we use
			 * 2*t^2=1 and by solving (x,y)=(t,t) we get two points on the line
			 * y=x (point symmetry, center in the origin). The same idea for +60
			 * and -60 is a bit different, here sin(60)=sqrt(3)/2=t and
			 * cos(60)=1/2, and we use 4*t^2=3, by solving (x,y)=(1/2,t) which
			 * yields points being symmetrical axially (the axis is the x-axis).
			 * TODO: try to generalize this idea.
			 * 
			 * Giac can actually compute e.g. cos(pi/10)=sqrt(2*sqrt(5)+10)/4
			 * and hopefully also a minimal polynomial can be computed for this.
			 */
			double angleDoubleVal = angle.getDouble();
			double angleDoubleValDeg = angleDoubleVal / Math.PI * 180;
			int angleValDeg = (int) angleDoubleValDeg;
			if (!DoubleUtil.isInteger(angleDoubleValDeg)) {
				// unhandled angle, not an integer degree
				throw new NoSymbolicParametersException();
			}

			PPolynomial a1 = new PPolynomial(vA[0]);
			PPolynomial a2 = new PPolynomial(vA[1]);
			PPolynomial b1 = new PPolynomial(vB[0]);
			PPolynomial b2 = new PPolynomial(vB[1]);
			PPolynomial a_1 = new PPolynomial(botanaVars[0]);
			PPolynomial a_2 = new PPolynomial(botanaVars[1]);
			PPolynomial t1 = new PPolynomial(botanaVars[6]);
			PPolynomial t2 = new PPolynomial(botanaVars[7]);

			angleValDeg %= 360;
			if (angleValDeg < 0) {
				angleValDeg += 360; // be non-negative
			}

			// rotate by 0 degrees
			if (angleValDeg == 0) {
				botanaPolynomials = new PPolynomial[2];
				botanaPolynomials[0] = a_1.subtract(a1).subtract(b1).add(a1);
				botanaPolynomials[1] = a_2.subtract(a2).subtract(b2).add(a2);
				return botanaPolynomials;
			}
			// rotate by 180 or -180 degrees
			else if (angleValDeg == 180) {
				botanaPolynomials = new PPolynomial[2];
				botanaPolynomials[0] = a_1.subtract(a1).add(b1).subtract(a1);
				botanaPolynomials[1] = a_2.subtract(a2).add(b2).subtract(a2);
				return botanaPolynomials;
			}
			// rotate by 90 degrees
			else if (angleValDeg == 90) {
				botanaPolynomials = new PPolynomial[2];
				botanaPolynomials[0] = a_1.subtract(a1).add(b2).subtract(a2);
				botanaPolynomials[1] = a_2.subtract(a2).subtract(b1).add(a1);
				return botanaPolynomials;
			}
			// rotate by -90 degrees
			else if (angleValDeg == 270) {
				botanaPolynomials = new PPolynomial[2];
				botanaPolynomials[0] = a_1.subtract(a1).subtract(b2).add(a2);
				botanaPolynomials[1] = a_2.subtract(a2).add(b1).subtract(a1);
				return botanaPolynomials;
			}
			/*
			 * TODO: Many parts of the following could be handled at the same
			 * time.
			 */
			// rotate by 30 or 150 degrees
			else if (angleValDeg == 30 || angleValDeg == 150) {
				botanaPolynomials = new PPolynomial[3];
				botanaPolynomials[0] = t1.multiply(t1)
						.subtract(new PPolynomial(3));
				PPolynomial p1 = new PPolynomial(2).multiply(a_1)
						.subtract(new PPolynomial(2).multiply(a1)).add(b2)
						.subtract(a2);
				PPolynomial p2 = b1.subtract(a1);
				PPolynomial p3 = t1.multiply(p2);
				botanaPolynomials[1] = p1.subtract(p3);
				PPolynomial p4 = new PPolynomial(2).multiply(a_2)
						.subtract(new PPolynomial(2).multiply(a2)).subtract(b1)
						.add(a1);
				PPolynomial p5 = b2.subtract(a2);
				PPolynomial p6 = t1.multiply(p5);
				botanaPolynomials[2] = p4.subtract(p6);
				return botanaPolynomials;
			}
			// rotate by -30 or -150 degrees
			else if (angleValDeg == 330 || angleValDeg == 210) {
				botanaPolynomials = new PPolynomial[3];
				botanaPolynomials[0] = t1.multiply(t1)
						.subtract(new PPolynomial(3));
				PPolynomial p1 = new PPolynomial(2).multiply(a_1)
						.subtract(new PPolynomial(2).multiply(a1)).subtract(b2)
						.add(a2);
				PPolynomial p2 = b1.subtract(a1);
				PPolynomial p3 = t1.multiply(p2);
				botanaPolynomials[1] = p1.subtract(p3);
				PPolynomial p4 = new PPolynomial(2).multiply(a_2)
						.subtract(new PPolynomial(2).multiply(a2)).add(b1)
						.subtract(a1);
				PPolynomial p5 = b2.subtract(a2);
				PPolynomial p6 = t1.multiply(p5);
				botanaPolynomials[2] = p4.subtract(p6);
				return botanaPolynomials;
			}
			// rotate by -45 or 135 degrees
			else if (angleValDeg == 315 || angleValDeg == 135) {
				botanaPolynomials = new PPolynomial[3];
				botanaPolynomials[0] = t2.multiply(t2)
						.subtract(new PPolynomial(2));
				PPolynomial p1 = new PPolynomial(2).multiply(a_1)
						.subtract(new PPolynomial(2).multiply(a1));
				PPolynomial p2 = b1.subtract(a1).add(b2).subtract(a2);
				botanaPolynomials[1] = p1.subtract(t2.multiply(p2));
				PPolynomial p3 = new PPolynomial(2).multiply(a_2)
						.subtract(new PPolynomial(2).multiply(a2));
				PPolynomial p4 = b2.subtract(a2).subtract(b1).add(a1);
				botanaPolynomials[2] = p3.subtract(t2.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by 45 or -135 degrees
			else if (angleValDeg == 45 || angleValDeg == 225) {
				botanaPolynomials = new PPolynomial[3];
				botanaPolynomials[0] = t2.multiply(t2)
						.subtract(new PPolynomial(2));
				PPolynomial p1 = new PPolynomial(2).multiply(a_1)
						.subtract(new PPolynomial(2).multiply(a1));
				PPolynomial p2 = b1.subtract(a1).subtract(b2).add(a2);
				botanaPolynomials[1] = p1.subtract(t2.multiply(p2));
				PPolynomial p3 = new PPolynomial(2).multiply(a_2)
						.subtract(new PPolynomial(2).multiply(a2));
				PPolynomial p4 = b1.subtract(a1).add(b2).subtract(a2);
				botanaPolynomials[2] = p3.subtract(t2.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by +-60 degrees
			else if (angleValDeg == 60 || angleValDeg == 300) {
				botanaPolynomials = new PPolynomial[3];
				botanaPolynomials[0] = t1.multiply(t1)
						.subtract(new PPolynomial(3));
				PPolynomial p1 = new PPolynomial(2).multiply(a_1)
						.subtract(new PPolynomial(2).multiply(a1)).subtract(b1)
						.add(a1);
				PPolynomial p2 = b2.subtract(a2);
				botanaPolynomials[1] = p1.subtract(t1.multiply(p2));
				PPolynomial p3 = new PPolynomial(2).multiply(a_2)
						.subtract(new PPolynomial(2).multiply(a2)).subtract(b2)
						.add(a2);
				PPolynomial p4 = a1.subtract(b1);
				botanaPolynomials[2] = p3.subtract(t1.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by +-120 degrees
			else if (angleValDeg == 120 || angleValDeg == 240) {
				botanaPolynomials = new PPolynomial[3];
				botanaPolynomials[0] = t1.multiply(t1)
						.subtract(new PPolynomial(3));
				PPolynomial p1 = new PPolynomial(2).multiply(a_1)
						.subtract(new PPolynomial(2).multiply(a1)).add(b1)
						.subtract(a1);
				PPolynomial p2 = b2.subtract(a2);
				botanaPolynomials[1] = p1.subtract(t1.multiply(p2));
				PPolynomial p3 = new PPolynomial(2).multiply(a_2)
						.subtract(new PPolynomial(2).multiply(a2)).add(b2)
						.subtract(a2);
				PPolynomial p4 = a1.subtract(b1);
				botanaPolynomials[2] = p3.subtract(t1.multiply(p4));
				return botanaPolynomials;
			}
			// unhandled angle
			throw new NoSymbolicParametersException();
		}
		throw new NoSymbolicParametersException();
	}
}
