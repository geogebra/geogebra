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

package org.geogebra.common.kernel.prover;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.algos.SymbolicParametersAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * Decides if the points are collinear. Can be embedded into the Prove command
 * to work symbolically.
 * 
 * @author Simon Weitzhofer 18th April 2012
 * @author Zoltan Kovacs
 */
public class AlgoAreCollinear extends AlgoElement
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgoAre {

	private GeoPointND inputPoint1, inputPoint2, inputPoint3; // input

	private GeoBoolean outputBoolean; // output
	private PPolynomial[] polynomials;
	private PPolynomial[][] botanaPolynomials;

	/**
	 * Creates a new AlgoAreCollinear function
	 * 
	 * @param cons
	 *            the Construction
	 * @param inputPoint1
	 *            the first point
	 * @param inputPoint2
	 *            the second point
	 * @param inputPoint3
	 *            the third point
	 */
	public AlgoAreCollinear(final Construction cons,
			final GeoPointND inputPoint1, final GeoPointND inputPoint2,
			final GeoPointND inputPoint3) {
		super(cons);
		this.inputPoint1 = inputPoint1;
		this.inputPoint2 = inputPoint2;
		this.inputPoint3 = inputPoint3;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.AreCollinear;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = inputPoint1.toGeoElement();
		input[1] = inputPoint2.toGeoElement();
		input[2] = inputPoint3.toGeoElement();

		setOnlyOutput(outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the result of the test
	 * 
	 * @return true if the three points lie on one line, false otherwise
	 */
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		outputBoolean.setValue(
				GeoPoint.collinearND(inputPoint1, inputPoint2, inputPoint3));
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if (getInputPoint1() != null && getInputPoint2() != null
				&& getInputPoint3() != null) {
			getInputPoint1().getFreeVariables(variables);
			getInputPoint2().getFreeVariables(variables);
			getInputPoint3().getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if (getInputPoint1() != null && getInputPoint2() != null
				&& getInputPoint3() != null) {
			int[] degree1 = getInputPoint1().getDegrees(a);
			int[] degree2 = getInputPoint2().getDegrees(a);
			int[] degree3 = getInputPoint3().getDegrees(a);
			int[] result = new int[1];
			result[0] = Math.max(degree1[0] + degree2[1] + degree3[2], Math.max(
					degree2[0] + degree3[1] + degree1[2],
					Math.max(degree3[0] + degree1[1] + degree2[2], Math.max(
							degree3[0] + degree2[1] + degree1[2], Math.max(
									degree2[0] + degree1[1] + degree3[2],
									degree1[0] + degree3[1] + degree2[2])))));
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			final HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (getInputPoint1() != null && getInputPoint2() != null
				&& getInputPoint3() != null) {
			BigInteger[] coords1 = getInputPoint1().getExactCoordinates(values);
			BigInteger[] coords2 = getInputPoint2().getExactCoordinates(values);
			BigInteger[] coords3 = getInputPoint3().getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[1];
			coords[0] = coords1[0].multiply(coords2[1]).multiply(coords3[2])
					.add(coords2[0].multiply(coords3[1]).multiply(coords1[2]))
					.add(coords3[0].multiply(coords1[1]).multiply(coords2[2]))
					.subtract(

							coords3[0].multiply(coords2[1]).multiply(coords1[2])
									.add(coords2[0].multiply(coords1[1])
											.multiply(coords3[2]))
									.add(coords1[0].multiply(coords3[1])
											.multiply(coords2[2])));
			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}

		if (getInputPoint1() != null && getInputPoint2() != null
				&& getInputPoint3() != null) {
			PPolynomial[] coords1 = getInputPoint1().getPolynomials();
			PPolynomial[] coords2 = getInputPoint2().getPolynomials();
			PPolynomial[] coords3 = getInputPoint3().getPolynomials();
			polynomials = new PPolynomial[1];
			polynomials[0] = coords1[0].multiply(coords2[1])
					.multiply(coords3[2])
					.add(coords2[0].multiply(coords3[1]).multiply(coords1[2]))
					.add(coords3[0].multiply(coords1[1]).multiply(coords2[2]))
					.subtract(

							coords3[0].multiply(coords2[1]).multiply(coords1[2])
									.add(coords2[0].multiply(coords1[1])
											.multiply(coords3[2]))
									.add(coords1[0].multiply(coords3[1])
											.multiply(coords2[2])));
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (getInputPoint1() != null && getInputPoint2() != null
				&& getInputPoint3() != null) {

			PVariable[] fv1 = getInputPoint1().getBotanaVars(getInputPoint1());
			PVariable[] fv2 = getInputPoint2().getBotanaVars(getInputPoint2());
			PVariable[] fv3 = getInputPoint3().getBotanaVars(getInputPoint3());

			botanaPolynomials = new PPolynomial[1][1];
			botanaPolynomials[0][0] = PPolynomial.collinear(fv1[0], fv1[1],
					fv2[0], fv2[1], fv3[0], fv3[1]);
			return botanaPolynomials;

		}
		throw new NoSymbolicParametersException();
	}

	private GeoPoint getInputPoint1() {
		return (GeoPoint) inputPoint1;
	}

	private GeoPoint getInputPoint2() {
		return (GeoPoint) inputPoint2;
	}

	private GeoPoint getInputPoint3() {
		return (GeoPoint) inputPoint3;
	}

}
