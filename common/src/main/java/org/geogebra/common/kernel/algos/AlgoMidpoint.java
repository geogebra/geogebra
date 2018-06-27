/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoMidPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.AlgoMidpointND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 *
 * @author Markus
 */
public class AlgoMidpoint extends AlgoMidpointND
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	private PPolynomial[] polynomials;
	private PPolynomial[] botanaPolynomials;
	private PVariable[] botanaVars;

	/**
	 * @param cons
	 *            construction
	 * @param P
	 *            first point
	 * @param Q
	 *            second point
	 */
	public AlgoMidpoint(Construction cons, GeoPoint P, GeoPoint Q) {
		super(cons, P, Q);
	}

	@Override
	protected GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint(cons1);
	}

	@Override
	public GeoPoint getPoint() {
		return (GeoPoint) super.getPoint();
	}

	@Override
	protected void copyCoords(GeoPointND point) {
		getPoint().setCoords((GeoPoint) point);
	}

	// Made public for LocusEqu
	@Override
	public GeoPoint getP() {
		return (GeoPoint) super.getP();
	}

	// Made public for LocusEqu
	@Override
	public GeoPoint getQ() {
		return (GeoPoint) super.getQ();
	}

	@Override
	protected void computeMidCoords() {

		GeoPoint P = getP();
		GeoPoint Q = getQ();

		getPoint().setCoords((P.inhomX + Q.inhomX) / 2.0d,
				(P.inhomY + Q.inhomY) / 2.0d, 1.0);
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		GeoPoint P = getP();
		GeoPoint Q = getQ();
		if (P != null && Q != null) {
			P.getFreeVariables(variables);
			Q.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		GeoPoint P = getP();
		GeoPoint Q = getQ();
		if (P != null && Q != null) {
			int[] degreeP = P.getDegrees(a);
			int[] degreeQ = Q.getDegrees(a);

			int[] result = new int[3];
			result[0] = Math.max(degreeP[0] + degreeQ[2],
					degreeQ[0] + degreeP[2]);
			result[1] = Math.max(degreeP[1] + degreeQ[2],
					degreeQ[1] + degreeP[2]);
			result[2] = degreeP[2] + degreeQ[2];
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		GeoPoint P = getP();
		GeoPoint Q = getQ();
		if (P != null && Q != null) {
			BigInteger[] pP = P.getExactCoordinates(values);
			BigInteger[] pQ = Q.getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[3];
			coords[0] = pP[0].multiply(pQ[2]).add(pQ[0].multiply(pP[2]));
			coords[1] = pP[1].multiply(pQ[2]).add(pQ[1].multiply(pP[2]));
			coords[2] = pP[2].multiply(pQ[2]).multiply(BigInteger.valueOf(2));
			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		GeoPoint P = getP();
		GeoPoint Q = getQ();
		if (P != null && Q != null) {
			PPolynomial[] pP = P.getPolynomials();
			PPolynomial[] pQ = Q.getPolynomials();
			polynomials = new PPolynomial[3];
			polynomials[0] = pP[0].multiply(pQ[2]).add(pQ[0].multiply(pP[2]));
			polynomials[1] = pP[1].multiply(pQ[2]).add(pQ[1].multiply(pP[2]));
			polynomials[2] = pP[2].multiply(pQ[2]).multiply(new PPolynomial(2));
			return polynomials;
		}
		throw new NoSymbolicParametersException();
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

		GeoPoint P = getP();
		GeoPoint Q = getQ();

		if (P == null || Q == null) {
			throw new NoSymbolicParametersException();
		}

		if (botanaVars == null) {
			botanaVars = new PVariable[2];
			botanaVars[0] = new PVariable(kernel);
			botanaVars[1] = new PVariable(kernel);
		}

		botanaPolynomials = SymbolicParameters.botanaPolynomialsMidpoint(P, Q,
				botanaVars);
		return botanaPolynomials;

	}

}
