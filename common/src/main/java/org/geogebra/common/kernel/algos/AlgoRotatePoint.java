/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoRotatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.PointRotateable;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoRotatePoint extends AlgoTransformation implements
		SymbolicParametersBotanaAlgo {

	private GeoPointND Q;
	private PointRotateable out;
	private NumberValue angle;
	private GeoElement inGeo, outGeo, angleGeo;

	private Variable[] botanaVars;
	private Polynomial[] botanaPolynomials;

	/**
	 * Creates new point rotation algo
	 */
	AlgoRotatePoint(Construction cons, String label, GeoElement A,
			NumberValue angle, GeoPointND Q) {
		this(cons, A, angle, Q);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new unlabeled point rotation algo
	 */
	public AlgoRotatePoint(Construction cons, GeoElement A, NumberValue angle,
			GeoPointND Q) {
		super(cons);
		this.angle = angle;
		this.Q = Q;

		angleGeo = angle.toGeoElement();
		inGeo = A;

		outGeo = getResultTemplate(inGeo);
		if (outGeo instanceof PointRotateable)
			out = (PointRotateable) outGeo;

		setInputOutput();
		compute();
		if (inGeo.isGeoFunction())
			cons.registerEuclidianViewCE(this);
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
		input[1] = angleGeo;
		input[2] = (GeoElement) Q;

		setOutputLength(1);
		setOutput(0, outGeo);
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
		} else
			outGeo.set(inGeo);
		if (!outGeo.isDefined()) {
			return;
		}
		out.rotate(angle, Q);
		if (inGeo.isLimitedPath())
			this.transformLimitedPath(inGeo, outGeo);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-25
		// simplified to allow better Chinese translation
		return getLoc().getPlain("ARotatedByAngleB", inGeo.getLabel(tpl),
				angleGeo.getLabel(tpl));
	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList))
			out = (PointRotateable) outGeo;
	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if (geo instanceof GeoFunction)
			return new GeoCurveCartesian(cons);
		return super.getResultTemplate(geo);
	}

	@Override
	public double getAreaScaleFactor() {
		return 1;
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		// point to rotate
		GeoPoint A = (GeoPoint) Q;
		// point around the rotation is processed
		GeoPoint B = (GeoPoint) input[0];

		if (A != null && B != null) {
			Variable[] vA = A.getBotanaVars(A);
			Variable[] vB = B.getBotanaVars(B);

			if (botanaVars == null) {
				botanaVars = new Variable[6];
				// A' - rotation point
				botanaVars[0] = new Variable();
				botanaVars[1] = new Variable();
				// A - point around the rotation is processed
				botanaVars[2] = vA[0];
				botanaVars[3] = vA[1];
				// B - point to rotate
				botanaVars[4] = vB[0];
				botanaVars[5] = vB[1];
			}

			double angleDoubleVal = angle.getDouble();

			Polynomial a1 = new Polynomial(vA[0]);
			Polynomial a2 = new Polynomial(vA[1]);
			Polynomial b1 = new Polynomial(vB[0]);
			Polynomial b2 = new Polynomial(vB[1]);
			Polynomial a_1 = new Polynomial(botanaVars[0]);
			Polynomial a_2 = new Polynomial(botanaVars[1]);

			botanaPolynomials = new Polynomial[2];

			// rotate by 0 degrees
			if (angleDoubleVal == 0.0) {
				botanaPolynomials[0] = a_1.subtract(a1).subtract(b1).add(a1);
				botanaPolynomials[1] = a_2.subtract(a2).subtract(b2).add(a2);
			}
			// rotate by 180 degrees
			if (angleDoubleVal == 3.141592653589793
					|| angleDoubleVal == -3.141592653589793) {
				botanaPolynomials[0] = a_1.subtract(a1).add(b1).subtract(a1);
				botanaPolynomials[1] = a_2.subtract(a2).add(b2).subtract(a2);
			}
			// rotate by 90 degrees
			if (angleDoubleVal == 1.5707963267948966) {
				botanaPolynomials[0] = a_1.subtract(a1).subtract(b2).add(a2);
				botanaPolynomials[1] = a_2.subtract(a2).add(b1).subtract(a1);
			}
			// rotate by -90 degrees
			if (angleDoubleVal == -1.5707963267948966) {
				botanaPolynomials[0] = a_1.subtract(a1).subtract(b2).add(a2);
				botanaPolynomials[1] = a_2.subtract(a2).add(b1).subtract(a1);
			}
			// rotate by 30 degrees
			if (angleDoubleVal == 0.5235987755982988) {
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).subtract(b2)
						.add(a2);
				Polynomial p2 = b1.subtract(a1);
				Polynomial p3 = new Polynomial(3).multiply(p2).multiply(p2);
				botanaPolynomials[0] = p1.multiply(p1).subtract(p3);
				Polynomial p4 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).add(b1)
						.subtract(a1);
				Polynomial p5 = b2.subtract(a2);
				Polynomial p6 = new Polynomial(3).multiply(p5).multiply(p5);
				botanaPolynomials[1] = p4.multiply(p4).subtract(p6);
			}
			// rotate by -30 degrees
			if (angleDoubleVal == -0.5235987755982988) {
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).add(b2)
						.subtract(a2);
				Polynomial p2 = b1.subtract(a1);
				Polynomial p3 = new Polynomial(3).multiply(p2).multiply(p2);
				botanaPolynomials[0] = p1.multiply(p1).subtract(p3);
				Polynomial p4 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).subtract(b1)
						.add(a1);
				Polynomial p5 = b2.subtract(a2);
				Polynomial p6 = new Polynomial(3).multiply(p5).multiply(p5);
				botanaPolynomials[1] = p4.multiply(p4).subtract(p6);
			}
			// rotate by 45 degrees
			if (angleDoubleVal == 0.7853981633974483) {
				Polynomial p1 = new Polynomial(2).multiply(a_1).subtract(
						new Polynomial(2).multiply(a1));
				Polynomial p2 = b1.subtract(a1).add(b2).subtract(a2);
				botanaPolynomials[0] = p1.multiply(p1).subtract(
						new Polynomial(2).multiply(p2).multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2).subtract(
						new Polynomial(2).multiply(a2));
				Polynomial p4 = b2.subtract(a2).subtract(b1).add(a1);
				botanaPolynomials[1] = p3.multiply(p3).subtract(
						new Polynomial(2).multiply(p4).multiply(p4));
			}
			// rotate by -45 degrees
			if (angleDoubleVal == -0.7853981633974483) {
				Polynomial p1 = new Polynomial(2).multiply(a_1).subtract(
						new Polynomial(2).multiply(a1));
				Polynomial p2 = b1.subtract(a1).subtract(b2).add(a2);
				botanaPolynomials[0] = p1.multiply(p1).subtract(
						new Polynomial(2).multiply(p2).multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2).subtract(
						new Polynomial(2).multiply(a2));
				Polynomial p4 = b1.subtract(a1).add(b2).subtract(a2);
				botanaPolynomials[1] = p3.multiply(p3).subtract(
						new Polynomial(2).multiply(p4).multiply(p4));
			}
			// rotate by 60 degrees
			if (angleDoubleVal == 1.0471975511965976) {
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).subtract(b1)
						.add(a1);
				Polynomial p2 = b2.subtract(a2);
				botanaPolynomials[0] = p1.multiply(p1).subtract(
						new Polynomial(3).multiply(p2).multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).subtract(b2)
						.add(a2);
				Polynomial p4 = b1.subtract(a1);
				botanaPolynomials[1] = p3.multiply(p3).subtract(
						new Polynomial(3).multiply(p4).multiply(p4));
			}
			// rotate by -60 degrees
			if (angleDoubleVal == -1.0471975511965976) {
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).subtract(b1)
						.add(a1);
				Polynomial p2 = b2.subtract(a2);
				botanaPolynomials[0] = p1.multiply(p1).subtract(
						new Polynomial(3).multiply(p2).multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).subtract(b2)
						.add(a2);
				Polynomial p4 = b1.subtract(a1);
				botanaPolynomials[1] = p3.multiply(p3).subtract(
						new Polynomial(3).multiply(p4).multiply(p4));
			}

			return botanaPolynomials;

		}
		throw new NoSymbolicParametersException();
	}

	// TODO Consider locusequability

}
