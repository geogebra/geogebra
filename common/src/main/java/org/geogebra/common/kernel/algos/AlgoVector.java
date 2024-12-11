/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoVector.java
 *
 * Created on 24. September 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * Vector between two points P and Q.
 * 
 * @author Markus
 */
public class AlgoVector extends AlgoElement
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	private GeoPointND P; // input
	private GeoPointND Q; // input
	private GeoVectorND v; // output
	private PPolynomial[] polynomials;

	private PPolynomial[] botanaPolynomials;
	private PVariable[] botanaVars;

	/**
	 * Creates new AlgoVector
	 * 
	 * @param cons
	 *            construction
	 * @param P
	 *            start point
	 * @param Q
	 *            end point
	 */
	public AlgoVector(Construction cons, GeoPointND P,
			GeoPointND Q) {
		super(cons);
		this.P = P;
		this.Q = Q;

		// create new vector
		v = createNewVector();
		// v = new GeoVector(cons);

		// set dependencies now to make sure the vector v is
		// not added to the locatable list of P
		// see #2462
		setInputOutput();

		// set startpoint of vector
		try {
			if (P.isLabelSet()) {
				v.setStartPoint(P);
			} else {
				GeoPointND startPoint = newStartPoint();
				// GeoPoint startPoint = new GeoPoint(P);
				startPoint.set(P);
				v.setStartPoint(startPoint);
			}
		} catch (CircularDefinitionException e) {
			// just formal; v is new, so can't really cause this
		}

		// compute vector PQ
		compute();
	}

	/**
	 * @return new vector (overridden in 3D)
	 */
	protected GeoVectorND createNewVector() {
		return new GeoVector(cons);
	}

	/**
	 * @return copy of P (overridden in 3D)
	 */
	protected GeoPointND newStartPoint() {

		return new GeoPoint((GeoPoint) P);
	}

	@Override
	public Commands getClassName() {
		return Commands.Vector;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_VECTOR;
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) P;
		input[1] = (GeoElement) Q;

		setOnlyOutput(v);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return output vector
	 */
	public GeoVectorND getVector() {
		return v;
	}

	/**
	 * @return input start point
	 */
	public GeoPointND getP() {
		return P;
	}

	/**
	 * @return input end point
	 */
	public GeoPointND getQ() {
		return Q;
	}

	// calc the vector between P and Q
	@Override
	public final void compute() {
		if (P.isFinite() && Q.isFinite()) {
			setCoords();

			// update position of unlabeled startpoint
			GeoPointND startPoint = v.getStartPoint();

			if (startPoint != null) {
				if (!startPoint.isLabelSet()) {
					startPoint.set(P);
				}
			}

		} else {
			v.setUndefined();
		}
	}

	/**
	 * Updates coords of v using the strtpoint and endpoint
	 */
	protected void setCoords() {
		v.setCoords(P.vectorTo(Q));
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if (P != null && Q != null && P instanceof SymbolicParametersAlgo
				&& Q instanceof SymbolicParametersAlgo) {
			((SymbolicParametersAlgo) P).getFreeVariables(variables);
			((SymbolicParametersAlgo) Q).getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if (P != null && Q != null && P instanceof SymbolicParametersAlgo
				&& Q instanceof SymbolicParametersAlgo) {
			int[] degree1 = ((SymbolicParametersAlgo) P).getDegrees(a);
			int[] degree2 = ((SymbolicParametersAlgo) Q).getDegrees(a);
			int[] result = new int[3];
			result[0] = Math.max(degree1[0] + degree2[2],
					degree2[0] + degree1[2]);
			result[1] = Math.max(degree1[1] + degree2[2],
					degree2[1] + degree1[2]);
			result[2] = degree2[2] + degree1[2];

			return result;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			final HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (P != null && Q != null && P instanceof SymbolicParametersAlgo
				&& Q instanceof SymbolicParametersAlgo) {
			BigInteger[] coords1 = ((SymbolicParametersAlgo) P)
					.getExactCoordinates(values);
			BigInteger[] coords2 = ((SymbolicParametersAlgo) Q)
					.getExactCoordinates(values);
			BigInteger[] result = new BigInteger[3];
			result[0] = coords2[0].multiply(coords1[2])
					.subtract(coords1[0].multiply(coords2[2]));
			result[1] = coords2[1].multiply(coords1[2])
					.subtract(coords1[1].multiply(coords2[2]));
			result[2] = coords1[2].multiply(coords2[2]);
			return SymbolicParameters.reduce(result);
		}
		return null;
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (P != null && Q != null && P instanceof SymbolicParametersAlgo
				&& Q instanceof SymbolicParametersAlgo) {
			PPolynomial[] coords1 = ((SymbolicParametersAlgo) P)
					.getPolynomials();
			PPolynomial[] coords2 = ((SymbolicParametersAlgo) Q)
					.getPolynomials();
			polynomials = new PPolynomial[3];
			polynomials[0] = coords2[0].multiply(coords1[2])
					.subtract(coords1[0].multiply(coords2[2]));
			polynomials[1] = coords2[1].multiply(coords1[2])
					.subtract(coords1[1].multiply(coords2[2]));
			polynomials[2] = coords1[2].multiply(coords2[2]);
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

		GeoPoint A = (GeoPoint) P;
		GeoPoint B = (GeoPoint) Q;

		if (P != null && Q != null) {

			PVariable[] vP = A.getBotanaVars(A);
			PVariable[] vQ = B.getBotanaVars(B);

			if (botanaVars == null) {
				botanaVars = new PVariable[6];
				// vector u
				botanaVars[0] = new PVariable(kernel);
				botanaVars[1] = new PVariable(kernel);
				// P
				botanaVars[2] = vP[0];
				botanaVars[3] = vP[1];
				// Q
				botanaVars[4] = vQ[0];
				botanaVars[5] = vQ[1];
			}

			botanaPolynomials = new PPolynomial[2];

			PPolynomial p1 = new PPolynomial(vP[0]);
			PPolynomial p2 = new PPolynomial(vP[1]);
			PPolynomial q1 = new PPolynomial(vQ[0]);
			PPolynomial q2 = new PPolynomial(vQ[1]);
			PPolynomial u1 = new PPolynomial(botanaVars[0]);
			PPolynomial u2 = new PPolynomial(botanaVars[1]);

			botanaPolynomials[0] = u1.subtract(q1).add(p1);
			botanaPolynomials[1] = u2.subtract(q2).add(p2);

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

}
