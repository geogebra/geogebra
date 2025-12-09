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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 *
 * @author Markus
 */
public class AlgoMidpointSegment extends AlgoElement
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	private GeoSegment segment; // input
	private GeoPoint M; // output
	private GeoPoint P; // endpoints of segment
	private GeoPoint Q; // endpoints of segment

	private PPolynomial[] polynomials;
	private PVariable[] botanaVars;
	private PPolynomial[] botanaPolynomials;

	/**
	 * Creates new AlgoMidpointSegment
	 * 
	 * @param cons
	 *            construction
	 * @param segment
	 *            segment
	 */
	AlgoMidpointSegment(Construction cons, GeoSegment segment) {
		super(cons);
		this.segment = segment;

		// create new Point
		M = new GeoPoint(cons);
		setInputOutput();

		// compute M = (P + Q)/2
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Midpoint;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_MIDPOINT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = segment;

		setOnlyOutput(M);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Created for LocusEqu
	 * 
	 * @return startpoint
	 */
	public GeoPoint getP() {
		return P;
	}

	/**
	 * Created for LocusEqu
	 * 
	 * @return endpoint
	 */
	public GeoPoint getQ() {
		return Q;
	}

	/**
	 * @return midpoint
	 */
	public GeoPoint getPoint() {
		return M;
	}

	// calc midpoint
	@Override
	public final void compute() {

		if (!segment.isDefined()) {
			M.setUndefined();
			return;
		}
		P = segment.getStartPoint();
		Q = segment.getEndPoint();
		boolean pInf = P.isInfinite();
		boolean qInf = Q.isInfinite();

		if (!pInf && !qInf) {
			// M = (P + Q) / 2
			M.setCoords((P.inhomX + Q.inhomX) / 2.0d,
					(P.inhomY + Q.inhomY) / 2.0d, 1.0);
		} else if (pInf && qInf) {
			M.setUndefined();
		} else if (pInf) {
			M.setCoords(P);
		} else {
			// qInf
			M.setCoords(Q);
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("MidpointOfA", "Midpoint of %0",
				segment.getLabel(tpl));

	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
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
